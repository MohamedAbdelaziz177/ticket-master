package com.ticketmaster.eventservice.services;

import com.ticketmaster.eventservice.dtos.events.CreateEventRequest;
import com.ticketmaster.eventservice.dtos.events.EventResponse;
import com.ticketmaster.eventservice.dtos.events.UpdateEventRequest;
import com.ticketmaster.eventservice.entities.Event;
import com.ticketmaster.eventservice.entities.Venue;
import com.ticketmaster.eventservice.enums.EventSeatStatus;
import com.ticketmaster.eventservice.enums.EventStatus;
import com.ticketmaster.eventservice.mappers.EventMapper;
import com.ticketmaster.eventservice.repositories.EventRepository;
import com.ticketmaster.eventservice.repositories.EventSeatRepository;
import com.ticketmaster.eventservice.repositories.VenueRepository;
import com.ticketmaster.eventservice.result.ApiError;
import com.ticketmaster.eventservice.result.Result;
import com.ticketmaster.eventservice.utils.PageResponse;
import com.ticketmaster.eventservice.validators.EventValidator;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EventService {

    private static final int MAX_PAGE_SIZE = 100;

    EventRepository      eventRepository;
    EventSeatRepository  eventSeatRepository;
    VenueRepository      venueRepository;
    EventValidator       eventValidator;

    @Transactional
    public Result<EventResponse> create(CreateEventRequest request) {
        Venue venue = venueRepository.findById(request.venueId()).orElse(null);
        if (venue == null) {
            return Result.Failure(ApiError.NotFound("No Venue associated with this id"));
        }

        Result<Void> windowValidation = eventValidator.validateTimeWindow(request.startsAt(), request.endsAt());
        if (!windowValidation.isSuccess()) {
            return Result.Failure(windowValidation.getError());
        }

        EventStatus status = request.status() == null ? EventStatus.UPCOMING : request.status();
        Event event = eventRepository.save(EventMapper.toEvent(request, venue, status));

        return Result.Success(EventResponse.from(event));
    }

    @Transactional(readOnly = true)
    public Result<EventResponse> getById(Long eventId) {
        return eventRepository.findByIdIncludeVenue(eventId)
                .map(event -> Result.Success(EventResponse.from(event)))
                .orElse(Result.Failure(ApiError.NotFound("No Event associated with this id")));
    }

    @Transactional(readOnly = true)
    public Result<PageResponse<Event, EventResponse>> getAll(
            Integer pageIdx,
            Integer pageSize,
            Long venueId,
            EventStatus status
    ) {
        Result<Pageable> pageableResult = toPageable(pageIdx, pageSize);
        if (!pageableResult.isSuccess()) {
            return Result.Failure(pageableResult.getError());
        }

        Page<Event> page = findPage(venueId, status, pageableResult.getData());

        return Result.Success(PageResponse.of(
                page,
                page.getContent().stream().map(EventResponse::from).toList()
        ));
    }

    @Transactional
    public Result<EventResponse> update(Long eventId, UpdateEventRequest request) {
        Event event = eventRepository.findByIdIncludeVenue(eventId).orElse(null);
        if (event == null) {
            return Result.Failure(ApiError.NotFound("No Event associated with this id"));
        }

        Result<Void> windowValidation = eventValidator.validateTimeWindow(request.startsAt(), request.endsAt());
        if (!windowValidation.isSuccess()) {
            return Result.Failure(windowValidation.getError());
        }

        Venue venue = event.getVenue();
        if (!Objects.equals(venue.getId(), request.venueId())) {
            Venue newVenue = venueRepository.findById(request.venueId()).orElse(null);
            if (newVenue == null) {
                return Result.Failure(ApiError.NotFound("No Venue associated with this id"));
            }

            if (eventSeatRepository.existsByEventId(eventId)) {
                return Result.Failure(
                        ApiError.Conflict("Event already has seats assigned; the venue cannot be changed")
                );
            }

            venue = newVenue;
        }

        EventMapper.updateEvent(event, request, venue);
        Event saved = eventRepository.save(event);

        return Result.Success(EventResponse.from(saved));
    }

    @Transactional
    public Result<Void> deleteById(Long eventId) {
        Event event = eventRepository.findByIdForUpdate(eventId).orElse(null);
        if (event == null) {
            return Result.Failure(ApiError.NotFound("No Event associated with this id"));
        }

        long soldSeats = eventSeatRepository.countByEventIdAndStatus(eventId, EventSeatStatus.SOLD);
        if (soldSeats > 0) {
            return Result.Failure(ApiError.Conflict("Event has sold seats and cannot be deleted"));
        }

        eventSeatRepository.deleteAllByEventId(eventId);
        eventRepository.delete(event);

        return Result.Success(null);
    }

    private Page<Event> findPage(Long venueId, EventStatus status, Pageable pageable) {
        if (venueId != null && status != null) {
            return eventRepository.findByVenueIdAndStatus(venueId, status, pageable);
        }

        if (venueId != null) {
            return eventRepository.findByVenueId(venueId, pageable);
        }

        if (status != null) {
            return eventRepository.findByStatus(status, pageable);
        }

        return eventRepository.findAll(pageable);
    }

    private Result<Pageable> toPageable(Integer pageIdx, Integer pageSize) {
        if (pageIdx == null || pageIdx < 0) {
            return Result.Failure(ApiError.BadRequest("pageIdx must be greater than or equal to 0"));
        }

        if (pageSize == null || pageSize < 1 || pageSize > MAX_PAGE_SIZE) {
            return Result.Failure(
                    ApiError.BadRequest("pageSize must be between 1 and " + MAX_PAGE_SIZE)
            );
        }

        Sort sort = Sort.by(Sort.Direction.ASC, "startsAt").and(Sort.by(Sort.Direction.ASC, "id"));

        return Result.Success(PageRequest.of(pageIdx, pageSize, sort));
    }
}