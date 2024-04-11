package com.duncantait.flights.service;

import com.duncantait.flights.model.FlightEvent;
import com.duncantait.flights.model.FlightState;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

public interface FlightEventAggregator {
    List<FlightState> aggregate(Stream<FlightEvent> events, LocalDateTime queryTimestamp);
}
