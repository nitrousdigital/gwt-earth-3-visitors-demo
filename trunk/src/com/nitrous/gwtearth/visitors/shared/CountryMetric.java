package com.nitrous.gwtearth.visitors.shared;


public class CountryMetric extends AbstractVisitorMetric {
    private static final long serialVersionUID = -7066953015476369607L;
    
    private String country;

	public CountryMetric() {
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}
}
