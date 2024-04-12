package com.duncantait.flights.service.impl;

import com.duncantait.flights.model.FlightState;
import com.duncantait.flights.service.OutputHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class LogOutputHandler implements OutputHandler {

    @Override
    public void handle(List<FlightState> states) {
        states.forEach(state -> log.info(toLogLine(state)));
    }

    private static String toLogLine(FlightState state) {
        return "%s %s %d".formatted(state.getPlaneID(), state.getStatus().getValue(), state.getLastFuelLevel());
    }

}
