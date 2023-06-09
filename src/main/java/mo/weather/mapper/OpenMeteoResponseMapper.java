package mo.weather.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import mo.weather.dto.WeatherDTO;
import org.springframework.data.mapping.MappingException;

import java.util.HashMap;
import java.util.Map;

/**
 * https://open-meteo.com/en/docs
 */
public class OpenMeteoResponseMapper implements WebClientResponseMapper<WeatherDTO> {
    private static final Map<Integer, String> WEATHER_CODE_TO_DESCRIPTION_MAP = createCodeToDescriptionMap();
    private static Map<Integer, String> createCodeToDescriptionMap() {
        Map<Integer, String> map = new HashMap<>();
        map.put(0, "Clear sky");
        map.put(1, "Mainly clear");
        map.put(2, "Partly cloudy");
        map.put(3, "Overcast");
        map.put(45, "Fog");
        map.put(48, "Depositing rime fog");
        map.put(51, "Drizzle: Light intensity");
        map.put(53, "Drizzle: Moderate intensity");
        map.put(55, "Drizzle: Dense intensity");
        map.put(56, "Freezing Drizzle: Light intensity");
        map.put(57, "Freezing Drizzle: Dense intensity");
        map.put(61, "Rain: Slight intensity");
        map.put(63, "Rain: Moderate intensity");
        map.put(65, "Rain: Heavy intensity");
        map.put(66, "Freezing Rain: Light intensity");
        map.put(67, "Freezing Rain: Heavy intensity");
        map.put(71, "Snow fall: Slight intensity");
        map.put(73, "Snow fall: Moderate intensity");
        map.put(75, "Snow fall: Heavy intensity");
        map.put(77, "Snow grains");
        map.put(80, "Rain showers: Slight intensity");
        map.put(81, "Rain showers: Moderate intensity");
        map.put(82, "Rain showers: Violent intensity");
        map.put(85, "Snow showers: Slight intensity");
        map.put(86, "Snow showers: Heavy intensity");
        map.put(95, "Thunderstorm: Slight or moderate");
        map.put(96, "Thunderstorm with slight hail");
        map.put(99, "Thunderstorm with heavy hail");
        return map;
    }

    @Override
    public WeatherDTO map(String body, String source) throws MappingException
    {
        try {
            JsonNode json = new ObjectMapper().readTree(body);
            double actualLat = json.get("latitude").asDouble();
            double actualLong = json.get("longitude").asDouble();
            JsonNode weatherJson = json.get("current_weather");
            double temp = weatherJson.get("temperature").asDouble();
            int weatherCode = weatherJson.get("weathercode").asInt();
            String description = WEATHER_CODE_TO_DESCRIPTION_MAP.get(weatherCode);
            if (description == null) {
                throw new MappingException(String.format("Unknown OpenMeteo WMO description code '%s'", weatherCode));
            }

            return new WeatherDTO(actualLat, actualLong, temp, description, source);
        } catch (JsonProcessingException e) {
            throw new MappingException("Could not map JSON response to WeatherDTO", e);
        }
    }
}
