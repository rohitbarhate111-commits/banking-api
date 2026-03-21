package com.rohit.banking.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class HelloController {

    @GetMapping("/hello")
    public String hello() {
        return "Banking API is running 🚀";
    }

    @GetMapping("/health")
    public String health() {
        return "Banking API is healthy and operational";
    }
}
