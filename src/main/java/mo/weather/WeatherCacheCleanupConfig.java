package mo.weather;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Objects;

@Configuration
public class WeatherCacheCleanupConfig {
    @Autowired
    private CacheManager weatherCacheManager;
    //Clear weatherCache hourly
    @Scheduled(cron = "0 0 * * * *")
    public void clearWeatherCache(){
        Objects.requireNonNull(weatherCacheManager.getCache("weatherCache")).clear();
    }

}
