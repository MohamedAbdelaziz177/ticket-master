package com.ticketmaster.eventservice.controllers;

import com.ticketmaster.eventservice.dtos.events.CreateEventRequest;
import com.ticketmaster.eventservice.dtos.events.EventResponse;
import com.ticketmaster.eventservice.dtos.events.UpdateEventRequest;
import com.ticketmaster.eventservice.entities.Event;
import com.ticketmaster.eventservice.enums.EventStatus;
import com.ticketmaster.eventservice.result.BaseController;
import com.ticketmaster.eventservice.result.Result;
import com.ticketmaster.eventservice.services.EventService;
import com.ticketmaster.eventservice.utils.PageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController extends BaseController {

    private final EventService eventService;

    @PostMapping
    public ResponseEntity<Result<EventResponse>> create(@Valid @RequestBody CreateEventRequest request) {
        Result<EventResponse> result = eventService.create(request);
        return ResponseEntity.status(resolveStatus(result)).body(result);
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<Result<EventResponse>> getById(@PathVariable Long eventId) {
        Result<EventResponse> result = eventService.getById(eventId);
        return ResponseEntity.status(resolveStatus(result)).body(result);
    }

    @GetMapping
    public ResponseEntity<Result<PageResponse<Event, EventResponse>>> getAll(
            @RequestParam(defaultValue = "0") Integer pageIdx,
            @RequestParam(defaultValue = "20") Integer pageSize,
            @RequestParam(required = false) Long venueId,
            @RequestParam(required = false) EventStatus status
    ) {
        Result<PageResponse<Event, EventResponse>> result = eventService.getAll(pageIdx, pageSize, venueId, status);
        return ResponseEntity.status(resolveStatus(result)).body(result);
    }

    @PutMapping("/{eventId}")
    public ResponseEntity<Result<EventResponse>> update(
            @PathVariable Long eventId,
            @Valid @RequestBody UpdateEventRequest request
    ) {
        Result<EventResponse> result = eventService.update(eventId, request);
        return ResponseEntity.status(resolveStatus(result)).body(result);
    }

    @DeleteMapping("/{eventId}")
    public ResponseEntity<Result<Void>> deleteById(@PathVariable Long eventId) {
        Result<Void> result = eventService.deleteById(eventId);
        return ResponseEntity.status(resolveStatus(result)).body(result);
    }
}