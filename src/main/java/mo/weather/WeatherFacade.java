package mo.weather;

import mo.weather.dto.WeatherDTO;
import mo.weather.exception.PrivateIpAddressException;
import mo.weather.service.LocationWebService;
import mo.weather.service.WeatherWebService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Component
public final class WeatherFacade {
    private final WeatherWebService weatherWebService;
    private final LocationWebService locationWebService;

    @Autowired
    public WeatherFacade(WeatherWebService weatherWebService, LocationWebService locationWebService)
    {
        this.weatherWebService = weatherWebService;
        this.locationWebService = locationWebService;
    }

    public WeatherDTO getWeather(String ip) throws UnknownHostException, PrivateIpAddressException
    {
        var locationDTO = locationWebService.get(ip);
        validate(ip);
        return weatherWebService.get(locationDTO);
    }

    private void validate(String ip) throws UnknownHostException, PrivateIpAddressException
    {
        InetAddress inetAddress = InetAddress.getByName(ip);
        if(inetAddress.isSiteLocalAddress()) {
            throw new PrivateIpAddressException(ip);
        }
    }
}
