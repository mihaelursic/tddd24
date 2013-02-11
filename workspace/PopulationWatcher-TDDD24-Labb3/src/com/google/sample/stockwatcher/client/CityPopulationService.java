package com.google.sample.stockwatcher.client;

import java.util.ArrayList;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("cityPopulations")
public interface CityPopulationService extends RemoteService {

  public ArrayList<CityPopulation> getPopulations(ArrayList<String> cityNames) throws DelistedException;
  
  public boolean isValidCity (String cityName);
  
  public void addNewAvailableCity(CityPopulation city) throws CityAlreadyExistsException;
}