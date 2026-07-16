package org.example;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class RabbitMQConfig {

    private static final String QUEUE_NAME = "numbers";

    public static Connection getConnection() throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setPort(5672);
        return factory.newConnection();
    }

    public static String getQueueName() {
        return QUEUE_NAME;
    }
}