package com.mathieuclement.nextbus.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.util.Assert;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Stop implements Comparable<Stop> {

    @Id
    protected String id;

    @Column(name = "STOP_CODE")
    @JsonIgnore
    protected String stopCode;

    @Column(name = "STOP_NAME")
    protected String stopName;

    protected float latitude;

    protected float longitude;

    @Column(name = "PLATFORM_CODE")
    protected String platformCode; // not present in GTFS reference

    protected Stop () {}

    public Stop(String id, String stopCode, String stopName, float latitude, float longitude, String platformCode) {
        Assert.hasLength(id);
        Assert.hasLength(stopCode);
        this.id = id;
        this.stopCode = stopCode;
        this.stopName = stopName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.platformCode = platformCode;
    }

    @Override
    public String toString() {
        return "Stop{" +
                "id='" + id + '\'' +
                ", stopCode='" + stopCode + '\'' +
                ", stopName='" + stopName + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", platformCode='" + platformCode + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Stop stop = (Stop) o;

        if (Float.compare(stop.latitude, latitude) != 0) return false;
        if (Float.compare(stop.longitude, longitude) != 0) return false;
        if (id != null ? !id.equals(stop.id) : stop.id != null) return false;
        if (stopCode != null ? !stopCode.equals(stop.stopCode) : stop.stopCode != null) return false;
        if (stopName != null ? !stopName.equals(stop.stopName) : stop.stopName != null) return false;
        return !(platformCode != null ? !platformCode.equals(stop.platformCode) : stop.platformCode != null);

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (stopCode != null ? stopCode.hashCode() : 0);
        result = 31 * result + (stopName != null ? stopName.hashCode() : 0);
        result = 31 * result + (latitude != +0.0f ? Float.floatToIntBits(latitude) : 0);
        result = 31 * result + (longitude != +0.0f ? Float.floatToIntBits(longitude) : 0);
        result = 31 * result + (platformCode != null ? platformCode.hashCode() : 0);
        return result;
    }

    @Override
    public int compareTo(Stop o) {
        return this.id.compareTo(o.id);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStopCode() {
        return stopCode;
    }

    public void setStopCode(String stopCode) {
        this.stopCode = stopCode;
    }

    public String getStopName() {
        return stopName;
    }

    public void setStopName(String stopName) {
        this.stopName = stopName;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public String getPlatformCode() {
        return platformCode;
    }

    public void setPlatformCode(String platformCode) {
        this.platformCode = platformCode;
    }
}
