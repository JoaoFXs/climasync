package com.project.climasync.config;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.component.http.HttpComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.jayway.jsonpath.JsonPath;

@Component
public class EndpointDestinationFactory {
	
	private CamelContext context;
	
	@Autowired
	public EndpointDestinationFactory(CamelContext context) {
		this.context = context;
	}
	
	@Autowired
	private RestTemplate restTemplate;
	
	public String createEndpoint(Exchange ex) throws Exception{
		String location = (String) ex.getIn().getBody();
		String jsonLocation = getLocationCord(location);
		String latitude = JsonPath.read(jsonLocation, "$[0].lat");
		String longitude = JsonPath.read(jsonLocation, "$[0].lon");
		String name = JsonPath.read(jsonLocation, "$[0].name");
		String detailLocation = JsonPath.read(jsonLocation, "$[0].display_name");
		
		ex.getOut().setHeader("name", name);
		ex.getOut().setHeader("detailLocation", detailLocation);
        String url = "https://api.open-meteo.com/v1/forecast?latitude=" + latitude + "&longitude=" + longitude + "1&daily=weather_code,temperature_2m_max,temperature_2m_min,apparent_temperature_max,apparent_temperature_min,sunrise,sunset,daylight_duration,sunshine_duration,uv_index_max,uv_index_clear_sky_max,precipitation_sum,rain_sum,showers_sum,snowfall_sum,precipitation_hours,precipitation_probability_max,wind_speed_10m_max,wind_gusts_10m_max,wind_direction_10m_dominant,shortwave_radiation_sum,et0_fao_evapotranspiration&forecast_days=16";
		String response = restTemplate.getForObject(url, String.class);
		
		return response;
		
	}
	
	public String getLocationCord(String location) {
		String url = "https://nominatim.openstreetmap.org/search.php?q="+location+"&format=jsonv2";
		
		String response = restTemplate.getForObject(url, String.class);
		
		return response;
	}
	

	
}
