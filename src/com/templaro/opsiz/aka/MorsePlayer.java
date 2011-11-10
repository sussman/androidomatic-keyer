package com.templaro.opsiz.aka;

import android.util.Log;

/* A bunch of this code is copied from
 * http://marblemice.blogspot.com/2010/04/generate-and-play-tone-in-android.htm
 * 
 * All of the public methods should be called within a new Thread.
 */


public class MorsePlayer {
	private String TAG = "MorsePlayer";

	// Prepare to play morse code at SPEED wpm and HERTZ frequency,
	// by pre-generating 'dit' and 'dah' sinewave tones of the proper length.
	public MorsePlayer(int hertz, int speed) {
		Log.i(TAG, "Generating dit and dah tones.");
	}
	
	public void playMorse(String message) {
		// check to make sure sine data is already generated
		Log.i(TAG, "Now playing morse code...");
		while (true) {
			if (Thread.interrupted()) { // main caller wants us to stop.
				Log.i(TAG, "Interrupted, stopping all sound...");
				// make sure no sound is playing
				return;
			}
		}
	}
	
}
