package com.project.climasync.pojo;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import lombok.Data;

@Data
public class UVInformation {

    @JacksonXmlProperty(localName = "UvIndexMax")
    private Double uvIndexMax;

    @JacksonXmlProperty(localName = "UvIndexClearSkyMax")
    private Double uvIndexClearSkyMax;

    @JacksonXmlProperty(localName = "ShortWaveRadiationSum")
    private Double shortWaveRadiationSum;
}