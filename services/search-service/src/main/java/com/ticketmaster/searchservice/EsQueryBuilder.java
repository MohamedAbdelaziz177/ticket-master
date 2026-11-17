package com.ticketmaster.searchservice;

import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EsQueryBuilder {

    public Query getSearchQuery(
            String searchTerm,
            String venueName,
            String eventStatus,
            String eventType,
            String address,
            Instant from,
            Instant to
    ) {
        BoolQuery.Builder builder = new BoolQuery.Builder();

        buildDescriptionOrNameFTSQuery(builder, searchTerm);
        buildDatesQuery(builder, from, to);
        buildStatusQuery(builder, eventStatus);
        buildVenueQuery(builder, venueName);
        buildTypeQuery(builder, eventType);
        buildAddressQuery(builder, address);

        return Query.of(q -> q.bool(builder.build()));
    }


    private void buildDatesQuery(BoolQuery.Builder builder, Instant from, Instant to) {
        if (from != null) {
            builder.filter(f -> f.range(r -> r
                    .date(d -> d
                            .field(SearchFields.DATE)
                            .gte(from.toString())
                    )
            ));
        }

        if (to != null) {
            builder.filter(f -> f.range(r -> r
                    .date(d -> d
                            .field(SearchFields.DATE)
                            .lte(to.toString())
                    )
            ));
        }
    }

    private void buildDescriptionOrNameFTSQuery(BoolQuery.Builder builder, String searchTerm) {
        if (searchTerm != null && !searchTerm.isBlank()) {

            String term = searchTerm.trim();
            builder.must(m -> m.bool(b -> b
                    .should(s -> s.match(mt -> mt
                            .field(SearchFields.NAME)
                            .query(term)
                    ))
                    .should(s -> s.match(mt -> mt
                            .field(SearchFields.DESCRIPTION)
                            .query(term)
                            .fuzziness("AUTO")
                    ))
                    .minimumShouldMatch("1")
            ));
        }
    }

    private void buildStatusQuery(
            BoolQuery.Builder builder,
            String eventStatus
    ) {
        if (eventStatus != null) {
            builder.filter(f -> f.term(t -> t
                    .field(SearchFields.EVENT_STATUS)
                    .value(FieldValue.of(eventStatus))
            ));
        }
    }

    private void buildVenueQuery(BoolQuery.Builder builder, String venueName) {
        if (venueName != null && !venueName.isBlank()) {
            builder.filter(f -> f.match(m -> m
                    .field(SearchFields.VENUE_NAME)
                    .query(venueName.trim())
            ));
        }
    }

    private void buildTypeQuery(BoolQuery.Builder builder, String eventType) {
        if (eventType != null && !eventType.isBlank()) {
            builder.filter(f -> f.term(t -> t
                    .field(SearchFields.EVENT_TYPE)
                    .value(FieldValue.of(eventType))
            ));
        }
    }

    private void buildAddressQuery(BoolQuery.Builder builder, String address) {
        if (address != null && !address.isBlank()) {
            builder.filter(f -> f.match(m -> m
                    .field(SearchFields.ADDRESS)
                    .query(address.trim())
            ));
        }
    }
}
