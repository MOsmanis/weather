package mo.weather.client;

import mo.weather.dto.LocationDTO;
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

class AuthLocationClientTest {

    @Mock
    private WebClientResponseMapper<LocationDTO> responseMapper;

    @Mock
    private Logger logger;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ResponseEntity<String> responseEntity;

    private AuthLocationClient authLocationClient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        authLocationClient = spy(new AuthLocationClient(responseMapper, "https://example.com/locations/%1$s/%2$s", "asf123fadsf"));
        when(authLocationClient.getLogger()).thenReturn(logger);
        when(authLocationClient.getRestTemplate()).thenReturn(restTemplate);
    }

    @Test
    void testGetResponse_SuccessfulResponse() {
        // Given
        var expectedResponse = new LocationDTO(10.0, -20.0, "https://example.com/locations/127.0.0.1/asf123fadsf");
        String jsonResponse = "jsonResponse";
        when(restTemplate.getForEntity("https://example.com/locations/127.0.0.1/asf123fadsf", String.class)).thenReturn(responseEntity);
        when(responseEntity.getBody()).thenReturn(jsonResponse);
        when(responseEntity.getStatusCode()).thenReturn(HttpStatus.OK);
        when(responseMapper.map(jsonResponse, "example.com")).thenReturn(expectedResponse);

        // When
        LocationDTO response = authLocationClient.getResponse("127.0.0.1");

        // Then
        assertEquals(expectedResponse, response);
        assertEquals(0, authLocationClient.getFailureCount());

        InOrder inOrder = inOrder(restTemplate, responseEntity, responseMapper);
        inOrder.verify(restTemplate).getForEntity("https://example.com/locations/127.0.0.1/asf123fadsf", String.class);
        inOrder.verify(responseEntity).getBody();
        inOrder.verify(responseMapper).map(jsonResponse, "example.com");
        verifyNoInteractions(logger);
    }

    @Test
    void testGetResponse_UnsuccessfulMapping() {
        // Given
        String jsonResponse = "jsonResponse";
        when(restTemplate.getForEntity("https://example.com/locations/127.0.0.1/asf123fadsf", String.class)).thenReturn(responseEntity);
        when(responseEntity.getBody()).thenReturn(jsonResponse);
        when(responseEntity.getStatusCode()).thenReturn(HttpStatus.OK);
        when(responseMapper.map(jsonResponse, "example.com")).thenThrow(new MappingException(""));

        // When
        LocationDTO response = authLocationClient.getResponse("127.0.0.1");

        // Then
        assertNull(response);
        assertEquals(1, authLocationClient.getFailureCount());
        verify(logger).error(anyString(), any(MappingException.class));
    }

    @Test
    void testGetResponse_UnsuccessfulResponse() {
        // Given
        String jsonResponse = "jsonResponse";
        when(restTemplate.getForEntity("https://example.com/locations/127.0.0.1/asf123fadsf", String.class)).thenReturn(responseEntity);
        when(responseEntity.getBody()).thenReturn(jsonResponse);
        when(responseEntity.getStatusCode()).thenReturn(HttpStatus.BAD_REQUEST);

        // When
        LocationDTO response = authLocationClient.getResponse("127.0.0.1");

        // Then
        assertNull(response);
        assertEquals(1, authLocationClient.getFailureCount());
        verify(logger).error(anyString(), any(RestClientResponseException.class));
    }

    @Test
    void testGetResponse_ExceptionOccurred() {
        // Given
        when(restTemplate.getForEntity("https://example.com/locations/127.0.0.1/asf123fadsf", String.class))
            .thenThrow(new RestClientException("Exception occurred"));

        // When
        LocationDTO response = authLocationClient.getResponse("127.0.0.1");

        // Then
        assertNull(response);
        assertEquals(1, authLocationClient.getFailureCount());
        verify(logger).error(anyString(), any(RestClientException.class));
    }

    @Test
    void testGetRequestUrl() {
        // When
        String requestUrl = authLocationClient.getRequestUrl("127.0.0.1");

        // Then
        assertEquals("https://example.com/locations/127.0.0.1/asf123fadsf", requestUrl);
    }
}
