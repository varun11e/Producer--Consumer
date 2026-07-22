package org.example;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.GetResponse;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class LabeledConsumer {

    private final String name;
    private final String label;
    private final int quantityNeeded;
    private int quantityConsumed = 0;

    private Connection connection;
    private Channel channel;

    public LabeledConsumer(String name, String label, int quantityNeeded) {
        this.name = name;
        this.label = label;
        this.quantityNeeded = quantityNeeded;
    }

    public void setup() throws Exception {
        connection = RabbitMQConfig.getConnection();
        channel = connection.createChannel();
        channel.queueDeclare(label, true, false, false, null);
    }

    public boolean consumeOnceIfMyTurn() {
        if (!RegistrationService.isMyTurn(name, label)) {
            return false;
        }

        try {
            GetResponse response = channel.basicGet(label, true);
            if (response == null) {
                return false;
            }

            String message = new String(response.getBody());
            quantityConsumed++;
            log.info("Consumer {} consumed: {} ({}/{})", name, message, quantityConsumed, quantityNeeded);

            RegistrationService.updateQuantityConsumed(name, quantityConsumed);

            if (quantityConsumed >= quantityNeeded) {
                shutdown();
                RegistrationService.consumerFinished(name, label);
                return true;
            }
        } catch (Exception e) {
            log.error("Consumer {} failed to consume: {}", name, e.getMessage());
        }

        return false;
    }

    public void shutdown() {
        try {
            if (channel != null) channel.close();
            if (connection != null) connection.close();
            log.info("Consumer {} connection closed.", name);
        } catch (Exception e) {
            log.error("Error closing consumer {}: {}", name, e.getMessage());
        }
    }
}