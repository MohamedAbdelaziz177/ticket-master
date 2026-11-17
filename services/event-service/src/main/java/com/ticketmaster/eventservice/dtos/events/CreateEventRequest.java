package com.ticketmaster.eventservice.dtos.events;

import com.ticketmaster.eventservice.enums.EventStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

/**
 * DTO for creating an {@link com.ticketmaster.eventservice.entities.Event}.
 * {@code status} is optional and defaults to {@link EventStatus#UPCOMING}.
 */
public record CreateEventRequest(
        @NotBlank String name,
        String description,
        @NotNull Long venueId,
        @NotNull LocalDateTime startsAt,
        @NotNull LocalDateTime endsAt,
        EventStatus status
) {
}