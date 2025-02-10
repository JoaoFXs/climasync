package com.project.climasync.bean;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.camel.Exchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.project.climasync.pojo.Daily;
import com.project.climasync.pojo.SixteenDayForecast;

@Component
public class CallDataBase {

    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    public void insertSixteenDayForecastTable(Exchange ex) throws JsonMappingException, JsonProcessingException {
        String body = ex.getIn().getBody(String.class);
        XmlMapper xmlMapper = new XmlMapper();
        SixteenDayForecast xml = xmlMapper.readValue(body, SixteenDayForecast.class);

        try (Connection conn = DriverManager.getConnection(url, username, password)) {
            String query = "INSERT INTO SixteenDayForecast (" +
                    "Namelocation, DetailLocation, Timezone, Latitude, Longitude, ForecastDate, " +
                    "Sunrise, Sunset, SunshineDuration, DaylightDuration, ApparentTemperatureMin, " +
                    "ApparentTemperatureMax, TemperatureMin, TemperatureMax, WindSpeedMax, " +
                    "WindGustsMax, WindDirectionDominant, RainSum, PrecipitationSum, " +
                    "PrecipitationProbabilityMax, PrecipitationHours, ShowerSum, UvIndexMax, " +
                    "UvIndexClearSkyMax, ShortWaveRadiationSum, Evapotranspiration, SnowfallSum" +
                    ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                for (Daily daily : xml.getDaily()) {
                    pstmt.setString(1, xml.getHeader().getLocationInformation().getNameLocation());
                    pstmt.setString(2, xml.getHeader().getLocationInformation().getDetailLocation());
                    pstmt.setString(3, xml.getHeader().getLocationInformation().getTimezone());
                    pstmt.setDouble(4, xml.getHeader().getLocationInformation().getLatitude());
                    pstmt.setDouble(5, xml.getHeader().getLocationInformation().getLongitude());
                    pstmt.setDate(6, daily.getTime());
                    pstmt.setString(7, daily.getSunInformation().getSunrise());
                    pstmt.setString(8, daily.getSunInformation().getSunset());
                    pstmt.setDouble(9, daily.getSunInformation().getSunshineDuration());
                    pstmt.setDouble(10, daily.getSunInformation().getDaylightDuration());
                    pstmt.setDouble(11, daily.getWeatherInformation().getApparentTemperatureMin());
                    pstmt.setDouble(12, daily.getWeatherInformation().getApparentTemperatureMax());
                    pstmt.setDouble(13, daily.getWeatherInformation().getTemperatureMin());
                    pstmt.setDouble(14, daily.getWeatherInformation().getTemperatureMax());
                    pstmt.setDouble(15, daily.getWindInformation().getWindSpeedMax());
                    pstmt.setDouble(16, daily.getWindInformation().getWindGustsMax());
                    pstmt.setInt(17, daily.getWindInformation().getWindDirectionDominant());
                    pstmt.setDouble(18, daily.getPrecipitationInformation().getRainSum());
                    pstmt.setDouble(19, daily.getPrecipitationInformation().getPrecipitationSum());
                    pstmt.setInt(20, daily.getPrecipitationInformation().getPrecipitationProbabilityMax());
                    pstmt.setDouble(21, daily.getPrecipitationInformation().getPrecipitationHours());
                    pstmt.setDouble(22, daily.getPrecipitationInformation().getShowerSum());
                    pstmt.setDouble(23, daily.getUvInformation().getUvIndexMax());
                    pstmt.setDouble(24, daily.getUvInformation().getUvIndexClearSkyMax());
                    pstmt.setDouble(25, daily.getUvInformation().getShortWaveRadiationSum());
                    pstmt.setDouble(26, daily.getOthers().getEvapotranspiration());
                    pstmt.setDouble(27, daily.getOthers().getSnowfallSum());

                    pstmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao conectar ao banco de dados: " + e.getMessage());
            e.printStackTrace();
        }
    }
}