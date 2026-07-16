package org.example;

import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

public class MongoConfig {

    public static MongoCollection<Document> getCollection() {
        var client = MongoClients.create("mongodb://localhost:27017");
        var database = client.getDatabase("prodcons");

        System.out.println("Connected to DB: " + database.getName());
        System.out.println("Collection: numbers");

        return database.getCollection("numbers");
    }
}