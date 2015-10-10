package com.mathieuclement.nexbus.backend.model;

import com.mathieuclement.nexbus.backend.model.id.CalendarDateId;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import java.time.LocalDate;

@Entity
@IdClass(CalendarDateId.class)
public class CalendarDate implements Comparable<CalendarDate> {

    @Id
    private String serviceId;

    @Id
    private LocalDate date;

    /**
     * True if there is a bus / train for that service on that date
     * False otherwise
     * (exception type in the CSV file)
     */
    private boolean isActive;

    protected CalendarDate() {
    }

    public CalendarDate(String serviceId, LocalDate date, boolean isActive) {
        this.serviceId = serviceId;
        this.date = date;
        this.isActive = isActive;
    }

    @Override
    public String toString() {
        return "CalendarDate{" +
                "serviceId='" + serviceId + '\'' +
                ", date=" + date +
                ", isActive=" + isActive +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CalendarDate that = (CalendarDate) o;

        if (isActive != that.isActive) return false;
        if (serviceId != null ? !serviceId.equals(that.serviceId) : that.serviceId != null) return false;
        return !(date != null ? !date.equals(that.date) : that.date != null);

    }

    @Override
    public int hashCode() {
        int result = serviceId != null ? serviceId.hashCode() : 0;
        result = 31 * result + (date != null ? date.hashCode() : 0);
        result = 31 * result + (isActive ? 1 : 0);
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

    public boolean isActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }
}
