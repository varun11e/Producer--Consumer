package org.example;

import org.springframework.web.bind.annotation.*;

@RestController
public class ApiServer {

    @PostMapping("/producers/register/{name}/{label}")
    public String registerProducer(@PathVariable String name, @PathVariable String label) {
        boolean success = RegistrationService.registerProducer(name, label);
        if (success) {
            return "Producer registered: " + name + " (label: " + label + ")";
        } else {
            return "Producer name already exists: " + name;
        }
    }

    @PostMapping("/consumers/register/{name}/{label}/{quantity}")
    public String registerConsumer(@PathVariable String name, @PathVariable String label, @PathVariable int quantity) {
        boolean success = RegistrationService.registerConsumer(name, label, quantity);
        if (success) {
            return "Consumer registered: " + name + " (label: " + label + ", needs: " + quantity + ")";
        } else {
            return "Consumer name already exists: " + name;
        }
    }

    @DeleteMapping("/producers/unregister/{name}")
    public String unregisterProducer(@PathVariable String name) {
        RegistrationService.unregisterProducer(name);
        return "Producer unregistered: " + name;
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