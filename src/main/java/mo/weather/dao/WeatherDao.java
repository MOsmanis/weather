package mo.weather.dao;

import mo.weather.dto.LocationDTO;
import mo.weather.jpa.Weather;

public interface WeatherDao {
    Weather findInLastHour(LocationDTO requestParam);
}
