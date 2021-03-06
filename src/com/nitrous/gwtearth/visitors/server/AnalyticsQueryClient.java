package com.nitrous.gwtearth.visitors.server;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

import com.google.gdata.client.analytics.AnalyticsService;
import com.google.gdata.client.analytics.DataQuery;
import com.google.gdata.data.analytics.AccountFeed;
import com.google.gdata.data.analytics.DataEntry;
import com.google.gdata.data.analytics.DataFeed;
import com.google.gdata.data.analytics.Dimension;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ServiceException;
import com.nitrous.gwtearth.visitors.server.config.ServerConfig;
import com.nitrous.gwtearth.visitors.shared.CityMetric;
import com.nitrous.gwtearth.visitors.shared.LatLon;
import com.nitrous.gwtearth.visitors.shared.RpcSvcException;

/**
 * This class interacts with the Google Analytics Service to retrieve visitor
 * information
 * 
 * @author nick
 * 
 */
public class AnalyticsQueryClient {
    public AnalyticsQueryClient() {
    }

    /**
     * Retrieve information about visitor cities
     * 
     * @param user the google analytics account user ID/email address.
     * @param password the google analytics account password
     * @param tableId The ID of the analytics feed
     * @return The visitor city information. 
     * @throws RpcSvcException
     */
    public HashSet<CityMetric> fetchVisitorInformation(String user, String password, String tableId) throws RpcSvcException {
        try {
            // Service Object to work with the Google Analytics Data Export API.
            AnalyticsService analyticsService = new AnalyticsService("gaExportAPI_acctSample_v2.0");

            // Client Login Authorization.
            analyticsService.setUserCredentials(user, password);

            // fetch the country visit metrics
            HashSet<CityMetric> metrics = getCityVisits(analyticsService, tableId);
            return metrics;
        } catch (AuthenticationException e) {

            throw new RpcSvcException("Authentication failed : " + e.getMessage());
        } catch (IOException e) {
            throw new RpcSvcException("Network error trying to retrieve visitor information: " + e.getMessage());
        } catch (ServiceException e) {
            throw new RpcSvcException("Analytics API responded with an error message: " + e.getMessage());
        }
    }
    
