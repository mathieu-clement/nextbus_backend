package com.mathieuclement.nextbus.backend.model;

import javax.persistence.*;

@Entity
public class Route implements Comparable<Route> {

    @Id
    private String id;

    @JoinColumn(name = "PK_AGENCY_ID")
    @ManyToOne
    private Agency agency;

    @Column(name = "SHORT_NAME")
    private String shortName;

    @Column(name = "LONG_NAME")
    private String longName;

    @Column(name = "ROUTE_TYPE")
    private RouteType type;

    protected Route() {}

    public Route(String id, Agency agency, String shortName, String longName, RouteType type) {
        this.id = id;
        this.agency = agency;
        this.shortName = shortName;
        this.longName = longName;
        this.type = type;
    }

    @Override
    public String toString() {
        return "Route{" +
                "id='" + id + '\'' +
                ", agency=" + agency +
                ", shortName='" + shortName + '\'' +
                ", longName='" + longName + '\'' +
                ", type=" + type +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Route route = (Route) o;

        if (id != null ? !id.equals(route.id) : route.id != null) return false;
        if (agency != null ? !agency.equals(route.agency) : route.agency != null) return false;
        if (shortName != null ? !shortName.equals(route.shortName) : route.shortName != null) return false;
        if (longName != null ? !longName.equals(route.longName) : route.longName != null) return false;
        return type == route.type;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (agency != null ? agency.hashCode() : 0);
        result = 31 * result + (shortName != null ? shortName.hashCode() : 0);
        result = 31 * result + (longName != null ? longName.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }

    @Override
    public int compareTo(Route o) {
        return this.id.compareTo(o.id);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Agency getAgency() {
        return agency;
    }

    public void setAgency(Agency agency) {
        this.agency = agency;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getLongName() {
        return longName;
    }

    public void setLongName(String longName) {
        this.longName = longName;
    }

    public RouteType getType() {
        return type;
    }

    public void setType(RouteType type) {
        this.type = type;
    }
}
