package com.project.climasync.pojo;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import lombok.Data;

@Data
public class WeatherInformation {

    @JacksonXmlProperty(localName = "ApparentTemperatureMin")
    private Double apparentTemperatureMin;

    @JacksonXmlProperty(localName = "ApparentTemperatureMax")
    private Double apparentTemperatureMax;

    @JacksonXmlProperty(localName = "TemperatureMin")
    private Double temperatureMin;

    @JacksonXmlProperty(localName = "TemperatureMax")
    private Double temperatureMax;
}