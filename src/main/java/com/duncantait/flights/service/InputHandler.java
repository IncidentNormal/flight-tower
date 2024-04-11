package com.duncantait.flights.service;

import com.duncantait.flights.model.FlightEvent;

import java.util.function.Consumer;
import java.util.stream.Stream;

public interface InputHandler {

    Stream<FlightEvent> processFlightEvents(String[] arguments, Consumer<Stream<FlightEvent>> consumer);
}
