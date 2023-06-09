package mo.weather.client;

import mo.weather.dto.LocationDTO;
import mo.weather.dto.WeatherDTO;
import mo.weather.mapper.WebClientResponseMapper;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.mapping.MappingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

class AuthWeatherClientTest {

    @Mock
    private WebClientResponseMapper<WeatherDTO> responseMapper;

    @Mock
    private Logger logger;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ResponseEntity<String> responseEntity;

    private AuthWeatherClient weatherClient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        weatherClient = spy(new AuthWeatherClient(responseMapper, "https://example.com/weather/%1$s/%2$s/%3$s", "asf123fadsf"));
        when(weatherClient.getLogger()).thenReturn(logger);
        when(weatherClient.getRestTemplate()).thenReturn(restTemplate);
    }

    @Test
    void testGetResponse_SuccessfulResponse() {
        // Given
        var locationDTO = new LocationDTO(10.0, -20.0, "https://example.com/location/1.1.1.1");
        var expectedResponse = new WeatherDTO(123.111, 321.111,30.5, "Clear Sky", "https://example.com/weather/10.0/-20.0/asf123fadsf");
        String jsonResponse = "jsonResponse";
        when(restTemplate.getForEntity("https://example.com/weather/10.0/-20.0/asf123fadsf", String.class)).thenReturn(responseEntity);
        when(responseEntity.getBody()).thenReturn(jsonResponse);
        when(responseEntity.getStatusCode()).thenReturn(HttpStatus.OK);
        when(responseMapper.map(jsonResponse, "example.com")).thenReturn(expectedResponse);

        // When
        WeatherDTO response = weatherClient.getResponse(locationDTO);

        // Then
        assertEquals(expectedResponse, response);
        assertEquals(0, weatherClient.getFailureCount());

        InOrder inOrder = inOrder(restTemplate, responseEntity, responseMapper);
        inOrder.verify(restTemplate).getForEntity("https://example.com/weather/10.0/-20.0/asf123fadsf", String.class);
        inOrder.verify(responseEntity).getBody();
        inOrder.verify(responseMapper).map(jsonResponse, "example.com");
        verifyNoInteractions(logger);
    }

    @Test
    void testGetResponse_UnsuccessfulMapping() {
        // Given
        var locationDTO = new LocationDTO(10.0, -20.0, "https://example.com/location/1.1.1.1");
        String jsonResponse = "jsonResponse";
        when(restTemplate.getForEntity("https://example.com/weather/10.0/-20.0/asf123fadsf", String.class)).thenReturn(responseEntity);
        when(responseEntity.getBody()).thenReturn(jsonResponse);
        when(responseEntity.getStatusCode()).thenReturn(HttpStatus.OK);
        when(responseMapper.map(jsonResponse, "example.com")).thenThrow(new MappingException(""));

        // When
        WeatherDTO response = weatherClient.getResponse(locationDTO);

        // Then
        assertNull(response);
        assertEquals(1, weatherClient.getFailureCount());
        verify(logger).error(anyString(), any(MappingException.class));
    }

    @Test
    void testGetResponse_UnsuccessfulResponse() {
        // Given
        var locationDTO = new LocationDTO(10.0, -20.0, "https://example.com/location/1.1.1.1");
        String jsonResponse = "jsonResponse";
        when(restTemplate.getForEntity("https://example.com/weather/10.0/-20.0/asf123fadsf", String.class)).thenReturn(responseEntity);
        when(responseEntity.getBody()).thenReturn(jsonResponse);
        when(responseEntity.getStatusCode()).thenReturn(HttpStatus.BAD_REQUEST);

        // When
        WeatherDTO response = weatherClient.getResponse(locationDTO);

        // Then
        assertNull(response);
        assertEquals(1, weatherClient.getFailureCount());
        verify(logger).error(anyString(), any(RestClientResponseException.class));
    }

    @Test
    void testGetResponse_ExceptionOccurred() {
        // Given
        var locationDTO = new LocationDTO(10.0, -20.0, "https://example.com/location/1.1.1.1");
        when(restTemplate.getForEntity("https://example.com/weather/10.0/-20.0/asf123fadsf", String.class))
            .thenThrow(new RestClientException("Exception occurred"));

        // When
        WeatherDTO response = weatherClient.getResponse(locationDTO);

        // Then
        assertNull(response);
        assertEquals(1, weatherClient.getFailureCount());
        verify(logger).error(anyString(), any(RestClientException.class));
    }

    @Test
    void testGetRequestUrl() {
        // When
        var locationDTO = new LocationDTO(10.0, -20.0, "https://example.com/location/1.1.1.1");
        String requestUrl = weatherClient.getRequestUrl(locationDTO);

        // Then
        assertEquals("https://example.com/weather/10.0/-20.0/asf123fadsf", requestUrl);
    }
}
