package org.example;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@Slf4j
public class RegistrationService {

    private static final Map<String, String> activeConsumerPerLabel = new ConcurrentHashMap<>();
    private static final Map<String, ConcurrentLinkedQueue<String>> waitingConsumersPerLabel = new ConcurrentHashMap<>();

    public static boolean registerProducer(String name, String label) {
        MongoCollection<Document> producers = RegistrationMongoConfig.getProducersCollection();

        Document existing = producers.find(Filters.eq("name", name)).first();
        if (existing != null) {
            log.info("Producer name already exists: {}", name);
            return false;
        }

        Document doc = new Document("name", name)
                .append("label", label)
                .append("status", "active");
        producers.insertOne(doc);

        ProducerManager.startProducer(name, label);

        log.info("Producer registered: {} (label: {})", name, label);
        return true;
    }

    public static boolean registerConsumer(String name, String label, int quantity) {
        MongoCollection<Document> consumers = RegistrationMongoConfig.getConsumersCollection();

        Document existing = consumers.find(Filters.eq("name", name)).first();
        if (existing != null) {
            log.info("Consumer name already exists: {}", name);
            return false;
        }

        Document doc = new Document("name", name)
                .append("label", label)
                .append("quantityNeeded", quantity)
                .append("quantityConsumed", 0)
                .append("status", "active");
        consumers.insertOne(doc);

        addToTurnLine(label, name);
        ConsumerManager.startConsumer(name, label, quantity);

        log.info("Consumer registered: {} (label: {}, needs: {})", name, label, quantity);
        return true;
    }

    public static void unregisterProducer(String name) {
        MongoCollection<Document> producers = RegistrationMongoConfig.getProducersCollection();
        producers.deleteOne(Filters.eq("name", name));

        ProducerManager.stopProducer(name);

        log.info("Producer deleted: {}", name);
    }

    public static void consumerFinished(String name, String label) {
        MongoCollection<Document> consumers = RegistrationMongoConfig.getConsumersCollection();
        consumers.updateOne(Filters.eq("name", name), new Document("$set", new Document("status", "inactive")));
        log.info("Consumer marked inactive (finished): {}", name);

        activeConsumerPerLabel.remove(label);
        moveToNextConsumer(label);
    }

    public static void updateQuantityConsumed(String name, int quantityConsumed) {
        MongoCollection<Document> consumers = RegistrationMongoConfig.getConsumersCollection();
        consumers.updateOne(Filters.eq("name", name), new Document("$set", new Document("quantityConsumed", quantityConsumed)));
    }

    public static long getActiveProducerCount() {
        MongoCollection<Document> producers = RegistrationMongoConfig.getProducersCollection();
        return producers.countDocuments();
    }

    public static long getActiveConsumerCount() {
        MongoCollection<Document> consumers = RegistrationMongoConfig.getConsumersCollection();
        return consumers.countDocuments(Filters.eq("status", "active"));
    }

    private static void addToTurnLine(String label, String consumerName) {
        waitingConsumersPerLabel.computeIfAbsent(label, l -> new ConcurrentLinkedQueue<>()).add(consumerName);
        moveToNextConsumer(label);
    }

    private static void moveToNextConsumer(String label) {
        if (activeConsumerPerLabel.containsKey(label)) {
            return;
        }
        ConcurrentLinkedQueue<String> line = waitingConsumersPerLabel.get(label);
        if (line != null) {
            String next = line.poll();
            if (next != null) {
                activeConsumerPerLabel.put(label, next);
                log.info("It is now {}'s turn for label: {}", next, label);
            }
        }
    }

    public static boolean isMyTurn(String consumerName, String label) {
        return consumerName.equals(activeConsumerPerLabel.get(label));
    }
}