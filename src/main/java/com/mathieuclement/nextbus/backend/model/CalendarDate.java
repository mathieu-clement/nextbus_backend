package com.mathieuclement.nextbus.backend.model;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.mathieuclement.nextbus.backend.model.id.CalendarDateId;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

@Entity
@IdClass(CalendarDateId.class)
@Table(name = "CALENDAR_DATE")
public class CalendarDate implements Comparable<CalendarDate>, Serializable {

    private static final long serialVersionUID = 2530656976449163911L;

    @Id
    @Column(name = "SERVICE_ID")
    private String serviceId;

    @Id
    @Column(name = "LOCAL_DATE")
    private String dateStr; // ISO yyyy-MM-dd

    @Transient
    private LocalDate localDate;

    protected CalendarDate() {
    }

    public CalendarDate(String serviceId, LocalDate localDate) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(serviceId));
        Preconditions.checkArgument(localDate != null);
        this.serviceId = serviceId;
        setLocalDate(localDate);
    }

    public CalendarDate(String serviceId, String dateStr) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(serviceId));
        Preconditions.checkArgument(!Strings.isNullOrEmpty(dateStr));
        this.serviceId = serviceId;
        setDateStr(dateStr);
    }

    @Override
    public String toString() {
        return "CalendarDate{" +
                "serviceId='" + serviceId + '\'' +
                ", dateStr='" + dateStr + '\'' +
                ", localDate=" + localDate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CalendarDate that = (CalendarDate) o;

        if (serviceId != null ? !serviceId.equals(that.serviceId) : that.serviceId != null) return false;
        return !(dateStr != null ? !dateStr.equals(that.dateStr) : that.dateStr != null);

    }

    @Override
    public int hashCode() {
        int result = serviceId != null ? serviceId.hashCode() : 0;
        result = 31 * result + (dateStr != null ? dateStr.hashCode() : 0);
        return result;
    }

    @Override
    public int compareTo(CalendarDate o) {
        int compareDates = localDate.compareTo(o.localDate);
        if (compareDates != 0) return compareDates;

        return serviceId.compareTo(serviceId);
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getDateStr() {
        if (dateStr == null && localDate != null) {
            setLocalDate(localDate);
        }
        return dateStr;
    }

    public void setDateStr(String dateStr) {
        this.dateStr = dateStr;
        this.localDate = LocalDate.parse(dateStr);
    }

    public LocalDate getLocalDate() {
        if (localDate == null && dateStr != null) {
            setDateStr(dateStr);
        }
        return localDate;
    }

    public void setLocalDate(LocalDate localDate) {
        this.localDate = localDate;
        this.dateStr = localDate.toString();
    }
}
