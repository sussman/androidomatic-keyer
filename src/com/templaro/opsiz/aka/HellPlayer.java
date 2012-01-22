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

import java.util.Arrays;

import android.util.Log;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

/* This is based roughly on the MorsePlayer class 
 */


public class HellPlayer  {
	
	/* Each of the 98 elements composing a standard Hellscreiber character can be a mark or space.
	 * to compensate for timing issues introduced by the phone, interface and transmitter, at transitions 
	 * from mark to space, the length of the mark can be increased or decreased slightly, with reciprocal
	 * shortening or lengthening of the following space to keep the letter length to 400 milliseconds. 
	 * Each column consists of 457 samples at the 8000 hz sample rate, so a final empty sample is added
	 * at the end of the letter, yielding exactly 3200 samples = 400 ms.
	 */
	private int mdarkness = 0; // darkness to be read from prefs; timing adjustment for modified mark & space

	private String TAG = "HellPlayer";
	private final int SAMPLE_RATE = 8000; //should be multiple of character duration
	private final int TONE_HERTZ = 900; // traditional tone for Hellscreiber
	private final int COLUMNS_PER_CHARACTER = 7; //based on standard Hellscreiber font
	private final int ELEMENTS_PER_COLUMN = 14; //based on standard Hellscreiber font
	private final double CHARACTER_DURATION = 0.4; // seconds, based on standard Hellscreiber 
	
	private double sample[];
	
	private byte markSnd[]; // an "on" element
	private byte modMarkSnd[]; // on element, modified by darkness setting
	private byte spaceSnd[]; // an "off" element
	private byte modSpaceSnd[]; // off element, modified by darkness setting
	private byte headerSnd[]; // after column space is divided among 14 elements, extra samples are 
	private byte footerSnd[];//  divided between top and bottom of the column
	private byte tailSnd[]; // end of each character is padded to assure total character length of 400 ms
	
	private HellBit[] pattern;
	private String currentMessage;  // message to play in Hell
	
	private AKASignaler signaler = AKASignaler.getInstance();
	private HellConverter mHell = new HellConverter();
	
	//Constructor
	public HellPlayer(int darkness_adjust) {  
		setDarkness(darkness_adjust);
		buildSounds();
	}
	
	private void setDarkness(int dark) {
		mdarkness = dark;
		Log.i(TAG, String.format("Set darkness to %d", mdarkness));
	}
	
