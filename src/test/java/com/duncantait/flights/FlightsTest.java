package com.duncantait.flights;

import com.duncantait.flights.model.FlightState;
import com.duncantait.flights.service.FlightEventAggregator;
import com.duncantait.flights.service.InputHandler;
import com.duncantait.flights.service.OutputHandler;
import com.duncantait.flights.service.impl.FileInputHandler;
import com.duncantait.flights.service.impl.FileOutputHandler;
import com.duncantait.flights.service.impl.SequentialFlightEventAggregator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class FlightsTest {

    @ParameterizedTest
    @CsvSource({
            "src/test/resources/test__001.input, 2025-01-01T00:00:00, src/test/resources/test__001.output", // only create events
            "src/test/resources/test__001.input, 2021-03-29T11:59:00, src/test/resources/test__001_t1.output", // 001 - query time is set to 11:59
            "src/test/resources/test__001.input, 2021-03-29T12:00:00, src/test/resources/test__001_t2.output", // 001 - query time is set to 12:00
            "src/test/resources/test__002.input, 2025-01-01T00:00:00, src/test/resources/test__002.output", // same as 001 + an amend event
            "src/test/resources/test__002.input, 2021-03-29T11:59:00, src/test/resources/test__002_t1.output", // 002 - query time is set to 11:59
            "src/test/resources/test__002.input, 2021-03-29T12:00:00, src/test/resources/test__002_t2.output", // 002 - query time is set to 12:00
            "src/test/resources/test__003.input, 2025-01-01T00:00:00, src/test/resources/test__003.output", // same as 002 + a cancel event
            "src/test/resources/test__003.input, 2021-03-29T11:59:00, src/test/resources/test__003_t1.output", // 003 - query time is set to 11:59
            "src/test/resources/test__003.input, 2021-03-29T12:00:00, src/test/resources/test__003_t2.output", // 003 - query time is set to 12:00
            "src/test/resources/test__004.input, 2025-01-01T00:00:00, src/test/resources/test__004.output", // same as 003 but duplicated for another 24 hours
            "src/test/resources/test__004.input, 2021-03-30T08:00:00, src/test/resources/test__004_t1.output", // 004 - query time is set to 08:00 on 2nd day
            "src/test/resources/test__004.input, 2021-03-30T12:00:00, src/test/resources/test__004_t2.output", // 004 - query time is set to 12:00 on 2nd day
    })
    void testFlightEventProcessing(String inputFilePath, String queryTimestampStr, String expectedOutputFilePath) throws Exception {
        LocalDateTime queryTimestamp = LocalDateTime.parse(queryTimestampStr);
        String actualOutputFilePath = getActualOutputFilePath(inputFilePath);

        InputHandler inputHandler = new FileInputHandler();
        FlightEventAggregator flightEventAggregator = new SequentialFlightEventAggregator();
        OutputHandler outputHandler = new FileOutputHandler(actualOutputFilePath); // Use `FileOutputHandler` so we can assert the results more easily

        // Run code
        AtomicReference<List<FlightState>> states = new AtomicReference<>();
        inputHandler.process(new String[] {queryTimestampStr, inputFilePath}, flightEvents -> {
            states.set(flightEventAggregator.aggregate(flightEvents, queryTimestamp));
            outputHandler.handle(states.get());
        });

        // Read output file and assert results
        List<String> expectedLines = Files.readAllLines(Path.of(expectedOutputFilePath));
        List<String> actualLines = Files.readAllLines(Path.of(actualOutputFilePath));
        assertThat(actualLines).isEqualTo(expectedLines);
    }

    private static String getActualOutputFilePath(String inputFilePath) {
        String inputFileName = Paths.get(inputFilePath).getFileName().toString();
        String tempDir = System.getProperty("java.io.tmpdir");
        Path actualOutputFilePath = Paths.get(tempDir , inputFileName + "-" + UUID.randomUUID() + ".actual");
        return actualOutputFilePath.toString();
    }

}