# ClimaSync - Integration Platform for Meteorological Data Capture and Distribution


ClimaSync is an integration platform developed with Apache Camel, designed to capture weather data from an external API (OpenWeather) and efficiently distribute it to consumer systems. By integrating with a weather API such as OpenMeteo, the platform collects crucial weather information, including temperature, humidity, and forecasts. 
This data is processed, validated, and stored in a database and ActiveMQ queues, ensuring its availability for future queries. The ClimaSync architecture aims to provide a continuous and automated flow of weather data, ensuring high availability, resilience, and easy access to information. As a result, the platform supports strategic and operational decisions across various applications.

## Table of Contents

- [Background](#background)


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