package org.example.demo1_1.com.controller;

import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.cloud.pubsub.v1.Subscriber;
import com.google.pubsub.v1.ProjectSubscriptionName;
import com.google.pubsub.v1.PubsubMessage;
import org.springframework.beans.factory.annotation.Value;
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

    @GetMapping("/subscribeMessage")
    public String subscribeMessage() {
        ProjectSubscriptionName subscriptionName =
                ProjectSubscriptionName.of(projectId, subscriptionId);

        StringBuilder receiveMessageBuilder = new StringBuilder();
        // Instantiate an asynchronous message receiver.
        MessageReceiver receiver =
                (PubsubMessage message, AckReplyConsumer consumer) -> {
                    // Handle incoming message, then ack the received message.
                    System.out.println("Id: " + message.getMessageId());
                    System.out.println("Data: " + message.getData().toStringUtf8());
                    receiveMessageBuilder.append(message.getData().toStringUtf8());
                    System.out.println("stringBuilder Data: " + receiveMessageBuilder);
                    consumer.ack();
                };

        Subscriber subscriber = null;
        try {
            subscriber = Subscriber.newBuilder(subscriptionName, receiver).build();
            System.out.println("subscriber : "+ subscriber);
//             Start the subscriber.
            subscriber.startAsync().awaitRunning();

            System.out.printf("Listening for messages on %s:\n", subscriptionName.toString());
//             Allow the subscriber to run for 30s unless an unrecoverable error occurs.
            subscriber.awaitTerminated(2, TimeUnit.SECONDS);
        }
        catch (TimeoutException timeoutException) {
            // Shut down the subscriber after 30s. Stop receiving messages.
            subscriber.stopAsync();
        }
        return receiveMessageBuilder.toString();
    }
}
