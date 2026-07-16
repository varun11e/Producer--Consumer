package org.example;

import org.example.MongoConsumer.MongoConsumer;
import org.example.Consumer.Consumer;
import org.example.Producer.Producer1;
import org.example.Producer.Producer2;

public class Prodcons {

    public static void main(String[] args) {
        Buffer buffer = new Buffer();

        Thread p1 = new Thread(new Producer1(buffer));
        Thread p2 = new Thread(new Producer2(buffer));
        Thread c = new Thread(new Consumer(buffer));
        Thread mongo = new Thread(new MongoConsumer());

        p1.start();
        p2.start();
        c.start();
        mongo.start();
    }
}