package com.ticketmaster.searchservice;

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

    @AssertTrue
    public boolean isRequestValid() {
        if(dateFrom.isAfter(dateTo)) {
            return false;
        }

        return Cache.EventStatuses.contains(eventStatus) && Cache.EventTypes.contains(eventType);
    }
}
