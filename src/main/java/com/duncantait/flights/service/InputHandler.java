package com.duncantait.flights.service;

import com.duncantait.flights.model.FlightEvent;

import java.util.stream.Stream;

public interface InputHandler {

    Stream<FlightEvent> getFlightEvents(String[] arguments);
}
