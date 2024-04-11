package com.duncantait.flights.model;

import lombok.Getter;

@Getter
public enum FlightStatus {
    AWAITING_TAKEOFF("Awaiting-Takeoff"),
    IN_FLIGHT("In-Flight"),
    LANDED("Landed"),

    UNKNOWN("Unknown")
    ;

    private final String variant;


    FlightStatus(String variant) {
        this.variant = variant;
    }
}
