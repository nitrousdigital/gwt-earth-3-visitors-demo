package com.nitrous.gwtearth.visitors.client.geocode;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.nitrous.gwtearth.visitors.client.geocode.jso.GeoResult;
import com.nitrous.gwtearth.visitors.client.geocode.jso.Geometry;
import com.nitrous.gwtearth.visitors.shared.CityMetric;
import com.nitrous.gwtearth.visitors.shared.LatLon;

/**
 * A class that performs a lookup to retrieve a lat/long for a given country name
 * @author nick
 *
 */
public class GeoCoder {
	// a cache of known locations - lookup from address to LatLon
	private Map<String, LatLon> cache = new HashMap<String, LatLon>();
	
    /** The queue of metrics to be processed */
    private Set<CityMetric> queue = new HashSet<CityMetric>();
    /** The set of metrics that have been processed */
    private Set<CityMetric> processed = new HashSet<CityMetric>();
    
    /** The callback to be notified when processing is complete */
    private AsyncCallback<Set<CityMetric>> callback;
    
    /**
     * Construct a GeoCoder for the specified set of metrics
     * @param metrics The metrics to be geo-coded
     */
    public GeoCoder(Collection<CityMetric> metrics) {
    	initCache();
    	queue.addAll(metrics);
    }
    
    /**
     * Help protect our geocoding quota by populating the cache with known locations
     */
    private void initCache() {
    	cache.put("Canada", new LatLon(56.130366D, -106.34677099999999D));
    	cache.put("Taiwan", new LatLon(23.69781D, 120.96051499999999D));
    	cache.put("Greece", new LatLon(39.074208D, 21.824311999999964D));
    	cache.put("Ukraine", new LatLon(48.379433D, 31.165579999999977D));
    	cache.put("Malaysia", new LatLon(4.210484D, 101.97576600000002D));
    	cache.put("Brazil", new LatLon(-14.235004D, -51.92527999999999D));
    	cache.put("Switzerland", new LatLon(46.818188D, 8.227511999999933D));
    	cache.put("Isle of Man", new LatLon(54.236107D, -4.548055999999974D));
    	cache.put("Italy", new LatLon(41.87194D, 12.567379999999957D));
    	cache.put("Indonesia", new LatLon(-0.789275D, 113.92132700000002D));
    	cache.put("Sweden", new LatLon(60.12816100000001D, 18.643501000000015D));
    	cache.put("China", new LatLon(35.86166D, 104.19539699999996D));
    	cache.put("United Kingdom", new LatLon(55.378051D, -3.43597299999999D));
    	cache.put("Spain", new LatLon(40.46366700000001D, -3.7492200000000366D));
    	cache.put("United States", new LatLon(37.09024D, -95.71289100000001D));
    	cache.put("Turkey", new LatLon(38.963745D, 35.243322000000035D));
    	cache.put("Germany", new LatLon(51.165691D, 10.451526000000058D));
    	cache.put("Netherlands", new LatLon(52.132633D, 5.2912659999999505D));
    	cache.put("India", new LatLon(20.593684D, 78.96288000000004D));
    	cache.put("Czech Republic", new LatLon(49.81749199999999D, 15.472962000000052D));
    	cache.put("Bulgaria", new LatLon(42.733883D, 25.485829999999964D));
    	cache.put("Poland", new LatLon(51.919438D, 19.14513599999998D));
    }

    /**
     * Begin the geo-encoding process
     * @param callback The callback to be notified when all entries in <code>metrics</code> have been populated with their geo-coded location
     */
    public void start(AsyncCallback<Set<CityMetric>> callback) {
    	this.callback = callback;
    	processNext();
    }
    
    /**
     * Process the next item in the queue if available or notify the callback of completion
     */
    private void processNext() {
    	if (queue.size() > 0) {
    		processNext(queue.iterator().next());
    	} else {
    		endSuccess();
    	}
    }
    
    /**
     * Cleanup and notify the callback of the result
     */
    private void endSuccess() {
    	if (callback != null) {
    		Set<CityMetric> result = new HashSet<CityMetric>(processed);
    		callback.onSuccess(result);
    	}
    	dispose();
    }
    
    /**
     * Cleanup and notify the callback of the failure
     * @param t The cause of failure
     */
    private void endFailure(Throwable t) {
    	if (callback != null) {
    		callback.onFailure(t);
    	}
    	dispose();
    }
    
    /**
     * Release all resources
     */
    private void dispose() {
    	callback = null;
    	
    	cache.clear();
    	cache = null;
    	
    	processed.clear();
    	processed = null;
    	
    	queue.clear();
    	queue = null;
    }
    

