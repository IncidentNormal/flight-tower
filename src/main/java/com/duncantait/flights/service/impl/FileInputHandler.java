package com.duncantait.flights.service.impl;

import com.duncantait.flights.helper.LocalDateTimeHelper;
import com.duncantait.flights.model.FlightEvent;
import com.duncantait.flights.model.FlightEventType;
import com.duncantait.flights.service.InputHandler;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

// TODO sort out AutoCloseable stuff here -- perhaps the caller needs to do a try with resources
@Slf4j
public class FileInputHandler implements InputHandler {

    @Override
    public Stream<FlightEvent> getFlightEvents(String[] args) {

        String filePath = args[1];

        // Check if file exists
        if (!Files.exists(Paths.get(filePath))) {
            throw new RuntimeException("File not found: " + filePath);
        }

        Path path = Paths.get(filePath);
        try {
            // Return a Stream<FlightEvent>
            return Files.lines(path)
                    .map(this::parseLineToEvent);
        } catch (IOException e) {
            e.printStackTrace();
            // In case of an IOException, return an empty Stream.
            // Consider better exception handling depending on your use case.
            return Stream.empty();
        }
    }

    private FlightEvent parseLineToEvent(String line) {
        // Assuming the line format is consistent with the provided example
        String[] parts = line.split(" ");

        if (parts.length == 2) {
            log.info("Cancelling flight: {}", parts[0]);
            return FlightEvent.builder()
                    .isCancel(true)
                    .planeID(parts[0])
                    .timestamp(LocalDateTimeHelper.toLocalDateTime(parts[1]))
                    .build();
        }

        // Simple error handling for malformed lines
        if (parts.length != 7) {
            throw new IllegalArgumentException("Malformed line: " + line);
        }

        String planeID = parts[0];
        String planeModel = parts[1];
        String origin = parts[2];
        String destination = parts[3];
        FlightEventType eventType = FlightEventType.fromString(parts[4]);
        String timestamp = parts[5];
        int fuelDelta = Integer.parseInt(parts[6]);

        return FlightEvent.builder()
                .planeID(planeID)
                .isCancel(false)
                .planeModel(planeModel)
                .origin(origin)
                .destination(destination)
                .eventType(eventType)
                .timestamp(LocalDateTimeHelper.toLocalDateTime(timestamp))
                .fuelDelta(fuelDelta)
                .build();

    }

}
