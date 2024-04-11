package com.duncantait.flights.factory;

import com.duncantait.flights.mapper.FlightStatusMapper;
import com.duncantait.flights.model.FlightEvent;
import com.duncantait.flights.model.FlightState;
import com.duncantait.flights.model.FlightStatus;

import java.time.LocalDateTime;

public class FlightStateFactory {

    public static FlightState update(FlightEvent event, FlightState previousState) {
        FlightStatus newStatus = FlightStatusMapper.map(event.getEventType());
        Integer newFuelLevel = previousState.getLastFuelLevel() + event.getFuelDelta();
        return FlightState.builder()
                .planeID(event.getPlaneID())
                .status(newStatus)
                .lastFuelLevel(newFuelLevel)
                .lastTimestamp(event.getTimestamp())
                .build();
    }

    public static FlightState initialState(String planeID) {
        return FlightState.builder()
                .planeID(planeID)
                .lastTimestamp(LocalDateTime.MIN)
                .status(FlightStatus.UNKNOWN)
                .lastFuelLevel(0)
                .build();
    }
}
