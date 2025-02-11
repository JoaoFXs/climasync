package com.project.climasync.utils;

public enum Logs {
	
	V001("LOGV001"),
	V100("LOGV100"),
	V002("LOGV002"),
	V102("LOGV102"),
	V003("LOGV002"),
	V103("LOGV102"),
	V004("LOGV004"),
	V104("LOGV104"),
	E001("ERROR001"),
	E002("ERROR002"),
	E003("ERROR003")
	;
	
	String value;
	
	Logs(String value){
		this.value = value;
	}
	
	public String message(String message) {
		return value + " - " + message;
	}
}
