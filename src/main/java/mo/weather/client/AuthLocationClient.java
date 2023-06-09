package mo.weather.client;

import mo.weather.dto.LocationDTO;
import mo.weather.mapper.WebClientResponseMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class AuthLocationClient extends LocationClient {
    private final static Logger LOG = LogManager.getLogger(AuthLocationClient.class);

    private final String apiKey;

    public AuthLocationClient(WebClientResponseMapper<LocationDTO> responseMapper,
                              String requestUrlTemplate, String apiKey)
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
    protected String getRequestUrl(String ip)
    {
        return String.format(this.requestUrlTemplate, ip, this.apiKey);
    }
}
