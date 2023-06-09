package mo.weather.mapper;

import mo.weather.dto.WeatherDTO;
import org.junit.jupiter.api.Test;
import org.springframework.data.mapping.MappingException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OpenWeatherMapResponseMapperTest {

    private final OpenWeatherMapResponseMapper responseMapper = new OpenWeatherMapResponseMapper();

    @Test
    void testMap_WithValidResponse() {
        // Given
        String responseBody = "{\"coord\": {\"lon\": -74.0060, \"lat\": 40.7128}, \"description\": {\"main\": \"Mainly clear\"}, \"main\": {\"temp\": 20.5}}";
        String source = "OpenWeatherMap";

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
    void testMap_WithInvalidJsonResponse() {
        // Given
        String responseBody = "{{{{{{\"coord\": {\"lon\": -74.0060, \"lat\": 40.7128}, \"description\": {\"main\": \"Mainly clear\"}, \"main\": {\"temp\": \"20.5\"}}";
        String source = "OpenWeatherMap";

        // When
        MappingException exception = assertThrows(MappingException.class, () -> responseMapper.map(responseBody, source));

        // Then
        assertEquals("Could not map JSON response to WeatherDTO", exception.getMessage());
    }
}
