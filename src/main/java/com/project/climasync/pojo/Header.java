package com.project.climasync.pojo;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import lombok.Data;

@Data
@JacksonXmlRootElement(localName = "Header")
public class Header {

    @JacksonXmlProperty(localName = "Location_Information")
    private LocationInformation locationInformation;
}