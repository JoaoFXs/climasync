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
import com.project.climasync.utils.Logs;
import com.project.climasync.utils.ToolBox;
import com.project.climasync.utils.ToolBoxEnum;

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
    		.log(Logs.E001.message("Generic error during integration - ${exception.message}"))
    		.setProperty("status").simple("NOK")
    		.setProperty("errorCode").simple("E950")
    		.setProperty("errorDescription").simple("Generic error during integration - ${exception.message}")
	        .setBody().simple("<root></root>")
	        .to(ToolBoxEnum.XSLT.file("ErrorXml.xslt"))
    		.toD(ConfigBroker.JMSQUEUE.queue(queueError))
    		;
    	
    	onException(org.apache.camel.http.base.HttpOperationFailedException.class, UnknownHostException.class,HttpHostConnectException.class, NoHttpResponseException.class, SSLHandshakeException.class, SocketException.class, TimeoutException.class,SocketTimeoutException.class, SSLException.class)
    		.handled(true)
    		.log(Logs.E002.message("Connection Error - ${exception.message"))
    		.setProperty("status").simple("NOK")
    		.setProperty("errorCode").simple("E950")
    		.setProperty("errorDescription").simple("Generic error during integration - ${exception.message}")
	        .setBody().simple("<root></root>")
	        .to(ToolBoxEnum.XSLT.file("ErrorXml.xslt"))
       		.toD(ConfigBroker.JMSQUEUE.queue(queueError))
    		;
    	
    	onException(SchemaValidationException.class)
    		.handled(true)
    		.log(Logs.E003.message("Message Validation Error - ${exception.message"))
    		.setProperty("status").simple("NOK")
    		.setProperty("errorCode").simple("E950")
    		.setProperty("errorDescription").simple("Generic error during integration - ${exception.message}")
	        .setBody().simple("<root></root>")
	        .to(ToolBoxEnum.XSLT.file("ErrorXml.xslt"))
       		.toD(ConfigBroker.JMSQUEUE.queue(queueError))
    		;
    		
    		
        // Timer para buscar
        from("timer:fetchWeather?period=" + periodTimer)
        .routeId("DailyWeatherRoute")
        .setHeader("CamelHttpMethod", constant("GET")) // Define o método HTTP como GET   
        .setBody().constant(locations)
        

        .log(Logs.V001.message("Weather Forecast Integration - Started"))

        .split(body()) 
	        .bean(EndpointDestinationFactory.class, "createEndpoint")
	        .setProperty("nameLocation").simple("${header.name}")
	        .setProperty("detailLocation").simple("${header.detailLocation}")

	        .bean(ToolBox.class, "convertJsontoXML")
	        //.bean(ToolBox.class, "saveText")
	        .setBody().simple("<root>${body}</root>")
	        
	        .to(ToolBoxEnum.XSLT.file("RenderXML.xslt"))
	        .to(ToolBoxEnum.XSLT.file("RemoveNulls.xslt"))
	        
	        .log(Logs.V002.message("Location - ${exchangeProperty.nameLocation} - Message Validation - Started"))

	        //Message Validation through XSD
	      
	        .to(ToolBoxEnum.VALIDATOR.file("WeatherForecast.xsd"))
	      
	        .log(Logs.V102.message("Location - ${exchangeProperty.nameLocation} - Message Validation - End"))

	        .log(Logs.V003.message("Location  - ${exchangeProperty.nameLocation} - Send to Queue - \" + \" - Start"))
	        
	        .toD(ConfigBroker.JMSQUEUE.queueLocation("weather.api.","${exchangeProperty.nameLocation}"))

	        .log(Logs.V103.message("Location  - ${exchangeProperty.nameLocation} - Send to Queue - \" +\" - End"))

	        .log(Logs.V004.message("Location - ${exchangeProperty.nameLocation} - Send to DataBase - Start"))
	 
	        .bean(CallDataBase.class, "insertSixteenDayForecastTable")
	        
	        .log(Logs.V104.message("LOG104 - Location - ${exchangeProperty.nameLocation} - Send to DataBase - End"))
	        
         .end()
         
         .log(Logs.V100.message("Weather Forecast Integration - End"));   
            
    }
}
