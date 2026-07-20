package org.example;

import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

public class RegistrationMongoConfig {

    public static MongoCollection<Document> getProducersCollection() {
        var client = MongoClients.create("mongodb://localhost:27017");
        var database = client.getDatabase("prodcons");
        return database.getCollection("producers");
    }

    public static MongoCollection<Document> getConsumersCollection() {
        var client = MongoClients.create("mongodb://localhost:27017");
        var database = client.getDatabase("prodcons");
        return database.getCollection("consumers");
    }
}