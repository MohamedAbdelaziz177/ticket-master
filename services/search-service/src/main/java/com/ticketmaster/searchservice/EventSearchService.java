package com.ticketmaster.searchservice;

import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import com.ticketmaster.searchservice.dtos.EventSearchResult;
import com.ticketmaster.searchservice.dtos.EventsSearchCtx;
import com.ticketmaster.searchservice.result.ApiError;
import com.ticketmaster.searchservice.result.Result;
import com.ticketmaster.searchservice.utils.PageResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

/**
 * Read-only search over the {@code events} projection. Elasticsearch is a derived read
 * model: results may lag behind the event service and must never be used to decide seat
 * availability or bookings.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EventSearchService {

    static final int MAX_PAGE_SIZE = 100;
    static final long MAX_RESULT_WINDOW = 10_000L;
    static final Duration SEARCH_TIMEOUT = Duration.ofSeconds(3);

    EsQueryBuilder esQueryBuilder;
    ElasticsearchOperations elasticsearchOperations;

    public Result<PageResponse<Event, EventSearchResult>> search(
            EventsSearchCtx criteria,
            Integer pageIdx,
            Integer pageSize
    ) {
        Result<Pageable> pageableResult = toPageable(pageIdx, pageSize);
        if (!pageableResult.isSuccess()) {
            return Result.Failure(pageableResult.getError());
        }

        Query query = esQueryBuilder.getSearchQuery(
                criteria.searchTerm(),
                criteria.venueName(),
                criteria.eventStatus(),
                criteria.eventType(),
                criteria.address(),
                criteria.dateFrom(),
                criteria.dateTo()
        );

        NativeQueryBuilder nativeQueryBuilder = NativeQuery.builder()
                .withQuery(query)
                .withPageable(pageableResult.getData())
                .withTrackTotalHits(true)
                .withTimeout(SEARCH_TIMEOUT);

        Sort sort = toSort(criteria);
        if (!sort.isUnsorted()) {
            nativeQueryBuilder.withSort(sort);
        }

        try {
            SearchHits<Event> hits = elasticsearchOperations.search(nativeQueryBuilder.build(), Event.class);

            List<EventSearchResult> content = hits.getSearchHits().stream()
                    .map(SearchHit::getContent)
                    .map(EventSearchResult::from)
                    .toList();

            log.debug(
                    "Event search succeeded: pageIdx={}, pageSize={}, totalHits={}",
                    pageIdx,
                    pageSize,
                    hits.getTotalHits()
            );

            return Result.Success(PageResponse.of(hits.getTotalHits(), pageIdx, pageSize, content));
        } catch (DataAccessException | ElasticsearchException exception) {
            log.error("Event search against Elasticsearch failed: pageIdx={}, pageSize={}", pageIdx, pageSize, exception);
            return Result.Failure(ApiError.ServerError("Search is temporarily unavailable"));
        }
    }

    private Result<Pageable> toPageable(Integer pageIdx, Integer pageSize) {
        if (pageIdx == null || pageIdx < 0) {
            return Result.Failure(ApiError.BadRequest("pageIdx must be greater than or equal to 0"));
        }

        if (pageSize == null || pageSize < 1 || pageSize > MAX_PAGE_SIZE) {
            return Result.Failure(ApiError.BadRequest("pageSize must be between 1 and " + MAX_PAGE_SIZE));
        }

        long window = ((long) pageIdx + 1) * pageSize;
        if (window > MAX_RESULT_WINDOW) {
            return Result.Failure(ApiError.BadRequest(
                    "pageIdx and pageSize exceed the maximum searchable window of " + MAX_RESULT_WINDOW + " results"
            ));
        }

        return Result.Success(PageRequest.of(pageIdx, pageSize));
    }

    /**
     * Relevance ordering is Elasticsearch's default when a full-text term is present.
     * Without one, order by event date so paging stays deterministic.
     */
    private Sort toSort(EventsSearchCtx criteria) {
        if (hasSearchTerm(criteria)) {
            return Sort.unsorted();
        }

        return Sort.by(Sort.Order.asc(SearchFields.DATE));
    }

    private boolean hasSearchTerm(EventsSearchCtx criteria) {
        return criteria.searchTerm() != null && !criteria.searchTerm().isBlank();
    }
}
