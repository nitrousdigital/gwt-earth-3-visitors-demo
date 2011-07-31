package com.nitrous.gwtearth.visitors.client.geocode.jso;

import com.google.gwt.core.client.JavaScriptObject;
import com.nitrous.gwtearth.visitors.shared.LatLon;

/**
 * <pre>
 * "geometry" : {
 *             "bounds" : {
 *                "northeast" : {
 *                   "lat" : 5.2716020,
 *                   "lng" : -34.10160
 *                },
 *                "southwest" : {
 *                   "lat" : -34.08910,
 *                   "lng" : -73.9828170
 *                }
 *             },
 *             "location" : {
 *                "lat" : -14.2350040,
 *                "lng" : -51.925280
 *             },
 *             "location_type" : "APPROXIMATE",
 *             "viewport" : {
 *                "northeast" : {
 *                   "lat" : 8.43715270,
 *                   "lng" : -19.14207610
 *                },
 *                "southwest" : {
 *                   "lat" : -34.86106250,
 *                   "lng" : -84.70848390
 *                }
 *             }
 *          }
 * </pre>
 * 
 * @author nick
 * 
 */
public final class Geometry extends JavaScriptObject {
	protected Geometry() {
	}

	public LatLon getLocation() {
		return new LatLon(getLocationLat(), getLocationLng());
	}

	private native double getLocationLat() /*-{
		return this.location.lat();
	}-*/;

	private native double getLocationLng() /*-{
		return this.location.lng();
	}-*/;
}