    /**
     * Failed to process the specified metric. Remove from the queue and process the next item
     * @param metric The metric that failed to be geo-coded
     * @param cause The cause of failure
     */
    private void onFailure(CityMetric metric, Throwable cause) {
    	GWT.log("Failed to geo-code location "+metric.getCity()+"," + metric.getCountry(), cause);
    	queue.remove(metric);
    	processNext();
    }
    
    /**
     * Finished processing the specified metric. Remove from the queue and process the next item
     * @param metric The metric that has completed processing
     */
    private void onSuccess(CityMetric metric) {
    	queue.remove(metric);
		processed.add(metric);
    	processNext();
    }
    
    private void processNext(CityMetric metric) {
        LatLon latLon = metric.getLatLon();
        
        // ignore (not set) city location and geo-code by country
        if (latLon != null) {
            if ("(not set)".equals(metric.getCity()) 
                    && latLon.getLatitude() == 0 
                    && latLon.getLongitude() == 0) {
                latLon = null;
                metric.setLatLon(null);
            }
        }
        
        if (latLon != null) {
            // already geocoded - skip
            onSuccess(metric);
            return;
        }
        
        // geo-code using country
        String address = getGeoAddress(metric);
    	latLon = cache.get(address);
    	if (latLon != null) {
    	    // found location in cache
    		metric.setLatLon(latLon);
    		onSuccess(metric);
    	} else {
    	    // need to geocode
    		geocode(this, metric, address);
    	}
    }
    
    private static String getGeoAddress(CityMetric metric) {
        if (metric == null) {
            return null;
        }
        String country = metric.getCountry();
        if (country == null){
            return null;
        }
        String city = metric.getCity();
        
        StringBuffer address = new StringBuffer();
        if (city != null && !"(not set)".equals(city)) {
            address.append(city);
            address.append(",");
        }
        address.append(country);
        return address.toString();
    }
    
    private static native void geocode(GeoCoder instance, final CityMetric metric, String address) /*-{
   		var geocoder = new $wnd.google.maps.Geocoder();
	    if (geocoder) {
	       geocoder.geocode(
	           {'address': address }, 
	           function (results, status) {
	              if (status == $wnd.google.maps.GeocoderStatus.OK) {
	              	instance.@com.nitrous.gwtearth.visitors.client.geocode.GeoCoder::processGeoCodeResult(Lcom/nitrous/gwtearth/visitors/shared/CityMetric;Lcom/google/gwt/core/client/JsArray;)(metric, results);
	              } else {
	              	  if (status == $wnd.google.maps.GeocoderStatus.OVER_QUERY_LIMIT) {
	              	      instance.@com.nitrous.gwtearth.visitors.client.geocode.GeoCoder::onGeocodeOverQueryLimitFailure()();
	              	  } else {	
 					  	  instance.@com.nitrous.gwtearth.visitors.client.geocode.GeoCoder::onGeocodeFailure(Lcom/nitrous/gwtearth/visitors/shared/CityMetric;)(metric);
	              	  }	              	
	              }
	           }
	       );
        } else {
        	$wnd.alert("geocoder not found");
            instance.@com.nitrous.gwtearth.visitors.client.geocode.GeoCoder::onGeocodeFailure(Lcom/nitrous/gwtearth/visitors/shared/CityMetric;)(metric);	              	
	    }
    }-*/;

    /**
     * Critical failure - exceeded geocoding query limit
     */
    private void onGeocodeOverQueryLimitFailure() {
    	endFailure(new Exception("Exceeded geo-coding query limit. Sorry! Please try again later."));
    }
    
    private void onGeocodeFailure(CityMetric metric) {
    	onFailure(metric, new Exception("Failed to geocode location '"+getGeoAddress(metric)+"'"));
    }
    
    private void processGeoCodeResult(CityMetric metric, JsArray<GeoResult> results) {
		if (results != null && results.length() > 0) {
			for (int i = 0, len = results.length(); i < len; i++) {
				GeoResult geo = results.get(i);
				Geometry geometry = geo.getGeometry();
				if (geometry != null) {
					LatLon latLon = geometry.getLocation();
					if (latLon != null) {
						processLatLon(metric, latLon);
						return;
					}
				}
			}
		}
		onFailure(metric, new Exception("Failed to parse location data for "+metric.getCountry()));
	}
	
	private void processLatLon(CityMetric metric, LatLon location) {
	    String address = getGeoAddress(metric);
        GWT.log("cache.put(\"" + address + "\", new LatLon("+location.getLatitude()+"D, "+location.getLongitude()+"D));");
		metric.setLatLon(location);
		cache.put(address, location);
		onSuccess(metric);
	}
}
