package com.templaro.opsiz.aka;

import android.util.Log;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

/* A bunch of this code is copied from
 * http://marblemice.blogspot.com/2010/04/generate-and-play-tone-in-android.htm
 * 
 * All of the public methods should be called within a new Thread.
 */


public class MorsePlayer {
	private String TAG = "MorsePlayer";
	private int sampleRate = 8000;
	private int duration = 1;  // in seconds
	private int numSamples;
	private double sample[];
	private byte generatedSnd[];
	private AudioTrack audioTrack;
	

	// Prepare to play morse code at SPEED wpm and HERTZ frequency,
	// by pre-generating 'dit' and 'dah' sinewave tones of the proper length.
	public MorsePlayer(int hertz, int speed) {
		Log.i(TAG, "Generating dit and dah tones.");
		
		// duration should be derived from SPEED, where (1200 / wpm) = element length in milliseconds
		numSamples = duration * sampleRate;
		sample = new double[numSamples];
		generatedSnd = new byte[2 * numSamples];
		audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, sampleRate,
				AudioFormat.CHANNEL_CONFIGURATION_MONO,
                AudioFormat.ENCODING_PCM_16BIT, numSamples,
                AudioTrack.MODE_STATIC);
		
        for (int i = 0; i < numSamples; ++i) {
            sample[i] = Math.sin(2 * Math.PI * i / (sampleRate/hertz));
        }
        // convert to 16 bit pcm sound array; assumes the sample buffer is normalised.
        int idx = 0;
        for (final double dVal : sample) {
            final short val = (short) ((dVal * 32767)); // scale to maximum amplitude
            // in 16 bit wav PCM, first byte is the low order byte
            generatedSnd[idx++] = (byte) (val & 0x00ff);
            generatedSnd[idx++] = (byte) ((val & 0xff00) >>> 8);
        }
	}
	
	public void playMorse(String message) {
		// check to make sure sine data is already generated
		Log.i(TAG, "Now playing morse code...");
		while (true) {
			try {
				audioTrack.write(generatedSnd, 0, generatedSnd.length);
			    audioTrack.play();
			    Thread.sleep(duration * 1000);
			} catch (InterruptedException e) {
				Log.i(TAG, "Interrupted, stopping all sound...");
				// make sure no sound is playing
				return;
			}
		}
	}
	
}
