package org.example;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ConsumerManager {

    private static final Map<String, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(10);

    public static void startConsumer(String name, String label, int quantity) {
        try {
            LabeledConsumer consumer = new LabeledConsumer(name, label, quantity);
            consumer.setup();

            ScheduledFuture<?> task = scheduler.scheduleAtFixedRate(() -> {
                boolean finished = consumer.consumeOnceIfMyTurn();
                if (finished) {
                    ScheduledFuture<?> selfTask = scheduledTasks.remove(name);
                    if (selfTask != null) {
                        selfTask.cancel(false);
                    }
                }
            }, 0, 500, TimeUnit.MILLISECONDS);

            scheduledTasks.put(name, task);

            log.info("Started consumer schedule for: {} (label: {}, needs: {})", name, label, quantity);
        } catch (Exception e) {
            log.error("Failed to start consumer {}: {}", name, e.getMessage());
        }
    }
}