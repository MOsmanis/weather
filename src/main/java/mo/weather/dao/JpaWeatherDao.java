package mo.weather.dao;

import mo.weather.jpa.Weather;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaWeatherDao extends JpaRepository<Weather, String>, WeatherDao {
}
