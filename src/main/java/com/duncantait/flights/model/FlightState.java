
package com.duncantait.flights.model;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder
public class FlightState {
    String planeID;
    FlightStatus status;
    Integer lastFuelLevel;

    LocalDateTime lastTimestamp;
}
