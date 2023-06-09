package mo.weather.mapper;

import mo.weather.dto.WeatherDTO;
import org.junit.jupiter.api.Test;
import org.springframework.data.mapping.MappingException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OpenMeteoResponseMapperTest {

    private final OpenMeteoResponseMapper responseMapper = new OpenMeteoResponseMapper();

    @Test
    void testMap_WithValidResponse() {
        // Given
        String responseBody = "{\"latitude\": 40.7128, \"longitude\": -74.0060, \"current_weather\": {\"temperature\": 20.5, \"weathercode\": 1}}";
        String source = "OpenMeteo";

        // When
        WeatherDTO result = responseMapper.map(responseBody, source);

        // Then
        assertEquals(40.7128, result.actualLat());
        assertEquals(-74.0060, result.actualLon());
        assertEquals(20.5, result.temp());
        assertEquals("Mainly clear", result.description());
        assertEquals(source, result.source());
    }

    @Test
    void testMap_WithUnknownWeatherCode() {
        // Given
        String responseBody = "{\"latitude\": 40.7128, \"longitude\": -74.0060, \"current_weather\": {\"temperature\": 20.5, \"weathercode\": 100}}";
        String source = "OpenMeteo";

        // When
        MappingException exception = assertThrows(MappingException.class,
                () -> responseMapper.map(responseBody, source));

        // Then
        assertEquals("Unknown OpenMeteo WMO description code '100'", exception.getMessage());
    }

    @Test
    void testMap_WithInvalidJsonResponse() {
        // Given
        String responseBody = "{{{{{{\"latitude\": 40.7128, \"longitude\": -74.0060, \"current_weather\": {\"temperature\": \"20.5\", \"weathercode\": 1}}";
        String source = "OpenMeteo";

        // When
        MappingException exception = assertThrows(MappingException.class,
                () -> responseMapper.map(responseBody, source));

        // Then
        assertEquals("Could not map JSON response to WeatherDTO", exception.getMessage());
    }
}
