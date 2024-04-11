
package com.duncantait.flights.service.impl;

import com.duncantait.flights.factory.FlightStateFactory;
import com.duncantait.flights.model.FlightEvent;
import com.duncantait.flights.model.FlightState;
import com.duncantait.flights.model.FlightStatus;
import com.duncantait.flights.service.FlightEventAggregator;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ParallelFlightEventAggregator implements FlightEventAggregator {

    public List<FlightState> aggregate(Stream<FlightEvent> events, LocalDateTime queryTimestamp) {


        ConcurrentMap<String, List<FlightEvent>> groupedEvents = events
                .parallel() // Enable parallel processing
                .filter(event -> !event.getTimestamp().isAfter(queryTimestamp)) // Filter events up to the query timestamp
                .collect(Collectors.groupingByConcurrent(FlightEvent::getPlaneID, Collectors.toList()));

        // Step 2: Process each group of events for each planeID in parallel
        return groupedEvents.entrySet().parallelStream() // Enable parallel processing for each group
                .map(entry -> {
                    String planeID = entry.getKey();
                    List<FlightEvent> planeEvents = entry.getValue();

                    // Sort events by timestamp to ensure chronological order
                    planeEvents.sort(Comparator.comparing(FlightEvent::getTimestamp));

                    // Use LinkedHashMap to preserve the order of events while processing cancel/amend
                    TreeMap<LocalDateTime, FlightEvent> processedEvents = new TreeMap<>();
                    // TreeMap is used to keep events _sorted_ by timestamp

                    for (FlightEvent event : planeEvents) {
                        if (event.isCancel()) {
                            // Cancel event: Remove the corresponding event if exists
                            processedEvents.remove(event.getTimestamp());
                        } else {
                            // Add or amend event: Put replacing any existing event at the same timestamp
                            processedEvents.put(event.getTimestamp(), event);
                        }
                    }

                    // Reduce the processed events to a FlightState, ensuring sequential processing
                    return processedEvents.values().stream()
                            .reduce(FlightStateFactory.initialState(planeID),
                                    (state, event) -> FlightStateFactory.update(event, state),
                                    (agg1, agg2) -> agg2); // This combiner is effectively unused in sequential stream
                })
                .sorted(Comparator.comparing(FlightState::getPlaneID))
                .collect(Collectors.toList());

    }
}