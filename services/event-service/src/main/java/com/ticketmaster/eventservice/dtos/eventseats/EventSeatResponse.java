package com.ticketmaster.eventservice.dtos.eventseats;

import com.ticketmaster.eventservice.entities.EventSeat;
import com.ticketmaster.eventservice.enums.EventSeatStatus;

import java.math.BigDecimal;

/**
 * DTO for {@link com.ticketmaster.eventservice.entities.EventSeat}.
 */
public record EventSeatResponse(
        Long id,
        Long eventId,
        Long seatId,
        String section,
        String rowNumber,
        String seatNumber,
        BigDecimal price,
        EventSeatStatus status
) {

    public static EventSeatResponse from(EventSeat eventSeat) {
        return new EventSeatResponse(
                eventSeat.getId(),
                eventSeat.getEvent().getId(),
                eventSeat.getSeat().getId(),
                eventSeat.getSeat().getSection(),
                eventSeat.getSeat().getRowNumber(),
                eventSeat.getSeat().getSeatNumber(),
                eventSeat.getPrice(),
                eventSeat.getStatus()
        );
    }
}