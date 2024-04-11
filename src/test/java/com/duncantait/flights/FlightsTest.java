package com.duncantait.flights;

import com.duncantait.flights.helper.LocalDateTimeHelper;
import com.duncantait.flights.model.FlightState;
import com.duncantait.flights.model.FlightStatus;
import com.duncantait.flights.service.FlightEventAggregator;
import com.duncantait.flights.service.InputHandler;
import com.duncantait.flights.service.OutputHandler;
import com.duncantait.flights.service.impl.FileInputHandler;
import com.duncantait.flights.service.impl.LogOutputHandler;
import com.duncantait.flights.service.impl.SequentialFlightEventAggregator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class FlightsTest {

    // TODO big e2e test (stream lots of test data to a file, then read from file)

    // Only 'create' events
    @Test
    void test001() {
        var args = new String[] {"2025-01-01T00:00:00", "src/test/resources/test__001.in"};

        InputHandler inputHandler = new FileInputHandler();
        FlightEventAggregator flightEventAggregator = new SequentialFlightEventAggregator();
        OutputHandler outputHandler = new LogOutputHandler();

        // Run code
        LocalDateTime queryTimestamp = LocalDateTimeHelper.toLocalDateTime(args[0]);
        AtomicReference<List<FlightState>> states = new AtomicReference<>();
        inputHandler.process(args, flightEvents -> {
            states.set(flightEventAggregator.aggregate(flightEvents, queryTimestamp));
            outputHandler.handle(states.get());
        });

        assertThat(states.get()).hasSize(4);
        assertThat(states.get()).usingElementComparatorIgnoringFields("lastTimestamp")
                .contains(
                        FlightState.builder().planeID("F123").status(FlightStatus.LANDED).lastFuelLevel(104).build(),
                        FlightState.builder().planeID("F222").status(FlightStatus.AWAITING_TAKEOFF).lastFuelLevel(200).build(),
                        FlightState.builder().planeID("F324").status(FlightStatus.IN_FLIGHT).lastFuelLevel(0).build(),
                        FlightState.builder().planeID("F551").status(FlightStatus.LANDED).lastFuelLevel(225).build()
                );
    }

    // Has an 'amend' event
    @Test
    void test002() {
        var args = new String[] {"2025-01-01T00:00:00", "src/test/resources/test__002.in"};

        InputHandler inputHandler = new FileInputHandler();
        FlightEventAggregator flightEventAggregator = new SequentialFlightEventAggregator();
        OutputHandler outputHandler = new LogOutputHandler();

        // Run code
        LocalDateTime queryTimestamp = LocalDateTimeHelper.toLocalDateTime(args[0]);
        AtomicReference<List<FlightState>> states = new AtomicReference<>();
        inputHandler.process(args, flightEvents -> {
            states.set(flightEventAggregator.aggregate(flightEvents, queryTimestamp));
            outputHandler.handle(states.get());
        });

        assertThat(states.get()).hasSize(4);
        assertThat(states.get()).usingElementComparatorIgnoringFields("lastTimestamp")
                .contains(
                        FlightState.builder().planeID("F123").status(FlightStatus.LANDED).lastFuelLevel(104).build(),
                        FlightState.builder().planeID("F222").status(FlightStatus.AWAITING_TAKEOFF).lastFuelLevel(200).build(),
                        FlightState.builder().planeID("F324").status(FlightStatus.IN_FLIGHT).lastFuelLevel(0).build(),
                        FlightState.builder().planeID("F551").status(FlightStatus.LANDED).lastFuelLevel(45).build()
                );
    }

    // Has an 'amend' and a 'cancel' event
    @Test
    void test003() {
        var args = new String[] {"2025-01-01T00:00:00", "src/test/resources/test__003.in"};

        InputHandler inputHandler = new FileInputHandler();
        FlightEventAggregator flightEventAggregator = new SequentialFlightEventAggregator();
        OutputHandler outputHandler = new LogOutputHandler();

        // Run code
        LocalDateTime queryTimestamp = LocalDateTimeHelper.toLocalDateTime(args[0]);
        AtomicReference<List<FlightState>> states = new AtomicReference<>();
        inputHandler.process(args, flightEvents -> {
            states.set(flightEventAggregator.aggregate(flightEvents, queryTimestamp));
            outputHandler.handle(states.get());
        });

        assertThat(states.get()).hasSize(4);
        assertThat(states.get()).usingElementComparatorIgnoringFields("lastTimestamp")
                .contains(
                        FlightState.builder().planeID("F123").status(FlightStatus.LANDED).lastFuelLevel(104).build(),
                        FlightState.builder().planeID("F222").status(FlightStatus.AWAITING_TAKEOFF).lastFuelLevel(200).build(),
                        FlightState.builder().planeID("F324").status(FlightStatus.IN_FLIGHT).lastFuelLevel(0).build(),
                        FlightState.builder().planeID("F551").status(FlightStatus.IN_FLIGHT).lastFuelLevel(345).build()
                );
    }

}