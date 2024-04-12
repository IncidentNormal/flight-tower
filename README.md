# flight-tower/Flights

## Description
Flights processes flight events and outputs their states based on given input files.

## Building the Application
To build this application, ensure you have Maven installed and run the following command from the root of the project:
```bash
mvn clean package
```

## Running the Application

After building the application, you can run it using the following command:
```bash
java -jar target/flights-1.0-SNAPSHOT.jar "<timestamp>" "<filePath>"
```
Replace <timestamp> with the desired timestamp in ISO-8601 format (e.g., 2025-01-01T00:00:00) and <filePath> with the path to the input file containing flight events.

## Input File Format
The input file should contain lines in the following format:
```
F222 747 DUBLIN LONDON Re-Fuel 2021-03-29T10:00:00 200
...
```

## Example

```bash
java -jar target/flights-1.0-SNAPSHOT.jar "2023-01-01T00:00:00" "src/test/resources/test__003.input"
```
