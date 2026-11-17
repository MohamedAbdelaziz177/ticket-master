package com.ticketmaster.eventservice.repositories;

import com.ticketmaster.eventservice.entities.EventSeat;
import com.ticketmaster.eventservice.enums.EventSeatStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface EventSeatRepository extends JpaRepository<EventSeat, Long> {

    @EntityGraph(attributePaths = {"seat"})
    Page<EventSeat> findByEventId(Long eventId, Pageable pageable);

    @EntityGraph(attributePaths = {"seat"})
    Page<EventSeat> findByEventIdAndStatus(Long eventId, EventSeatStatus status, Pageable pageable);

    @EntityGraph(attributePaths = {"seat"})
    Optional<EventSeat> findByIdAndEventId(Long id, Long eventId);

    boolean existsByEventId(Long eventId);

    boolean existsByEventIdAndSeatId(Long eventId, Long seatId);

    long countByEventIdAndStatus(Long eventId, EventSeatStatus status);

    @Modifying(flushAutomatically = true)
    @Query("delete from EventSeat es where es.event.id = ?1")
    int deleteAllByEventId(Long eventId);
}