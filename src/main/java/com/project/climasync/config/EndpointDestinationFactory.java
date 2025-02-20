package com.project.climasync.config;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.jayway.jsonpath.JsonPath;

/**
 * A factory class responsible for creating endpoints to fetch weather forecast data
 * based on a given location. It integrates with external services to obtain geographical
 * coordinates and subsequently retrieves weather data for those coordinates.
 */
@Component
public class EndpointDestinationFactory {

    private final CamelContext context;
    private final RestTemplate restTemplate;

    /**
     * Constructs an instance of EndpointDestinationFactory with the provided CamelContext.
     *
     * @param context the CamelContext used in the application
     */
    @Autowired
    public EndpointDestinationFactory(CamelContext context, RestTemplate restTemplate) {
        this.context = context;
        this.restTemplate = restTemplate;
    }

    /**
     * Creates an endpoint by obtaining the geographical coordinates of a given location
     * and fetching the corresponding weather forecast data.
     *
     * @param exchange the current exchange containing the location in the message body
     * @return a JSON string containing the weather forecast data
     * @throws Exception if an error occurs during the process
     */
    public String createEndpoint(Exchange exchange) throws Exception {
        String location = exchange.getIn().getBody(String.class);
        String jsonLocation = getLocationCoordinates(location);
        String latitude = JsonPath.read(jsonLocation, "$[0].lat");
        String longitude = JsonPath.read(jsonLocation, "$[0].lon");
        String name = JsonPath.read(jsonLocation, "$[0].name");
        String detailLocation = JsonPath.read(jsonLocation, "$[0].display_name");

        exchange.getMessage().setHeader("name", name);
        exchange.getMessage().setHeader("detailLocation", detailLocation);

        String url = String.format(
            "https://api.open-meteo.com/v1/forecast?latitude=%s&longitude=%s&daily=weather_code,temperature_2m_max,temperature_2m_min,apparent_temperature_max,apparent_temperature_min,sunrise,sunset,daylight_duration,sunshine_duration,uv_index_max,uv_index_clear_sky_max,precipitation_sum,rain_sum,showers_sum,snowfall_sum,precipitation_hours,precipitation_probability_max,wind_speed_10m_max,wind_gusts_10m_max,wind_direction_10m_dominant,shortwave_radiation_sum,et0_fao_evapotranspiration&forecast_days=16",
            latitude, longitude
        );

        return restTemplate.getForObject(url, String.class);
    }

    /**
     * Retrieves the geographical coordinates (latitude and longitude) for a given location
     * by querying the Nominatim API.
     *
     * @param location the name of the location to search for
     * @return a JSON string containing the search results with geographical data
     */
    public String getLocationCoordinates(String location) {
        String url = String.format(
            "https://nominatim.openstreetmap.org/search.php?q=%s&format=jsonv2",
            location
        );

        return restTemplate.getForObject(url, String.class);
    }
}
