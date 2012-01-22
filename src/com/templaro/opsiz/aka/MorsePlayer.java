/*
 * Copyright (C) 2011 Ben Collins-Sussman and Jack Welch
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.templaro.opsiz.aka;

import android.util.Log;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.AudioTrack.OnPlaybackPositionUpdateListener;


/* A bunch of this wave-generating code is copied from
 * http://marblemice.blogspot.com/2010/04/generate-and-play-tone-in-android.html
 */

public class MorsePlayer {
	
	private String TAG = "MorsePlayer";
	private final int SAMPLE_RATE = 8000;
	private double duration;  // in seconds
	private int wpmSpeed;
	private int toneHertz;
	private int numSamples;
	private double sample[];
	private byte ditSnd[];
	private byte dahSnd[];
	private byte pauseInnerSnd[];
	private MorseBit[] pattern;
	private AKASignaler signaler = AKASignaler.getInstance();
	private String currentMessage;  // message to play in morse

	// Constructor: prepare to play morse code at SPEED wpm and HERTZ frequency
	public MorsePlayer(int hertz, int speed) {
		setSpeed(speed);
		setTone(hertz);
		buildSounds(); 
	}
	
	
	// Generate 'dit','dah' and empty sinewave tones of the proper lengths.
	private void buildSounds() {
		// where (1200 / wpm) = element length in milliseconds
		duration = (double)((1200 / wpmSpeed) * .001);
		numSamples = (int)(duration * SAMPLE_RATE - 1);
		double sineMagnitude = 1; // starting with a dummy value for absolute normalized value of sine wave 
		double CUTOFF = 0.1; // threshold for whether sine wave is near zero crossing
		double phaseAngle = 2 * Math.PI / (SAMPLE_RATE/toneHertz);
		while (sineMagnitude > CUTOFF){
			numSamples++;
			//check to see if  is near zero-crossing to avoid clicks when sound cuts off
			sineMagnitude = Math.abs(Math.sin(phaseAngle*numSamples));
		}
		sample = new double[numSamples];
		ditSnd = new byte[2 * numSamples];
		dahSnd = new byte[6 * numSamples];
		pauseInnerSnd = new byte[2 * numSamples];
				
		for (int i = 0; i < numSamples; ++i) {
			sample[i] = Math.sin(phaseAngle * i);
		}
		// convert to 16 bit pcm sound array; assumes the sample buffer is normalised.
		int idx = 0;
		for (final double dVal : sample) {
			final short val = (short) ((dVal * 32767)); // scale to maximum amplitude
			// in 16 bit wav PCM, first byte is the low order byte
			ditSnd[idx++] = (byte) (val & 0x00ff);
			ditSnd[idx++] = (byte) ((val & 0xff00) >>> 8);
		}
		for (int i = 0; i < (dahSnd.length); i++) {
			dahSnd[i] = ditSnd[i % ditSnd.length];
		}
		for (int i = 0; i < (pauseInnerSnd.length); i++) {
			pauseInnerSnd[i] = 0;
		}
	}
	
	
	public void setMessage(String message) {
		currentMessage = message;
	}
	
	private void setSpeed(int speed) {
		wpmSpeed = speed;
		}
	
	private void setTone(int hertz) {
		toneHertz = hertz;
	}
	
	// The main method of this class; runs exactly once in a standalone thread.
	public void playMorse() {
		Log.i(TAG, "Now playing morse code...");
        pattern = MorseConverter.pattern(currentMessage);
		
        // Calculate size of data we're going to push.
        int msgSize = 0;
        for (MorseBit bit : pattern) {
			switch (bit) {
				case GAP:  msgSize += pauseInnerSnd.length;  break;
				case DOT:  msgSize += ditSnd.length;  break;
				case DASH: 	msgSize += dahSnd.length;  break;
				case LETTER_GAP:
					for (int i = 0; i < 3; i++)
						msgSize += pauseInnerSnd.length;
					break;
				case WORD_GAP:
					for (int i = 0; i < 7; i++)
						msgSize += pauseInnerSnd.length;
					break;
				default:  break;
			}
		}
        
        // Create an audioTrack with a buffer exactly the size of our message.
        signaler.audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, SAMPLE_RATE,
        						AudioFormat.CHANNEL_CONFIGURATION_MONO,
        						AudioFormat.ENCODING_PCM_16BIT,
        						msgSize, AudioTrack.MODE_STREAM);
        signaler.msgSize = msgSize;
    	
        // Start playing sound out of the buffer
		signaler.audioTrack.play();
         
		// Start pushing sound data into the audiotrack's buffer.
		for (MorseBit bit : pattern) {
			switch (bit) {
				case GAP:  
					signaler.audioTrack.write(pauseInnerSnd, 0, pauseInnerSnd.length); break;
				case DOT:  
					signaler.audioTrack.write(ditSnd, 0, ditSnd.length); break;
				case DASH: 
					signaler.audioTrack.write(dahSnd, 0, dahSnd.length); break;
				case LETTER_GAP:
					for (int i = 0; i < 3; i++)
						signaler.audioTrack.write(pauseInnerSnd, 0, pauseInnerSnd.length);
					break;
				case WORD_GAP:
					for (int i = 0; i < 7; i++)
						signaler.audioTrack.write(pauseInnerSnd, 0, pauseInnerSnd.length);
					break;
				default:  break;
			}
		}

		// All data are pushed, and callback is set.  This thread can now commit suicide.
		Log.i(TAG, "All data pushed to audio buffer; thread quitting.");
		return;
	}
}
