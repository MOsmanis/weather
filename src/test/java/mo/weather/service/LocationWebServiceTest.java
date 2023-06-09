package mo.weather.service;

import mo.weather.client.LocationClient;
import mo.weather.dao.JpaLocationDao;
import mo.weather.dto.LocationDTO;
import mo.weather.jpa.Location;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LocationWebServiceTest {

    private final String ip = "127.0.0.1";

    private LocationWebService locationWebService;

    @Mock
    private JpaLocationDao jpaLocationDao;
    @Mock
    private LocationClient locationClient1;
    @Mock
    private LocationClient locationClient2;
    @Captor
    private ArgumentCaptor<Location> locationCaptor;

    @BeforeEach
    void setUp()
    {
        MockitoAnnotations.openMocks(this);
        ArrayList<LocationClient> locationClients = new ArrayList<>(Arrays.asList(locationClient1, locationClient2));
        locationWebService = new LocationWebService(locationClients, jpaLocationDao);
    }

    @Test
    void testGet_WithResponseFromFirstLocationClient()
    {
        // Given
        var locationDTO = new LocationDTO(10.0, -20.0, "https://example.com/location/1.1.1.1");
        when(locationClient1.getResponse(ip)).thenReturn(locationDTO);

        // When
        LocationDTO result = locationWebService.get(ip);

        // Then
        assertEquals(locationDTO, result);

        InOrder inOrder = inOrder(locationClient1, locationClient2, jpaLocationDao);
        inOrder.verify(locationClient1).getResponse(ip);
        inOrder.verify(locationClient2, times(0)).getResponse(any());
        inOrder.verify(jpaLocationDao).save(locationCaptor.capture());

        Location locationResult = locationCaptor.getValue();
        assertEquals(locationDTO.source(), locationResult.getSource());
        assertEquals(locationDTO.latitude(), locationResult.getLat());
        assertEquals(locationDTO.longitude(), locationResult.getLon());
        assertEquals(ip, locationResult.getIp());
        assertNotNull(locationResult.getTimestamp());
    }

    @Test
    void testGet_WithMultipleLocationClients()
    {
        // Given
        var locationDTO = new LocationDTO(10.0, -20.0, "https://example.com/location/1.1.1.1");
        when(locationClient1.getResponse(ip)).thenReturn(null);
        when(locationClient2.getResponse(ip)).thenReturn(locationDTO);

        // When
        LocationDTO result = locationWebService.get(ip);

        // Then
        assertEquals(locationDTO, result);

        InOrder inOrder = inOrder(locationClient1, locationClient2, jpaLocationDao);
        inOrder.verify(locationClient1).getResponse(ip);
        inOrder.verify(locationClient2).getResponse(ip);
        inOrder.verify(jpaLocationDao).save(locationCaptor.capture());

        Location locationResult = locationCaptor.getValue();
        assertEquals(locationDTO.source(), locationResult.getSource());
        assertEquals(locationDTO.latitude(), locationResult.getLat());
        assertEquals(locationDTO.longitude(), locationResult.getLon());
        assertEquals(ip, locationResult.getIp());
        assertNotNull(locationResult.getTimestamp());
    }

    @Test
    void testGet_WithUnreliableLocationClient()
    {
        // Given
        var locationDTO = new LocationDTO(10.0, -20.0, "https://example.com/location/1.1.1.1");
        when(locationClient1.getResponse(ip)).thenReturn(null);
        // Unreliable client with mocked 1 failure
        when(locationClient1.getFailureCount()).thenReturn(1);
        when(locationClient2.getResponse(ip)).thenReturn(locationDTO);

        ArrayList<LocationClient> locationClients = new ArrayList<>(Arrays.asList(locationClient1, locationClient2));
        locationWebService = new LocationWebService(locationClients, jpaLocationDao);


        // When
        LocationDTO result = locationWebService.get(ip);

        // Then
        assertEquals(locationDTO, result);
        InOrder inOrder = inOrder(locationClient1, locationClient2, jpaLocationDao);
        inOrder.verify(locationClient2).getResponse(ip);
        inOrder.verify(locationClient1, times(0)).getResponse(ip);
        inOrder.verify(jpaLocationDao).save(locationCaptor.capture());

        Location locationResult = locationCaptor.getValue();
        assertEquals(locationDTO.source(), locationResult.getSource());
        assertEquals(locationDTO.latitude(), locationResult.getLat());
        assertEquals(locationDTO.longitude(), locationResult.getLon());
        assertEquals(ip, locationResult.getIp());
        assertNotNull(locationResult.getTimestamp());
    }

    @Test
    void testGet_WithNoResponseFromLocationClients()
    {
        // Given
        var locationDTO = new LocationDTO(10.0, -20.0, "https://example.com/location/1.1.1.1");
        var location = new Location();
        location.setLat(10.0);
        location.setLon(-20.0);
        location.setIp("1.1.1.1");
        location.setSource("https://example.com/location/1.1.1.1");
        when(jpaLocationDao.find(ip)).thenReturn(location);

        // When
        LocationDTO result = locationWebService.get(ip);

        // Then
        assertEquals(locationDTO, result);

        InOrder inOrder = inOrder(locationClient1, locationClient2, jpaLocationDao);
        inOrder.verify(locationClient1).getResponse(ip);
        inOrder.verify(locationClient2).getResponse(ip);
        inOrder.verify(jpaLocationDao).find(ip);
    }

    @Test
    void testGet_WithNoResponse()
    {
        // Given
        when(jpaLocationDao.find(ip)).thenThrow(new EmptyResultDataAccessException(1));

        // When
        assertThrows(EmptyResultDataAccessException.class, () -> locationWebService.get(ip));

        // Then
        InOrder inOrder = inOrder(locationClient1, locationClient2, jpaLocationDao);
        inOrder.verify(locationClient1).getResponse(ip);
        inOrder.verify(locationClient2).getResponse(ip);
        inOrder.verify(jpaLocationDao).find(ip);
    }
}
