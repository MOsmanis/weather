package mo.weather;

import mo.weather.dto.WeatherDTO;
import mo.weather.exception.PrivateIpAddressException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.persistence.NoResultException;
import javax.servlet.http.HttpServletRequest;
import java.net.UnknownHostException;

@RestController
public class WeatherController {
    private final WeatherFacade weatherFacade;

    @Autowired
    public WeatherController(WeatherFacade weatherFacade)
    {
        this.weatherFacade = weatherFacade;
    }

    @GetMapping("/weather")
    @ResponseStatus(HttpStatus.OK)
    public WeatherDTO getWeather(HttpServletRequest request) throws Exception
    {
        String ip = request.getRemoteAddr();
        return weatherFacade.getWeather(ip);
    }

    @GetMapping("/weather/{ip}")
    @ResponseStatus(HttpStatus.OK)
    public WeatherDTO getWeatherForIp(@PathVariable(value="ip") String ip) throws Exception
    {
        return weatherFacade.getWeather(ip);
    }

    @ExceptionHandler({PrivateIpAddressException.class, UnknownHostException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleBadRequestExceptions(Exception ex) {
        return ex.getMessage();
    }

    @ExceptionHandler({NoResultException.class})
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public String handleNoResultException() {
        return HttpStatus.SERVICE_UNAVAILABLE.getReasonPhrase();
    }

    @ExceptionHandler({Exception.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleGenericException() {
        return HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase();
    }
}
