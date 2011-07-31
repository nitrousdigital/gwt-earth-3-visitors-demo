package com.nitrous.gwtearth.visitors.server;

import java.util.HashMap;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.nitrous.gwtearth.visitors.client.VisitorService;
import com.nitrous.gwtearth.visitors.shared.CountryMetric;
import com.nitrous.gwtearth.visitors.shared.RpcSvcException;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class VisitorServiceImpl extends RemoteServiceServlet implements VisitorService {

	private AnalyticsQueryClient client;
	
	/**
	 * Retrieve information about visitor countries
	 * @return The visitor country information. A lookup from country name to <code>CountryMetric</code>.
	 * @throws RpcSvcException
	 */
	public HashMap<String, CountryMetric> fetchVisitorInformation() throws RpcSvcException {
		if (client == null) {
			client = new AnalyticsQueryClient();
		}
		return client.fetchVisitorInformation();
	}
	
}
