package com.mathieuclement.nextbus.backend.model.id;

import com.mathieuclement.nextbus.backend.model.Stop;
import com.mathieuclement.nextbus.backend.model.Trip;

import java.io.Serializable;
import java.time.Instant;

public class StopTimeId implements Serializable {
    private Trip trip;
    private Stop stop;
    private Instant departureDateTime;

    public Trip getTrip() {
        return trip;
    }

    public void setTrip(Trip trip) {
        this.trip = trip;
    }

    public Stop getStop() {
        return stop;
    }

    public void setStop(Stop stop) {
        this.stop = stop;
    }

    public Instant getdepartureDateTime() {
        return departureDateTime;
    }

    public void setdepartureDateTime(Instant departureDateTime) {
        this.departureDateTime = departureDateTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StopTimeId that = (StopTimeId) o;

        if (trip != null ? !trip.equals(that.trip) : that.trip != null) return false;
        if (stop != null ? !stop.equals(that.stop) : that.stop != null) return false;
        return !(departureDateTime != null ? !departureDateTime.equals(that.departureDateTime) : that.departureDateTime != null);

    }

    @Override
    public int hashCode() {
        int result = trip != null ? trip.hashCode() : 0;
        result = 31 * result + (stop != null ? stop.hashCode() : 0);
        result = 31 * result + (departureDateTime != null ? departureDateTime.hashCode() : 0);
        return result;
    }
}
