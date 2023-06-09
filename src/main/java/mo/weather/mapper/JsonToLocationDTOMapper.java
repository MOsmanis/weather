package mo.weather.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import mo.weather.dto.LocationDTO;
import org.springframework.data.mapping.MappingException;

public class JsonToLocationDTOMapper implements WebClientResponseMapper<LocationDTO> {
    private final String latitudeField;
    private final String longitudeField;

    public JsonToLocationDTOMapper(String latitudeField, String longitudeField)
    {
        this.latitudeField = latitudeField;
        this.longitudeField = longitudeField;
    }

    @Override
    public LocationDTO map(String body, String source) throws MappingException
    {
        try {
            JsonNode json = new ObjectMapper().readTree(body);
            return new LocationDTO(
                json.get(this.latitudeField).asDouble(),
                json.get(this.longitudeField).asDouble(),
                source);
        } catch (JsonProcessingException e) {
            throw new MappingException("Could not map JSON response to LocationDTO", e);
        }
    }
}
