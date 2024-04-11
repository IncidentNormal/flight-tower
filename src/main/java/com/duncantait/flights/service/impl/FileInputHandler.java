package com.duncantait.flights.service.impl;

import com.duncantait.flights.helper.LocalDateTimeHelper;
import com.duncantait.flights.model.FlightEvent;
import com.duncantait.flights.model.FlightEventType;
import com.duncantait.flights.service.InputHandler;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Consumer;
import java.util.stream.Stream;

@Slf4j
public class FileInputHandler implements InputHandler {

    @Override
    public void process(String[] args, Consumer<Stream<FlightEvent>> consumer) {

        String filePath = args[1];

        // Check if file exists
        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            throw new RuntimeException("File not found: " + filePath);
        }

        // Open the stream and process it within a try-with-resources block
        try (Stream<String> lines = Files.lines(path)) {
            Stream<FlightEvent> flightEvents = lines.map(this::parseLineToEvent);
            consumer.accept(flightEvents);
        } catch (IOException e) {
            log.error("Error reading file: {}", filePath, e);
            throw new UncheckedIOException(e);
        }
    }

    private FlightEvent parseLineToEvent(String line) {
        // Assuming the line format is consistent with the provided example
        String[] parts = line.split(" ");

        if (parts.length == 2) {
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
