package com.mathieuclement.nexbus.backend.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mathieuclement.nexbus.backend.serializer.InstantToDateTimeSerializer;

import java.time.Instant;

public class Connection {

    @JsonSerialize(using = InstantToDateTimeSerializer.class)
    private Instant dateTime;
    private Agency agency;
    private Stop stop;
    private Route route;

    public Connection() {
    }

    public Connection(Instant dateTime, Agency agency, Stop stop, Route route) {
        this.dateTime = dateTime;
        this.agency = agency;
        this.stop = stop;
        this.route = route;
    }

    public Instant getDateTime() {
        return dateTime;
    }

    public Agency getAgency() {
        return agency;
    }

    public Stop getStop() {
        return stop;
    }

    public Route getRoute() {
        return route;
    }
}
