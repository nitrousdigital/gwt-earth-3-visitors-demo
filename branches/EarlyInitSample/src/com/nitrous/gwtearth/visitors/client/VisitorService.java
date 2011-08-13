package com.nitrous.gwtearth.visitors.client;

import java.util.HashSet;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.nitrous.gwtearth.visitors.shared.AccountProfile;
import com.nitrous.gwtearth.visitors.shared.CityMetric;
import com.nitrous.gwtearth.visitors.shared.RpcSvcException;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("rpc")
public interface VisitorService extends RemoteService {
    
	/**
	 * Retrieve information about visitor cities from the specified table id 
	 * @return The visitor city information about visitor countries from the specified table id
	 * @throws RpcSvcException
	 */
	public HashSet<CityMetric> fetchCityVisitorInformation(String tableId) throws RpcSvcException;
	
	/**
	 * Fetch all accessible account profiles
	 * @param maxQuerySize The maximum number of profiles to retrieve
	 * @return all accessible account profiles
	 * @throws RpcSvcException
	 */
	public HashSet<AccountProfile> getAccountProfiles(int maxQuerySize) throws RpcSvcException;
    
    
}
