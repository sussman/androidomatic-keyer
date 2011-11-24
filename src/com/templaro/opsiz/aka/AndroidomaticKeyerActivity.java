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
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;


public class AndroidomaticKeyerActivity extends Activity {
	
	private String TAG = "AndroidomaticKeyer";
	private Thread soundThread;
	private Button playButton;
	private EditText keyerEditText;
	private SeekBar speedBar;
	private TextView speedLabel;
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
        
        speedBar = (SeekBar)findViewById(R.id.speedBar);
        speedLabel = (TextView)findViewById(R.id.speedLabel);
        speedBar.setOnSeekBarChangeListener(speedBarListener);
        speedBar.setMax(45);  // actually WPM speed range is 5-50.
    }
    
    private OnClickListener playButtonListener = new OnClickListener() {
        public void onClick(View v) {
        	if ((soundThread != null) && (soundThread.isAlive())) {
        		stopMessage();
        	} else {
        		startMessage();
        	}
        }
    };
    
    
    private OnSeekBarChangeListener speedBarListener = new OnSeekBarChangeListener() {
    	public void onProgressChanged(SeekBar bar, int progress, boolean fromUser) {
    		speedLabel.setText(String.format("%d WPM", progress + 5));
    	}
    	
    	public void onStartTrackingTouch(SeekBar seekBar) {
    	}
    	
    	public void  onStopTrackingTouch(SeekBar seekBar) {
    	}
    };
    
    
	// Play sound (infinite loop) on separate thread from main UI thread.
    void startMessage() {
    	if ((soundThread != null) && (soundThread.isAlive())) {
    		Log.i(TAG, "Trying to stop old thread first...");
    		stopMessage();
    	}
    	Log.i(TAG, "Starting morse thread with new message.");
    	player.setMessage(keyerEditText.getText().toString());
    	soundThread = new Thread(new Runnable() {
	           @Override
	            public void run() {
	        	   player.playMorse();
	            }
	        });
    	soundThread.start();
    	playButton.setText("STOP");
    }
    
    void stopMessage() {
    	if (soundThread.isAlive()) {
    		Log.i(TAG, "Stopping existing morse thread.");
    		soundThread.interrupt();
    		try {
    			soundThread.join();  // wait for thread to die
    		} catch (InterruptedException e) {
    			Log.i(TAG, "Main thread interrupted while waiting for child to die!");
    		}
    	}
    	playButton.setText("START");
    }
}


