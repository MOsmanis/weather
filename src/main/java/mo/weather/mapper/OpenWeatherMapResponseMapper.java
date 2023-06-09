package mo.weather.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import mo.weather.dto.WeatherDTO;
import org.springframework.data.mapping.MappingException;

public class OpenWeatherMapResponseMapper implements WebClientResponseMapper<WeatherDTO> {

    @Override
    public WeatherDTO map(String body, String source) throws MappingException
    {
        try {
            JsonNode json = new ObjectMapper().readTree(body);
            JsonNode coordJson = json.get("coord");
            double actualLat = coordJson.get("lat").asDouble();
            double actualLong = coordJson.get("lon").asDouble();
            JsonNode weatherJson = json.get("description");
            String description = weatherJson.get("main").asText();
            JsonNode temperatureJson = json.get("main");
            double temp = temperatureJson.get("temp").asDouble();

            return new WeatherDTO(actualLat, actualLong, temp, description, source);
        } catch (JsonProcessingException e) {
            throw new MappingException("Could not map JSON response to WeatherDTO", e);
        }
    }
}
