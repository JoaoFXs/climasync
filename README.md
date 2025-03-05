# ClimaSync - Integration Platform for Meteorological Data Capture and Distribution


ClimaSync is an integration platform developed with Apache Camel, designed to capture weather data from an external API (OpenWeather) and efficiently distribute it to consumer systems. By integrating with a weather API such as OpenMeteo, the platform collects crucial weather information, including temperature, humidity, and forecasts. 
This data is processed, validated, and stored in a database and ActiveMQ queues, ensuring its availability for future queries. The ClimaSync architecture aims to provide a continuous and automated flow of weather data, ensuring high availability, resilience, and easy access to information. As a result, the platform supports strategic and operational decisions across various applications.

## Table of Contents

- [Background](#background)
- [Installation](#installation)
- [Usage](#usage)
  - [Consume Messages From ActiveMQ](#consume-messages-from-activemq)
  - [Data Transformation and Validation](#data-transformation-and-validation)
  - [Error Handling](#error-handling)
  - [Log Viewing](#log-viewing)
  
## Background

The ClimaSync project integrates weather data from an external meteorological API, OpenMeteo API, to periodically collect essential climate information—including temperature, humidity, and weather forecasts. The data collection frequency is configurable in days through project properties, ensuring that the latest data is always retrieved. Developed with the Apache Camel framework, the integration is designed to be robust and scalable, enabling efficient message flow management.
Additionally, ClimaSync captures the geographical coordinates required for weather queries using the Nominatim API. By providing a location name, Nominatim returns the corresponding latitude and longitude, allowing the OpenMeteo API to be queried with precision for accurate weather data. Once the data is retrieved, it undergoes processing to convert the received information—typically in JSON format—into XML format, ensuring a clearer visualization of the structure. For this transformation, XSLT is used, while XSD is employed to validate the expected structure, ensuring that the messages conform to the defined standard.
 The processed data is stored in a relational database (MySQL). A table schema has been designed to store structured information, including the city name, collection date and time, temperature, humidity, and weather forecast for the coming days. Additionally, the generated XML is sent to an ActiveMQ queue, allowing consumers to access the data directly in XML format, if needed. To enhance system reliability, error-handling techniques provided by Apache Camel have been applied, along with Resilience4j for circuit breaker management.
 
### The goals for this repository are:

1. Utilize this project as a study tool for the Apache Camel framework, ActiveMQ, MySQL, XSD Validation, XSLT, Resilience4j, and Docker.
2. Develop a complete integration in Apache Camel, covering the entire messaging lifecycle of a microservice—from message capture, validation, transformation, and routing to different destinations—ensuring data persistence and high availability.
3. Explore routing strategies and asynchronous processing in Apache Camel, enabling the efficient distribution of captured data to various destinations, such as databases, message queues, and consuming APIs.
4. Apply best practices in monitoring and logging, using tools like Camel Tracing and Log4j, along with Promtail, Grafana, and Loki, to collect, centralize, and visualize logs efficiently, facilitating debugging, failure analysis, and improved observability of the integration.
5. Implement fault tolerance and resilience mechanisms, ensuring that the integration remains operational even in the event of temporary failures in the external API or database, using Resilience4j for circuit breaker management.



## Installation

If you want to run this project on your machine, a docker-compose file and a Dockerfile are available for this purpose. Just follow the commands below:

1. Generate a .jar
```sh
mvn clean package
```
2. Generate a build without cache

```sh
docker-compose build --no-cache
```
3. Start container with docker-compose

```sh
docker-compose up -d
```
With this, you will have a container running with the following images:
- MySQL Database: The necessary configuration, such as database and table creation, are already found in the [init.sql](https://github.com/JoaoFXs/climasync/blob/main/src/main/resources/sql/init.sql) file.
- Adminer: Interface web para gerenciar o MySQL
- ActiveMQ: Used for asynchronous communication between services. You can access the console with usr and pwd admin [activemq](http://localhost:8161/admin/)
- Loki: Log collector and storage
- Promtail: Log Collector for Loki
- Grafana: Dashboards for data visualization. To view logs, register a DataSource in grafana for loki using the ip http://loki:3100. In [grafana](http://localhost:3000/connections/datasources/edit/ceeyacppo2x34f), use the user and pwd admin to access
- ClimaSync-app: ClimaSync integration app generated from .jar. To configure application.properties, access [config](https://github.com/JoaoFXs/climasync/blob/main/config/application.properties). With this file, you will be able to configure several settings, such as locations (location) and scheduling (period.timer)

## Usage

With everything installed and Docker running, just access ClimaSync-app and you will see that the application is running. It is very important that you enter the locations you want to view, separated by commas, this way the integration will make a call for each location, as configured in the config. Soon after, you will be able to see the integration running in the terminal itself:

![terminal example](https://github.com/JoaoFXs/climasync/blob/main/src/main/resources/static/terminal.JPG)

### Consume Messages From ActiveMQ
In the image, it is possible to see several logs, where there are two cities. First, a validation is performed on the message through the [XSD](https://github.com/JoaoFXs/climasync/blob/main/src/main/resources/validator/WeatherForecast.xsd) file where, after processing the message captured from the API using a [XSLT](https://github.com/JoaoFXs/climasync/blob/main/src/main/resources/schema/RenderXML.xslt), it performs the validation of how this message construction is expected to be. It is then sent to the AMQ queues of each location, where the future consumer can access them asynchronously. Queues are created automatically when messages are sent and can be viewed in the activemq dashboard. There are queues for both sent messages and possible errors, captured by camel handler.

![activemq portal](https://github.com/JoaoFXs/climasync/blob/main/src/main/resources/static/activemq.JPG)

### Data Transformation and Validation
Data transformation and validation are essential steps to ensure the integrity and compliance of information received from external sources, such as the Open Meteo API. When consuming data from external APIs, it is common for responses to be in formats such as JSON or XML, which can be unstructured or contain irrelevant information.
In the specific case of the Open Meteo API, the JSON response for a one-day weather forecast can be long and contain several fields that are not always necessary for the application. To deal with this complexity, an effective approach was to convert the JSON to XML, a format that allows for a clearer hierarchical structure and makes data manipulation easier.
After conversion to XML, transformations could be applied using XSLT (Extensible Stylesheet Language Transformations). XSLT is a language designed to transform XML documents into other formats, such as XML, HTML or plain text. In the aforementioned context, two XSLT transformations are applied:

- [RenderXML.xslt](https://github.com/JoaoFXs/climasync/blob/main/src/main/resources/schema/RenderXML.xslt): This transformation is responsible for structuring and formatting the XML data in a way that meets the specific requirements of the application, preparing it for the subsequent processing steps. From this file, it was possible to structure the message as follows, with data on the location and the weather data for each day.
- [RemoveNulls.xslt](https://github.com/JoaoFXs/climasync/blob/main/src/main/resources/schema/RemoveNulls.xslt): After rendering, it is common for the XML to contain elements or attributes with null or empty values. This transformation removes these unnecessary elements, reducing the size of the document and eliminating irrelevant information.
- [ErrorXml.xslt](https://github.com/JoaoFXs/climasync/blob/main/src/main/resources/schema/ErrorXML.xslt): Error message transformation;

These transformations ensure that the data is clean, structured and ready for validation.
- [XML Success Example](https://github.com/JoaoFXs/climasync/blob/main/src/main/resources/examples/xmlsucess.xml) for Barueri localization
- [XML Error Example](https://github.com/JoaoFXs/climasync/blob/main/src/main/resources/examples/xmlerror.xml) for Connection Error

### Error handling

For possible errors, two different types of techniques were implemented.
- Redelivery: It is possible to configure "n" attempts in case of an error, using the original message. Aimed at connection errors with external APIs and generic errors. In this option, the integration will stop after the delivery attempts:
```java
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
            .setProperty("errorDescription").simple("Connection error during integration - ${exception.message}")
            .setBody().simple("<root></root>")
            .to(ToolBoxEnum.XSLT.file("ErrorXml.xslt"))
            .toD(ConfigBroker.JMSQUEUE.queue(queueError));
```

- Resilience4j: For use as a circuit breaker, which opens the circuit when 50% of the calls fail, considering the last 10 requests. Used only to cut the connection with MySql. Since the messages are already saved in ActiveMQ, this option was inserted so as not to stop the integration completely, and log that the connection with MySql did not work:

```java
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
```

### Consume MySQL

In addition to sending messages to ActiveMQ queues, there is also storage in a MySQL database, a table was created with all WeatherForecast items, thus allowing greater persistence of the data captured by the weather API. A MySQL image was also used in docker compose for possible local testing.

### Log Viewing 

The Logback configuration defines how the application logs will be captured and stored. The logging system was configured to record important events, allowing for better monitoring and debugging. To this end, a specific appender was created for the DailyWeatherRoute route, ensuring that its logs are stored separately in files within the logs/ folder, with daily rotation and a history of up to seven days. In addition, a console appender was configured to display logs directly in the terminal while the application is running.
The CAMEL_FILE appender was configured to exclusively capture the logs of the DailyWeatherRoute route, allowing for more organized management of the events generated by this specific functionality. The log format includes information such as date, time, thread, log level, and the corresponding message. This approach makes it easier to analyze events and possible failures in the application. The root logger maintains the general log level of the application as INFO, ensuring that important messages are recorded both in the console and in the log files.
For monitoring and analyzing the logs, the Promtail, Loki, and Grafana stack was used. Promtail is responsible for collecting the log files generated by Logback and sending them to Loki, a scalable log storage system developed by Grafana Labs. This allows logs to be queried efficiently and in a structured way within Grafana, allowing for detailed visualizations. This entire infrastructure was provisioned through a docker-compose file, ensuring a standardized and easy-to-manage environment.

To view the logs in Grafana, simply follow the installation steps in the usage section. Then use the query {job="camel_logs"}

![logs](https://github.com/JoaoFXs/climasync/blob/main/src/main/resources/static/logs.JPG)


