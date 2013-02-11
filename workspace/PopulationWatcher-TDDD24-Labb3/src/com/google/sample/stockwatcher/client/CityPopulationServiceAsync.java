package com.google.sample.stockwatcher.client;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface CityPopulationServiceAsync {

	void getPopulations(ArrayList<String> cityNames, AsyncCallback<ArrayList<CityPopulation>> callback);

	void isValidCity(String cityName, AsyncCallback<Boolean> callback);

	void addNewAvailableCity(CityPopulation city, AsyncCallback<Void> callback);

}
