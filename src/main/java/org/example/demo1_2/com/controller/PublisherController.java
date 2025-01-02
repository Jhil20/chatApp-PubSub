package org.example.demo1_2.com.controller;

import org.example.demo1_2.com.service.PubSubPublisherService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/publish")
public class PublisherController {
    private final PubSubPublisherService pubSubPublisherService;

    public PublisherController(PubSubPublisherService pubSubPublisherService) {
        this.pubSubPublisherService = pubSubPublisherService;
    }

    @PostMapping
    public ResponseEntity<String> publishMessage(@RequestBody String message) {
        pubSubPublisherService.publishMessage(message);
        return ResponseEntity.ok("Message published successfully");
    }
}
