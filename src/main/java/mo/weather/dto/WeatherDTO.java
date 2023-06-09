package mo.weather.dto;

public record WeatherDTO(double actualLat, double actualLon, double temp, String description, String source) {
}
