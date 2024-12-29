package com.innolabs.spydroid;

import java.util.ArrayList;

import android.graphics.drawable.Drawable;
import android.util.Log;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

/**
 * Current Location Overlay is a class extending Itemized Overlay from Google Maps API which helps to add a custom icon or custom image on the Current location Marker on the Google Map
 * 
 * @author Srikanth Rao
 * 
 * @see com.google.android.maps.ItemizedOverlay<OverlayItem>
 *
 */
public class CurrentLocationOverlay extends ItemizedOverlay<OverlayItem> {
	
	/** Arraylist of Overlay item used to overlay the current location marker */
	private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();

	/** Current Location Overlay Constructor
	 * 
	 * @param defaultMarker The marker the comes default with the Google Maps API
	 */
	public CurrentLocationOverlay(Drawable defaultMarker) {
		
		super(boundCenterBottom(defaultMarker));
		
	}

	/** 
	 * Executed, when populate() method is called
	 */
	@Override
	protected OverlayItem createItem(int arg0) {
		return mOverlays.get(arg0);		
	}

	/**
	 * Executed when OverLay item Size is called
	 */
	@Override
	public int size() {		
		return mOverlays.size();
	}
	
	/**
	 * Method to add the custom overlay item to the current location marker of Google Map
	 * 
	 * @param overlay The drawable i.e custom image that is to be added to the marker position
	 */
	public void addOverlay(OverlayItem overlay) {
		
		mOverlays.add(overlay);
		
		populate(); // Calls the method createItem()
	}
	
	/** 
	 * Method called when a tap is received and used to change the position of the overlay according to the current default marker location
	 */
	@Override
	protected boolean onTap(int arg0) {
		Log.d("Tapped", mOverlays.get(arg0).getSnippet());
		return true;
	}
}