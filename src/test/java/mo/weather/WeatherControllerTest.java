package mo.weather;

import mo.weather.dto.WeatherDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.http.HttpServletRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class WeatherControllerTest {
    @Mock
    private WeatherFacade weatherFacade;
    @Mock
    private HttpServletRequest request;

    private WeatherController weatherController;

    @BeforeEach
    public void setUp()
    {
        MockitoAnnotations.openMocks(this);
        weatherController = new WeatherController(weatherFacade);
    }

    @Test
    public void testGetWeather() throws Exception
    {
        // Given
        String ip = "127.0.0.1";
        var expectedResponse = new WeatherDTO(123.111, 321.111,30.5, "Clear Sky", "example.com/123.123/321.321/weather");
        when(request.getRemoteAddr()).thenReturn(ip);
        when(weatherFacade.getWeather(ip)).thenReturn(expectedResponse);

        // When
        WeatherDTO result = weatherController.getWeather(request);

        // Then
        assertEquals(expectedResponse, result);
        verify(weatherFacade).getWeather(ip);
        verifyNoMoreInteractions(weatherFacade);
    }

    @Test
    public void testGetWeatherForIp() throws Exception
    {
        // Given
        String ip = "127.0.0.1";
        var expectedResponse = new WeatherDTO(123.111, 321.111,30.5, "Clear Sky", "example.com/123.123/321.321/weather");
        when(weatherFacade.getWeather(ip)).thenReturn(expectedResponse);

        // When
        WeatherDTO result = weatherController.getWeatherForIp(ip);

        // Then
        assertEquals(expectedResponse, result);
        verify(weatherFacade).getWeather(ip);
        verifyNoMoreInteractions(weatherFacade);
    }
}
