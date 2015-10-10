package com.mathieuclement.nextbus.backend.model;

import com.mathieuclement.nextbus.backend.model.converter.LocalDateConverter;
import com.mathieuclement.nextbus.backend.model.id.CalendarDateId;
import org.springframework.util.Assert;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@IdClass(CalendarDateId.class)
public class CalendarDate implements Comparable<CalendarDate> {

    @Id
    @Column(name = "SERVICE_ID")
    private String serviceId;

    @Id
    @Column(name = "LOCAL_DATE")
    @Convert(converter = LocalDateConverter.class)
    private LocalDate date;

    protected CalendarDate() {
    }

    public CalendarDate(String serviceId, LocalDate date) {
        Assert.hasLength(serviceId);
        Assert.notNull(date);
        this.serviceId = serviceId;
        this.date = date;
    }

    @Override
    public String toString() {
        return "CalendarDate{" +
                "serviceId='" + serviceId + '\'' +
                ", date=" + date +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CalendarDate that = (CalendarDate) o;

        if (serviceId != null ? !serviceId.equals(that.serviceId) : that.serviceId != null) return false;
        return !(date != null ? !date.equals(that.date) : that.date != null);

    }

    @Override
    public int hashCode() {
        int result = serviceId != null ? serviceId.hashCode() : 0;
        result = 31 * result + (date != null ? date.hashCode() : 0);
        return result;
    }

    @Override
    public int compareTo(CalendarDate o) {
        int compareDates = date.compareTo(o.date);
        if (compareDates != 0) return compareDates;

        return serviceId.compareTo(serviceId);
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
