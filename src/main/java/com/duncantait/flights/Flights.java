package com.duncantait.flights;

import com.duncantait.flights.helper.LocalDateTimeHelper;
import com.duncantait.flights.model.FlightState;
import com.duncantait.flights.service.FlightEventAggregator;
import com.duncantait.flights.service.InputHandler;
import com.duncantait.flights.service.OutputHandler;
import com.duncantait.flights.service.impl.FileInputHandler;
import com.duncantait.flights.service.impl.LogOutputHandler;
import com.duncantait.flights.service.impl.SequentialFlightEventAggregator;

import java.time.LocalDateTime;
import java.util.List;

public class Flights {

    public static void main(String[] args) {
        if (args.length != 2) {
            throw new IllegalArgumentException("Usage: Flights <timestamp> <filePath>");
        }

        LocalDateTime queryTimestamp = LocalDateTimeHelper.toLocalDateTime(args[0]);

        // Instantiate the necessary concrete classes (forgoing Spring / DI for simplicity)
        InputHandler fileInputHandler = new FileInputHandler();
        FlightEventAggregator flightEventAggregator = new SequentialFlightEventAggregator();
        OutputHandler logOutputHandler = new LogOutputHandler();

        // Run code
        fileInputHandler.processFlightEvents(args, flightEvents -> {
            List<FlightState> states = flightEventAggregator.aggregateEvents(flightEvents, queryTimestamp);
            logOutputHandler.write(states);
        });
    }
}
