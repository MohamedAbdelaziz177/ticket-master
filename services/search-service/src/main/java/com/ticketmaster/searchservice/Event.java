package com.ticketmaster.searchservice;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.Instant;

@Document(indexName = "events")
@Getter @Setter @NoArgsConstructor @FieldDefaults(level = AccessLevel.PRIVATE)
public class Event {

    @Id
    private Long id;

    @Field(type = FieldType.Text)
    private String name;

    @Field(type = FieldType.Text)
    private String description;

    @Field(type = FieldType.Text)
    private String venueName;

    @Field(type = FieldType.Text)
    private String venueCity;

    @Field(type = FieldType.Text)
    private String organizerName;

    @Field(type = FieldType.Keyword)
    private String eventStatus;

    @Field(type = FieldType.Keyword)
    private String eventType;

    @Field(type = FieldType.Date)
    private Instant date;

    @Field(type = FieldType.Text)
    private String address;
}
