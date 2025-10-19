package com.darTabkh.darTabkh.entity;

public enum OrderStatus {
    PENDING,        // Order created but not confirmed by cooker
    CONFIRMED,      // Order confirmed by cooker
    PREPARING,      // Order is being prepared
    READY,          // Order is ready for delivery
    DELIVERED,      // Order has been delivered
    CANCELLED,      // Order has been cancelled
    REJECTED        // Order has been rejected by cooker
}
