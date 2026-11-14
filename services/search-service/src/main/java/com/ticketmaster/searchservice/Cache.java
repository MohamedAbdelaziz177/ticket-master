package com.ticketmaster.searchservice;

import java.util.Set;

public final class Cache {
    public static final Set<String> EventStatuses = Set.of(
            "UPCOMING",
            "ONGOING",
            "COMPLETED",
            "CANCELLED"
    );

    public static final Set<String> EventTypes = Set.of(
            "SPORTS",
            "MUSIC",
            "EDU",
            "CULTURE"
    );
}
