package mo.weather.client;

import mo.weather.dto.LocationDTO;
import mo.weather.dto.WeatherDTO;
import mo.weather.mapper.WebClientResponseMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public sealed class WeatherClient extends WebClient<WeatherDTO, LocationDTO> permits AuthWeatherClient {
    private final static Logger LOG = LogManager.getLogger(WeatherClient.class);

    public WeatherClient(WebClientResponseMapper<WeatherDTO> responseMapper, String requestUrlTemplate)
    {
        super(responseMapper, requestUrlTemplate);
    }

    @Override
    protected Logger getLogger()
    {
        return LOG;
    }

    @Override
    protected String getRequestUrl(LocationDTO locationDTO)
    {
        return String.format(this.requestUrlTemplate, locationDTO.latitude(), locationDTO.longitude());
    }
}
