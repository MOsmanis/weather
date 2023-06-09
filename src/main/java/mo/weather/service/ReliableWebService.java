package mo.weather.service;

import mo.weather.client.WebClient;
import mo.weather.exception.NoWebClientAvailableException;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Reliability component
 *
 * @param <T> Response value DTO type
 * @param <P> Request parameter DTO type
 */
public abstract sealed class ReliableWebService<T, P> permits LocationWebService, WeatherWebService {
    private final List<? extends WebClient<T, P>> webClientsByReliability;

    public ReliableWebService(List<? extends WebClient<T, P>> webClientsByReliability)
    {
        this.webClientsByReliability = webClientsByReliability;
        this.webClientsByReliability.sort(Comparator.comparing(WebClient::getFailureCount));
    }

    public T get(P requestParam)
    {
        try {
            T response = getResponse(requestParam);
            saveResponse(response, requestParam);
            return response;
        } catch (NoWebClientAvailableException e) {
            return getHistoricalResponse(requestParam);
        } finally {
            webClientsByReliability.sort(Comparator.comparing(WebClient::getFailureCount));
        }
    }

    private T getResponse(P requestParam) throws NoWebClientAvailableException
    {
        synchronized (webClientsByReliability) {
            for (var webClient : webClientsByReliability) {
                Optional<T> response = Optional.ofNullable(webClient.getResponse(requestParam));
                if(response.isPresent()) {
                    return response.get();
                }
            }
        }
        throw new NoWebClientAvailableException();
    }

    /**
     * As last resort for reliability use historical values
     */
    protected abstract T getHistoricalResponse(P requestParam);

    protected abstract void saveResponse(T response, P requestParam);
}
