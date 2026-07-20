package org.example;

import org.example.MongoConsumer.MongoConsumer;
import org.example.Consumer.Consumer;
import org.example.Producer.Producer1;
import org.example.Producer.Producer2;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.annotation.Bean;

@SpringBootApplication(exclude = {MongoAutoConfiguration.class})
public class ProdconsApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProdconsApplication.class, args);
    }

    @Bean
    public CommandLineRunner startThreads() {
        return args -> {
            Buffer buffer = new Buffer();

            Thread p1 = new Thread(new Producer1(buffer));
            Thread p2 = new Thread(new Producer2(buffer));
            Thread c = new Thread(new Consumer(buffer));
            Thread mongo = new Thread(new MongoConsumer());

            p1.start();
            p2.start();
            c.start();
            mongo.start();
        };
    }
}