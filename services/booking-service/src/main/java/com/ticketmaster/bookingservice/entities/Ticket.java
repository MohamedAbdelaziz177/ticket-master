package com.ticketmaster.bookingservice.entities;

import com.ticketmaster.bookingservice.enums.TicketStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity
@Table(name = "tickets")
public class Ticket extends Auditable{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String eventId;
    private String seatId;

    @Enumerated(EnumType.STRING)
    private TicketStatus ticketStatus;

    @OneToOne(mappedBy = "ticket")
    private BookingItem bookingItem;


}
