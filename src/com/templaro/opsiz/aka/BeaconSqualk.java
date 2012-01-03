package com.templaro.opsiz.aka;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class BeaconSqualk extends BroadcastReceiver {
	
	private String TAG = "BeaconSqualk";

	@Override
	public void onReceive(Context context, Intent intent) {
		 Toast.makeText(context, "Activating Beacon. Squalk! Squalk!", Toast.LENGTH_SHORT).show();
		 Log.i(TAG, "Squalk!");
	}
}