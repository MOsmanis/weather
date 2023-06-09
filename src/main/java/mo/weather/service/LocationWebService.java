package mo.weather.service;

import mo.weather.client.LocationClient;
import mo.weather.dao.JpaLocationDao;
import mo.weather.dto.LocationDTO;
import mo.weather.jpa.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public non-sealed class LocationWebService extends ReliableWebService<LocationDTO, String> {
    private final JpaLocationDao jpaLocationDao;

    @Autowired
    public LocationWebService(List<LocationClient> locationClients, JpaLocationDao jpaLocationDao)
    {
        super(locationClients);
        this.jpaLocationDao = jpaLocationDao;
    }

    @Override
    @Cacheable(cacheNames = "locationCache", cacheManager = "locationCacheManager")
    public LocationDTO get(String requestParam)
    {
        return super.get(requestParam);
    }

    @Override
    protected LocationDTO getHistoricalResponse(String ip)
    {
        Location location = jpaLocationDao.find(ip);
        return new LocationDTO(location.getLat(), location.getLon(), location.getSource());
    }

    @Override
    protected void saveResponse(LocationDTO locationDTO, String ip)
    {
        var location = new Location();
        location.setIp(ip);
        location.setLat(locationDTO.latitude());
        location.setLon(locationDTO.longitude());
        location.setSource(locationDTO.source());
        location.setTimestamp(LocalDateTime.now());
        jpaLocationDao.save(location);
    }
}
