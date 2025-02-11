package com.project.climasync.routes;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.TimeoutException;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.support.processor.validation.SchemaValidationException;
import org.apache.hc.client5.http.HttpHostConnectException;
import org.apache.hc.core5.http.NoHttpResponseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.project.climasync.bean.CallDataBase;
import com.project.climasync.config.ConfigBroker;
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
	
	@Value("${activemq.queue.error}")
	public String queueError;
	

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
    		.toD(ConfigBroker.JMSQUEUE.queue(queueError))
    		;
    	
    	onException(org.apache.camel.http.base.HttpOperationFailedException.class, UnknownHostException.class,HttpHostConnectException.class, NoHttpResponseException.class, SSLHandshakeException.class, SocketException.class, TimeoutException.class,SocketTimeoutException.class, SSLException.class)
    		.handled(true)
    		.log("ERROR002 - Connection Error - ${exception.message}")
    		.setProperty("status").simple("NOK")
    		.setProperty("errorCode").simple("E950")
    		.setProperty("errorDescription").simple("Generic error during integration - ${exception.message}")
	        .setBody().simple("<root></root>")
    		.to("xslt:classpath:schema/ErrorXml.xslt")
       		.toD(ConfigBroker.JMSQUEUE.queue(queueError))
    		;
    	
    	onException(SchemaValidationException.class)
    		.handled(true)
    		.log("ERROR003 - Message Validation Error - ${exception.message}")
    		.setProperty("status").simple("NOK")
    		.setProperty("errorCode").simple("E950")
    		.setProperty("errorDescription").simple("Generic error during integration - ${exception.message}")
	        .setBody().simple("<root></root>")
    		.to("xslt:classpath:schema/ErrorXml.xslt")
       		.toD(ConfigBroker.JMSQUEUE.queue(queueError))
    		;
    		
    		
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
	        
	        
	        .log("LOG003 - Location  - ${exchangeProperty.nameLocation} - Send to Queue - " + " - Start")
	        
	        .toD(ConfigBroker.JMSQUEUE.queueLocation("weather.api.","${exchangeProperty.nameLocation}"))

	        .log("LOG103 - Location  - ${exchangeProperty.nameLocation} - Send to Queue - " +" - End")
	        
	        .log("LOG004 - Location - ${exchangeProperty.nameLocation} - Send to DataBase - Start")
	        .bean(CallDataBase.class, "insertSixteenDayForecastTable")
	        .log("LOG104 - Location - ${exchangeProperty.nameLocation} - Send to DataBase - Start")
	        
         .end()
         
         .log("LOG100 - Weather Forecast Integration - End");   
            
    }
}
