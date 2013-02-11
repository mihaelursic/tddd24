package com.google.sample.stockwatcher.client;

import java.io.Serializable;

public class DelistedException extends Exception implements Serializable {
	
	private String cityName;
	
	public DelistedException(){
		
	}
	
	public DelistedException(String cityName){
		this.cityName = cityName;
	}
	
	public String getCityName(){
		return cityName;
	}

}
