package com.project.climasync.pojo;

import java.sql.Date;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import lombok.Data;

@Data
public class Daily {

    @JacksonXmlProperty(localName = "Time")
    private Date time;

    @JacksonXmlProperty(localName = "SunInformation")
    private SunInformation sunInformation;

    @JacksonXmlProperty(localName = "WeatherInformation")
    private WeatherInformation weatherInformation;

    @JacksonXmlProperty(localName = "WindInformation")
    private WindInformation windInformation;

    @JacksonXmlProperty(localName = "PrecipitationInformation")
    private PrecipitationInformation precipitationInformation;

    @JacksonXmlProperty(localName = "UVInformation")
    private UVInformation uvInformation;

    @JacksonXmlProperty(localName = "Others")
    private Others others;
}