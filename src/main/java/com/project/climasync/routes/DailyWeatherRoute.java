package com.project.climasync.routes;
import java.util.List;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.project.climasync.config.EndpointDestinationFactory;
import com.project.climasync.utils.ToolBox;

@Component
public class DailyWeatherRoute extends RouteBuilder {
	
	@Autowired
	public EndpointDestinationFactory endpoint;
	
	@Value("#{'${locations}'.split(',')}")
	public List<String> locations;
	
    @Override
    public void configure() throws Exception {
        
        // Timer para buscar
        from("timer:fetchWeather?period=1hr")
        .routeId("DailyWeatherRoute")
        .setHeader("CamelHttpMethod", constant("GET")) // Define o método HTTP como GET   
        .setBody().constant(locations)
        
        .log("LOG001 - Daily Weather Integration - Started")
        
        .split(body()) 
	        .bean(EndpointDestinationFactory.class, "createEndpoint")
	        .setProperty("nameLocation").simple("${header.name}")
	        .setProperty("detailLocation").simple("${header.detailLocation}")
	        .bean(ToolBox.class, "convertJsontoXML")
	        .setBody().simple("<root>${body}</root>")
	
	        
	
	        .to("xslt:classpath:schema/RenderXML.xslt")
	        
	        .log("LOG002 - Location - ${exchangeProperty.nameLocation} - Message Validation - Started")
	        //Message Validation through XSD
	        .to("validator:classpath:validator/SixteenDayForecast.xsd")
	        
	        .log("LOG102 - Location - ${exchangeProperty.nameLocation} - Message Validation - End")
	        //.bean(ToolBox.class, "saveText")
	        
	        
	        .log("${body}")
         .end()
         
         .log("LOG100 - Daily Weather Integration - End")
         ;   
//            .process(exchange -> {
//                Map<String, Object> body = exchange.getIn().getBody(Map.class);
//                String city = (String) body.get("name");
//                Double temperature = (Double) ((Map<String, Object>) body.get("main")).get("temp");
//                Integer humidity = (Integer) ((Map<String, Object>) body.get("main")).get("humidity");
//
//                exchange.getIn().setBody(new Object[] { city, temperature, humidity });
//            })
            //.to("jdbc:dataSource?useHeadersAsParameters=true")
            //.log("Weather data saved for: ${body[0]}");
            
            ;

        // Exposição de API REST
//        from("servlet:/weather")
//            .setHeader("CamelSqlQuery", constant("SELECT * FROM weather_data WHERE city = :?city"))
//            .to("jdbc:dataSource")
//            .marshal().json();
            
    }
}
