package com.nitrous.gwtearth.visitors.client;

import java.util.HashSet;
import java.util.Set;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.nitrous.gwt.earth.client.api.GELayerId;
import com.nitrous.gwt.earth.client.api.GENavigationControlType;
import com.nitrous.gwt.earth.client.api.GEPlugin;
import com.nitrous.gwt.earth.client.api.GEPluginReadyListener;
import com.nitrous.gwt.earth.client.api.GEVisibility;
import com.nitrous.gwt.earth.client.api.GoogleEarthWidget;
import com.nitrous.gwt.earth.client.api.KmlAltitudeMode;
import com.nitrous.gwt.earth.client.api.KmlLookAt;
import com.nitrous.gwt.earth.client.api.KmlPlacemark;
import com.nitrous.gwt.earth.client.api.KmlPoint;
import com.nitrous.gwtearth.visitors.client.geocode.GeoCoder;
import com.nitrous.gwtearth.visitors.shared.CityMetric;
import com.nitrous.gwtearth.visitors.shared.LatLon;
import com.nitrous.gwtearth.visitors.shared.RpcSvcException;

/**
 * The entry-point class for the GWT application that renders visitor locations on a map
 * @author Nick
 *
 */
public class GwtEarthVisitors implements EntryPoint {
    private static final double CITY_RANGE = 500000D;
    private static final double COUNTRY_RANGE = 4000000D;

    private GoogleEarthWidget earth;
    private MetricTable metrics;
    private boolean earthPluginReady = false;
    
    public void onModuleLoad() {
        // construct the UI widget
        earth = new GoogleEarthWidget();

        // register a listener to be notified when the earth plug-in has loaded
        earth.addPluginReadyListener(new GEPluginReadyListener() {
            public void pluginReady(GEPlugin ge) {
                // show map content once the plugin has loaded
                onEarthPluginReady();
            }

            public void pluginInitFailure() {
                // failure!
                Window.alert("Failed to initialize Google Earth Plug-in");
            }
        });

        metrics = new MetricTable();
        metrics.setWidth("100%");
        metrics.setSelectionListener(new SelectionListener(){
			@Override
			public void onSelected(CityMetric metric) {
				panToLocation(metric, CITY_RANGE);
			}
        });
        
        ScrollPanel leftCol = new ScrollPanel();
        leftCol.setHeight("100%");
        leftCol.add(metrics);
        
        
        SplitLayoutPanel layout = new SplitLayoutPanel();
        layout.addWest(leftCol, 450);
        layout.add(earth);
                
        RootLayoutPanel.get().add(layout);

        // begin loading the Google Earth Plug-in
        earth.init();
    }

    /**
     * Display content on the map
     */
    private void onEarthPluginReady() {
        // The GEPlugin is the core class and is a great place to start browsing the API
        GEPlugin plugin = earth.getGEPlugin();
        
        // show some layers
        plugin.enableLayer(GELayerId.LAYER_BORDERS, true);
        plugin.enableLayer(GELayerId.LAYER_ROADS, true);

        // show an over-view pane
        plugin.getOptions().setOverviewMapVisibility(true);
        plugin.getNavigationControl().setControlType(GENavigationControlType.NAVIGATION_CONTROL_LARGE);
        plugin.getNavigationControl().setVisibility(GEVisibility.VISIBILITY_SHOW);
    	earthPluginReady = true;
        
        loadVisitorInfo();
    }
    
    /**
     * Load the visitor information
     */
    private void loadVisitorInfo() {
    	VisitorServiceAsync rpc = GWT.create(VisitorService.class);
    	rpc.fetchVisitorInformation(new AsyncCallback<HashSet<CityMetric>>(){

			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Failed to load visitor information", caught);
				if (caught instanceof RpcSvcException) {
					Window.alert(caught.getMessage());
				} else {
					Window.alert("Failed to load visitor information");
				}				
			}

			@Override
			public void onSuccess(HashSet<CityMetric> result) {
				geoCodeLocations(result);				
			}    		
    	});
    }
    
    /**
     * GeoCode the locations and plot on the map
     * @param result The data to geocode and plot on the map
     */
    private void geoCodeLocations(HashSet<CityMetric> result) {
    	GeoCoder coder = new GeoCoder(result);
    	coder.start(new AsyncCallback<Set<CityMetric>>(){
			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Failed to encode visitor locations", caught);
				if (caught instanceof RpcSvcException) {
					Window.alert(caught.getMessage());
				} else {
					Window.alert("Failed to encode visitor locations. Cause:" +caught.getMessage());
				}				
			}

			@Override
			public void onSuccess(Set<CityMetric> result) {
				plotLocations(result);
			}
    	});
    }

    /**
     * Plot the specified visitor metrics on the map
     * @param result The data to plot on the map
     */
    private void plotLocations(Set<CityMetric> result) {
    	metrics.showMetrics(result);
        for (CityMetric metric : result) {
        	plotLocation(metric);
        }        
        
        // pan to first result
        if (result != null && result.size() > 0) {
            panToLocation(result.iterator().next(), COUNTRY_RANGE);
        }
    }
    
    private void panToLocation(CityMetric metric, double range) {
    	if (metric == null || earthPluginReady == false) {
    		return;
    	}
    	
    	LatLon location = metric.getLatLon();
    	if (location == null) {
    		GWT.log("Missing location for "+metric.getCountry());
    		return;
    	}
    	
		// pan to the new position
    	GEPlugin ge = earth.getGEPlugin();
		KmlLookAt lookAt = ge.getView().copyAsLookAt(KmlAltitudeMode.ALTITUDE_RELATIVE_TO_GROUND);
		lookAt.setLatitude(location.getLatitude());
		lookAt.setLongitude(location.getLongitude());
		lookAt.setRange(range);
		
		// hide any existing balloon
		ge.setBalloon(null);
		ge.getView().setAbstractView(lookAt);
    }
    
    private void plotLocation(CityMetric metric) {
    	LatLon location = metric.getLatLon();
    	if (location == null) {
    		GWT.log("Missing location for "+metric.getCountry());
    		return;
    	}
    	
    	GEPlugin ge = earth.getGEPlugin();
        KmlPlacemark placemark = ge.createPlacemark("");
		KmlPoint kmlPoint = ge.createPoint("");
		kmlPoint.setLatLng(location.getLatitude(), location.getLongitude());
		kmlPoint.setAltitudeMode(KmlAltitudeMode.ALTITUDE_CLAMP_TO_GROUND);
		placemark.setGeometry(kmlPoint);
		
		String description = getDescription(metric);
		placemark.setDescription(description);		

		ge.getFeatures().appendChild(placemark);
	}
    
    
    private static final DateTimeFormat format = DateTimeFormat.getFormat("MMM-dd-yyyy");
    private static String getDescription(CityMetric metric) {
    	StringBuffer html = new StringBuffer();
    	html.append("<table>");
    	
        html.append("<tr><td><b>City</b></td><td>");
        html.append(metric.getCity());
        html.append("</td></tr>");
        
    	html.append("<tr><td><b>Country</b></td><td>");
    	html.append(metric.getCountry());
    	html.append("</td></tr>");
        
    	html.append("<tr><td><b>Visits</b></td><td>");
    	html.append(metric.getVisitCount());
    	html.append("</td></tr>");
    	
    	html.append("<tr><td><b>Last&nbsp;Visit</b></td><td>");
    	html.append(format.format(metric.getLastVisitDate()));
    	html.append("</td></tr>");
    	
    	html.append("</table>");
    	return html.toString();
    }
}
