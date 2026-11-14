package com.ticketmaster.eventservice.dtos.events;

import com.ticketmaster.eventservice.entities.Event;
import com.ticketmaster.eventservice.enums.EventStatus;

import java.time.LocalDateTime;

/**
 * DTO for {@link com.ticketmaster.eventservice.entities.Event}.
 */
public record EventResponse(
        Long id,
        String name,
        String description,
        Long venueId,
        LocalDateTime startsAt,
        LocalDateTime endsAt,
        EventStatus status
) {

    public static EventResponse from(Event event) {
        return new EventResponse(
                event.getId(),
                event.getName(),
                event.getDescription(),
                event.getVenue().getId(),
                event.getStartsAt(),
                event.getEndsAt(),
                event.getStatus()
        );
    }
}