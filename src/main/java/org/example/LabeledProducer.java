package org.example;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LabeledProducer {

    private final String name;
    private final String label;
    private Connection connection;
    private Channel channel;
    private int counter = 1;

    public LabeledProducer(String name, String label) {
        this.name = name;
        this.label = label;
    }

    public void setup() throws Exception {
        connection = RabbitMQConfig.getConnection();
        channel = connection.createChannel();
        channel.queueDeclare(label, true, false, false, null);
    }
    public void produceOnce() {
        try {
            String message = label + "-item-" + counter;
            channel.basicPublish("", label, null, message.getBytes());
            log.info("Producer {} sent: {}", name, message);
            counter++;
        } catch (Exception e) {
            log.error("Producer {} failed to send: {}", name, e.getMessage());
        }
    }

    public void shutdown() {
        try {
            if (channel != null) channel.close();
            if (connection != null) connection.close();
            log.info("Producer {} connection closed.", name);
        } catch (Exception e) {
            log.error("Error closing producer {}: {}", name, e.getMessage());
        }
    }
}