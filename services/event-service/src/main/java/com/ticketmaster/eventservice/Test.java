package com.ticketmaster.eventservice;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Test {
    @GetMapping("/event/")
    public String index() {
        return "Hello World!";
    }
}
