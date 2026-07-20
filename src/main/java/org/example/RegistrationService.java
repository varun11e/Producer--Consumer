package org.example;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;

public class RegistrationService {

    public static boolean registerProducer(String name) {
        MongoCollection<Document> producers = RegistrationMongoConfig.getProducersCollection();

        Document existing = producers.find(Filters.eq("name", name)).first();
        if (existing != null) {
            System.out.println(name + "this producer name already exists " );
            return false;
        }

        Document doc = new Document("name", name).append("status", "active");
        producers.insertOne(doc);
        System.out.println("Producer registered: " + name);
        return true;
    }

    public static boolean registerConsumer(String name) {
        MongoCollection<Document> consumers = RegistrationMongoConfig.getConsumersCollection();

        Document existing = consumers.find(Filters.eq("name", name)).first();
        if (existing != null) {
            System.out.println("Consumer name already exists: " + name);
            return false;
        }

        Document doc = new Document("name", name).append("status", "active");
        consumers.insertOne(doc);
        System.out.println("Consumer registered: " + name);
        return true;
    }

    public static void unregisterProducer(String name) {
        MongoCollection<Document> producers = RegistrationMongoConfig.getProducersCollection();
        producers.deleteOne(Filters.eq("name", name));
        System.out.println("Producer deleted: " + name);
    }

    public static void unregisterConsumer(String name) {
        MongoCollection<Document> consumers = RegistrationMongoConfig.getConsumersCollection();
        consumers.deleteOne(Filters.eq("name", name));
        System.out.println("Consumer deleted: " + name);
    }

    public static long getActiveProducerCount() {
        MongoCollection<Document> producers = RegistrationMongoConfig.getProducersCollection();
        return producers.countDocuments(Filters.eq("status", "active"));
    }

    public static long getActiveConsumerCount() {
        MongoCollection<Document> consumers = RegistrationMongoConfig.getConsumersCollection();
        return consumers.countDocuments(Filters.eq("status", "active"));
    }
}