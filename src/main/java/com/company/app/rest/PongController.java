package com.company.app.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PongController {
    @GetMapping("/ping")
    public String index() {
        return "pong";
    }
}
