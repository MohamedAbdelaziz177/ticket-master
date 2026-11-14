package com.ticketmaster.eventservice.dtos.eventseats;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * DTO for adding a venue seat to an event as priced inventory.
 */
public record AddEventSeatRequest(
        @NotNull Long seatId,
        @NotNull @DecimalMin("0.00") @Digits(integer = 8, fraction = 2) BigDecimal price
) {
}