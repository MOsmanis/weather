package mo.weather.service;

import mo.weather.client.WeatherClient;
import mo.weather.dao.JpaWeatherDao;
import mo.weather.dto.LocationDTO;
import mo.weather.dto.WeatherDTO;
import mo.weather.jpa.Weather;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WeatherWebServiceTest {

    private WeatherWebService weatherWebService;

    @Mock
    private JpaWeatherDao jpaWeatherDao;

    @Mock
    private WeatherClient weatherClient1;

    @Mock
    private WeatherClient weatherClient2;

    @Mock
    private LocationDTO locationDTO;

    @Captor
    private ArgumentCaptor<Weather> weatherCaptor;

    @BeforeEach
    void setUp()
    {
        MockitoAnnotations.openMocks(this);
        ArrayList<WeatherClient> weatherClients = new ArrayList<>(Arrays.asList(weatherClient1, weatherClient2));
        weatherWebService = new WeatherWebService(weatherClients, jpaWeatherDao);
    }

    @Test
    void testGet_WithResponseFromFirstWeatherClient()
    {
        // Given
        var weatherDTO = new WeatherDTO(123.111, 321.111,30.5, "Clear Sky", "example.com/123.123/321.321/weather");
        when(weatherClient1.getResponse(locationDTO)).thenReturn(weatherDTO);

        // When
        WeatherDTO result = weatherWebService.get(locationDTO);

        // Then
        assertEquals(weatherDTO, result);

        InOrder inOrder = inOrder(weatherClient1, weatherClient2, jpaWeatherDao);
        inOrder.verify(weatherClient1).getResponse(locationDTO);
        inOrder.verify(weatherClient2, times(0)).getResponse(any());
        inOrder.verify(jpaWeatherDao).save(weatherCaptor.capture());

        Weather weatherResult = weatherCaptor.getValue();
        assertEquals(weatherDTO.source(), weatherResult.getSource());
        assertEquals(weatherDTO.actualLat(), weatherResult.getActualLat());
        assertEquals(weatherDTO.actualLon(), weatherResult.getActualLon());
        assertEquals(weatherDTO.temp(), weatherResult.getTemp());
        assertEquals(weatherDTO.description(), weatherResult.getDescription());
        assertNotNull(weatherResult.getTimestamp());
    }

    @Test
    void testGet_WithMultipleWeatherClients()
    {
        // Given
        var weatherDTO = new WeatherDTO(123.111, 321.111,30.5, "Clear Sky", "example.com/123.123/321.321/weather");
        when(weatherClient1.getResponse(locationDTO)).thenReturn(null);
        when(weatherClient2.getResponse(locationDTO)).thenReturn(weatherDTO);

        // When
        WeatherDTO result = weatherWebService.get(locationDTO);

        // Then
        assertEquals(weatherDTO, result);

        InOrder inOrder = inOrder(weatherClient1, weatherClient2, jpaWeatherDao);
        inOrder.verify(weatherClient1).getResponse(locationDTO);
        inOrder.verify(weatherClient2).getResponse(locationDTO);
        inOrder.verify(jpaWeatherDao).save(weatherCaptor.capture());

        Weather weatherResult = weatherCaptor.getValue();
        assertEquals(weatherDTO.source(), weatherResult.getSource());
        assertEquals(weatherDTO.actualLat(), weatherResult.getActualLat());
        assertEquals(weatherDTO.actualLon(), weatherResult.getActualLon());
        assertEquals(weatherDTO.temp(), weatherResult.getTemp());
        assertEquals(weatherDTO.description(), weatherResult.getDescription());
        assertNotNull(weatherResult.getTimestamp());
    }

    @Test
    void testGet_WithUnreliableWeatherClient()
    {
        // Given
        var weatherDTO = new WeatherDTO(123.111, 321.111,30.5, "Clear Sky", "example.com/123.123/321.321/weather");
        when(weatherClient1.getResponse(locationDTO)).thenReturn(null);
        // Unreliable client with a failure
        when(weatherClient1.getFailureCount()).thenReturn(1);
        when(weatherClient2.getResponse(locationDTO)).thenReturn(weatherDTO);

        ArrayList<WeatherClient> weatherClients = new ArrayList<>(Arrays.asList(weatherClient1, weatherClient2));
        weatherWebService = new WeatherWebService(weatherClients, jpaWeatherDao);

        // When
        WeatherDTO result = weatherWebService.get(locationDTO);

        // Then
        assertEquals(weatherDTO, result);
        InOrder inOrder = inOrder(weatherClient1, weatherClient2, jpaWeatherDao);
        inOrder.verify(weatherClient2).getResponse(locationDTO);
        inOrder.verify(weatherClient1, times(0)).getResponse(locationDTO);
        inOrder.verify(jpaWeatherDao).save(weatherCaptor.capture());

        Weather weatherResult = weatherCaptor.getValue();
        assertEquals(weatherDTO.source(), weatherResult.getSource());
        assertEquals(weatherDTO.actualLat(), weatherResult.getActualLat());
        assertEquals(weatherDTO.actualLon(), weatherResult.getActualLon());
        assertEquals(weatherDTO.temp(), weatherResult.getTemp());
        assertEquals(weatherDTO.description(), weatherResult.getDescription());
        assertNotNull(weatherResult.getTimestamp());
    }

    @Test
    void testGet_WithNoResponseFromWeatherClients()
    {
        // Given
        var weatherDTO = new WeatherDTO(123.111, 321.111,30.5, "Clear Sky", "example.com/123.123/321.321/weather");
        var weather = new Weather();
        weather.setActualLat(123.111);
        weather.setActualLon(321.111);
        weather.setTemp(30.5);
        weather.setDescription("Clear Sky");
        weather.setSource("example.com/123.123/321.321/weather");
        when(jpaWeatherDao.findInLastHour(locationDTO)).thenReturn(weather);

        // When
        WeatherDTO result = weatherWebService.get(locationDTO);

        // Then
        assertEquals(weatherDTO, result);

        InOrder inOrder = inOrder(weatherClient1, weatherClient2, jpaWeatherDao);
        inOrder.verify(weatherClient1).getResponse(locationDTO);
        inOrder.verify(weatherClient2).getResponse(locationDTO);
        inOrder.verify(jpaWeatherDao).findInLastHour(locationDTO);
    }

    @Test
    void testGet_WithNoResponse()
    {
        // Given
        when(jpaWeatherDao.findInLastHour(locationDTO)).thenThrow(new EmptyResultDataAccessException(1));

        // When
        assertThrows(EmptyResultDataAccessException.class, () -> weatherWebService.get(locationDTO));

        // Then

        InOrder inOrder = inOrder(weatherClient1, weatherClient2, jpaWeatherDao);
        inOrder.verify(weatherClient1).getResponse(locationDTO);
        inOrder.verify(weatherClient2).getResponse(locationDTO);
        inOrder.verify(jpaWeatherDao).findInLastHour(locationDTO);
    }
}
