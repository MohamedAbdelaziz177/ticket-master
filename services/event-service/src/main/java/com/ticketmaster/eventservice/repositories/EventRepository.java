package com.ticketmaster.eventservice.repositories;

import com.ticketmaster.eventservice.entities.Event;
import com.ticketmaster.eventservice.enums.EventStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {

    @EntityGraph(attributePaths = {"venue"})
    @Query("select e from Event e where e.id = ?1")
    Optional<Event> findByIdIncludeVenue(Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select e from Event e where e.id = ?1")
    Optional<Event> findByIdForUpdate(Long id);

    Page<Event> findByVenueId(Long venueId, Pageable pageable);

    Page<Event> findByStatus(EventStatus status, Pageable pageable);

    Page<Event> findByVenueIdAndStatus(Long venueId, EventStatus status, Pageable pageable);
}