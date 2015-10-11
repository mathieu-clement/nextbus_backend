package com.mathieuclement.nextbus.backend.model;

import javax.persistence.Entity;
import javax.persistence.NamedStoredProcedureQuery;
import javax.persistence.StoredProcedureParameter;

@Entity
@NamedStoredProcedureQuery(
        name = StopWithDistance.FIND_CLOSEST_STOPS,
        procedureName = "CLOSEST_STOPS",
        parameters = {
                @StoredProcedureParameter(name = "latitude", type = Float.class),
                @StoredProcedureParameter(name = "longitude", type = Float.class),
                @StoredProcedureParameter(name = "maxDistance", type = Integer.class)
        },
        resultClasses = {
                StopWithDistance.class
        }
)
public class StopWithDistance extends Stop {

    public static final String FIND_CLOSEST_STOPS = "StopWithDistance.findClosestStops";

    private int distance;

    protected StopWithDistance() {
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }
}
