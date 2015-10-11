package com.mathieuclement.nextbus.backend.db.repository;

import com.mathieuclement.nextbus.backend.model.StopWithDistance;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Repository
public class StopWithDistanceRepositoryImpl implements StopWithDistanceRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<StopWithDistance> findStopsByDistance(float latitude,
                                                      float longitude,
                                                      int maxDistance,
                                                      int maxNbResults) {
        Query query = entityManager.createNamedStoredProcedureQuery(StopWithDistance.FIND_CLOSEST_STOPS);
        query.setParameter("latitude", latitude);
        query.setParameter("longitude", longitude);
        query.setParameter("maxDistance", maxDistance);
        query.setMaxResults(maxNbResults);
        List<StopWithDistance> results = query.getResultList();
        return results;
    }
}
