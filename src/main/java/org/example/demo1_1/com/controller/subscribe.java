package org.example.demo1_1.com.controller;

import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.cloud.pubsub.v1.Subscriber;
import com.google.pubsub.v1.ProjectSubscriptionName;
import com.google.pubsub.v1.PubsubMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/subscriber")
public class subscribe {

    @Value("${gcp.project-id}")
    private String projectId;

    @Value("${gcp.pubsub.subscription-name}")
    private String subscriptionId;

    @GetMapping("/subscribeMessage")
    public ResponseEntity<String> subscribeMessage() {
        ProjectSubscriptionName name = ProjectSubscriptionName.of(projectId, subscriptionId);

        CountDownLatch latch = new CountDownLatch(1);
        StringBuilder receivedMessageBuilder = new StringBuilder();

        MessageReceiver messageReceiver = (PubsubMessage message, AckReplyConsumer consumer) -> {
            String receivedMessage = message.getData().toStringUtf8();
            String messageId = message.getMessageId();

            System.out.println("Received message: " + receivedMessage);
            System.out.println("Message ID: " + messageId);

            // Append message details to the response
            receivedMessageBuilder.append("Message: ").append(receivedMessage)
                    .append(", ID: ").append(messageId);

            // Acknowledge the message
            consumer.ack();

            // Signal the latch to continue
            latch.countDown();
        };

        Subscriber subscriber = Subscriber.newBuilder(name, messageReceiver).build();

        try {
            subscriber.startAsync().awaitRunning();
            System.out.println("Subscriber started...");

            // Wait for a message or timeout
            boolean received = latch.await(30, TimeUnit.SECONDS);

            if (!received) {
                System.out.println("Timeout while waiting for a message.");
                return ResponseEntity.status(408).body("No message received within the timeout period.");
            }

            // Return the received message
            return ResponseEntity.ok("{\"message\": \"" + receivedMessageBuilder.toString() + "\"}");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return ResponseEntity.status(500).body("Interrupted while waiting for a message.");
        } finally {
            if (subscriber != null) {
                subscriber.stopAsync();
                System.out.println("Subscriber stopped.");
            }
        }
    }
}
