package com.ticketmaster.searchservice.dtos;

import com.ticketmaster.searchservice.utils.Cache;
import jakarta.validation.constraints.AssertTrue;

import java.time.Instant;

public record EventsSearchCtx (
      String venueName,
      String address,
      Instant dateFrom,
      Instant dateTo,
      String eventType,
      String eventStatus,
      String searchTerm
){

    @AssertTrue(message = "dateFrom must be before or equal to dateTo")
    public boolean isDateRangeValid() {
        if (dateFrom == null || dateTo == null) {
            return true;
        }

        return !dateFrom.isAfter(dateTo);
    }

    @AssertTrue(message = "eventStatus is not supported")
    public boolean isEventStatusSupported() {
        return Cache.isSupportedEventStatus(eventStatus);
    }

    @AssertTrue(message = "eventType is not supported")
    public boolean isEventTypeSupported() {
        return Cache.isSupportedEventType(eventType);
    }
}
