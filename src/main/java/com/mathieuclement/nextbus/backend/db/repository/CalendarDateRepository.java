package com.mathieuclement.nextbus.backend.db.repository;

import com.mathieuclement.nextbus.backend.model.CalendarDate;
import com.mathieuclement.nextbus.backend.model.id.CalendarDateId;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.Collection;

public interface CalendarDateRepository extends CrudRepository<CalendarDate, CalendarDateId> {

    CalendarDate findByDateAndServiceId(LocalDate date, String serviceId);

    Collection<CalendarDate> findByServiceId(String serviceId);
}
