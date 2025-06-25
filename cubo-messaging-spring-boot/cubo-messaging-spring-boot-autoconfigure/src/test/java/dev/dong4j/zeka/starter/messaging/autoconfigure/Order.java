package dev.dong4j.zeka.starter.messaging.autoconfigure;

import lombok.Data;

@Data
public class Order {
    // Getters
    private String id;
    private double amount;

    public Order(String id, double amount) {
        this.id = id;
        this.amount = amount;
    }

}
