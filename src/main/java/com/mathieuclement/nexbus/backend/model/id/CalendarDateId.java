package com.mathieuclement.nexbus.backend.model.id;

import java.time.LocalDate;

public class CalendarDateId {
    private String serviceId;
    private LocalDate date;

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
