package com.project.climasync.routes;
import java.util.List;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.project.climasync.bean.CallDataBase;
import com.project.climasync.config.EndpointDestinationFactory;
import com.project.climasync.utils.ToolBox;

@Component
public class DailyWeatherRoute extends RouteBuilder {
	
	@Autowired
	public EndpointDestinationFactory endpoint;
	
	@Value("#{'${locations}'.split(',')}")
	public List<String> locations;
	
	@Value("${period.timer}")
	public String periodTimer;
	
	@Value("activemq.queue")
	public String queue;
    @Override
    public void configure() throws Exception {
        
    	
    	onException(Exception.class)
    		.handled(true)
    		.log("ERROR001 - Generic error during integration - ${exception.message}")
    		.setProperty("status").simple("NOK")
    		.setProperty("errorCode").simple("E950")
    		.setProperty("errorDescription").simple("Generic error during integration - ${exception.message}")
	        .setBody().simple("<root></root>")
    		.to("xslt:classpath:schema/ErrorXml.xslt")
    		.toD("jms:queue:weather.api.errors")
    		;
//    		.process(exchange -> {
//    		        exchange.getContext().getRouteController().stopRoute("DailyWeatherRoute");
//    		   });
    	
    	
    		
        // Timer para buscar
        from("timer:fetchWeather?period=" + periodTimer)
        .routeId("DailyWeatherRoute")
        .setHeader("CamelHttpMethod", constant("GET")) // Define o método HTTP como GET   
        .setBody().constant(locations)
        
        .log("LOG001 - Weather Forecast Integration - Started")
        
        .split(body()) 
	        .bean(EndpointDestinationFactory.class, "createEndpoint")
	        .setProperty("nameLocation").simple("${header.name}")
	        .setProperty("detailLocation").simple("${header.detailLocation}")
	     
	        .bean(ToolBox.class, "convertJsontoXML")
	        //.bean(ToolBox.class, "saveText")
	        .setBody().simple("<root>${body}</root>")

	        .to("xslt:classpath:schema/RenderXML.xslt")
	        .to("xslt:classpath:schema/RemoveNulls.xslt")

	        .log(LoggingLevel.INFO, "LOG002 - Location - ${exchangeProperty.nameLocation} - Message Validation - Started")
	        //Message Validation through XSD
	       

	        .to("validator:classpath:validator/WeatherForecast.xsd")
	        .log("LOG102 - Location - ${exchangeProperty.nameLocation} - Message Validation - End")
	        
	        
	        .log("LOG003 - Location  - ${exchangeProperty.nameLocation} - Send to Queue - " + queue + " - Start")
	        .toD("jms:queue:weather.api." + "${exchangeProperty.nameLocation}")
	        .log("LOG103 - Location  - ${exchangeProperty.nameLocation} - Send to Queue - "+queue +" - End")
	        
	        .log("LOG004 - Location - ${exchangeProperty.nameLocation} - Send to DataBase - Start")
	        .bean(CallDataBase.class, "insertSixteenDayForecastTable")
	        .log("LOG104 - Location - ${exchangeProperty.nameLocation} - Send to DataBase - Start")
	        
         .end()
         
         .log("LOG100 - Weather Forecast Integration - End")
         ;   
            
    }
}
