package com.mathieuclement.nextbus.backend.db.repository;

import com.mathieuclement.nextbus.backend.model.Connection;

import java.util.Date;
import java.util.List;

public interface ConnectionRepository {
    List<Connection> findNextBuses(String stopId, Date maxDatetime);
}
