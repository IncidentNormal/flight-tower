package com.duncantait.flights.service;

import com.duncantait.flights.model.FlightState;

import java.util.List;

public interface OutputHandler {

    void write(List<FlightState> states);
}
