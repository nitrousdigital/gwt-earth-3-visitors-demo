package com.nitrous.gwtearth.visitors.client.images;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface Images extends ClientBundle {
	public static final Images INSTANCE = GWT.create(Images.class);
	
	@Source("loading.gif")
	public ImageResource loading();
}
