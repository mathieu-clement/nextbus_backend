package com.mathieuclement.nextbus.backend.db.repository;

import com.mathieuclement.nextbus.backend.model.Trip;
import org.springframework.data.repository.CrudRepository;

public interface TripRepository extends CrudRepository<Trip, String> {
}
