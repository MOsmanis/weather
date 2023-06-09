package mo.weather.mapper;

import org.springframework.data.mapping.MappingException;

public interface WebClientResponseMapper<T> {
    T map(String body, String source) throws MappingException;
}
