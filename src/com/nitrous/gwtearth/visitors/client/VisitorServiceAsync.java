package com.nitrous.gwtearth.visitors.client;

import java.util.HashSet;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.nitrous.gwtearth.visitors.shared.AccountProfile;
import com.nitrous.gwtearth.visitors.shared.CityMetric;

/**
 * The async counterpart of <code>VisitorService</code>.
 */
public interface VisitorServiceAsync {
    /**
     * Retrieve information about visitor cities from the configured default account profile
     * @param callback The callback to be notified with the visitor city information for the configured default account profile
     */
	void fetchCityVisitorInformation(AsyncCallback<HashSet<CityMetric>> callback);

	/**
	 * Retrieve the account profiles accessible from the configured google analytics account
	 * @param maxQuerySize The maximum number of results to return
	 * @param callback The callback to be notified with the results
	 */
	void getAccountProfiles(int maxQuerySize, AsyncCallback<HashSet<AccountProfile>> callback);

	/**
	 * Retrieve information about visitor cities from the specified table id
	 * @param tableId The ID of the google analytics account profile to query 
	 * @param callback The callback to be notified with the visitor city information about visitor countries from the specified table id
	 */
	void fetchCityVisitorInformation(String tableId, AsyncCallback<HashSet<CityMetric>> callback);
}
