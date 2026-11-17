package com.ticketmaster.searchservice.controllers;

import com.ticketmaster.searchservice.Event;
import com.ticketmaster.searchservice.EventSearchService;
import com.ticketmaster.searchservice.dtos.EventsSearchCtx;
import com.ticketmaster.searchservice.dtos.EventSearchResult;
import com.ticketmaster.searchservice.result.BaseController;
import com.ticketmaster.searchservice.result.Result;
import com.ticketmaster.searchservice.utils.PageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/search/events")
@RequiredArgsConstructor
public class SearchController extends BaseController {

    private final EventSearchService eventSearchService;

    @GetMapping
    public ResponseEntity<Result<PageResponse<Event, EventSearchResult>>> search(
            @Valid @ModelAttribute EventsSearchCtx criteria,
            @RequestParam(defaultValue = "0") Integer pageIdx,
            @RequestParam(defaultValue = "20") Integer pageSize
    ) {
        Result<PageResponse<Event, EventSearchResult>> result =
                eventSearchService.search(criteria, pageIdx, pageSize);
        return ResponseEntity.status(resolveStatus(result)).body(result);
    }
}
