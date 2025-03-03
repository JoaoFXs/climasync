CREATE DATABASE IF NOT EXISTS weather_db;
USE weather_db;

CREATE TABLE IF NOT EXISTS WeatherForecast (
    id INT AUTO_INCREMENT PRIMARY KEY,
    Namelocation VARCHAR(255),
    DetailLocation TEXT,
    Timezone VARCHAR(50),
    Latitude DECIMAL(9, 6),
    Longitude DECIMAL(9, 6),
    ForecastDate DATE,
    Sunrise DATETIME,
    Sunset DATETIME,
    SunshineDuration DECIMAL(10, 2),
    DaylightDuration DECIMAL(10, 2),
    ApparentTemperatureMin DECIMAL(5, 2),
    ApparentTemperatureMax DECIMAL(5, 2),
    TemperatureMin DECIMAL(5, 2),
    TemperatureMax DECIMAL(5, 2),
    WindSpeedMax DECIMAL(5, 2),
    WindGustsMax DECIMAL(5, 2),
    WindDirectionDominant INT,
    RainSum DECIMAL(5, 2),
    PrecipitationSum DECIMAL(5, 2),
    PrecipitationProbabilityMax INT,
    PrecipitationHours DECIMAL(5, 2),
    ShowerSum DECIMAL(5, 2),
    UvIndexMax DECIMAL(5, 2),
    UvIndexClearSkyMax DECIMAL(5, 2),
    ShortWaveRadiationSum DECIMAL(10, 2),
    Evapotranspiration DECIMAL(5, 2),
    SnowfallSum DECIMAL(5, 2)
);
