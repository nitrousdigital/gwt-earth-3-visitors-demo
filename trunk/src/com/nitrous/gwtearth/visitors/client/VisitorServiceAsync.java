package com.nitrous.gwtearth.visitors.client;

import java.util.HashSet;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.nitrous.gwtearth.visitors.shared.CityMetric;

/**
 * The async counterpart of <code>VisitorService</code>.
 */
public interface VisitorServiceAsync {
	void fetchVisitorInformation(AsyncCallback<HashSet<CityMetric>> callback);
}