    /**
     * Retrieve number of visits from each city.
     * 
     * @param analyticsService
     *            Google Analytics service object that is authorized through
     *            Client Login.
     * @param tableId
     *            The ID of the table to be queried
     * @return the HashSet of CityMetric describing the number of visits and date of last visit from each city.
     */
    private static HashSet<CityMetric> getCityVisits(AnalyticsService analyticsService, String tableId)
            throws IOException, MalformedURLException, ServiceException {

        // Create a query using the DataQuery Object.
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String today = sdf.format(new Date(System.currentTimeMillis()));
        DataQuery query = new DataQuery(new URL("https://www.google.com/analytics/feeds/data"));
        
        String startDate = ServerConfig.getInstance().getProperty("query.start.date");
        if (startDate == null || startDate.trim().length() == 0) {
        	throw new ServiceException("Missing start date configuration property");
        }
        query.setStartDate(startDate);
        query.setEndDate(today);
        query.setDimensions("ga:date,ga:country,ga:city,ga:latitude,ga:longitude");
        query.setMetrics("ga:visits");
        query.setSort("ga:date");
        query.setMaxResults(10000);
        query.setIds(tableId);

        // Make a request to the API.
        DataFeed dataFeed = analyticsService.getFeed(query.getUrl(), DataFeed.class);

        SimpleDateFormat feedFormat = new SimpleDateFormat("yyyyMMdd");

        // determine the city & country to be removed from the results
        // both city and country must be specified for a filter to be enabled
        String hideCountry = ServerConfig.getInstance().getProperty("filter.country");
        if (hideCountry != null && hideCountry.trim().length() == 0) {
        	hideCountry = null;
        }
        String hideCity = null;
        if (hideCountry != null) {
        	hideCity = ServerConfig.getInstance().getProperty("filter.city");
        	if (hideCity == null || hideCity.trim().length() == 0) {
        		hideCountry = null;
        		hideCity = null;
        	}
        }
        
        // count the number of visits from each country+city and track the last visit date
        HashMap<String, CityMetric> metrics = new HashMap<String, CityMetric>();
        for (DataEntry entry : dataFeed.getEntries()) {
            try {
                String country = entry.stringValueOf("ga:country");
                if ("(not set)".equals(country)) {
                    continue;
                }
                
                String city = entry.stringValueOf("ga:city");
                
                // support filtered country/city filter
                if (hideCountry != null) {
                	if (hideCountry.equalsIgnoreCase(country) && hideCity.equalsIgnoreCase(city)) {
                		continue;
                	}
                }
                
                LatLon location = getLatLon(entry);
                Double latitude = location != null ? location.getLatitude() : null;
                Double longitude = location != null ? location.getLongitude() : null;
                
                String dateStr = entry.stringValueOf("ga:date");
                int visits = Integer.parseInt(entry.stringValueOf("ga:visits"));
                Date date = feedFormat.parse(dateStr);
                System.out.println(sdf.format(date) 
                        + " visits=" + visits 
                        + " country=" + country
                        + " city=" + city
                        + " latitide= " + latitude
                        + " longitude=" + longitude);

                // find or create the country metric
                String key = country+":"+city+":"+latitude+":"+longitude;
                CityMetric metric = metrics.get(key);
                if (metric == null) {
                    metric = new CityMetric();
                    metric.setCountry(country);
                    metric.setCity(city);
                    metric.setLatLon(location);
                    metrics.put(key, metric);
                }

                // check if last visit date is more recent
                Date lastDate = metric.getLastVisitDate();
                if (lastDate == null || lastDate.getTime() < date.getTime()) {
                    metric.setLastVisitDate(date);
                }
                // update number of visits from this country/city
                metric.setVisitCount(metric.getVisitCount() + visits);

            } catch (Exception ex) {
                System.err.println("Failed to parse feed entry: " + ex.getMessage());
                ex.printStackTrace(System.err);
            }
        }
        
        HashSet<CityMetric> entries = new HashSet<CityMetric>();
        entries.addAll(metrics.values());
        return entries;
    }
    
    private static LatLon getLatLon(DataEntry entry) {
        Dimension lat = entry.getDimension("ga:latitude");
        Dimension lng = entry.getDimension("ga:longitude");
        Double latitude = null;
        Double longitude = null;
        LatLon location = null;
        try {
            String latStr = lat.getValue();
            latitude = Double.parseDouble(latStr);
            
            String lonStr = lng.getValue();
            longitude = Double.parseDouble(lonStr);
            
            location = new LatLon(latitude, longitude);
        } catch (Exception ex) {
        }
        return location;
    }
    
    /**
     * Request 50 Google Analytics profiles the authorized user has access to
     * and display Account Name, Profile Name, Profile ID and Table ID for each
     * profile. The Table ID value is used to make requests to the Data Feed.
     * 
     * @param user the google analytics account user id
     * @param password the google analytics account password
     * @param limit The maximum number of results to retrieve
     * @param {AnalyticsService} Google Analytics service object that is
     *        authorized through Client Login.
     * @throws ServiceException 
     * @throws IOException 
     */
    public AccountFeed getAccountFeed(String user, String password, int limit) throws IOException, ServiceException {
        // Service Object to work with the Google Analytics Data Export API.
        AnalyticsService analyticsService = new AnalyticsService("gaExportAPI_acctSample_v2.0");

        // Client Login Authorization.
        analyticsService.setUserCredentials(user, password);

        // Construct query from a string.
        URL queryUrl = new URL("https://www.google.com/analytics/feeds/accounts/default?max-results="+limit);

        // Make request to the API.
        AccountFeed accountFeed = analyticsService.getFeed(queryUrl, AccountFeed.class);

        // Output the data to the screen.
//        System.out.println("-------- Account Feed Results --------");
//        for (AccountEntry entry : accountFeed.getEntries()) {
//            System.out.println("\nAccount Name  = " + entry.getProperty("ga:accountName") + "\nProfile Name  = "
//                    + entry.getTitle().getPlainText() + "\nProfile Id    = " + entry.getProperty("ga:profileId")
//                    + "\nTable Id      = " + entry.getTableId().getValue());
//        }
        return accountFeed;
    }

}
