package com.templaro.opsiz.aka;

import android.media.AudioTrack;
import android.util.Log;

// Singleton class shared by main UI thread and MorsePlayer thread.

public class AKASignaler {
	private static final AKASignaler instance = new AKASignaler();
	public AudioTrack audioTrack = null;  //does this need to be public?
	int msgSize = 0;
	private String TAG = "AKASignaler";
	
	private AKASignaler() {
	}
	
	public static AKASignaler getInstance() {
		return instance;
	}
	
	public void killAudioTrack() {
		if (audioTrack != null) {
			Log.i(TAG, "stopping all sound and releasing audioTrack resources...");
			audioTrack.stop();
			audioTrack.flush(); //flush is necessary even though audiotrack is released
									 //on next line. Without this, the message keeps playing.
			audioTrack.release();  // release underlying audio resources
			audioTrack = null;  // release object for garbage collection
		}
		else {
			Log.i(TAG, "Null audioTrack, nothing to kill off. What a pity.");
		}
		
	}
}
