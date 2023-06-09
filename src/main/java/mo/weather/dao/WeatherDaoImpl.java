package mo.weather.dao;

import mo.weather.dto.LocationDTO;
import mo.weather.jpa.Weather;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;

@Repository
public class WeatherDaoImpl implements WeatherDao {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Weather findInLastHour(LocationDTO locationDTO) throws NoResultException
    {
        String hql = """
            select w from Weather w
            where :hourBefore <= w.timestamp
            and ((:lat = w.callerLat and :lon = w.callerLon) or (:lat = w.actualLat and :lon = w.actualLon))
            order by w.timestamp desc
            """;

        TypedQuery<Weather> query = entityManager.createQuery(hql, Weather.class);
        query.setParameter("lat", locationDTO.latitude());
        query.setParameter("lon", locationDTO.longitude());
        query.setParameter("hourBefore", LocalDateTime.now().minusHours(1));
        query.setMaxResults(1);

        return query.getSingleResult();
    }
}
