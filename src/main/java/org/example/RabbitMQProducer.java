package org.example;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

public class RabbitMQProducer {

    private static Connection connection;
    private static Channel channel;

    private static synchronized void init() throws Exception {
        if (connection == null || !connection.isOpen()) {
            connection = RabbitMQConfig.getConnection();
        }
        if (channel == null || !channel.isOpen()) {
            channel = connection.createChannel();
            channel.queueDeclare(RabbitMQConfig.getQueueName(), true, false, false, null);
        }
    }

    public static void send(String message) {
        try {
            init();
            channel.basicPublish("", RabbitMQConfig.getQueueName(), null, message.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}