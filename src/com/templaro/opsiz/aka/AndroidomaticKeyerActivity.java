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

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;


public class AndroidomaticKeyerActivity extends Activity {
	private String TAG = "AndroidomaticKeyer";
	private Thread soundThread;
	private Button playButton;
	private EditText keyerEditText;
	private String activeMessage;  // eventually chosen from SQLite list
	private int hertz = 700;  // should be tweakable
	private int speed = 20;  // should be tweakable
	private MorsePlayer player = new MorsePlayer(hertz, speed);
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        playButton = (Button)findViewById(R.id.playButton);
        playButton.setOnClickListener(playButtonListener);
        
        keyerEditText = (EditText)findViewById(R.id.keyerEditText);
    }
    
    private OnClickListener playButtonListener = new OnClickListener() {
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
	        	   player.playMorse(keyerEditText.getText().toString());
	            }
	        });
    	soundThread.start();
    	playButton.setText("STOP");
    }
    
    void stopMessage() {
    	if ((soundThread != null) && (soundThread.isAlive())) {
    		Log.i(TAG, "Stopping morse thread.");
    		soundThread.interrupt();
    	}
    	playButton.setText("START");
    }
}


