package mo.weather.client;

import mo.weather.dto.LocationDTO;
import mo.weather.dto.WeatherDTO;
import mo.weather.mapper.WebClientResponseMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class AuthWeatherClient extends WeatherClient {
    private final static Logger LOG = LogManager.getLogger(AuthWeatherClient.class);
    private final String apiKey;

    public AuthWeatherClient(WebClientResponseMapper<WeatherDTO> responseMapper, String requestUrlTemplate,
                             String apiKey)
    {
        super(responseMapper, requestUrlTemplate);
        this.apiKey = apiKey;
    }

    @Override
    protected Logger getLogger()
    {
        return LOG;
    }

    @Override
    protected String getRequestUrl(LocationDTO locationDTO)
    {
        return String.format(this.requestUrlTemplate, locationDTO.latitude(), locationDTO.longitude(), this.apiKey);
    }
}
