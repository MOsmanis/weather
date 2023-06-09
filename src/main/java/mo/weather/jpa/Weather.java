package mo.weather.jpa;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "weather")
public class Weather {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Double callerLat;
    private Double callerLon;
    private Double actualLat;
    private Double actualLon;
    private Double temp;
    private String description;
    private String source;
    private LocalDateTime timestamp;

    public Long getId()
    {
        return id;
    }

    public Double getCallerLat()
    {
        return callerLat;
    }

    public void setCallerLat(Double callerLat)
    {
        this.callerLat = callerLat;
    }

    public Double getCallerLon()
    {
        return callerLon;
    }

    public void setCallerLon(Double callerLon)
    {
        this.callerLon = callerLon;
    }

    public Double getActualLat()
    {
        return actualLat;
    }

    public void setActualLat(Double actualLat)
    {
        this.actualLat = actualLat;
    }

    public Double getActualLon()
    {
        return actualLon;
    }

    public void setActualLon(Double actualLon)
    {
        this.actualLon = actualLon;
    }

    public Double getTemp()
    {
        return temp;
    }

    public void setTemp(Double temp)
    {
        this.temp = temp;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getSource()
    {
        return source;
    }

    public void setSource(String source)
    {
        this.source = source;
    }

    public LocalDateTime getTimestamp()
    {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp)
    {
        this.timestamp = timestamp;
    }
}
