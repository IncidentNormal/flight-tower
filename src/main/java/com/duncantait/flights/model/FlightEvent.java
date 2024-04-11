package com.duncantait.flights.model;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder
public class FlightEvent {
    String planeID;
    LocalDateTime timestamp;
    boolean isCancel;

    String planeModel;
    String origin;
    String destination;
    FlightEventType eventType;
    Integer fuelDelta;
}
