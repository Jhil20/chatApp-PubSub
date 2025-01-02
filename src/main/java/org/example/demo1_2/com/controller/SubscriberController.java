package org.example.demo1_2.com.controller;

import org.example.demo1_2.com.service.PubSubSubscriberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/subscribe")
public class SubscriberController {
    private final PubSubSubscriberService pubSubSubscriberService;

    public SubscriberController(PubSubSubscriberService pubSubSubscriberService) {
        this.pubSubSubscriberService = pubSubSubscriberService;
    }

    @GetMapping
    public ResponseEntity<String> subscribe() {
        pubSubSubscriberService.pullMessages(null); // In case of WebSocket, pass the session
        return ResponseEntity.ok("Subscription started");
    }
}
