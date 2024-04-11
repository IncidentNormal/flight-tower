package com.duncantait.flights.factory;

import com.duncantait.flights.mapper.FlightStatusMapper;
import com.duncantait.flights.model.FlightEvent;
import com.duncantait.flights.model.FlightState;
import com.duncantait.flights.model.FlightStatus;

public class FlightStateFactory {

    public static FlightState createFlightState(FlightEvent event, FlightState state) {
        FlightStatus newStatus = FlightStatusMapper.map(event.getEventType());
        Integer newFuelLevel = state.getLastFuelLevel() + event.getFuelDelta();
        return FlightState.builder()
                .planeID(event.getPlaneID())
                .status(newStatus)
                .lastFuelLevel(newFuelLevel)
                .lastTimestamp(event.getTimestamp())
                .build();
    }
}
