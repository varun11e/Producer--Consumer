package org.example;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ProducerManager {
    private static final Map<String, LabeledProducer> producers = new ConcurrentHashMap<>();
    private static final Map<String, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(10);

    public static void startProducer(String name, String label) {
        try {
            LabeledProducer producer = new LabeledProducer(name, label);
            producer.setup();

            ScheduledFuture<?> task = scheduler.scheduleAtFixedRate(
                    producer::produceOnce, 0, 1, TimeUnit.SECONDS
            );

            producers.put(name, producer);
            scheduledTasks.put(name, task);

            log.info("Started producer schedule for: {} (label: {})", name, label);
        } catch (Exception e) {
            log.error("Failed to start producer {}: {}", name, e.getMessage());
        }
    }

    public static void stopProducer(String name) {
        ScheduledFuture<?> task = scheduledTasks.remove(name);
        LabeledProducer producer = producers.remove(name);

        if (task != null) {
            task.cancel(false);
        }
        if (producer != null) {
            producer.shutdown();
        }

        log.info("Stopped producer schedule for: {}", name);
    }
}