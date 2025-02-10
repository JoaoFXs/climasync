package com.project.climasync.pojo;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import lombok.Data;

@Data
public class LocationInformation {

    @JacksonXmlProperty(localName = "Namelocation")
    private String nameLocation;

    @JacksonXmlProperty(localName = "DetailLocation")
    private String detailLocation;

    @JacksonXmlProperty(localName = "Timezone")
    private String timezone;

    @JacksonXmlProperty(localName = "Latitude")
    private Double latitude;

    @JacksonXmlProperty(localName = "Longitude")
    private Double longitude;
}