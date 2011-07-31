package com.nitrous.gwtearth.visitors.client;

import java.util.HashMap;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.nitrous.gwtearth.visitors.shared.CountryMetric;
import com.nitrous.gwtearth.visitors.shared.RpcSvcException;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("rpc")
public interface VisitorService extends RemoteService {
	/**
	 * Retrieve information about visitor countries
	 * @return The visitor country information. A lookup from country name to <code>CountryMetric</code>.
	 * @throws RpcSvcException
	 */
	public HashMap<String, CountryMetric> fetchVisitorInformation() throws RpcSvcException;
}
