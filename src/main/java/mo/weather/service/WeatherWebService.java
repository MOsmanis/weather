package mo.weather.service;

import mo.weather.client.WeatherClient;
import mo.weather.dao.JpaWeatherDao;
import mo.weather.dto.LocationDTO;
import mo.weather.dto.WeatherDTO;
import mo.weather.jpa.Weather;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public non-sealed class WeatherWebService extends ReliableWebService<WeatherDTO, LocationDTO> {
    private final JpaWeatherDao jpaWeatherDao;

    @Autowired
    public WeatherWebService(List<WeatherClient> weatherClients, JpaWeatherDao jpaWeatherDao)
    {
        super(weatherClients);
        this.jpaWeatherDao = jpaWeatherDao;
    }

    @Override
    @Cacheable("weatherCache")
    public WeatherDTO get(LocationDTO requestParam)
    {
        return super.get(requestParam);
    }

    @Override
    protected WeatherDTO getHistoricalResponse(LocationDTO locationDTO)
    {
        Weather weather = jpaWeatherDao.findInLastHour(locationDTO);
        return new WeatherDTO(weather.getActualLat(), weather.getActualLon(),
            weather.getTemp(), weather.getDescription(), weather.getSource());
    }

    @Override
    protected void saveResponse(WeatherDTO weatherDTO, LocationDTO locationDTO)
    {
        var weather = new Weather();

        weather.setCallerLat(locationDTO.latitude());
        weather.setCallerLon(locationDTO.longitude());

        weather.setActualLat(weatherDTO.actualLat());
        weather.setActualLon(weatherDTO.actualLon());
        weather.setTemp(weatherDTO.temp());
        weather.setDescription(weatherDTO.description());
        weather.setSource(weatherDTO.source());
        weather.setTimestamp(LocalDateTime.now());

        jpaWeatherDao.save(weather);
    }
}
