package com.ticketmaster.searchservice.utils;

import java.util.Set;

public final class Cache {
    public static final Set<String> EventStatuses = Set.of(
            "UPCOMING",
            "ONGOING",
            "COMPLETED",
            "CANCELLED"
    );

    /**
     * Mirrors the canonical {@code EventType} enum owned by the event service.
     * Keep every value in sync with that enum; event types are only searchable once
     * the projection pipeline indexes the {@code eventType} field.
     */
    public static final Set<String> EventTypes = Set.of(
            "ENTERTAINMENT",
            "EDU",
            "SPORTS",
            "CONCERTS",
            "THEATRE"
    );

    public static boolean isSupportedEventStatus(String status) {
        return status == null || EventStatuses.contains(status);
    }

    public static boolean isSupportedEventType(String type) {
        return type == null || EventTypes.contains(type);
    }
}
