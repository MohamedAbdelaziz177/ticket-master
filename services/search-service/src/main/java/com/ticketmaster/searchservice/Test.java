package com.ticketmaster.searchservice;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Test {
    @GetMapping("/search/")
    public String index() {
        return "Hello World!";
    }
}
