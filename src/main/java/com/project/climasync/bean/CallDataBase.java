//package com.project.climasync.bean;
//
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.PreparedStatement;
//import java.sql.SQLException;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class CallDataBase {
//	
//	@Value("${spring.datasource.url}")
//	private String url;
//	
//	@Value("${spring.datasource.username}")
//	private String username;
//	
//	@Value("${spring.datasource.password}")
//	private String password;
//
//	@Value("${spring.datasource.driver}")
//	private String driver;
//
//
//
//    public void insertSixteenDayForecastTable() throws SQLException {
//
//
//	       // Conectar ao banco de dados
//	       try (Connection conn = DriverManager.getConnection(url, username, password)) {
//	                String query = "INSERT INTO SixteenDayForecast (" +
//	                        "Namelocation, DetailLocation, Timezone, Latitude, Longitude, ForecastDate, " +
//	                        "Sunrise, Sunset, SunshineDuration, DaylightDuration, ApparentTemperatureMin, " +
//	                        "ApparentTemperatureMax, TemperatureMin, TemperatureMax, WindSpeedMax, " +
//	                        "WindGustsMax, WindDirectionDominant, RainSum, PrecipitationSum, " +
//	                        "PrecipitationProbabilityMax, PrecipitationHours, ShowerSum, UvIndexMax, " +
//	                        "UvIndexClearSkyMax, ShortWaveRadiationSum, Evapotranspiration, SnowfallSum" +
//	                        ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
//	                PreparedStatement pstmt = conn.prepareStatement(query);
//
//	                // Extrair dados do XML
//	                Element header = rootElement.getChild("Header");
//	                Element locationInfo = header.getChild("Location_Information");
//	                String namelocation = locationInfo.getChildText("Namelocation");
//	                String detailLocation = locationInfo.getChildText("DetailLocation");
//	                String timezone = locationInfo.getChildText("Timezone");
//	                double latitude = Double.parseDouble(locationInfo.getChildText("Latitude"));
//	                double longitude = Double.parseDouble(locationInfo.getChildText("Longitude"));
//
//	                for (Element daily : rootElement.getChildren("Daily")) {
//	                    pstmt.setString(1, namelocation);
//	                    pstmt.setString(2, detailLocation);
//	                    pstmt.setString(3, timezone);
//	                    pstmt.setDouble(4, latitude);
//	                    pstmt.setDouble(5, longitude);
//	                    pstmt.setString(6, daily.getChildText("Time"));
//	                    pstmt.setString(7, daily.getChild("SunInformation").getChildText("Sunrise"));
//	                    pstmt.setString(8, daily.getChild("SunInformation").getChildText("Sunset"));
//	                    pstmt.setDouble(9, Double.parseDouble(daily.getChild("SunInformation").getChildText("SunshineDuration")));
//	                    pstmt.setDouble(10, Double.parseDouble(daily.getChild("SunInformation").getChildText("DaylightDuration")));
//	                    pstmt.setDouble(11, Double.parseDouble(daily.getChild("SunInformation").getChildText("ApparentTemperatureMin")));
//	                    pstmt.setDouble(12, Double.parseDouble(daily.getChild("WeatherInformation").getChildText("ApparentTemperatureMax")));
//	                    pstmt.setDouble(13, Double.parseDouble(daily.getChild("WeatherInformation").getChildText("TemperatureMin")));
//	                    pstmt.setDouble(14, Double.parseDouble(daily.getChild("WeatherInformation").getChildText("TemperatureMax")));
//	                    pstmt.setDouble(15, Double.parseDouble(daily.getChild("WindInformation").getChildText("WindSpeedMax")));
//	                    pstmt.setDouble(16, Double.parseDouble(daily.getChild("WindInformation").getChildText("WindGustsMax")));
//	                    pstmt.setInt(17, Integer.parseInt(daily.getChild("WindInformation").getChildText("WindDirectionDominant")));
//	                    pstmt.setDouble(18, Double.parseDouble(daily.getChild("PrecipitationInformation").getChildText("RainSum")));
//	                    pstmt.setDouble(19, Double.parseDouble(daily.getChild("PrecipitationInformation").getChildText("PrecipitationSum")));
//	                    pstmt.setInt(20, Integer.parseInt(daily.getChild("PrecipitationInformation").getChildText("PrecipitationProbabilityMax")));
//	                    pstmt.setDouble(21, Double.parseDouble(daily.getChild("PrecipitationInformation").getChildText("PrecipitationHours")));
//	                    pstmt.setDouble(22, Double.parseDouble(daily.getChild("PrecipitationInformation").getChildText("ShowerSum")));
//	                    pstmt.setDouble(23, Double.parseDouble(daily.getChild("UVInformation").getChildText("UvIndexMax")));
//	                    pstmt.setDouble(24, Double.parseDouble(daily.getChild("UVInformation").getChildText("UvIndexClearSkyMax")));
//	                    pstmt.setDouble(25, Double.parseDouble(daily.getChild("UVInformation").getChildText("ShortWaveRadiationSum")));
//	                    pstmt.setDouble(26, Double.parseDouble(daily.getChild("Others").getChildText("Evapotranspiration")));
//	                    pstmt.setDouble(27, Double.parseDouble(daily.getChild("Others").getChildText("SnowfallSum")));
//
//	                    pstmt.executeUpdate();
//	                }
//
//	                System.out.println("Dados inseridos com sucesso!");
//	            } catch (SQLException e) {
//	                e.printStackTrace();
//	            }
//	        } catch (JDOMException | IOException e) {
//	            e.printStackTrace();
//	        }
//	    }
//			
//	}
//
