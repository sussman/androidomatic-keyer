package com.templaro.opsiz.aka;

// Singleton class shared by main UI thread and MorsePlayer thread.

public class AKASignaler {
	private static final AKASignaler instance = new AKASignaler();
	public boolean pleaseShutUp;
	public boolean pleaseChangeButtonText;
	
	private AKASignaler() {
		pleaseShutUp = false;
		pleaseChangeButtonText = false;
	}
	
	public static AKASignaler getInstance() {
		return instance;
	}
}
