package com.nitrous.gwtearth.visitors.server;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.google.gdata.client.analytics.AnalyticsService;
import com.google.gdata.client.analytics.DataQuery;
import com.google.gdata.data.analytics.DataEntry;
import com.google.gdata.data.analytics.DataFeed;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ServiceException;
import com.nitrous.gwtearth.visitors.server.config.ServerConfig;
import com.nitrous.gwtearth.visitors.shared.CountryMetric;
import com.nitrous.gwtearth.visitors.shared.RpcSvcException;

/**
 * This class interacts with the Google Analytics Service to retrieve visitor information
 * @author nick
 *
 */
public class AnalyticsQueryClient {
	// Credentials for Client Login Authorization.
	private static final String CLIENT_USERNAME = ServerConfig.getInstance().getProperty("anayltics.account.id");
	private static final String CLIENT_PASS = ServerConfig.getInstance().getProperty("anayltics.account.password");
	// the table id for the gwt-earth-3 project
	private static final String TABLE_ID = ServerConfig.getInstance().getProperty("anayltics.account.table.id");

	public AnalyticsQueryClient() {		
	}
	
	/**
	 * Retrieve information about visitor countries
	 * @return The visitor country information. A lookup from country name to <code>CountryMetric</code>.
	 * @throws RpcSvcException
	 */
	public HashMap<String, CountryMetric> fetchVisitorInformation() throws RpcSvcException {
		try {
			// Service Object to work with the Google Analytics Data Export API.
			AnalyticsService analyticsService = new AnalyticsService("gaExportAPI_acctSample_v2.0");

			// Client Login Authorization.
			analyticsService.setUserCredentials(CLIENT_USERNAME, CLIENT_PASS);

			// fetch the country visit metrics
			HashMap<String, CountryMetric> metrics = getCountryVisits(analyticsService, TABLE_ID);
			return metrics;
		} catch (AuthenticationException e) {
			
			throw new RpcSvcException("Authentication failed : " + e.getMessage());
		} catch (IOException e) {
			throw new RpcSvcException("Network error trying to retrieve visitor information: "
					+ e.getMessage());
		} catch (ServiceException e) {
			throw new RpcSvcException("Analytics API responded with an error message: "
							+ e.getMessage());
		}
	}

	/**
	 * Retrieve number of visits from each country.
	 * 
	 * @param analyticsService Google Analytics service object that is
	 *        authorized through Client Login.
	 * @param tableId The ID of the table to be queried
	 */
	private static HashMap<String, CountryMetric> getCountryVisits(AnalyticsService analyticsService, String tableId)
			throws IOException, MalformedURLException, ServiceException {

		// Create a query using the DataQuery Object.
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String today = sdf.format(new Date(System.currentTimeMillis()));
		DataQuery query = new DataQuery(new URL("https://www.google.com/analytics/feeds/data"));
		query.setStartDate("2011-06-01");
		query.setEndDate(today);
		query.setDimensions("ga:date,ga:country");
		query.setMetrics("ga:visits");
		query.setSort("ga:date");
		query.setMaxResults(1000);
		query.setIds(tableId);

		// Make a request to the API.
		DataFeed dataFeed = analyticsService.getFeed(query.getUrl(), DataFeed.class);

		SimpleDateFormat feedFormat = new SimpleDateFormat("yyyyMMdd");
		
		// count the number of visits from each country and track the last visit date
		HashMap<String, CountryMetric> metrics = new HashMap<String, CountryMetric>();
		for (DataEntry entry : dataFeed.getEntries()) {
			try {
				String country = entry.stringValueOf("ga:country");
				if ("(not set)".equals(country)) {
					continue;
				}
				String dateStr = entry.stringValueOf("ga:date");
				int visits = Integer.parseInt(entry.stringValueOf("ga:visits"));
				Date date = feedFormat.parse(dateStr);
				System.out.println(sdf.format(date)+" visits="+visits+" country " + country);

				// find or create the country metric
				CountryMetric metric = metrics.get(country);
				if (metric == null) {
					metric = new CountryMetric();
					metric.setCountry(country);
					metrics.put(country, metric);
				}
				
				// check if last visit date is more recent
				Date lastDate = metric.getLastVisitDate();
				if (lastDate == null || lastDate.getTime() < date.getTime()) {
					metric.setLastVisitDate(date);
				}
				// update number of visits from this country
				metric.setVisitCount(metric.getVisitCount() + visits);
				
			} catch (Exception ex) {
				System.err.println("Failed to parse feed entry: "+ex.getMessage());
				ex.printStackTrace(System.err);
			}			
		}
		return metrics;
	}

	public static void main(String args[]) {
		AnalyticsQueryClient client = new AnalyticsQueryClient();
		try {
			HashMap<String, CountryMetric> metrics = client.fetchVisitorInformation();
			
			// output
			SimpleDateFormat sdf = new SimpleDateFormat("MMM-dd-yyyy");
			for (Map.Entry<String, CountryMetric> entry : metrics.entrySet()) {
				CountryMetric metric = entry.getValue();
				String date = sdf.format(metric.getLastVisitDate());
				System.out.println(metric.getVisitCount() + " visit(s) from country "+metric.getCountry()+". Last visited on "+date);
			}
			
		} catch (RpcSvcException e) {
			e.printStackTrace(System.err);
		}
	}
	
}
