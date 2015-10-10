package com.mathieuclement.nextbus.backend.model;

import javax.persistence.*;

@Entity
public class Trip implements Comparable<Trip> {
    @Id
    private String id;

    @Column(name = "SERVICE_ID")
    private String serviceId;

    @JoinColumn(name = "ROUTE_ID")
    @ManyToOne
    private Route route;

    @Column(name = "HEAD_SIGN")
    private String headSign;

    @Column(name = "SHORT_NAME")
    private String shortName;

    protected Trip() {}

    public Trip(String id, String serviceId, Route route, String headSign, String shortName) {
        this.id = id;
        this.serviceId = serviceId;
        this.route = route;
        this.headSign = headSign;
        this.shortName = shortName;
    }

    @Override
    public String toString() {
        return "Trip{" +
                ", shortName='" + shortName + '\'' +
                ", headSign='" + headSign + '\'' +
                ", route=" + route +
                ", serviceId=" + serviceId +
                ", id=" + id +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Trip trip = (Trip) o;

        if (id != null ? !id.equals(trip.id) : trip.id != null) return false;
        if (serviceId != null ? !serviceId.equals(trip.serviceId) : trip.serviceId != null) return false;
        if (route != null ? !route.equals(trip.route) : trip.route != null) return false;
        if (headSign != null ? !headSign.equals(trip.headSign) : trip.headSign != null) return false;
        return !(shortName != null ? !shortName.equals(trip.shortName) : trip.shortName != null);

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (serviceId != null ? serviceId.hashCode() : 0);
        result = 31 * result + (route != null ? route.hashCode() : 0);
        result = 31 * result + (headSign != null ? headSign.hashCode() : 0);
        result = 31 * result + (shortName != null ? shortName.hashCode() : 0);
        return result;
    }

    @Override
    public int compareTo(Trip o) {
        return this.id.compareTo(o.id);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public String getHeadSign() {
        return headSign;
    }

    public void setHeadSign(String headSign) {
        this.headSign = headSign;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }
}
