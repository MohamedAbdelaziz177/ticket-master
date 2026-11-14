package com.ticketmaster.eventservice.controllers;

import com.ticketmaster.eventservice.dtos.eventseats.AddEventSeatRequest;
import com.ticketmaster.eventservice.dtos.eventseats.EventSeatResponse;
import com.ticketmaster.eventservice.dtos.eventseats.UpdateEventSeatRequest;
import com.ticketmaster.eventservice.entities.EventSeat;
import com.ticketmaster.eventservice.enums.EventSeatStatus;
import com.ticketmaster.eventservice.result.BaseController;
import com.ticketmaster.eventservice.result.Result;
import com.ticketmaster.eventservice.services.EventSeatService;
import com.ticketmaster.eventservice.utils.PageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/events/{eventId}/seats")
@RequiredArgsConstructor
public class EventSeatController extends BaseController {

    private final EventSeatService eventSeatService;

    @PostMapping
    public ResponseEntity<Result<EventSeatResponse>> addSeat(
            @PathVariable Long eventId,
            @Valid @RequestBody AddEventSeatRequest request
    ) {
        Result<EventSeatResponse> result = eventSeatService.addSeat(eventId, request);
        return ResponseEntity.status(resolveStatus(result)).body(result);
    }

    @GetMapping("/{eventSeatId}")
    public ResponseEntity<Result<EventSeatResponse>> getById(
            @PathVariable Long eventId,
            @PathVariable Long eventSeatId
    ) {
        Result<EventSeatResponse> result = eventSeatService.getById(eventId, eventSeatId);
        return ResponseEntity.status(resolveStatus(result)).body(result);
    }

    @GetMapping
    public ResponseEntity<Result<PageResponse<EventSeat, EventSeatResponse>>> listByEvent(
            @PathVariable Long eventId,
            @RequestParam(defaultValue = "0") Integer pageIdx,
            @RequestParam(defaultValue = "20") Integer pageSize,
            @RequestParam(required = false) EventSeatStatus status
    ) {
        Result<PageResponse<EventSeat, EventSeatResponse>> result =
                eventSeatService.listByEvent(eventId, pageIdx, pageSize, status);
        return ResponseEntity.status(resolveStatus(result)).body(result);
    }

    @PutMapping("/{eventSeatId}")
    public ResponseEntity<Result<EventSeatResponse>> update(
            @PathVariable Long eventId,
            @PathVariable Long eventSeatId,
            @Valid @RequestBody UpdateEventSeatRequest request
    ) {
        Result<EventSeatResponse> result = eventSeatService.update(eventId, eventSeatId, request);
        return ResponseEntity.status(resolveStatus(result)).body(result);
    }

    @DeleteMapping("/{eventSeatId}")
    public ResponseEntity<Result<Void>> delete(
            @PathVariable Long eventId,
            @PathVariable Long eventSeatId
    ) {
        Result<Void> result = eventSeatService.delete(eventId, eventSeatId);
        return ResponseEntity.status(resolveStatus(result)).body(result);
    }
}