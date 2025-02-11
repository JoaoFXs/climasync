package com.project.climasync.config;

import lombok.Data;

public enum ConfigBroker {
	
	JMSQUEUE("jms");
	
	private String value;
	
	ConfigBroker(String value) {
		this.value=value;
	}
	
	public String queue(String name) {
		return value + ":queue:" + name;
	}
	public String queueLocation(String name, String location) {
		return value + ":queue:" + name + location;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	
	

}
