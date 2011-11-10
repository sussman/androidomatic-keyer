package com.templaro.opsiz.aka;

import android.app.Activity;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;


public class AndroidomaticKeyerActivity extends Activity {
	private String TAG = "AndroidomaticKeyer";
	private Thread soundThread = null;
	private MorsePlayer player;
	private String activeMessage;  // eventually chosen from SQLite list
	private int hertz = 800;  // should be tweakable
	private int speed = 12;  // should be tweakable
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        Button bigButton = (Button)findViewById(R.id.bigButton);
        bigButton.setOnClickListener(bigButtonListener);
        
    }
    
    private OnClickListener bigButtonListener = new OnClickListener() {
        public void onClick(View v) {
        	if((soundThread != null) && (soundThread.isAlive())) {
        		stopMessage();
        	} else {
        		startMessage("hello bootians");
        	}
        }
    };
    
	// This spawns a new thread, since we don't want the sine-data generation
	// nor the playing of sound to interfere with the main UI thread.
    void startMessage(String message) {
    	Log.i(TAG, "Starting morse thread.");
    	activeMessage = message;
    	soundThread = new Thread(new Runnable() {
	           @Override
	            public void run() {
	        	   player = new MorsePlayer(hertz, speed);
	        	   player.playMorse(activeMessage);
	            }
	        });
    	soundThread.start();
    }
    
    void stopMessage() {
    	if ((soundThread != null) && (soundThread.isAlive())) {
    		Log.i(TAG, "Stopping morse thread.");
    		soundThread.interrupt();
    	}
    }
}


