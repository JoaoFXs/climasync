package com.project.climasync.utils;

public enum ToolBoxEnum {
	
	XSLT("xslt:classpath:schema/"),
	VALIDATOR("validator:classpath:validator/");
	
	String value;
	ToolBoxEnum(String value){
		this.value = value;
	}
	
	public String file(String file) {
		return value + file;
	}
}
