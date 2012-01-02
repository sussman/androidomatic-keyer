package com.templaro.opsiz.aka;

/* Portions adapted from code examples in 
 * Hello Android, second edition, Ed Burnette and
 * JavaCodeGeeks GPS tutorial
 * http://www.javacodegeeks.com/2010/09/android-location-based-services.html
 */

import java.util.List;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;

public class GeoHelper implements LocationListener {
	
	private String TAG = "GeoHelper";
	private static final long MINIMUM_DISTANCE_CHANGE_FOR_UPDATES = 100; // in Meters
	private static final long MINIMUM_TIME_BETWEEN_UPDATES = 180000; // in Milliseconds
	
	private String NORTH = "N";  // Later, grab these from XML to facilitate localization
	private String SOUTH = "S";
	private String EAST = "E";
	private String WEST = "W";
	private String NOFIX = "??";
	
	private static final String[] A = { "n/a", "fine", "coarse" };
	private static final String[] P = { "n/a", "low", "medium", "high" };
	private static final String[] S = { "out of service", "temporarily unavailable", 
		"available" };
	
	private LocationManager locationManager;
	private Context mContext;
	private String best;
	private Criteria criteria;
	
	public GeoHelper(Context context) {
		//Constructor
		Log.i(TAG, "Initialize GeoHelper.");
		mContext = context;
		
		locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
		
		listAllProviders();
		criteria = new Criteria();
    	criteria.setAccuracy(Criteria.ACCURACY_COARSE);
    	criteria.setAltitudeRequired(false);
    	criteria.setCostAllowed(false);
    	criteria.setPowerRequirement(Criteria.POWER_LOW);
    	criteria.setSpeedRequired(false);
		
		chooseBestProvider();
    
	}
	
	protected String currentDecimalLocation() {

		String message = currentLocation(Location.FORMAT_DEGREES);
		return message;
	}	
		
	protected String currentSexagesimalLocation() {
		
		String message = currentLocation(Location.FORMAT_SECONDS);
		return message;
	}   
	
	private String currentLocation (int formatting) {
		
		String message;
		String latFix;
		String longFix;
		
		Location location = locationManager.getLastKnownLocation(best);
		if (location != null) {
								
			double latVal =  location.getLatitude();
			double longVal = location.getLongitude();
			
			if (latVal < 0) {
				latFix = SOUTH; 
				latVal *= -1;
			}
			else  latFix = NORTH;
			if (longVal < 0) {
				longFix = WEST; 
				longVal *= -1;
			}
			else longFix = EAST;
			switch (formatting) {
				case Location.FORMAT_SECONDS:
					String[] latField  = (Location.convert(latVal, Location.FORMAT_SECONDS)).split(":");
					String[] longField = (Location.convert(longVal, Location.FORMAT_SECONDS)).split(":");
					message = String.format("%s/%s/%d%s,%s/%s/%d%s", latField[0], latField[1],
							Math.round(Double.parseDouble(latField[2])), latFix, longField[0], longField[1], 
							Math.round(Double.parseDouble(longField[2])), longFix);
					break;
				case Location.FORMAT_DEGREES:
					message = String.format("%3.3f%s,%3.3f%s",latVal,latFix,longVal,longFix);
					break;
				default:
					message = NOFIX;
					break;
			}		
		}
		else {
			message = NOFIX;
		}
		return message;
	}

	
	protected String maidenheadGrid() {
		
		StringBuffer grid = new StringBuffer();
		Location location = locationManager.getLastKnownLocation(best);
		
		if(null==location){
			grid.append(NOFIX);
			Log.w(TAG,"Can't compute grid -- null location");	
		}
		else {
			//retrieve decimal value long and lat
			float longitude = (float) location.getLongitude();
			float latitude = (float) location.getLatitude();
			//measure east from antimeridian, north from south pole
			float absLong = 180 + longitude;
			float absLat = 90 + latitude;
			
			//Field
			//first capital letter represents 20 degrees of longitude, 'A' to 'R'
			int longField = (int) absLong/20;
			grid.append((char) ('A' + longField));
			//second capital letter represents 10 degrees of latitude, 'A' to 'R'
			int latField = (int) absLat/10;
			grid.append((char) ('A' + latField));
			//Square
			//first number represents 2 degrees of longitude, '0' to '9'
			int longSquare = (int) ((absLong - longField*20)/2);
			grid.append((char) ('0' + longSquare));
			//second number represents 1 degree of latitude, '0' to '9'
			int latSquare = (int) absLat - latField*10;
			grid.append((char) ('0' + latSquare));
			//Subsquare
			//first miniscule letter represents 5 minutes of longitude (1/12th of a degree), 'a' to 'x'
			int longSubSquare = (int) ((absLong - longField*20 - longSquare*2)*12);
			grid.append((char)('a' + longSubSquare));
			//second miniscule letter represents 2.5 minutes of latitude (1/24th of degree), 'a' to 'x'
			int latSubSquare = (int) ((absLat - latField*10 - latSquare)*24);
			grid.append((char)('a' + latSubSquare));
			Log.i(TAG, "Grid square: " + grid.toString());
		}
		return grid.toString();
	}

