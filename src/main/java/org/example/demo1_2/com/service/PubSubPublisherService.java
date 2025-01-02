package org.example.demo1_2.com.service;

import com.google.cloud.pubsub.v1.Publisher;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import com.google.pubsub.v1.TopicName;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class
PubSubPublisherService {
    @Value("${gcp.project-id}")
    private String projectId;
    @Value("${gcp.pubsub.topic-name}")
    private String topicId;

    public void publishMessage(String message) {
        try {
            TopicName topicName = TopicName.of(projectId, topicId);
            Publisher publisher = Publisher.newBuilder(topicName).build();

            PubsubMessage pubsubMessage = PubsubMessage.newBuilder()
                    .setData(ByteString.copyFromUtf8(message))
                    .build();

            publisher.publish(pubsubMessage).get(); // Ensure message is sent
            publisher.shutdown();
        } catch (Exception e) {
            throw new RuntimeException("Error publishing message", e);
        }
    }}
