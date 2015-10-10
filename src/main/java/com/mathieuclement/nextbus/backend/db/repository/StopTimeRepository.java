package com.mathieuclement.nextbus.backend.db.repository;

import com.mathieuclement.nextbus.backend.model.StopTime;
import com.mathieuclement.nextbus.backend.model.id.StopTimeId;
import org.springframework.data.repository.CrudRepository;

public interface StopTimeRepository extends CrudRepository<StopTime, StopTimeId> {
}
