package com.templaro.opsiz.aka;

import android.media.AudioTrack;

// Singleton class shared by main UI thread and MorsePlayer thread.

public class AKASignaler {
	private static final AKASignaler instance = new AKASignaler();
	public AudioTrack audioTrack;
	int msgSize;
	
	private AKASignaler() {
		AudioTrack audioTrack = null;
		int msgSize = 0;
	}
	
	public static AKASignaler getInstance() {
		return instance;
	}
}
