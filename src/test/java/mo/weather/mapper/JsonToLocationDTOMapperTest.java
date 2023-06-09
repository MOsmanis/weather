package mo.weather.mapper;

import mo.weather.dto.LocationDTO;
import org.junit.jupiter.api.Test;
import org.springframework.data.mapping.MappingException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JsonToLocationDTOMapperTest {

    @Test
    void testMap_WithValidResponse() {
        // Given
        String responseBody = "{\"latitude\": 40.7128, \"longitude\": -74.0060}";
        String source = "TestSource";
        String latitudeField = "latitude";
        String longitudeField = "longitude";

        JsonToLocationDTOMapper mapper = new JsonToLocationDTOMapper(latitudeField, longitudeField);

        // When
        LocationDTO result = mapper.map(responseBody, source);

        // Then
        assertEquals(40.7128, result.latitude());
        assertEquals(-74.0060, result.longitude());
        assertEquals(source, result.source());
    }

    @Test
    void testMap_WithInvalidJsonResponse() {
        // Given
        String responseBody = "{{{{{\"latitude\": \"40.7128\", \"longitude\": -74.0060}";
        String source = "TestSource";
        String latitudeField = "latitude";
        String longitudeField = "longitude";

        JsonToLocationDTOMapper mapper = new JsonToLocationDTOMapper(latitudeField, longitudeField);

        // When
        MappingException exception = assertThrows(MappingException.class,
            () -> mapper.map(responseBody, source));

        // Then
        assertEquals("Could not map JSON response to LocationDTO", exception.getMessage());
    }
}