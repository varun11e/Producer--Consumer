package org.example.Consumer;

import org.example.Buffer;
import org.example.RabbitMQProducer;

public class Consumer implements Runnable {

    private final Buffer buffer;

    public Consumer(Buffer buffer) {
        this.buffer = buffer;
    }

    @Override
    public void run() {
        try {
            while (true) {
                synchronized (buffer) {
                    while (!buffer.consumerTurn || buffer.list.size() < 2) {
                        buffer.wait();
                    }

                    int odd = buffer.list.removeFirst();
                    int even = buffer.list.removeFirst();

                    System.out.println("Consumed = " + odd);
                    System.out.println("Consumed = " + even);

                    // Send consumed pair to RabbitMQ
                    RabbitMQProducer.send(odd + "," + even);

                    buffer.consumerTurn = false;
                    buffer.producer1Turn = true;

                    buffer.notifyAll();
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}