package com.google.sample.stockwatcher.server;

import java.util.HashMap;

import com.google.sample.stockwatcher.client.CityPopulation;

public class DataManager {
	private static HashMap<String, CityPopulation> availableCities = new HashMap<String, CityPopulation>();
	
	static{
		availableCities.put("new york", new CityPopulation("New York", 8391881, 1214));
		availableCities.put("stockholm", new CityPopulation("Stockholm", 1372565, 381));
		availableCities.put("oslo", new CityPopulation("Oslo", 925242, 289));
	}
	
	public static HashMap<String, CityPopulation> getAvailableCities(){
		return availableCities;
	}

}
