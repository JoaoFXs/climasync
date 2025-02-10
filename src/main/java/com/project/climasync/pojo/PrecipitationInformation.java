package com.project.climasync.pojo;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import lombok.Data;

@Data
public class PrecipitationInformation {

    @JacksonXmlProperty(localName = "RainSum")
    private Double rainSum;

    @JacksonXmlProperty(localName = "PrecipitationSum")
    private Double precipitationSum;

    @JacksonXmlProperty(localName = "PrecipitationProbabilityMax")
    private Integer precipitationProbabilityMax;

    @JacksonXmlProperty(localName = "PrecipitationHours")
    private Double precipitationHours;

    @JacksonXmlProperty(localName = "ShowerSum")
    private Double showerSum;
}