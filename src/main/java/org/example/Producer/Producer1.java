package org.example.Producer;

import org.example.Buffer;

public class Producer1 implements Runnable {

    private final Buffer buffer;

    public Producer1(Buffer buffer) {
        this.buffer = buffer;
    }

    @Override
    public void run() {
        int odd = 1;
        try {
            while (true) {
                synchronized (buffer) {
                    while (!buffer.producer1Turn) {
                        buffer.wait();
                    }

                    buffer.list.add(odd);
                    System.out.println("Producer 1 : " + odd);
                    odd += 2;

                    buffer.producer1Turn = false;
                    buffer.producer2Turn = true;

                    buffer.notifyAll();
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}