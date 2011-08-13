package com.nitrous.gwtearth.visitors.client;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.nitrous.gwt.earth.client.api.GEFeatureContainer;
import com.nitrous.gwt.earth.client.api.GEHtmlStringBalloon;
import com.nitrous.gwt.earth.client.api.GELayerId;
import com.nitrous.gwt.earth.client.api.GENavigationControlType;
import com.nitrous.gwt.earth.client.api.GEPlugin;
import com.nitrous.gwt.earth.client.api.GEPluginReadyListener;
import com.nitrous.gwt.earth.client.api.GEVisibility;
import com.nitrous.gwt.earth.client.api.GoogleEarthWidget;
import com.nitrous.gwt.earth.client.api.KmlAltitudeMode;
import com.nitrous.gwt.earth.client.api.KmlFeature;
import com.nitrous.gwt.earth.client.api.KmlLookAt;
import com.nitrous.gwt.earth.client.api.KmlPlacemark;
import com.nitrous.gwt.earth.client.api.KmlPoint;
import com.nitrous.gwtearth.visitors.client.geocode.GeoCoder;
import com.nitrous.gwtearth.visitors.shared.AccountProfile;
import com.nitrous.gwtearth.visitors.shared.CityMetric;
import com.nitrous.gwtearth.visitors.shared.LatLon;
import com.nitrous.gwtearth.visitors.shared.RpcSvcException;

/**
 * The entry-point class for the GWT application that renders visitor locations on a map
 * @author Nick
 *
 */
public class GwtEarthVisitors implements EntryPoint {
	private static final VisitorServiceAsync RPC = GWT.create(VisitorService.class);
    private static final double CITY_RANGE = 500000D;
    private static final double COUNTRY_RANGE = 4000000D;
    private static int balloonId = 0;
    
    private HashMap<CityMetric, KmlPlacemark> mapContent = new HashMap<CityMetric, KmlPlacemark>();
    private GoogleEarthWidget earth;
    private MetricTable metrics;
    private boolean earthPluginReady = false;
    private ListBox listBox;
    private Label label;
    private String displayedProfileId = null;
    
    public void onModuleLoad() {
    	label = new Label("Loading, Please Wait...");
    	RootPanel.get().add(label);
    	
    	// load the account profiles
    	RPC.getAccountProfiles(50, new AsyncCallback<HashSet<AccountProfile>>(){

			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Failed to load account profiles");
				label.setText("Failed to load account profiles: "+caught.getMessage());
			}

			@Override
			public void onSuccess(HashSet<AccountProfile> result) {
				showUI(result);				
			}
    		
    	});
    }
    
    private void showUI(HashSet<AccountProfile> profiles) {
    	RootPanel.get().remove(label);
    	
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
				panToLocation(metric, CITY_RANGE, true);
			}
        });
        
        ScrollPanel leftCol = new ScrollPanel();
        leftCol.setHeight("100%");
        leftCol.add(metrics);
        
        SplitLayoutPanel split = new SplitLayoutPanel();
        split.addWest(leftCol, 450);
        split.add(earth);
        
        
        HorizontalPanel topPanel = new HorizontalPanel();
        topPanel.setWidth("100%");
        topPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        HorizontalPanel profileSelectionPanel = new HorizontalPanel();
        Label accountLabel = new Label("Profile:");
        listBox = new ListBox(false);
        // sort the profiles
        TreeSet<AccountProfile> sortedProfiles = new TreeSet<AccountProfile>(new ProfileComparator());
        sortedProfiles.addAll(profiles);
        for (AccountProfile profile : sortedProfiles) {
        	listBox.addItem(profile.getProfileName(), profile.getTableId());
        }
        listBox.addChangeHandler(new ChangeHandler(){
			@Override
			public void onChange(ChangeEvent event) {
				loadSelectedProfile();
			}
        });
        profileSelectionPanel.add(accountLabel);
        profileSelectionPanel.add(listBox);
        topPanel.add(profileSelectionPanel);
                
        DockLayoutPanel layout = new DockLayoutPanel(Unit.PX);
        layout.addNorth(topPanel, 40D);
        layout.add(split);
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
    	loadSelectedProfile();
    }
    
    private void loadSelectedProfile() {
    	int selectedIdx = listBox.getSelectedIndex();
    	if (selectedIdx != -1) {
    		final String selectedProfileId = listBox.getValue(selectedIdx);
    		if (selectedProfileId == null || selectedProfileId.trim().length() == 0 || selectedProfileId.equals(displayedProfileId)) {
    			return;
    		}
    		
        	RPC.fetchCityVisitorInformation(selectedProfileId, new AsyncCallback<HashSet<CityMetric>>(){
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
    				displayedProfileId = selectedProfileId;
    			}    		
        	});
    	}
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
    	clearMapAndTable();
    	metrics.showMetrics(result);
        for (CityMetric metric : result) {
        	plotLocation(metric);
        }        
        
        // pan to first result
        if (result != null && result.size() > 0) {
            panToLocation(result.iterator().next(), COUNTRY_RANGE, false);
        }
    }
    
    private void panToLocation(CityMetric metric, double range, boolean showPopup) {
    	if (metric == null || earthPluginReady == false) {
    		return;
    	}
    	
    	LatLon location = metric.getLatLon();
    	if (location == null) {
    		GWT.log("Missing location for "+metric.getCountry());
    		return;
    	}
    	
		// pan to the new position
    	final GEPlugin ge = earth.getGEPlugin();
		KmlLookAt lookAt = ge.getView().copyAsLookAt(KmlAltitudeMode.ALTITUDE_RELATIVE_TO_GROUND);
		lookAt.setLatitude(location.getLatitude());
		lookAt.setLongitude(location.getLongitude());
		lookAt.setRange(range);
		
		ge.getView().setAbstractView(lookAt);
		
		// hide any existing balloon
		ge.setBalloon(null);
		
		if (showPopup) {
			KmlFeature feature = mapContent.get(metric);
			if (feature != null) {			
				final GEHtmlStringBalloon balloon = ge.createHtmlStringBalloon("Balloon_"+balloonId++);
				balloon.setContentString(getDescription(metric));
				balloon.setFeature(feature);
				// give the map 2 seconds to pan and then show the balloon
				Timer timer = new Timer(){
					@Override
					public void run() {
						ge.setBalloon(balloon);
					}
				};
				timer.schedule(2000);
			}
		}
				
    }
    
    /**
     * Remove all locations from the map and metrics table
     */
    private void clearMapAndTable() {
    	GEPlugin plugin = earth.getGEPlugin();
    	GEFeatureContainer container = plugin.getFeatures();
    	while (container.hasChildNodes()) {
    		container.removeChild(container.getFirstChild());
    	}
    	mapContent.clear();
    	metrics.clear();
    }
    
    private KmlPlacemark plotLocation(CityMetric metric) {
    	LatLon location = metric.getLatLon();
    	if (location == null) {
    		GWT.log("Missing location for "+metric.getCountry());
    		return null;
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
    	mapContent.put(metric, placemark);
		return placemark;
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
