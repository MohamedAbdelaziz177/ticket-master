package com.ticketmaster.bookingservice.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ticketmaster.bookingservice.entities.Ticket;

public interface TicketRepository extends JpaRepository<Ticket, UUID> {
    
}
