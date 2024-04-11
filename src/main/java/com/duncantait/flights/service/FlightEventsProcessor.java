package com.duncantait.flights.service;

import com.duncantait.flights.helper.LocalDateTimeHelper;
import com.duncantait.flights.model.FlightEvent;
import com.duncantait.flights.model.FlightState;
import com.duncantait.flights.service.impl.FileInputHandler;
import com.duncantait.flights.service.impl.SequentialFlightEventAggregator;
import com.duncantait.flights.service.impl.LogOutputHandler;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

public class FlightEventsProcessor {

    public static void main(String[] args) {
        if (args.length != 2) {
            throw new IllegalArgumentException("Usage: FlightEventsProcessor <timestamp> <filePath>");
        }

        LocalDateTime queryTimestamp = LocalDateTimeHelper.toLocalDateTime(args[0]);

        // Instantiate the necessary concrete classes (forgoing Spring / DI for simplicity)
        InputHandler fileInputHandler = new FileInputHandler();
        FlightEventAggregator flightEventAggregator = new SequentialFlightEventAggregator();
        OutputHandler logOutputHandler = new LogOutputHandler();

        // Run code
        Stream<FlightEvent> flightEvents = fileInputHandler.getFlightEvents(args);
        List<FlightState> states = flightEventAggregator.aggregateEvents(flightEvents, queryTimestamp);
        logOutputHandler.write(states);
    }
}
