package com.mathieuclement.nextbus.backend.db.repository;

import com.mathieuclement.nextbus.backend.model.Stop;
import org.springframework.data.repository.CrudRepository;

public interface StopRepository extends CrudRepository<Stop, String> {
}
