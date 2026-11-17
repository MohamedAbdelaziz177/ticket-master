package com.ticketmaster.searchservice;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface EventRepository extends ElasticsearchRepository<Event, Long> {
}
