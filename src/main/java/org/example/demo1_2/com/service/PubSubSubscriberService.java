package org.example.demo1_2.com.service;

import com.google.api.gax.rpc.ApiException;
import com.google.cloud.pubsub.v1.stub.SubscriberStub;
import com.google.cloud.pubsub.v1.stub.SubscriberStubSettings;
import com.google.pubsub.v1.ProjectSubscriptionName;
import com.google.pubsub.v1.PullRequest;
import com.google.pubsub.v1.PullResponse;
import com.google.pubsub.v1.ReceivedMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;

@Service
public class PubSubSubscriberService {
    @Value("${gcp.project-id}")
    private String projectId;
    @Value("${gcp.pubsub.subscription-name}")
    private String subscriptionId;

    public void pullMessages(WebSocketSession webSocketSession) {
        ProjectSubscriptionName subscriptionName = ProjectSubscriptionName.of(projectId, subscriptionId);

        try (SubscriberStub subscriberStub = SubscriberStubSettings.newBuilder().build().createStub()) {
            PullRequest pullRequest = PullRequest.newBuilder()
                    .setSubscription(subscriptionName.toString())
                    .setMaxMessages(10) // Number of messages to pull in one batch
                    .build();

            // Pull response containing the messages
            PullResponse pullResponse = subscriberStub.pullCallable().call(pullRequest);

            List<ReceivedMessage> messages = pullResponse.getReceivedMessagesList();
            for (ReceivedMessage message : messages) {
                try {
                    // Push message to WebSocket client
                    if (webSocketSession != null && webSocketSession.isOpen()) {
                        webSocketSession.sendMessage(new TextMessage(message.getMessage().getData().toStringUtf8()));
                    }

                    // Acknowledge the message to remove it from the queue
                    acknowledgeMessage(subscriberStub, subscriptionName, message.getAckId());
                } catch (Exception e) {
                    e.printStackTrace();
                    // Log the issue without acknowledging the message for retry
                }
            }
        } catch (ApiException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException("Error pulling messages from Pub/Sub", e);
        }
    }

    // Acknowledge the pulled message
    private void acknowledgeMessage(SubscriberStub subscriberStub, ProjectSubscriptionName subscriptionName, String ackId) {
        subscriberStub.acknowledgeCallable().call(
                com.google.pubsub.v1.AcknowledgeRequest.newBuilder()
                        .setSubscription(subscriptionName.toString())
                        .addAckIds(ackId)
                        .build()
        );
    }
}
