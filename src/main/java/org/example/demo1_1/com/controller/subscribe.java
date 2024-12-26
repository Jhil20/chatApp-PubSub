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

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@RestController
@RequestMapping("/subscriber")
public class subscribe {
    @Value("${gcp.project-id}")
    private String projectId;

    @Value("${gcp.pubsub.subscription-name}")
    private String subscriptionId;

    String receivedMessage="";

    @GetMapping("/subscribeMessage")
    public ResponseEntity<String> subscribeMessage() throws TimeoutException {
        ProjectSubscriptionName name = ProjectSubscriptionName.of(projectId, subscriptionId);

        MessageReceiver messageReceiver =(PubsubMessage message, AckReplyConsumer consumer)->{
            receivedMessage =message.getData().toStringUtf8();
            String messageId = message.getMessageId();
            System.out.println("message ID is : "+messageId);
            consumer.ack();
        };

        Subscriber subscriber = null;
        try {
            subscriber = Subscriber.newBuilder(name, messageReceiver).build();
            subscriber.startAsync().awaitRunning();

            subscriber.awaitTerminated(1, TimeUnit.SECONDS);
        }
        catch (TimeoutException timeoutException)
        {
            subscriber.stopAsync();
        }
        return ResponseEntity.ok(receivedMessage);
    }
}
