package com.ticketmaster.eventservice.dtos.events;

import com.ticketmaster.eventservice.enums.EventStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

/**
 * DTO for replacing the mutable fields of an {@link com.ticketmaster.eventservice.entities.Event}.
 * A {@code null} status keeps the current status.
 */
public record UpdateEventRequest(
        @NotBlank String name,
        String description,
        @NotNull Long venueId,
        @NotNull LocalDateTime startsAt,
        @NotNull LocalDateTime endsAt,
        EventStatus status
) {
}