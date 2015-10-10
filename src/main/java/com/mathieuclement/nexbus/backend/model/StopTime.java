package com.mathieuclement.nexbus.backend.model;

import com.mathieuclement.nexbus.backend.model.id.StopTimeId;

import javax.persistence.Entity;
import javax.persistence.IdClass;
import java.time.Instant;

@Entity
@IdClass(StopTimeId.class)
public class StopTime implements Comparable<StopTime> {
    private Trip trip;
    private Instant departureDateTime;
    private Stop stop;
    private int stopSequence;

    protected StopTime() {}

    public StopTime(Trip trip, Instant departureDateTime, Stop stop, int stopSequence) {
        this.trip = trip;
        this.departureDateTime = departureDateTime;
        this.stop = stop;
        this.stopSequence = stopSequence;
    }

    @Override
    public String toString() {
        return "StopTime{" +
                "trip=" + trip +
                ", departureDateTime=" + departureDateTime +
                ", stop=" + stop +
                ", stopSequence=" + stopSequence +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StopTime stopTime = (StopTime) o;

        if (stopSequence != stopTime.stopSequence) return false;
        if (trip != null ? !trip.equals(stopTime.trip) : stopTime.trip != null) return false;
        if (departureDateTime != null ? !departureDateTime.equals(stopTime.departureDateTime) : stopTime.departureDateTime != null)
            return false;
        return !(stop != null ? !stop.equals(stopTime.stop) : stopTime.stop != null);

    }

    @Override
    public int hashCode() {
        int result = trip != null ? trip.hashCode() : 0;
        result = 31 * result + (departureDateTime != null ? departureDateTime.hashCode() : 0);
        result = 31 * result + (stop != null ? stop.hashCode() : 0);
        result = 31 * result + stopSequence;
        return result;
    }

    @Override
    public int compareTo(StopTime o) {
        int departureDateTimeCompare = this.departureDateTime.compareTo(o.departureDateTime);
        if(departureDateTimeCompare != 0) return departureDateTimeCompare;

        int stopCompare = this.stop.compareTo(o.stop);
        if(stopCompare != 0) return stopCompare;

        return this.trip.compareTo(o.trip);
    }

    public Trip getTrip() {
        return trip;
    }

    public void setTrip(Trip trip) {
        this.trip = trip;
    }

    public Instant getDepartureDateTime() {
        return departureDateTime;
    }

    public void setDepartureDateTime(Instant departureDateTime) {
        this.departureDateTime = departureDateTime;
    }

    public Stop getStop() {
        return stop;
    }

    public void setStop(Stop stop) {
        this.stop = stop;
    }

    public int getStopSequence() {
        return stopSequence;
    }

    public void setStopSequence(int stopSequence) {
        this.stopSequence = stopSequence;
    }
}
