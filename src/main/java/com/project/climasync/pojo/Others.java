package com.project.climasync.pojo;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import lombok.Data;

@Data
public class Others {

    @JacksonXmlProperty(localName = "Evapotranspiration")
    private Double evapotranspiration;

    @JacksonXmlProperty(localName = "SnowfallSum")
    private Double snowfallSum;
}