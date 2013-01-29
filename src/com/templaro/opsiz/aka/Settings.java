package com.templaro.opsiz.aka;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class Settings extends PreferenceActivity {
	
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {       
		super.onCreate(savedInstanceState);       
		addPreferencesFromResource(R.xml.preferences);       
	}
}
