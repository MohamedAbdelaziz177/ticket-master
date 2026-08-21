package com.ticketmaster.bookingservice;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Test {
    @GetMapping("/bookings/")
    public String index() {
        return "Hello World!";
    }
}
