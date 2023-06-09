package mo.weather.dao;

import mo.weather.jpa.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaLocationDao extends JpaRepository<Location, String>, LocationDao {
}
