package com.templaro.opsiz.aka;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

public class BeaconSqualk extends BroadcastReceiver {
	
	private String TAG = "BeaconSqualk";
	private String beacon_text = "Beacon Text";
	private int hertz = 800;
	private int speed = 15;
	private String beacon_interval = "15";
	private boolean qrss = false;
	private String qrss_rate = "10";	
	private String callsign ="URCALL";

	@Override
	public void onReceive(Context context, Intent intent) {
		 LoadPrefs(context);
		 Toast.makeText(context, "Activating Beacon: "+ beacon_text  +"Squalk! Squalk!", Toast.LENGTH_SHORT).show();
		 Log.i(TAG, "Squalk! "+ beacon_text);
		 
		 MorsePlayer player = new MorsePlayer(hertz,speed);
		 player.setMessage(beacon_text);
		 player.playMorse();
	}
	
	private void LoadPrefs(Context context) {
		Log.i(TAG, "Loading saved preferences");
		SharedPreferences prefs = 
				PreferenceManager.getDefaultSharedPreferences(context);
		hertz = prefs.getInt("sidetone", 800);
		speed = prefs.getInt("wpm", 15);
		beacon_interval = prefs.getString("beacon_interval", "15");
		beacon_text = xmlImport(prefs.getString("beacon_text", "QTH # DE @/B @/B"));
		callsign = xmlImport(prefs.getString("callsign","UR CALL"));
		qrss = prefs.getBoolean("qrss", false);
		qrss_rate = prefs.getString("qrss_rate", "10");
	}
	
	 protected String xmlImport(String str){
	    	str.replaceAll("&lt;","<");
	    	str.replaceAll("&gt;",">");
	    	str.replaceAll("&amp;","&");
	    	return str;
	 }
		
}