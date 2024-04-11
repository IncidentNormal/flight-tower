package com.duncantait.flights.mapper;

import com.duncantait.flights.model.FlightEventType;
import com.duncantait.flights.model.FlightStatus;

public class FlightStatusMapper {

    public static FlightStatus map(FlightEventType eventType) {
        switch (eventType) {
            case TAKEOFF:
                return FlightStatus.IN_FLIGHT;
            case LAND:
                return FlightStatus.LANDED;
            case REFUEL:
                return FlightStatus.AWAITING_TAKEOFF;
            default:
                throw new IllegalArgumentException("Invalid event type: " + eventType);
        }
    }

}
