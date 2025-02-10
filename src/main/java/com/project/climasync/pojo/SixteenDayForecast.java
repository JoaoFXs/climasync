package com.project.climasync.pojo;

import java.util.List;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import lombok.Data;

@JacksonXmlRootElement(localName = "SixteenDayForecast")
@Data
public class SixteenDayForecast {

    @JacksonXmlProperty(localName = "Header")
    private Header header;

    @JacksonXmlProperty(localName = "Daily")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<Daily> daily;
}