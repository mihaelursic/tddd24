package com.google.sample.stockwatcher.server;

import java.util.ArrayList;
import java.util.HashMap;

import java.util.Random;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.sample.stockwatcher.client.CityAlreadyExistsException;
import com.google.sample.stockwatcher.client.CityPopulation;
import com.google.sample.stockwatcher.client.CityPopulationService;
import com.google.sample.stockwatcher.client.DelistedException;

public class CityPopulationServiceImpl extends RemoteServiceServlet implements CityPopulationService {

	private final int MIN_POPULATION = 500;
	private final double MAX_POPULATION_CHANGE = 0.1; // 10 %
	private HashMap<String, CityPopulation> availableCities = null;

	
	@Override
	public ArrayList<CityPopulation> getPopulations(ArrayList<String> cityNames) throws DelistedException {
		
		if(availableCities == null){
			availableCities = DataManager.getAvailableCities();
		}
		
		Random rnd = new Random();
		
		ArrayList<CityPopulation> updatedPops = new ArrayList<CityPopulation>();
		for (int i = 0; i<cityNames.size(); i++){
			if (!isValidCity(cityNames.get(i))){
				throw new DelistedException(cityNames.get(i));
			}
			int newPopulation = (int)(availableCities.get(cityNames.get(i)).getPopulation()
					* ((rnd.nextDouble() * 2 - 1) * MAX_POPULATION_CHANGE + 1));
			
			if(newPopulation < MIN_POPULATION){
				break;
			}
			availableCities.get(cityNames.get(i)).setPopulation(newPopulation);
			updatedPops.add(availableCities.get(cityNames.get(i)));
		}
		
		return updatedPops;
		
	}


	@Override
	public boolean isValidCity(String cityName) {
		if(availableCities.containsKey(cityName.toLowerCase())){
			return true;
		}
		else{
			return false;
		}
	}


	@Override
	public void addNewAvailableCity(CityPopulation city) throws CityAlreadyExistsException {
		
		if(availableCities == null){
			availableCities = DataManager.getAvailableCities();
		}
		
		if(availableCities.containsKey(city.getName().toLowerCase())){
			throw new CityAlreadyExistsException(city.getName());
		}
		
		availableCities.put(city.getName().toLowerCase(), city);
		
	}

}
