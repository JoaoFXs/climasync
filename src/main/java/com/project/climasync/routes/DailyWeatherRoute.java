package com.project.climasync.routes;
import java.util.List;

import org.apache.camel.LoggingLevel;
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
	
	
	@Value("activemq.queue")
	public String queue;
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
	        //.bean(ToolBox.class, "saveText")
	        .setBody().simple("<root>${body}</root>")
	
	        
	
	        .to("xslt:classpath:schema/RenderXML.xslt")
	        .to("xslt:classpath:schema/RemoveNulls.xslt")

	        .to("validator:classpath:validator/SixteenDayForecast.xsd")
	        .log(LoggingLevel.INFO, "LOG002 - Location - ${exchangeProperty.nameLocation} - Message Validation - Started")
	        //Message Validation through XSD
	       
	        
	        .log("LOG102 - Location - ${exchangeProperty.nameLocation} - Message Validation - End")
	        
	        
	        .log("LOG003 - Location  - ${exchangeProperty.nameLocation} - Send to Queue - " + queue + " - Start")
	        .toD("jms:queue:weather.api." + "${exchangeProperty.nameLocation}")
	        .log("LOG103 - Location  - ${exchangeProperty.nameLocation} - Send to Queue - "+queue +" - End")
	   
         .end()
         
         .log("LOG100 - Daily Weather Integration - End")
         ;   
            
    }
}
