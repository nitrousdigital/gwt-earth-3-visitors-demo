package com.nitrous.gwtearth.visitors.client.geocode.jso;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

public final class GeoResultList extends JavaScriptObject {
	protected GeoResultList() {
	}
	
	/**
	 * Retrieve the array of results in this list
	 * @return The results in this list
	 */
	public native JsArray<GeoResult> getEntries() /*-{
		return this.results;
	}-*/;
	
	public native String getStatus() /*-{
		return this.status;
	}-*/;
	
	/**
	 * Evaluate the specified JSON into an instance of GeoResult
	 * @param json The JSON to evaluate
	 * @return The GeoResult evaluated from the specified JSON
	 */
    public static native GeoResultList eval(String json) /*-{
		var ret = eval('(' + json + ')');
		return ret;
	}-*/;
	
}
