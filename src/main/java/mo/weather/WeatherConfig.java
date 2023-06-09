package mo.weather;

import com.github.benmanes.caffeine.cache.Caffeine;
import mo.weather.client.AuthLocationClient;
import mo.weather.client.AuthWeatherClient;
import mo.weather.client.LocationClient;
import mo.weather.client.WeatherClient;
import mo.weather.mapper.JsonToLocationDTOMapper;
import mo.weather.mapper.OpenMeteoResponseMapper;
import mo.weather.mapper.OpenWeatherMapResponseMapper;
import mo.weather.mapper.WeatherBitResponseMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Configuration
@EnableCaching
public class WeatherConfig {
    @Value("${api.key.ipstack}")
    private String ipStackApiKey;
    @Value("${api.key.openweathermap}")
    private String openWeatherMapApiKey;
    @Value("${api.key.weatherbit}")
    private String weatherBitApiKey;

    @Bean
    public List<LocationClient> locationClients() {
        return Collections.synchronizedList(
            Arrays.asList(
                new AuthLocationClient(
                    new JsonToLocationDTOMapper("latitude", "longitude"),
                    "http://api.ipstack.com/%1$s?access_key=%2$s",
                    ipStackApiKey
                ),
                new LocationClient(
                    new JsonToLocationDTOMapper("latitude", "longitude"),
                    "https://ipapi.co/%1$s/json/"
                ),
                new LocationClient(
                    new JsonToLocationDTOMapper("lat", "lon"),
                    "http://ip-api.com/json/%1$s"
                )
            )
        );
    }

    @Bean
    public List<WeatherClient> weatherClients() {
        return Collections.synchronizedList(
            Arrays.asList(
                new WeatherClient(
                    new OpenMeteoResponseMapper(),
                    "https://api.open-meteo.com/v1/forecast?latitude=%1$s&longitude=%2$s&current_weather=true"
                ),
                new AuthWeatherClient(
                    new OpenWeatherMapResponseMapper(),
                    "https://api.openweathermap.org/data/2.5/weather?lat=%1$s&lon=%2$s&units=metric&appid=%3$s",
                    openWeatherMapApiKey
                ),
                new AuthWeatherClient(
                    new WeatherBitResponseMapper(),
                    "https://api.weatherbit.io/v2.0/current?lat=%1$s&lon=%2$s&key=%3$s",
                    weatherBitApiKey
                )
            )
        );
    }

    @Bean
    @Primary
    public CacheManager weatherCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("weatherCache");
        cacheManager.setCaffeine(Caffeine.newBuilder()
            .maximumSize(100)
            .expireAfterWrite(Duration.ofMinutes(60)));
        return cacheManager;
    }

    @Bean
    public CacheManager locationCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("locationCache");
        cacheManager.setCaffeine(Caffeine.newBuilder()
            .maximumSize(500)
            .expireAfterWrite(Duration.ofDays(Integer.MAX_VALUE)));
        return cacheManager;
    }
}
