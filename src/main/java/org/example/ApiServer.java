package org.example;

import org.springframework.web.bind.annotation.*;

@RestController
public class ApiServer {

    @PostMapping("/producers/register/{name}")
    public String registerProducer(@PathVariable String name) {
        boolean success = RegistrationService.registerProducer(name);
        if (success) {
            return "Producer registered: " + name;
        } else {
            return "Producer name already exists: " + name;
        }
    }

    @PostMapping("/consumers/register/{name}")
    public String registerConsumer(@PathVariable String name) {
        boolean success = RegistrationService.registerConsumer(name);
        if (success) {
            return "Consumer registered: " + name;
        } else {
            return "Consumer name already exists: " + name;
        }
    }

    @PostMapping("/producers/unregister/{name}")
    public String unregisterProducer(@PathVariable String name) {
        RegistrationService.unregisterProducer(name);
        return "Producer unregistered: " + name;
    }

    @PostMapping("/consumers/unregister/{name}")
    public String unregisterConsumer(@PathVariable String name) {
        RegistrationService.unregisterConsumer(name);
        return "Consumer unregistered: " + name;
    }

    @GetMapping("/count/producers")
    public long countProducers() {
        return RegistrationService.getActiveProducerCount();
    }

    @GetMapping("/count/consumers")
    public long countConsumers() {
        return RegistrationService.getActiveConsumerCount();
    }
}