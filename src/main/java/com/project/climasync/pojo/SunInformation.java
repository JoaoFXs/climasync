package com.project.climasync.pojo;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import lombok.Data;

@Data
public class SunInformation {

    @JacksonXmlProperty(localName = "Sunrise")
    private String sunrise;

    @JacksonXmlProperty(localName = "Sunset")
    private String sunset;

    @JacksonXmlProperty(localName = "SunshineDuration")
    private Double sunshineDuration;

    @JacksonXmlProperty(localName = "DaylightDuration")
    private Double daylightDuration;

    @JacksonXmlProperty(localName = "ApparentTemperatureMin")
    private Double apparentTemperatureMin;
}