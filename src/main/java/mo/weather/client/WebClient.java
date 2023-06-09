package mo.weather.client;

import mo.weather.mapper.WebClientResponseMapper;
import org.apache.logging.log4j.Logger;
import org.springframework.data.mapping.MappingException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;

public abstract sealed class WebClient<T, P> permits LocationClient, WeatherClient {
    private final WebClientResponseMapper<T> responseMapper;
    private final RestTemplate restTemplate;

    protected String requestUrlTemplate;
    protected int failureCount;

    public WebClient(WebClientResponseMapper<T> responseMapper, String requestUrlTemplate)
    {
        this.responseMapper = responseMapper;
        this.requestUrlTemplate = requestUrlTemplate;
        this.failureCount = 0;
        this.restTemplate = new RestTemplate();
    }

    public int getFailureCount()
    {
        return this.failureCount;
    }

    public T getResponse(P requestParam)
    {
        try {
            return call(requestParam);
        } catch (MappingException | RestClientException | URISyntaxException e) {
            getLogger().error("Exception occurred:", e);
            this.failureCount++;
        }

        return null;
    }

    protected T call(P requestParam) throws URISyntaxException
    {
        String requestUrl = getRequestUrl(requestParam);
        String host = new URI(requestUrl).getHost(); // Validate before call and save for mapping
        ResponseEntity<String> response = getRestTemplate().getForEntity(requestUrl, String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            return responseMapper.map(response.getBody(), host);
        }

        throw new RestClientResponseException(
            String.format("Request to %s was unsuccessful", requestUrl),
            response.getStatusCodeValue(),
            response.getStatusCode().getReasonPhrase(),
            null, null, null);
    }

    protected RestTemplate getRestTemplate()
    {
        return this.restTemplate;
    }

    protected abstract Logger getLogger();

    protected abstract String getRequestUrl(P requestParam);
}
