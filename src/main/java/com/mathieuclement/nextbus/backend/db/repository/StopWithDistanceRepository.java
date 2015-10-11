package com.mathieuclement.nextbus.backend.db.repository;

import com.mathieuclement.nextbus.backend.model.StopWithDistance;

import java.util.List;

public interface StopWithDistanceRepository {
    List<StopWithDistance> findStopsByDistance(float latitude,
                                               float longitude,
                                               int maxDistance,
                                               int maxNbResults);
}
