package com.mathieuclement.nextbus.backend.db.repository;

import com.mathieuclement.nextbus.backend.model.CalendarDate;
import com.mathieuclement.nextbus.backend.model.id.CalendarDateId;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;

public interface CalendarDateRepository extends CrudRepository<CalendarDate, CalendarDateId> {
    Collection<CalendarDate> findByServiceId(String serviceId);
}
