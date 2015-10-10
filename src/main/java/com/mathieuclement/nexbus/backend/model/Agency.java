package com.mathieuclement.nexbus.backend.model;

import org.springframework.util.Assert;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.util.TimeZone;

@Entity
public class Agency implements Comparable<Agency> {

    @Id
    private long id;

    private String name;

    private TimeZone timeZone;

    protected Agency() {}

    public Agency(long id, String name, String timeZoneName) {
        this.id = id;
        this.name = name;
        this.timeZone = TimeZone.getTimeZone(timeZoneName);
    }

    @Override
    public String toString() {
        return "Agency{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", timeZone=" + timeZone +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Agency agency = (Agency) o;

        if (id != agency.id) return false;
        if (name != null ? !name.equals(agency.name) : agency.name != null) return false;
        return !(timeZone != null ? !timeZone.equals(agency.timeZone) : agency.timeZone != null);

    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (timeZone != null ? timeZone.hashCode() : 0);
        return result;
    }

    @Override
    public int compareTo(Agency o) {
        return Long.compare(this.id, o.id);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TimeZone getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(TimeZone timeZone) {
        this.timeZone = timeZone;
    }
}
