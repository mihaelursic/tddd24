package com.google.sample.stockwatcher.client;

import java.io.Serializable;

public class CityPopulation implements Serializable {

	private String name;
	private int population;
	private int area;

	public CityPopulation() {
	}

	public CityPopulation(String name, int population, int area) {
		this.name = name;
		this.population = population;
		this.area = area;
	}

	public String getName() {
		return this.name;
	}

	public int getPopulation() {
		return this.population;
	}

	public int getArea() {
		return this.area;
	}

	public void setName(String symbol) {
		this.name = symbol;
	}

	public void setPopulation(int price) {
		this.population = price;
	}

	public void setArea(int change) {
		this.area = change;
	}
}
