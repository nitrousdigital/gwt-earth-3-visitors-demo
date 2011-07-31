package com.nitrous.gwtearth.visitors.shared;

import java.io.Serializable;
import java.util.Date;

public class CountryMetric implements Serializable {
	private static final long serialVersionUID = 4639193340989592000L;
	private String country;
	private Date lastVisitDate;
	private int visitCount;
	private LatLon latLong;

	public CountryMetric() {
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public Date getLastVisitDate() {
		return lastVisitDate;
	}

	public void setLastVisitDate(Date lastVisitDate) {
		this.lastVisitDate = lastVisitDate;
	}

	public int getVisitCount() {
		return visitCount;
	}

	public void setVisitCount(int visitCount) {
		this.visitCount = visitCount;
	}

	public LatLon getLatLon() {
		return latLong;
	}

	public void setLatLon(LatLon location) {
		this.latLong = location;
	}

}
