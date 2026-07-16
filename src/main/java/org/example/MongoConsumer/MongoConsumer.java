package org.example.MongoConsumer;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import org.bson.Document;
import org.example.MongoConfig;
import org.example.RabbitMQConfig;

public class MongoConsumer implements Runnable {

    private Connection connection;
    private Channel channel;

    @Override
    public void run() {
        System.out.println("MongoConsumer started");

        try {
            connection = RabbitMQConfig.getConnection();
            channel = connection.createChannel();
            channel.queueDeclare(RabbitMQConfig.getQueueName(), true, false, false, null);

            var collection = MongoConfig.getCollection();

            channel.basicConsume(
                    RabbitMQConfig.getQueueName(),
                    true,
                    (consumerTag, delivery) -> {
                        System.out.println("Message received from RabbitMQ");

                        String message = new String(delivery.getBody());
                        System.out.println("Mongo received : " + message);

                        String[] values = message.split(",");

                        Document document = new Document()
                                .append("odd", Integer.parseInt(values[0]))
                                .append("even", Integer.parseInt(values[1]));

                        collection.insertOne(document);
                        System.out.println("Inserted into MongoDB");
                    },
                    consumerTag -> System.out.println("MongoConsumer cancelled")
            );

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}