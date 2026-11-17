package com.ticketmaster.searchservice.dtos;

import com.ticketmaster.searchservice.Event;

import java.time.Instant;

/**
 * API representation of an indexed event. Keeps the Elasticsearch document out of
 * the HTTP contract.
 */
public record EventSearchResult(
        Long id,
        String name,
        String description,
        String venueName,
        String venueCity,
        String organizerName,
        String eventStatus,
        String eventType,
        Instant date,
        String address
) {

    public static EventSearchResult from(Event event) {
        return new EventSearchResult(
                event.getId(),
                event.getName(),
                event.getDescription(),
                event.getVenueName(),
                event.getVenueCity(),
                event.getOrganizerName(),
                event.getEventStatus(),
                event.getEventType(),
                event.getDate(),
                event.getAddress()
        );
    }
}
