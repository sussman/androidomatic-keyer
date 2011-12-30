/*
 * Copyright (C) 2011 Ben Collins-Sussman
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

/* A bunch of this wave-generating code is copied from
 * http://marblemice.blogspot.com/2010/04/generate-and-play-tone-in-android.html
 */


public class MorsePlayer {
	
	private String TAG = "MorsePlayer";
	private int sampleRate = 8000;
	private double duration;  // in seconds
	private int wpmSpeed;
	private int toneHertz;
	private int numSamples;
	private double sample[];
	private byte ditSnd[];
	private byte dahSnd[];
	private byte pauseInnerSnd[];
	private MorseBit[] pattern;
	private AudioTrack audioTrack;
	private AKASignaler signaler = AKASignaler.getInstance();
	private String currentMessage;  // message to play in morse
	

	// Constructor: prepare to play morse code at SPEED wpm and HERTZ frequency,
	// by 
	public MorsePlayer(int hertz, int speed) {
		Log.i(TAG, "Generating dit and dah tones.");
		wpmSpeed = speed;
		toneHertz = hertz;
		buildSounds();
		audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, sampleRate,
				AudioFormat.CHANNEL_CONFIGURATION_MONO,
                AudioFormat.ENCODING_PCM_16BIT, 10 * numSamples,
                AudioTrack.MODE_STREAM);
	}
	
	
	// Generate 'dit','dah' and empty sinewave tones of the proper lengths.
	private void buildSounds() {
		// where (1200 / wpm) = element length in milliseconds
		duration = (double)((1200 / wpmSpeed) * .001);
		numSamples = (int)(duration * sampleRate - 1);
		double sineMagnitude = 1; // starting with a dummy value for absolute normalized value of sine wave 
		double CUTOFF = 0.1; // threshold for whether sine wave is near zero crossing
		double phaseAngle = 2 * Math.PI / (sampleRate/toneHertz);
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
	
	public void setSpeed(int speed) {
		wpmSpeed = speed;
		buildSounds();
	}
	
	public void setTone(int hertz) {
		toneHertz = hertz;
		buildSounds();
	}
	
	private void hibernate() {
		Log.i(TAG, "soundThread now sleeping in wait()...");
		synchronized (signaler) {
			try {
				signaler.wait();
			}
			catch (Exception e) {
				return;  // TODO: deep failure, time to abort the whole app probably
			}
		}
		// we've been awakened to play sound!
		Log.i(TAG, "Now playing morse code...");
		pattern = MorseConverter.pattern(currentMessage);
		buildSounds();  // in case tone or speed settings changed
		audioTrack.play();  // sound is now active
	}
	
	// The main logic loop of this class;  what the soundThread runs forever.
	public void playMorse() {
	
		// Begin thread by sleeping indefinitely within a wait() call
		hibernate();
			
		while (true) {  // the main keyer sound loop
			for (MorseBit bit : pattern) {
				if (signaler.pleaseShutUp == true) {
					Log.i(TAG, "Interrupted, stopping all sound...");
					audioTrack.stop(); // make sure no sound is playing
					audioTrack.flush();
					// TODO:  clear out audioTrack buffer here?
					signaler.pleaseShutUp = false;
					hibernate();  // sleep until we're reawoken
					break;  // start the (possibly new) message over
				}
				switch (bit) {
				case GAP:  audioTrack.write(pauseInnerSnd, 0, pauseInnerSnd.length);  break;
				case DOT:  audioTrack.write(ditSnd, 0, ditSnd.length);  break;
				case DASH: audioTrack.write(dahSnd, 0, dahSnd.length);  break;
				case LETTER_GAP:
					for (int i = 0; i < 3; i++)
						audioTrack.write(pauseInnerSnd, 0, pauseInnerSnd.length);  
					break;
				case WORD_GAP:
					for (int i = 0; i < 7; i++)
						audioTrack.write(pauseInnerSnd, 0, pauseInnerSnd.length);  
					break;
				default:  break;
				}	
			}
			try {
				Thread.sleep(2000);  // TODO: should be configurable pause
			} catch (InterruptedException e) {
				Log.i(TAG, "Interrupted, stopping all sound...");
				audioTrack.stop(); // make sure no sound is playing
				audioTrack.flush();
				break;
			}
		}
	}
}
