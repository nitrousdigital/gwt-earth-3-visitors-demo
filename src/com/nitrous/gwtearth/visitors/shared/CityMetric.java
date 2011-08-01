package com.nitrous.gwtearth.visitors.shared;


public class CityMetric extends CountryMetric {
    private static final long serialVersionUID = -6894905075568445399L;
    
    private String city;
	
	public CityMetric() {
	}

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
