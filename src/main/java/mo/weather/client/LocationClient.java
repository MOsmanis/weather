package mo.weather.client;

import mo.weather.dto.LocationDTO;
import mo.weather.mapper.WebClientResponseMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public sealed class LocationClient extends WebClient<LocationDTO, String> permits AuthLocationClient {
    private final static Logger LOG = LogManager.getLogger(LocationClient.class);

    
    public LocationClient(WebClientResponseMapper<LocationDTO> responseMapper, String requestUrlTemplate)
    {
        super(responseMapper, requestUrlTemplate);
    }

    @Override
    protected Logger getLogger()
    {
        return LOG;
    }

    @Override
    protected String getRequestUrl(String ip)
    {
        return String.format(this.requestUrlTemplate, ip);
    }
}
