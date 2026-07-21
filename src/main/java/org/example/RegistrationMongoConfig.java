package org.example;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class RegistrationMongoConfig {

    private static final MongoClient client = MongoClients.create("mongodb://localhost:27017");
    private static final MongoDatabase database = client.getDatabase("prodcons");

    public static MongoCollection<Document> getProducersCollection() {
        return database.getCollection("producers");
    }

    public static MongoCollection<Document> getConsumersCollection() {
        return database.getCollection("consumers");
    }
}