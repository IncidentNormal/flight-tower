package com.duncantait.flights.service.impl;

import com.duncantait.flights.factory.FlightStateFactory;
import com.duncantait.flights.model.FlightEvent;
import com.duncantait.flights.model.FlightState;
import com.duncantait.flights.model.FlightStatus;
import com.duncantait.flights.service.FlightEventAggregator;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Stream;

// TODO check the other 2 versions of the aggregator, but be aware they might be incorrect
public class SequentialFlightEventAggregator implements FlightEventAggregator {

    @Override public List<FlightState> aggregate(Stream<FlightEvent> events, LocalDateTime queryTimestamp) {

        // Partition events by PlaneID, and then by timestamp
        Map<String, TreeMap<LocalDateTime, FlightEvent>> processedEvents = new HashMap<>();

        events.forEach(event -> {
            if (event.getTimestamp().isAfter(queryTimestamp)) {
                return; // Skip events after the query timestamp
            }

            processedEvents.computeIfAbsent(event.getPlaneID(), k -> new TreeMap<>());
            TreeMap<LocalDateTime, FlightEvent> planeEvents = processedEvents.get(event.getPlaneID());
            // TreeMap is used to keep events _sorted_ by timestamp

            if (event.isCancel()) {
                // Cancel event: Remove if exists
                planeEvents.remove(event.getTimestamp());
            } else {
                // Add event, potentially overwriting ('amending') a previous event if it already exists
                planeEvents.put(event.getTimestamp(), event);
            }
        });

        return processedEvents.entrySet().stream()
                .map(entry -> {
                    String planeID = entry.getKey();
                    TreeMap<LocalDateTime, FlightEvent> planeEvents = entry.getValue();

                    return planeEvents.entrySet().stream()
                            .reduce(
                                    initialFlightState(planeID),
                                    (FlightState state, Map.Entry<LocalDateTime, FlightEvent> eventEntry) -> FlightStateFactory.createFlightState(eventEntry.getValue(), state),
                                    (agg1, agg2) -> agg2);
                })
                .toList();
    }

    private static FlightState initialFlightState(String planeID) {
        return FlightState.builder()
                .planeID(planeID)
                .lastTimestamp(LocalDateTime.MIN)
                .status(FlightStatus.UNKNOWN)
                .lastFuelLevel(0)
                .build();
    }
}