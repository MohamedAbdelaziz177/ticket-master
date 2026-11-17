package com.ticketmaster.bookingservice.entities;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity
@Table(name = "booking_items")
public class BookingItem {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private Integer quantity;

    private BigDecimal price;
    @ManyToOne
    @JoinColumn(referencedColumnName = "id",name = "booking_id")
    private Booking bookings;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id",referencedColumnName = "id")
    private Ticket ticket;
    
}