	protected void locationUpdatesOn() {
		//suggest turning updates on in the onResume() of parent activity
		Log.i(TAG,"Turning location updates ON");
		locationManager.requestLocationUpdates(best, MINIMUM_TIME_BETWEEN_UPDATES,
				MINIMUM_DISTANCE_CHANGE_FOR_UPDATES, this);
	}
	
	protected void locationUpdatesOff() {
		//to save power, suggest turning updates off in onPause() of parent activity
		Log.i(TAG,"Turning location updates OFF");
		locationManager.removeUpdates(this);
	}
	
	private void listAllProviders() {
	     List<String> providers = locationManager.getAllProviders();
	     if(!providers.isEmpty()) {
	    	 for (String provider : providers) {
	    		 providerInfo(provider);
	     	}
	   }
	}

	private void providerInfo(String provider) {
	      LocationProvider info = locationManager.getProvider(provider);
	      StringBuilder builder = new StringBuilder();
	      builder.append("LocationProvider[")
	            .append("name=")
	            .append(info.getName())
	            .append(",enabled=")
	            .append(locationManager.isProviderEnabled(provider))
	            .append(",hasMonetaryCost=")
	            .append(info.hasMonetaryCost())
	            .append(",requiresCell=")
	            .append(info.requiresCell())
	            .append(",requiresNetwork=")
	            .append(info.requiresNetwork())
	            .append(",requiresSatellite=")
	            .append(info.requiresSatellite())
	            .append(",supportsAltitude=")
	            .append(info.supportsAltitude())
	            .append(",supportsBearing=")
	            .append(info.supportsBearing())
	            .append(",supportsSpeed=")
	            .append(info.supportsSpeed())
  	  			.append(",getAccuracy=");
	      int accuracy = info.getAccuracy();
	      if (accuracy > -1) builder.append(A[accuracy]);
	      else builder.append("No Data");
	      builder.append(",getPowerRequirement=");
	      int power = info.getPowerRequirement();
	      if (power > -1) builder.append(P[power]);
	      else builder.append("No Data");
	      builder.append("]");
	      Log.i(TAG, builder.toString());
	   }
	
	private void chooseBestProvider() {
		//Choose the best of available providers, trying to meet criteria
		best = locationManager.getBestProvider(criteria, true);
    	Log.i(TAG, "Best provider is " + best);
	}
	
	@Override
	public void onLocationChanged(Location location) {
		String message = String.format("Location update: %s",
				currentDecimalLocation());
		Log.i(TAG, message);
	}

	@Override
	public void onProviderDisabled(String provider) {
		String message = String.format("Provider %s disabled by user",provider);
		Log.i(TAG, message);
		chooseBestProvider();
	}

	@Override
	public void onProviderEnabled(String provider) {
		String message = String.format("Provider %s enabled by user",provider);
		Log.i(TAG, message);
		chooseBestProvider();
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		if (status < 0) status = 1; //some insurance against out of range subscript
		String message = String.format("Provider %s changed status to %s. Extras = %s",
				provider, S[status], extras);
		Log.i(TAG, message);
		chooseBestProvider();
	}
}	
	
	
