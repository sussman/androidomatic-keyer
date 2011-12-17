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
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.ArrayAdapter;

public class AndroidomaticKeyerActivity extends Activity {
	
	private String TAG = "AndroidomaticKeyer";
	private Thread soundThread;
	private Button playButton;
	private EditText keyerEditText;
	private SeekBar speedBar;
	private TextView speedLabel;
	private SeekBar toneBar;
	private TextView toneLabel;
	private int hertz = 700;  // should be tweakable
	private int speed = 15;  // should be tweakable
	private MorsePlayer player = new MorsePlayer(hertz, speed);
	private ListView list;
	private String[] messages;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        //TODO: Populate String array from SQLite database
        messages = getResources().getStringArray(R.array.messages_array);
        
        ListView list = (ListView)findViewById(R.id.list);
        //TODO: A custom listview -- smaller fonts, etc.
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, 
        		android.R.layout.simple_list_item_1, messages);
        list.setAdapter(adapter);
        registerForContextMenu(list);

  	  	list.setOnItemClickListener(new OnItemClickListener() {
  	  		public void onItemClick(AdapterView<?> parent, View view,
  	  				int position, long id) {
  	    		keyerEditText.setText(((TextView) view).getText().toString());
  	  		}
  	  	});
        
      
        soundThread = new Thread(new Runnable() {
	            @Override
	            public void run() {
	        	   player.playMorse();
	            }
	        });
        
        playButton = (Button)findViewById(R.id.playButton);
        playButton.setOnClickListener(playButtonListener);
        
        keyerEditText = (EditText)findViewById(R.id.keyerEditText);
        
        speedBar = (SeekBar)findViewById(R.id.speedBar);
        speedLabel = (TextView)findViewById(R.id.speedLabel);
        speedBar.setOnSeekBarChangeListener(speedBarListener);
        speedBar.setMax(45);  // actually WPM speed range is 5-50.
        speedBar.setProgress(10);  // so the starting val here is 15 wpm.
        
        toneBar = (SeekBar)findViewById(R.id.toneBar);
        toneLabel = (TextView)findViewById(R.id.toneLabel);
        toneBar.setOnSeekBarChangeListener(toneBarListener);
        toneBar.setMax(1000);  // tone range is 500-1500
        toneBar.setProgress(200);  // so the starting val here is 700hz.
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
        case R.id.mode:
            switchMode();
            return true;
        case R.id.help:
        	Intent i = new Intent(this, Help.class);
    		startActivity(i);
            return true;
        case R.id.settings:
        	editSetting();
        	return true;
        case R.id.exit:
        	finish();
        	return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
    
    private void switchMode() {
	}
    
    private void editSetting() {
    }
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
        ContextMenuInfo menuInfo) {
      if (v.getId()==R.id.list) {
        menu.setHeaderTitle("Message Options");
        String[] menuItems = getResources().getStringArray(R.array.message_options_array);
        for (int i = 0; i<menuItems.length; i++) {
          menu.add(Menu.NONE, i, i, menuItems[i]);
        }
      }
    }
    
    private OnClickListener playButtonListener = new OnClickListener() {
        public void onClick(View v) {
        	if (soundThread.isAlive()) {
        		stopMessage();
        	} else {
        		startMessage();
        	}
        }
    };
    
    private OnSeekBarChangeListener speedBarListener = new OnSeekBarChangeListener() {
    	private boolean was_playing = false;
    	
    	public void onProgressChanged(SeekBar bar, int progress, boolean fromUser) {
    		speedLabel.setText(String.format("%d WPM", progress + 5));
    	}
    	
    	public void onStartTrackingTouch(SeekBar seekBar) {
    		if (soundThread.isAlive())
    			was_playing = true;
    		stopMessage();  // user has begun changing WPM; kill any current sound
    	}
    	
    	public void onStopTrackingTouch(SeekBar seekBar) {
    		if (was_playing) {
    			startMessage(); // user finished changing WPM; restart sound if necessary
    			was_playing = false;
    		}
    	}
    };
    
    
    private OnSeekBarChangeListener toneBarListener = new OnSeekBarChangeListener() {
    	private boolean was_playing = false;
    	
    	public void onProgressChanged(SeekBar bar, int progress, boolean fromUser) {
    		toneLabel.setText(String.format("%d Hz", progress + 500));
    	}
    	
    	public void onStartTrackingTouch(SeekBar seekBar) {
    		if (soundThread.isAlive())
    			was_playing = true;
    		stopMessage();  // user has begun changing tone; kill any current sound
    	}
    	
    	public void onStopTrackingTouch(SeekBar seekBar) {
    		if (was_playing) {
    			startMessage(); // user finished changing tone; restart sound if necessary
    			was_playing = false;
    		}
    	}
    };
    
    
	// Play sound (infinite loop) on separate thread from main UI thread.
    void startMessage() {
    	if (soundThread.isAlive()) {
    		Log.i(TAG, "Trying to stop old thread first...");
    		stopMessage();
    	}
    	Log.i(TAG, "Starting morse thread with new message.");
    	player.setMessage(keyerEditText.getText().toString());
    	player.setSpeed(speedBar.getProgress() + 5);
    	player.setTone(toneBar.getProgress() + 500);
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


