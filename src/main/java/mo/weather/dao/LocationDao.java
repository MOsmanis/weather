package mo.weather.dao;

import mo.weather.jpa.Location;

public interface LocationDao {
    Location find(String ip);
}
