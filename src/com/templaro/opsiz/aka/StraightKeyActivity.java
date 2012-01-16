package com.templaro.opsiz.aka;

import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;

public class StraightKeyActivity extends Activity {
	
	private View mView;
	private int SAMPLE_RATE = 8000; //samples per second
	private int hertz = 800; //cycles per second
	private double sample[];
	private byte tone[];
	private AudioTrack audioTrack;
	private String TAG = "SK";
	private int numSamples;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sk);
        mView = (ImageView) findViewById(R.id.bootianView);
        mView.setOnTouchListener(bootianListener);
        buildSounds();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // The activity has become visible (it is now "resumed"); also,
        // the first time the program is run, this comes after onCreate.
        Log.i(TAG,"Setting up audioTrack");
        setupTrack();
    }
   
    @Override
    protected void onPause() {
        super.onPause();
        // Another activity is taking focus (this activity is about to be "paused").
        // Release the audioTrack resources as this activity stops being the center 
        // of attention (otherwise the audioTrack thread would keep rolling along, 
        // even when another activity is active.
        Log.i(TAG,"Release audioTrack resource");
        audioTrack.release();
    }
    
    private OnTouchListener bootianListener = new OnTouchListener() {
        public boolean onTouch(View v, MotionEvent event) {
        	int eventAction = event.getAction();
        	switch (eventAction) {
        	case MotionEvent.ACTION_DOWN: 
        		Log.i(TAG,"Button down.");
        		audioTrack.setStereoVolume(1f,1f);
        		break;
        	case MotionEvent.ACTION_UP:
        		Log.i(TAG,"Button up.");
        		audioTrack.setStereoVolume(0f,0f);
        		break;
        	default:
        		Log.i(TAG,"Hmmm...some other Motion Event for Bootian Listener");
        		break;
        	}
        	return true;
        }
    };
 
    /* Generate tone and silence */
 	private void buildSounds() {
 		//assume 8000hz samples rate, 800hz frequency, so 10 samples = 1 wavelength
 		numSamples = (int) (SAMPLE_RATE / hertz); // samples / cycle
 		double phaseAngle = 2 * Math.PI / (SAMPLE_RATE / hertz);
 		sample = new double[numSamples];
 		tone = new byte[2 * numSamples];
 		
 		for (int i = 0; i < numSamples; ++i) {
 			sample[i] = Math.sin(phaseAngle * i);
 			Log.i(TAG,String.format("Sample of %d is %f",i,sample[i]));
 		}
 		// convert to 16 bit pcm sound array; assumes the sample buffer is normalized.
 		int idx = 0;
 		for (final double dVal : sample) {
 			final short val = (short) ((dVal * 32767)); // scale to maximum amplitude
 			// in 16 bit wav PCM, first byte is the low order byte
 			tone[idx++] = (byte) (val & 0x00ff);
 			tone[idx++] = (byte) ((val & 0xff00) >>> 8);
 		}
 		Log.i(TAG,"Tone built.");
 	}
 	
 	private void setupTrack() {

		audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, SAMPLE_RATE,
				AudioFormat.CHANNEL_CONFIGURATION_MONO,
                AudioFormat.ENCODING_PCM_16BIT,2*numSamples,
                AudioTrack.MODE_STATIC);
		audioTrack.write(tone, 0, tone.length);	
 		audioTrack.setLoopPoints(0,numSamples,-1); //just keep looping over a single sine wave
 		audioTrack.setStereoVolume(0f,0f);
 		audioTrack.play();	
 		Log.i(TAG,"Started playing tone silently in new audioTrack");
 		
 	}
 	
}