package com.nitrous.gwtearth.visitors.client.geocode.jso;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

/**
 * <pre>
 *  "address_components" : [
 *             {
 *                "long_name" : "Brazil",
 *                "short_name" : "BR",
 *                "types" : [ "country", "political" ]
 *             }
 *          ],
 *          "formatted_address" : "Brazil",
 *          "geometry" : {
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
 *          },
 *          "types" : [ "country", "political" ]
 * </pre>
 * 
 * @author nick
 * 
 */
public final class GeoResult extends JavaScriptObject {
	protected GeoResult() {
	}

	public native JsArray<AddressComponent> getAddressComponents() /*-{
		return this.address_components;
	}-*/;

	public native Geometry getGeometry() /*-{
		return this.geometry;
	}-*/;
	
	public native String getFormattedAddress() /*-{
		return this.formatted_address;
	}-*/;
}
