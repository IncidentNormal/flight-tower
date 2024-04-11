package com.duncantait.flights.model;

import java.util.Arrays;

public enum FlightEventType {
    TAKEOFF,
    LANDING,
    REFUEL,
    ;

    public static FlightEventType fromString(String eventType) {
        return Arrays.stream(values())
                .filter(value -> value.name().equalsIgnoreCase(eventType))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("Invalid event type: " + eventType));
    }
}
