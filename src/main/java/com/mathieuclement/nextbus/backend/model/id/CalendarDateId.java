package com.mathieuclement.nextbus.backend.model.id;

import java.io.Serializable;

public class CalendarDateId implements Serializable {
    private String serviceId;

    private String dateStr;

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CalendarDateId that = (CalendarDateId) o;

        if (serviceId != null ? !serviceId.equals(that.serviceId) : that.serviceId != null) return false;
        return !(dateStr != null ? !dateStr.equals(that.dateStr) : that.dateStr != null);

    }

    @Override
    public int hashCode() {
        int result = serviceId != null ? serviceId.hashCode() : 0;
        result = 31 * result + (dateStr != null ? dateStr.hashCode() : 0);
        return result;
    }
}
