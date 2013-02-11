package com.google.sample.stockwatcher.client;

import java.io.Serializable;

public class CityAlreadyExistsException extends Exception implements Serializable {

private String cityName;
	
	public CityAlreadyExistsException() {
		// TODO Auto-generated constructor stub
	}
	
	public CityAlreadyExistsException(String cityName){
		this.cityName = cityName;
	}
	
	public String getCityName(){
		return cityName;
	}
	
}
