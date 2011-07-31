package com.nitrous.gwtearth.visitors.client;

import java.util.HashMap;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.nitrous.gwtearth.visitors.shared.CountryMetric;

/**
 * The async counterpart of <code>VisitorService</code>.
 */
public interface VisitorServiceAsync {
	void fetchVisitorInformation(AsyncCallback<HashMap<String, CountryMetric>> callback);
}
