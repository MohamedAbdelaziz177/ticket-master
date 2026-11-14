package com.ticketmaster.eventservice.services;

import com.ticketmaster.eventservice.dtos.eventseats.AddEventSeatRequest;
import com.ticketmaster.eventservice.dtos.eventseats.EventSeatResponse;
import com.ticketmaster.eventservice.dtos.eventseats.UpdateEventSeatRequest;
import com.ticketmaster.eventservice.entities.Event;
import com.ticketmaster.eventservice.entities.EventSeat;
import com.ticketmaster.eventservice.entities.Seat;
import com.ticketmaster.eventservice.enums.EventSeatStatus;
import com.ticketmaster.eventservice.mappers.EventSeatMapper;
import com.ticketmaster.eventservice.repositories.EventRepository;
import com.ticketmaster.eventservice.repositories.EventSeatRepository;
import com.ticketmaster.eventservice.repositories.SeatRepository;
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

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EventSeatService {

    private static final int MAX_PAGE_SIZE = 100;

    EventRepository     eventRepository;
    EventSeatRepository eventSeatRepository;
    SeatRepository      seatRepository;
    EventValidator      eventValidator;

    @Transactional
    public Result<EventSeatResponse> addSeat(Long eventId, AddEventSeatRequest request) {
        Result<Void> priceValidation = validatePrice(request.price());
        if (!priceValidation.isSuccess()) {
            return Result.Failure(priceValidation.getError());
        }

        Event event = eventRepository.findByIdIncludeVenue(eventId).orElse(null);
        if (event == null) {
            return Result.Failure(ApiError.NotFound("No Event associated with this id"));
        }

        Seat seat = seatRepository.findByIdIncludeVenue(request.seatId()).orElse(null);
        if (seat == null) {
            return Result.Failure(ApiError.NotFound("No Seat associated with this id"));
        }

        Result<Void> venueValidation = eventValidator.validateSeatBelongsToEventVenue(event, seat);
        if (!venueValidation.isSuccess()) {
            return Result.Failure(venueValidation.getError());
        }

        if (eventSeatRepository.existsByEventIdAndSeatId(eventId, seat.getId())) {
            return Result.Failure(ApiError.Conflict("Seat is already assigned to this event"));
        }

        EventSeat eventSeat = eventSeatRepository.save(
                EventSeatMapper.toEventSeat(event, seat, request.price())
        );

        return Result.Success(EventSeatResponse.from(eventSeat));
    }

    @Transactional(readOnly = true)
    public Result<EventSeatResponse> getById(Long eventId, Long eventSeatId) {
        return eventSeatRepository.findByIdAndEventId(eventSeatId, eventId)
                .map(eventSeat -> Result.Success(EventSeatResponse.from(eventSeat)))
                .orElse(Result.Failure(ApiError.NotFound("No Event Seat associated with this id")));
    }

    @Transactional(readOnly = true)
    public Result<PageResponse<EventSeat, EventSeatResponse>> listByEvent(
            Long eventId,
            Integer pageIdx,
            Integer pageSize,
            EventSeatStatus status
    ) {
        if (!eventRepository.existsById(eventId)) {
            return Result.Failure(ApiError.NotFound("No Event associated with this id"));
        }

        Result<Pageable> pageableResult = toPageable(pageIdx, pageSize);
        if (!pageableResult.isSuccess()) {
            return Result.Failure(pageableResult.getError());
        }

        Pageable pageable = pageableResult.getData();
        Page<EventSeat> page = status == null
                ? eventSeatRepository.findByEventId(eventId, pageable)
                : eventSeatRepository.findByEventIdAndStatus(eventId, status, pageable);

        return Result.Success(PageResponse.of(
                page,
                page.getContent().stream().map(EventSeatResponse::from).toList()
        ));
    }

    @Transactional
    public Result<EventSeatResponse> update(Long eventId, Long eventSeatId, UpdateEventSeatRequest request) {
        Result<Void> priceValidation = validatePrice(request.price());
        if (!priceValidation.isSuccess()) {
            return Result.Failure(priceValidation.getError());
        }

        EventSeat eventSeat = eventSeatRepository.findByIdAndEventId(eventSeatId, eventId).orElse(null);
        if (eventSeat == null) {
            return Result.Failure(ApiError.NotFound("No Event Seat associated with this id"));
        }

        EventSeatMapper.updateEventSeat(eventSeat, request.price());
        EventSeat saved = eventSeatRepository.save(eventSeat);

        return Result.Success(EventSeatResponse.from(saved));
    }

    @Transactional
    public Result<Void> delete(Long eventId, Long eventSeatId) {
        EventSeat eventSeat = eventSeatRepository.findByIdAndEventId(eventSeatId, eventId).orElse(null);
        if (eventSeat == null) {
            return Result.Failure(ApiError.NotFound("No Event Seat associated with this id"));
        }

        if (eventSeat.getStatus() != EventSeatStatus.AVAILABLE) {
            return Result.Failure(ApiError.Conflict("Only available event seats can be deleted"));
        }

        eventSeatRepository.delete(eventSeat);

        return Result.Success(null);
    }

    private Result<Void> validatePrice(BigDecimal price) {
        if (price == null || price.signum() < 0) {
            return Result.Failure(ApiError.Validation("price must be greater than or equal to 0"));
        }

        return Result.Success(null);
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

        Sort sort = Sort.by(Sort.Direction.ASC, "id");

        return Result.Success(PageRequest.of(pageIdx, pageSize, sort));
    }
}