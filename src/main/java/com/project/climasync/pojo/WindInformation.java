package com.project.climasync.pojo;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import lombok.Data;

@Data
public class WindInformation {

    @JacksonXmlProperty(localName = "WindSpeedMax")
    private Double windSpeedMax;

    @JacksonXmlProperty(localName = "WindGustsMax")
    private Double windGustsMax;

    @JacksonXmlProperty(localName = "WindDirectionDominant")
    private Integer windDirectionDominant;
}