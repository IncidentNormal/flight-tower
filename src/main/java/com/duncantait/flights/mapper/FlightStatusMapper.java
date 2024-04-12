package com.duncantait.flights.mapper;

import com.duncantait.flights.model.FlightEventType;
import com.duncantait.flights.model.FlightStatus;

public class FlightStatusMapper {

    public static FlightStatus map(FlightEventType eventType) {
        return switch (eventType) {
            case TAKEOFF -> FlightStatus.IN_FLIGHT;
            case LAND -> FlightStatus.LANDED;
            case REFUEL -> FlightStatus.AWAITING_TAKEOFF;
        };
    }

}
