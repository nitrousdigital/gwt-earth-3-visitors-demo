package com.nitrous.gwtearth.visitors.shared;

import java.io.Serializable;

public class LatLon implements Serializable {
	private static final long serialVersionUID = 1372971529995778510L;
	private double latitude;
	private double longitude;
	public LatLon() {
	}
	public LatLon(double latitude, double longitude) {
		super();
		this.latitude = latitude;
		this.longitude = longitude;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
    
    @Override
	public String toString() {
		return "LatLon [latitude=" + latitude + ", longitude=" + longitude
				+ "]";
	}
	
}
