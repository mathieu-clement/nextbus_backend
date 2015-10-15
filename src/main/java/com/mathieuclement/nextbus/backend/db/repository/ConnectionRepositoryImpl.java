package com.mathieuclement.nextbus.backend.db.repository;

import com.mathieuclement.nextbus.backend.model.Connection;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import java.util.Date;
import java.util.List;

@Repository
public class ConnectionRepositoryImpl implements ConnectionRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Connection> findNextBuses(String stopId, Date maxDatetime) {
        Query query = entityManager.createNamedStoredProcedureQuery(Connection.FIND_NEXT_BUSES);
        query.setParameter("stopId", stopId);
        query.setParameter("maxDatetime", maxDatetime, TemporalType.TIMESTAMP);
        //query.setMaxResults(maxNbResults);
        List<Connection> results = query.getResultList();
        return results;
    }

}
