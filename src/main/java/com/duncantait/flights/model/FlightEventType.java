package com.duncantait.flights.model;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum FlightEventType {

    TAKEOFF("Take-Off"),
    LAND("Land"),
    REFUEL("Re-Fuel"),
    ;

    private final String variant;

    FlightEventType(String s) {
        variant = s;
    }

    public static FlightEventType fromString(String eventType) {
        return Arrays.stream(values())
                .filter(value -> value.name().equalsIgnoreCase(eventType) || value.getVariant().equalsIgnoreCase(eventType))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("Invalid event type: " + eventType));
    }
}