	// Generate mark and space tones of the proper lengths.
	private void buildSounds() {
		Log.i(TAG, "Generating mark and space tones.");
		int samplesPerCharacter = (int) (SAMPLE_RATE * CHARACTER_DURATION);
		Log.d(TAG,"samplesPerCharacter "+ samplesPerCharacter);
		int samplesPerColumn =  (int) Math.floor(samplesPerCharacter / COLUMNS_PER_CHARACTER);
		Log.d(TAG,"samplesPerColumn "+ samplesPerColumn);
		int extraPerCharacter = samplesPerCharacter - COLUMNS_PER_CHARACTER * samplesPerColumn;
		Log.d(TAG,"extraPerCharacter "+ extraPerCharacter);
		int samplesPerElement = (int) Math.floor(samplesPerColumn/ELEMENTS_PER_COLUMN);
		Log.d(TAG,"samplesPerElement "+ samplesPerElement);
		int	extraPerColumn = samplesPerColumn - ELEMENTS_PER_COLUMN * samplesPerElement;
		Log.d(TAG,"extraPerColumn "+ extraPerColumn);
		int extraHead = Math.round(extraPerColumn / 2);
		Log.d(TAG,"extraHead "+ extraHead);
		int extraFoot = extraPerColumn - extraHead;
		Log.d(TAG,"extraFoot "+ extraFoot);
		
		sample = new double[samplesPerElement];
		
		markSnd = new byte[2 * samplesPerElement];
		//as specified in xml/preferences.xml, the abs value of mdarkness can't be bigger
		//than samplesPerElement
		modMarkSnd = new byte[2 * (samplesPerElement + mdarkness)];
		spaceSnd = new byte[2 * samplesPerElement];
		modSpaceSnd = new byte[2 * (samplesPerElement - mdarkness)];
		headerSnd = new byte[2 * extraHead];
		footerSnd = new byte[2 * extraFoot];
		tailSnd = new byte[2 * extraPerCharacter];
		
		double phaseAngle = 2 * Math.PI / (SAMPLE_RATE/TONE_HERTZ);
				
		for (int i = 0; i < samplesPerElement; ++i) {
			sample[i] = Math.sin(i * phaseAngle);
		}
		// convert to 16 bit pcm sound array; assumes the sample buffer is normalised.
		int idx = 0;
		for (final double dVal : sample) {
			final short val = (short) ((dVal * 32767)); // scale to maximum amplitude
			// in 16 bit wav PCM, first byte is the low order byte
			markSnd[idx++] = (byte) (val & 0x00ff);
			markSnd[idx++] = (byte) ((val & 0xff00) >>> 8);
		}
		for (int i = 0; i < (modMarkSnd.length); i++) {
			modMarkSnd[i] = markSnd[i % markSnd.length];
		}
		Arrays.fill(spaceSnd, (byte) 0);
		Arrays.fill(modSpaceSnd, (byte) 0);
		Arrays.fill(headerSnd, (byte) 0);
		Arrays.fill(footerSnd, (byte) 0);
		Arrays.fill(tailSnd, (byte) 0);	
	}
	
	
	public void setMessage(String message) {
		currentMessage = message;
	}

	
	// The main method of this class; runs exactly once in a standalone thread.
	public void playHell() {
		Log.i(TAG, "Now playing Hellscreiber message...");
		pattern = mHell.pattern(currentMessage);
		
		// Calculate size of data we're going to push.
        int msgSize = 0;
        HellBit bit;
        int elementsPerCharacter = COLUMNS_PER_CHARACTER * ELEMENTS_PER_COLUMN;
        int charsToSend = pattern.length / elementsPerCharacter;
        
        for (int charIndex =0; charIndex < charsToSend; charIndex++) {
        	for (int column=0; column < COLUMNS_PER_CHARACTER; column++) {
        		for (int row =0; row < ELEMENTS_PER_COLUMN; row++) {
        			bit = pattern[charIndex*elementsPerCharacter + column*ELEMENTS_PER_COLUMN + row];
        			if (row == 0) {
        			// top of the column
        			msgSize += headerSnd.length;
					}
					switch (bit) {
						case MARK:  
							msgSize += markSnd.length;
							break;
						case MODMARK:  
							msgSize += modMarkSnd.length;  
							break;
						case SPACE: 
							msgSize += spaceSnd.length;  
							break;
						case MODSPACE:
							msgSize += modSpaceSnd.length;  
							break;
						default:  
							Log.d(TAG,"Default/unknown Hellbit type error");
							break;
					}
					if (row == 13 ){
						//bottom of column
						msgSize += footerSnd.length;
						if (column == 6) {
							//last position, pad out to CHARACTER_DURATION
							msgSize += tailSnd.length;
						}//end loast position
					}//end bottom of column	
				}//next row
			}//next column	
		}//next character
        
        // Create an audioTrack with a buffer exactly the size of our message.
        signaler.audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, SAMPLE_RATE,
        						AudioFormat.CHANNEL_CONFIGURATION_MONO,
        						AudioFormat.ENCODING_PCM_16BIT,
        						msgSize, AudioTrack.MODE_STREAM);
        signaler.msgSize = msgSize;
    	
        // Start playing sound out of the buffer
		signaler.audioTrack.play();   
        
		// Start pushing sound data into the audiotrack's buffer.
		
		for (int charIndex =0; charIndex < charsToSend; charIndex++) {
        	for (int column=0; column < COLUMNS_PER_CHARACTER; column++) {
        		for (int row =0; row < ELEMENTS_PER_COLUMN; row++) {
        			bit = pattern[charIndex*elementsPerCharacter + column*ELEMENTS_PER_COLUMN + row];
        			if (row == 0) {
        			// top of the column
        			signaler.audioTrack.write(headerSnd, 0, headerSnd.length); 	
					}
					switch (bit) {
						case MARK:  
							signaler.audioTrack.write(markSnd, 0, markSnd.length);
							break;
						case MODMARK:  
							signaler.audioTrack.write(modMarkSnd,0, modMarkSnd.length); 
							break;
						case SPACE: 
							signaler.audioTrack.write(spaceSnd, 0, spaceSnd.length);
							break;
						case MODSPACE:
							signaler.audioTrack.write(modSpaceSnd, 0, modSpaceSnd.length);
							break;
						default:  
							Log.d(TAG,"Default/unknown Hellbit type error");
							break;
					}
					if (row == 13 ){
						//bottom of column
						signaler.audioTrack.write(footerSnd, 0, footerSnd.length);
						if (column == 6) {
							//last position, pad out to CHARACTER_DURATION
							signaler.audioTrack.write(tailSnd, 0, tailSnd.length);
						}//end loast position
					}//end bottom of column	
				}//next row
			}//next column	
		}//next character
		
		// All data are pushed, and callback is set.  This thread can now commit suicide.
		Log.i(TAG, "All data pushed to audio buffer; thread quitting.");
		return;   
	}
	
}	