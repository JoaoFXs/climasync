package com.project.climasync.routes;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.TimeoutException;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;

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
	
	@Value("${activemq.queue.redelivery}")
	public String queueRedelivery;
	
	@Value("${redelivery.count}")
	public int redeliveryCount;
	
	@Value("${redelivery.delay}")
	public int redeliveryDelay;
	
	@Value("${timeout.jdbc}")
	public int timeoutIntegration;
    @Override
    public void configure() throws Exception {
        
        /**
         * Configures the dead-letter channel to handle message failures.
         * Messages that cannot be processed will be sent to a JMS dead-letter queue.
         */
        errorHandler(deadLetterChannel(ConfigBroker.JMSQUEUE.queue(queueError))
                       .useOriginalMessage()
                       .maximumRedeliveries(redeliveryCount)
                       .redeliveryDelay(redeliveryDelay));

        /**
         * Handles generic exceptions during integration.
         * Logs the error, sets error properties, and sends the message to an error queue.
         */
        onException(Exception.class)
            .handled(true)
            .useOriginalMessage()
            .maximumRedeliveries(redeliveryCount)
            .redeliveryDelay(redeliveryDelay)
            .useOriginalMessage()
            .toD(ConfigBroker.JMSQUEUE.queue(queueRedelivery))
            .log(Logs.E001.message("Generic error during integration - ${exception.message}"))
            .setProperty("status").simple("NOK")
            .setProperty("errorCode").simple("E001")
            .setProperty("errorDescription").simple("Generic error during integration - ${exception.message}")
            .setBody().simple("<root></root>")
            .to(ToolBoxEnum.XSLT.file("ErrorXml.xslt"))
            .toD(ConfigBroker.JMSQUEUE.queue(queueError));

        /**
         * Handles connection-related exceptions.
         * These include HTTP errors, timeouts, and SSL issues.
         * Logs the error, sets error properties, and routes the message accordingly.
         */
        onException(org.apache.camel.http.base.HttpOperationFailedException.class, 
                    UnknownHostException.class, 
                    HttpHostConnectException.class, 
                    NoHttpResponseException.class, 
                    SSLHandshakeException.class, 
                    SocketException.class, 
                    TimeoutException.class, 
                    SocketTimeoutException.class, 
                    SSLException.class)
            .handled(true)
            .useOriginalMessage()
            .maximumRedeliveries(redeliveryCount)
            .redeliveryDelay(redeliveryDelay)
            .useOriginalMessage()
            .toD(ConfigBroker.JMSQUEUE.queue(queueRedelivery))
            .log(Logs.E002.message("Connection Error - ${exception.message}"))
            .setProperty("status").simple("NOK")
            .setProperty("errorCode").simple("E002")
            .setProperty("errorDescription").simple("Generic error during integration - ${exception.message}")
            .setBody().simple("<root></root>")
            .to(ToolBoxEnum.XSLT.file("ErrorXml.xslt"))
            .toD(ConfigBroker.JMSQUEUE.queue(queueError));

        /**
         * Handles schema validation exceptions.
         * Occurs when messages fail validation against a predefined schema.
         * Logs the error, sets error properties, and sends the message to an error queue.
         */
        onException(SchemaValidationException.class)
            .handled(true)
            .useOriginalMessage()
            .maximumRedeliveries(redeliveryCount)
            .redeliveryDelay(redeliveryDelay)
            .useOriginalMessage()
            .toD(ConfigBroker.JMSQUEUE.queue(queueRedelivery))
            .log(Logs.E003.message("Message Validation Error - ${exception.message}"))
            .setProperty("status").simple("NOK")
            .setProperty("errorCode").simple("E003")
            .setProperty("errorDescription").simple("Generic error during integration - ${exception.message}")
            .setBody().simple("<root></root>")
            .to(ToolBoxEnum.XSLT.file("ErrorXml.xslt"))
            .toD(ConfigBroker.JMSQUEUE.queue(queueError));
    		
    		
    	/**
    	* A route that starts a timer to periodically fetch weather forecasts
    	* based on the interval defined in 'period.timer'. For each location
    	* specified in 'locations', the route makes a call to the weather API,
    	* converts the JSON response to XML, validates the generated XML, and sends the data
    	* to the corresponding queue and database.
    	*/
        from("timer:fetchWeather?period=" + periodTimer)
     		        .routeId("DailyWeatherRoute")
			        .setHeader("CamelHttpMethod", constant("GET")) // Define o método HTTP como GET   
			        .setBody().constant(locations)
			     
			        .log(Logs.V001.message("Weather Forecast Integration - Started"))
			
				        /**
				        * Splits the message body, which contains the list of locations,
				        * into individual messages for each location, allowing
				        * separate processing of each location.
				        */
				        .split(body()) 
					        .bean(EndpointDestinationFactory.class, "createEndpoint")
					        .setProperty("nameLocation").simple("${header.name}")
					        .setProperty("detailLocation").simple("${header.detailLocation}")
					        /**
					         *Method for converting xml to json 
					         */
					        .bean(ToolBox.class, "convertJsontoXML")
					        
					       
					        /**
					         *inserting root to structure the xml
					         */
					        .setBody().simple("<root>${body}</root>")
					        
					        .to(ToolBoxEnum.XSLT.file("RenderXML.xslt"))
					        .to(ToolBoxEnum.XSLT.file("RemoveNulls.xslt"))
					        
					        .log(Logs.V002.message("Location - ${exchangeProperty.nameLocation} - Message Validation - Started"))
				
					  
					        /**
					         *Message Validation through XSD
					         */
					        .to(ToolBoxEnum.VALIDATOR.file("WeatherForecast.xsd"))
					      
					        .log(Logs.V102.message("Location - ${exchangeProperty.nameLocation} - Message Validation - End"))
				
					        .log(Logs.V003.message("Location  - ${exchangeProperty.nameLocation} - Send to Queue - \" + \" - Start"))
					        
					        /**
					         * Send the generated message to the JMS Queue in ActiveMQ.  Multicast was used to send to multiple queues
					         */
						        .multicast()
							        .toD(ConfigBroker.JMSQUEUE.queueLocation("weather.api.location.","${exchangeProperty.nameLocation}"))
							        .toD(ConfigBroker.JMSQUEUE.queue("weather.api.all.messages"))
						        .end()
					        .log(Logs.V103.message("Location  - ${exchangeProperty.nameLocation} - Send to Queue - \" +\" - End"))
				
					        .log(Logs.V004.message("Location - ${exchangeProperty.nameLocation} - Send to DataBase - Start"))
					      	
					        .to("direct:insertDatabase")
					        
					        .log(Logs.V104.message("Location - ${exchangeProperty.nameLocation} - Send to DataBase - End"))
					        
				         .end()
			         
			         .log(Logs.V100.message("Weather Forecast Integration - End"))

         .end();   
     
        
        
        from("direct:insertDatabase")
        .routeId("insertDatabase")
        .circuitBreaker()
            .resilience4jConfiguration()
                .failureRateThreshold(50)  // Opens the circuit if 50% of calls fail
                .slidingWindowSize(10)     // Considers the last 10 requests
                .waitDurationInOpenState(20000) // Wait 20s before trying again
            .end()
            .bean(CallDataBase.class, "insertSixteenDayForecastTable")
        .onFallback()
            .log("ERROR002 - Error accessing database, please try again later")
        .end();
    }
}
