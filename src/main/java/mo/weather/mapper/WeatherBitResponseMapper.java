package mo.weather.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import mo.weather.dto.WeatherDTO;
import org.springframework.data.mapping.MappingException;

public class WeatherBitResponseMapper implements WebClientResponseMapper<WeatherDTO> {

    @Override
    public WeatherDTO map(String body, String source) throws MappingException
    {
        try {
            JsonNode json = new ObjectMapper().readTree(body).get("data").get(0);
            double actualLat = json.get("lat").asDouble();
            double actualLong = json.get("lon").asDouble();
            double temp = json.get("app_temp").asDouble();
            JsonNode weatherJson = json.get("description");
            String description = weatherJson.get("description").asText();

            return new WeatherDTO(actualLat, actualLong, temp, description, source);
        } catch (JsonProcessingException e) {
            throw new MappingException("Could not map JSON response to WeatherDTO", e);
        }
    }
}
