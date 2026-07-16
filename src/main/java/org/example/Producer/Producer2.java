package org.example.Producer;

import org.example.Buffer;

public class Producer2 implements Runnable {

    private final Buffer buffer;

    public Producer2(Buffer buffer) {
        this.buffer = buffer;
    }

    @Override
    public void run() {
        int even = 2;
        try {
            while (true) {
                synchronized (buffer) {
                    while (!buffer.producer2Turn) {
                        buffer.wait();
                    }

                    buffer.list.add(even);
                    System.out.println("Producer 2 : " + even);
                    even += 2;

                    buffer.producer2Turn = false;
                    buffer.consumerTurn = true;

                    buffer.notifyAll();
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}