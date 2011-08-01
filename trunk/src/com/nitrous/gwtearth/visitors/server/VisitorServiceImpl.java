package com.nitrous.gwtearth.visitors.server;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import com.google.gdata.data.analytics.AccountEntry;
import com.google.gdata.data.analytics.AccountFeed;
import com.google.gdata.util.ServiceException;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.nitrous.gwtearth.visitors.client.VisitorService;
import com.nitrous.gwtearth.visitors.server.config.ServerConfig;
import com.nitrous.gwtearth.visitors.shared.CityMetric;
import com.nitrous.gwtearth.visitors.shared.CountryMetric;
import com.nitrous.gwtearth.visitors.shared.RpcSvcException;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class VisitorServiceImpl extends RemoteServiceServlet implements VisitorService {
    // Credentials for Client Login Authorization.
    private static final String CLIENT_USERNAME = ServerConfig.getInstance().getProperty("anayltics.account.id");
    private static final String CLIENT_PASS = ServerConfig.getInstance().getProperty("anayltics.account.password");
    // the table id for the gwt-earth-3 project
    private static final String TABLE_ID = ServerConfig.getInstance().getProperty("anayltics.account.table.id");


	private AnalyticsQueryClient client;
	
	/**
	 * Retrieve information about visitor countries
	 * @return The visitor city information. 
	 * @throws RpcSvcException
	 */
	public HashSet<CityMetric> fetchVisitorInformation() throws RpcSvcException {
		if (client == null) {
			client = new AnalyticsQueryClient();
		}
		return client.fetchDetailedVisitorInformation(CLIENT_USERNAME, CLIENT_PASS, TABLE_ID);
	}
	
    public static void main(String args[]) {
        AnalyticsQueryClient client = new AnalyticsQueryClient();
        try {
            // fetch account feed
            AccountFeed feed = client.getAccountFeed(CLIENT_USERNAME, CLIENT_PASS, 50);

            System.out.println(" ****** COUNTRY VISITOR INFORMATION ******* ");
            // show visitors from each country for each feed
            for (AccountEntry accountEntry : feed.getEntries()) {
                String tableId = accountEntry.getTableId().getValue();
                // show country metrics
                HashMap<String, CountryMetric> metrics = client.fetchVisitorInformation(CLIENT_USERNAME, CLIENT_PASS, tableId);
    
                // output
                SimpleDateFormat sdf = new SimpleDateFormat("MMM-dd-yyyy");
                for (Map.Entry<String, CountryMetric> entry : metrics.entrySet()) {
                    CountryMetric metric = entry.getValue();
                    String date = sdf.format(metric.getLastVisitDate());
                    System.out.println(
                            metric.getVisitCount() 
                            + " visit(s) from country " + metric.getCountry()
                            + " " + metric.getLatLon()
                            + ". Last visited on " + date);
                }
            }
            
            System.out.println(" ****** CITY VISITOR INFORMATION ******* ");
            // show visitors from each city for each feed
            for (AccountEntry accountEntry : feed.getEntries()) {
                String tableId = accountEntry.getTableId().getValue();
                // show city metrics
                HashSet<CityMetric> metrics = client.fetchDetailedVisitorInformation(CLIENT_USERNAME, CLIENT_PASS, tableId);
    
                // output
                SimpleDateFormat sdf = new SimpleDateFormat("MMM-dd-yyyy");
                for (CityMetric metric : metrics) {
                    String date = sdf.format(metric.getLastVisitDate());
                    System.out.println(metric.getVisitCount() + " visit(s) from" 
                            + " city " + metric.getCity()
                    		+ " country " + metric.getCountry()
                            + " location " + metric.getLatLon()
                            + ". Last visited on " + date);
                }
            }
        } catch (IOException e) {
            e.printStackTrace(System.err);
        } catch (ServiceException e) {
            e.printStackTrace(System.err);
        } catch (RpcSvcException e) {
            e.printStackTrace(System.err);
        }
    }

}
