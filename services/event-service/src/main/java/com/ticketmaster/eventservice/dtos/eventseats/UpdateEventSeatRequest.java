package com.ticketmaster.eventservice.dtos.eventseats;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * DTO for updating an event seat. Only the price is mutable here:
 * the {@link com.ticketmaster.eventservice.enums.EventSeatStatus} is owned by the booking flow.
 */
public record UpdateEventSeatRequest(
        @NotNull @DecimalMin("0.00") @Digits(integer = 8, fraction = 2) BigDecimal price
) {
}