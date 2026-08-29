package com.ticketmaster.bookingservice.entities;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import com.ticketmaster.bookingservice.enums.BookingStatus;

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
@Entity
@SuperBuilder
@Table(name = "bookings")
public class Booking extends Auditable {
    @Id 
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String userId;

    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    private BigDecimal totalPrice;

    private String idempotencyKey;
    @OneToMany(mappedBy = "bookings",fetch = FetchType.LAZY,cascade = {CascadeType.MERGE,CascadeType.PERSIST,CascadeType.REFRESH},orphanRemoval = true)
    private List<BookingItem> bookingItems;





}
