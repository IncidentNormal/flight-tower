package com.duncantait.flights.service.impl;

import com.duncantait.flights.model.FlightState;
import com.duncantait.flights.service.OutputHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class FileOutputHandler implements OutputHandler {

    private final String outputFilePath;

    @Override
    public void handle(List<FlightState> states) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath))) {
            for (FlightState state : states) {
                String logLine = toLogLine(state);
                writer.write(logLine);
                writer.newLine();
            }
        } catch (IOException e) {
            log.error("Failed to write to file: " + outputFilePath, e);
            throw new UncheckedIOException(e);
        }
    }

    private static String toLogLine(FlightState state) {
        return "%s %s %d".formatted(state.getPlaneID(), state.getStatus().getValue(), state.getLastFuelLevel());
    }

}
