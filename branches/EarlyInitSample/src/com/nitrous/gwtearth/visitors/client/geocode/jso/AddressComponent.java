package com.nitrous.gwtearth.visitors.client.geocode.jso;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;

/**
 * <pre>
 * "address_components" : [
 *             {
 *                "long_name" : "Brazil",
 *                "short_name" : "BR",
 *                "types" : [ "country", "political" ]
 *             }
 *          ]
 * </pre>
 * 
 * @author nick
 * 
 */
public final class AddressComponent extends JavaScriptObject {
	protected AddressComponent() {
	}

	public native String getLongName() /*-{
		return this.long_name;
	}-*/;

	public native String getShortName() /*-{
		return this.short_name;
	}-*/;

	public native JsArrayString getTypes() /*-{
		return this.types;
	}-*/;

	public boolean isCountry() {
		JsArrayString types = getTypes();
		if (types != null) {
			for (int i = 0, len = types.length(); i < len; i++) {
				if ("country".equalsIgnoreCase(types.get(i))) {
					return true;
				}
			}
		}
		return false;
	}
}
