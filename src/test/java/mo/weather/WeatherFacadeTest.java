package mo.weather;

import mo.weather.dto.LocationDTO;
import mo.weather.dto.WeatherDTO;
import mo.weather.exception.PrivateIpAddressException;
import mo.weather.service.LocationWebService;
import mo.weather.service.WeatherWebService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.net.UnknownHostException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class WeatherFacadeTest {

    @Mock
    private WeatherWebService weatherWebService;

    @Mock
    private LocationWebService locationWebService;

    private WeatherFacade weatherFacade;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        weatherFacade = new WeatherFacade(weatherWebService, locationWebService);
    }

    @Test
    public void testGetWeather_ValidIP() throws UnknownHostException, PrivateIpAddressException {
        String ip = "93.177.208.153";
        LocationDTO locationDTO = new LocationDTO(37.7749, -122.4194, "example.com");
        WeatherDTO weatherDTO = new WeatherDTO(37.7749, -122.4194, 20.5, "Sunny", "weather.com");

        when(locationWebService.get(ip)).thenReturn(locationDTO);
        when(weatherWebService.get(locationDTO)).thenReturn(weatherDTO);

        WeatherDTO result = weatherFacade.getWeather(ip);

        assertEquals(weatherDTO, result);
        verify(locationWebService, times(1)).get(ip);
        verify(weatherWebService, times(1)).get(locationDTO);
    }

    @Test
    public void testGetWeather_PrivateIP() {
        String ip = "192.168.0.1";

        when(locationWebService.get(ip)).thenReturn(
            new LocationDTO(37.7749, -122.4194, "example.com"));

        PrivateIpAddressException exception = assertThrows(PrivateIpAddressException.class, () -> weatherFacade.getWeather(ip));

        assertEquals("Cannot find location for a private IP address (192.168.0.1)", exception.getMessage());
        verify(locationWebService, times(1)).get(ip);
        verify(weatherWebService, never()).get(any(LocationDTO.class));
    }
}
