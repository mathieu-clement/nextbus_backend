package com.mathieuclement.nextbus.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Date;

@Entity
@NamedStoredProcedureQuery(
        name = Connection.FIND_NEXT_BUSES,
        procedureName = "NEXT_BUSES",
        parameters = {
                @StoredProcedureParameter(name = "stopId", type = String.class),
                @StoredProcedureParameter(name = "maxDatetime", type = Date.class),
        },
        resultClasses = {
                Connection.class
        }
)
public class Connection implements Serializable {

    public static final String FIND_NEXT_BUSES = "Connection.FindNextBuses";

    @Id
    @Column(name = "next_departure")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonIgnore
    private Date nextDepartureDate;

    @Id
    @Column(name = "trip_short_name")
    private String tripShortName;

    @Id
    @Column(name = "trip_head_sign")
    private String tripHeadSign;

    @Column(name = "agency_timezone")
    @JsonIgnore
    private String agencyTimezoneId;

    @Transient
    private String departure;

    protected Connection() {
    }

    @Override
    public String toString() {
        return "Connection{" +
                "nextDepartureDate=" + nextDepartureDate +
                ", tripShortName='" + tripShortName + '\'' +
                ", tripHeadSign='" + tripHeadSign + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Connection that = (Connection) o;

        if (nextDepartureDate != null ? !nextDepartureDate.equals(that.nextDepartureDate) : that.nextDepartureDate != null)
            return false;
        if (tripShortName != null ? !tripShortName.equals(that.tripShortName) : that.tripShortName != null)
            return false;
        return !(tripHeadSign != null ? !tripHeadSign.equals(that.tripHeadSign) : that.tripHeadSign != null);

    }

    @Override
    public int hashCode() {
        int result = nextDepartureDate != null ? nextDepartureDate.hashCode() : 0;
        result = 31 * result + (tripShortName != null ? tripShortName.hashCode() : 0);
        result = 31 * result + (tripHeadSign != null ? tripHeadSign.hashCode() : 0);
        return result;
    }

    public Date getnextDepartureDate() {
        return nextDepartureDate;
    }

    public void setnextDepartureDate(Date nextDepartureDate) {
        this.nextDepartureDate = nextDepartureDate;
    }

    public String getTripShortName() {
        return tripShortName;
    }

    public void setTripShortName(String tripShortName) {
        this.tripShortName = tripShortName;
    }

    public String getTripHeadSign() {
        return tripHeadSign;
    }

    public void setTripHeadSign(String tripHeadSign) {
        this.tripHeadSign = tripHeadSign;
    }

    public String getAgencyTimezoneId() {
        return agencyTimezoneId;
    }

    public void setAgencyTimezoneId(String agencyTimezoneId) {
        this.agencyTimezoneId = agencyTimezoneId;
    }

    public String getDeparture() {
        ZoneId zoneId = ZoneId.of(agencyTimezoneId);
        OffsetDateTime dbDateTime = ((Timestamp) nextDepartureDate).toLocalDateTime().atOffset(ZoneOffset.UTC);
        return dbDateTime.atZoneSameInstant(zoneId).toString();
    }
}
