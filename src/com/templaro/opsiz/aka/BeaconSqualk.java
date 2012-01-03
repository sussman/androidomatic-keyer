package com.templaro.opsiz.aka;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class BeaconSqualk extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		 Toast.makeText(context, "Activating Beacon", Toast.LENGTH_SHORT).show();
	}
}