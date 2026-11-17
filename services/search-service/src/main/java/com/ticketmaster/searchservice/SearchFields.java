package com.ticketmaster.searchservice;

/**
 * Names of the {@code events} index fields queried by {@link EsQueryBuilder}.
 * They must stay aligned with the mapping declared on {@link Event}.
 */
public final class SearchFields {

    public static final String INDEX = "events";

    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String DESCRIPTION = "description";
    public static final String VENUE_NAME = "venueName";
    public static final String VENUE_CITY = "venueCity";
    public static final String ORGANIZER_NAME = "organizerName";
    public static final String ADDRESS = "address";
    public static final String EVENT_STATUS = "eventStatus";
    public static final String EVENT_TYPE = "eventType";
    public static final String DATE = "date";

    private SearchFields() {
    }
}
