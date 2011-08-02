package com.nitrous.gwtearth.visitors.server;

import java.text.SimpleDateFormat;
import java.util.HashSet;

import com.google.gdata.data.analytics.AccountEntry;
import com.google.gdata.data.analytics.AccountFeed;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.nitrous.gwtearth.visitors.client.VisitorService;
import com.nitrous.gwtearth.visitors.server.config.ServerConfig;
import com.nitrous.gwtearth.visitors.shared.AccountProfile;
import com.nitrous.gwtearth.visitors.shared.CityMetric;
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
	 * Retrieve information about visitor cities from the table id specified in config.properties
	 * @return The visitor city information about visitor countries from the table id specified in config.properties
	 * @throws RpcSvcException
	 */
	public HashSet<CityMetric> fetchCityVisitorInformation() throws RpcSvcException {
		if (TABLE_ID == null) {
			throw new RpcSvcException("Server configuration is missing.");
		}
		return fetchCityVisitorInformation(TABLE_ID); 
	}

	/**
	 * Retrieve information about visitor cities from the specified table id 
	 * @return The visitor city information about visitor countries from the specified table id
	 * @throws RpcSvcException
	 */
	public HashSet<CityMetric> fetchCityVisitorInformation(String tableId) throws RpcSvcException {
		validateUserConfig();
		return getQueryClient().fetchDetailedVisitorInformation(CLIENT_USERNAME, CLIENT_PASS, tableId);
	}
	
	private AnalyticsQueryClient getQueryClient() {
		if (client == null) {
			client = new AnalyticsQueryClient();
		}
		return client;
	}
	
	/**
	 * Fetch all accessible account profiles
	 * @param maxQuerySize The maximum number of profiles to retrieve
	 * @return all accessible account profiles
	 * @throws RpcSvcException
	 */
	public HashSet<AccountProfile> getAccountProfiles(int maxQuerySize) throws RpcSvcException {
		validateUserConfig();
		HashSet<AccountProfile> profiles = new HashSet<AccountProfile>();
		
        // fetch account feed
        try {
			AccountFeed feed = getQueryClient().getAccountFeed(CLIENT_USERNAME, CLIENT_PASS, 50);
			
            // show visitors from each country for each feed
			System.out.println("Found " + feed.getEntries().size() + " account profiles:");
            for (AccountEntry entry : feed.getEntries()) {
            	String accountName = entry.getProperty("ga:accountName");
            	String profileId = entry.getProperty("ga:profileId");
            	String profileName = entry.getTitle().getPlainText();
            	String tableId = entry.getTableId().getValue();
            	
                System.out.println("\nAccount Name  = " + accountName 
                		+ "\nProfile Name  = " + profileName 
                		+ "\nProfile Id    = " + profileId
                        + "\nTable Id      = " + tableId);
                
                AccountProfile profile = new AccountProfile(accountName, profileName, profileId, tableId);
                profiles.add(profile);
            }
		} catch (Exception e) {
			System.err.println("Failed to load account profiles");
			e.printStackTrace(System.err);
			throw new RpcSvcException("Failed to retrieve account profiles");
		}
        
        return profiles;
	}

	/**
	 * Ensure user and password have been specified in config.properties
	 * @throws RpcSvcException
	 */
	private void validateUserConfig() throws RpcSvcException {
		if (CLIENT_USERNAME == null || CLIENT_PASS == null) {
			throw new RpcSvcException("Server configuration is missing.");
		}
	}
	
	
    public static void main(String args[]) {
    	VisitorServiceImpl svc = new VisitorServiceImpl();
        try {
            // fetch account feed
        	HashSet<AccountProfile> profiles = svc.getAccountProfiles(50);

            // show visitors from each city for each feed
            for (AccountProfile profile : profiles) {
            	String profileName = profile.getProfileName();
                System.out.println(" ****** CITY VISITOR INFORMATION FOR "+profileName+" ******* ");
                String tableId = profile.getTableId();
                // show city metrics
                HashSet<CityMetric> metrics = svc.fetchCityVisitorInformation(tableId);
    
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
        } catch (RpcSvcException e) {
            e.printStackTrace(System.err);
        }
    }

}
