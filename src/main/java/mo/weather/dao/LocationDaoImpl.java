package mo.weather.dao;

import mo.weather.jpa.Location;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

@Repository
public class LocationDaoImpl implements LocationDao {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Location find(String ip) throws NoResultException
    {
        String hql = """
            select l from Location l
            where :ip = l.ip
            order by l.timestamp desc
            """;

        TypedQuery<Location> query = entityManager.createQuery(hql, Location.class);
        query.setParameter("ip", ip);
        query.setMaxResults(1);
        return query.getSingleResult();
    }
}
