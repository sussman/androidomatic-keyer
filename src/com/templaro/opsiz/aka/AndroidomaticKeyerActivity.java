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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.AudioTrack.OnPlaybackPositionUpdateListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

public class AndroidomaticKeyerActivity extends Activity implements OnClickListener {
	
	//Context Menu Options
	private static final int MENU_EDIT = 0;
	private static final int MENU_COPY = 1;
	private static final int MENU_MOVE_UP = 2;
	private static final int MENU_MOVE_DOWN = 3;
	private static final int MENU_DELETE = 4;
	
	//Text file to store messages
	private static final String MESSAGE_STORE = "MessageStore.txt";
	
    // Dialog boxes we manage. Passed to showDialog().
    // Processed in onDialogCreate().
	private static final int DIALOG_EDIT_MESSAGE= 1;
	
	private String TAG = "AndroidomaticKeyer";
	
	//preference items
	private int hertz = 800; 
	private int speed = 15;
	private int darkness = 0;
	private String callsign;
	private String beacon_interval = "15";
	private String beacon_text = "Beacon Text";
	private boolean suppress_other_sound = true;
	private boolean qrss = false;
	private String qrss_rate = "10";
	
	private boolean sound_playing = false;
	
	private Thread soundThread;
	private AKASignaler signaler = AKASignaler.getInstance();
	private Button playButton;
	private Button clearMessageButton;
	private Button addMessageButton;
	private EditText keyerEditText;
	private TextView modeTextView;
	private TextView beaconTextView;
	private ImageView bootianImageView;
	

	private MorsePlayer player; //instantiate as needed rather than
	private HellPlayer hplayer;  //build sounds for both every time activity starts
	private boolean cwMode = true;
	private boolean beaconOn = false;
	private ListView messageList;
	private TextView emptyMessageList;
	private ArrayList<String> messages = new ArrayList<String>();
	private int currentPick = 0;
	private EditText messageEditText;
	
	private GeoHelper mGeo;
	private Handler mHandler = new Handler();
	private long mStartTime;
	

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "Initialize layout");
        setContentView(R.layout.main);
        LoadMessages();
        DisplayMessages();
        playButton = (Button)findViewById(R.id.playButton);
        playButton.setOnClickListener(this);
        clearMessageButton = (Button)findViewById(R.id.clearMessageButton);
        clearMessageButton.setOnClickListener(this);
        addMessageButton = (Button)findViewById(R.id.addMessageButton);
        addMessageButton.setOnClickListener(this);
        modeTextView = (TextView)findViewById(R.id.mode_textView);
        modeTextView.setOnClickListener(this);
        beaconTextView = (TextView)findViewById(R.id.beacon_textView);
        beaconTextView.setOnClickListener(this);
        bootianImageView = (ImageView)findViewById(R.id.bootian_imageView);
        bootianImageView.setOnClickListener(this);
        
        keyerEditText = (EditText)findViewById(R.id.keyerEditText);
        
        mGeo = new GeoHelper(this);
    }
    
 
	private void LoadMessages() {
        File file = getFileStreamPath(MESSAGE_STORE);
        boolean fileReadSucceeded=false;
        if(file.exists()) {
        	Log.i(TAG,"Message text file found, attempting to import from internal storage");
        	 try
        	   {
        		 BufferedReader br = new BufferedReader(new FileReader(file));
        		    String line;
        		    while ((line = br.readLine()) != null) {
        		        messages.add(line);
        		    }
        		    br.close();    
        		    fileReadSucceeded = true;
        	   }
        	   catch(IOException e)
        	   { 
        		   Log.i(TAG, "IO Exception trying to import messages from internal storage");
        	   }
       } 	 
       if (!fileReadSucceeded) {
        	Log.i(TAG, "Failed to read messages from internal storage -- importing initial messages from XML");
        	String [] importedMessages = getResources().getStringArray(R.array.messages_array);
        	for (String s: importedMessages) {
        		messages.add(s);
        	}
        	Log.i(TAG, "Canned messages imported");	
        }
	}
	
	private void setMuting(boolean isSolo) {
		// Give this app full control of phone sound or not.
		// CW is music to android's ears
		// Do other activities need to call this as well? TOCONSIDER refactoring out of this activity
		AudioManager mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
		mAudioManager.setStreamSolo(AudioManager.STREAM_MUSIC, isSolo);
		Log.i(TAG,"Setting the audio stream to " + (String) ((isSolo) ? "Music Stream Solo" : "Unmuted" + " mode"));
		// TODO: Ideally, provide some sort of visual feedback about on main screen
		//Note -- probably need to overhaul entire system using phone stream & indicate call in progress
		//to fully block
		//Also need to somehow suppress pending timed activities, if any
	}
	
    private void DisplayMessages() {
    	messageList = (ListView)findViewById(R.id.messageList);
    	emptyMessageList = (TextView)findViewById(R.id.emptyMessageList);
    	if (messages.size() > 0) {
    		emptyMessageList.setVisibility(View.INVISIBLE);
    		Log.i(TAG, "Hiding the empty list warning");
    	}
        //TODO: A custom listview -- smaller fonts, etc.
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, 
        		android.R.layout.simple_list_item_1, messages);
        messageList.setAdapter(adapter);
        registerForContextMenu(messageList);

  	  	messageList.setOnItemClickListener(new OnItemClickListener() {
  	  		public void onItemClick(AdapterView<?> parent, View view,
  	  				int position, long id) {
  	    		keyerEditText.setText(((TextView) view).getText().toString());
  	  		}
  	  	});
  	  Log.i(TAG, "Displayed Message Listview");	
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
        case R.id.help:
    		startActivity(new Intent(this, Help.class));
            return true;
        case R.id.settings:
        	startActivity(new Intent(this, Settings.class));
        	return true;	
        default:
            return super.onOptionsItemSelected(item);
        }
    }
    
    private void switchMode() {
    	//TODO: Revise UI to reflect change of mode
    	//(in addition to menu button text changing)
    	cwMode = !cwMode;
    	Log.i(TAG, "Switched to " + (String) ((cwMode) ? "CW" : "Hell" + " mode"));
    	stopMessage();
    	
	}
    
    private void armBeacon() {
    	Intent intent = new Intent(AndroidomaticKeyerActivity.this, BeaconSqualk.class);
        PendingIntent sender = PendingIntent.getBroadcast(AndroidomaticKeyerActivity.this,
                0, intent, 0);
        
        // We want the alarm to go off beacon_period minutes from now.
        
        long beacon_period = Integer.parseInt(beacon_interval) * 60000; //period in milliseconds
        long firstTime = SystemClock.elapsedRealtime() + beacon_period; //in milliseconds
       
        // Schedule the repeating alarm
        AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
        am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                        firstTime, beacon_period, sender);
        //alarm manager's wake lock is now engaged; while it runs, there will be no deep sleeps,
        //so uptimeMillis will track elapsed real time.
        mStartTime = SystemClock.uptimeMillis();
        
    	beaconOn = true;
        Log.i(TAG, String.format("Armed beacon for %s minutes", beacon_interval));
        //TODO: Save beacon status in onpause/resume
        
        mHandler.removeCallbacks(mUpdateTimeTask);
        mHandler.postDelayed(mUpdateTimeTask, 100);
    }
    
    private Runnable mUpdateTimeTask = new Runnable() {
    	   public void run() {
    	       
    	       
    	       long beacon_period = Integer.parseInt(beacon_interval) * 60000;
    	       long elapsed = SystemClock.uptimeMillis() - mStartTime;
    	       
    	       long millis = beacon_period - elapsed % beacon_period;
    	       
    	       int seconds = (int) (millis / 1000);
    	       int hours = (int) seconds/3600;
    	       seconds = seconds - hours*3600;		  
    	       int minutes = seconds / 60;
    	       seconds     = seconds % 60;

    	       beaconTextView.setText(String.format("%02d:%02d:%02d",hours,minutes,seconds));	
    	       
    	       //The whacky integer division below yields an update very second
    	       //of uptime.
    	       mHandler.postAtTime(this, mStartTime + elapsed/1000*1000 + 1000);
    	   }
    	};
    
    
    private void disarmBeacon() {
    	
    	Intent intent = new Intent(AndroidomaticKeyerActivity.this, BeaconSqualk.class);
        PendingIntent sender = PendingIntent.getBroadcast(AndroidomaticKeyerActivity.this,
                0, intent, 0);
        
        //Cancel the alarm.
        AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
        am.cancel(sender);

    	beaconOn = false;
    	
    	mHandler.removeCallbacks(mUpdateTimeTask);
        Log.i(TAG, "Beacon disarmed");
        //TODO: save beacon status in onpause/resume
    }

    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
        ContextMenuInfo menuInfo) {
      if (v.getId()==R.id.messageList) {
        menu.setHeaderTitle(getResources().getString(R.string.messageOptions));
        String[] menuItems = getResources().getStringArray(R.array.message_options_array);
        for (int i = 0; i<menuItems.length; i++) {
          menu.add(Menu.NONE, i, i, menuItems[i]);
        }
      }
    }
    
    @Override
    public boolean onContextItemSelected(MenuItem item) {
    	AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
    	int menuItemIndex = item.getItemId();
    	currentPick = info.position;
    	String listItemName = messages.get(currentPick);
    	switch (menuItemIndex) {
    	case MENU_EDIT:
    		showDialog(DIALOG_EDIT_MESSAGE);
    		Log.i(TAG, String.format("Editing message %s at index %d", 
    				listItemName, currentPick));
    		return true;
    	case MENU_COPY:
    		Log.i(TAG, String.format("Copying message at index %d", info.position));
    		messages.add(info.position, messages.get(currentPick));
    		messageList.invalidateViews();
    		return true;
    	case MENU_MOVE_UP:
    		if(info.position>0){
    			Log.i(TAG, String.format("Try to move up message at index %d", currentPick));
    			messages.add(currentPick-1,messages.get(currentPick));
    			messages.remove(currentPick+1);
    			messageList.invalidateViews();
    		}
    		else {
    			Toast.makeText(getApplicationContext(), R.string.cant_move_up,
    			          Toast.LENGTH_SHORT).show();
    		}
    		return true;
    	case MENU_MOVE_DOWN:
    		Log.i(TAG, String.format("Try to move down message at index %d", currentPick));
    		if(currentPick<(messages.size()-1)){
    			messages.add(currentPick,messages.get(currentPick+1));
    			messages.remove(currentPick+2);
    			messageList.invalidateViews();
    		}
    		else {
    			Toast.makeText(getApplicationContext(), R.string.cant_move_down,
    			          Toast.LENGTH_SHORT).show();
    		}
    		return true;
    	case MENU_DELETE:
    		Log.i(TAG, String.format("Delete message at index %d", currentPick));
    		messages.remove(currentPick);
    		messageList.invalidateViews();
    		if (messages.size() == 0) {
        		emptyMessageList.setVisibility(View.VISIBLE);
        		Log.i(TAG, "Deleted the last entry - display empty list warning");
        	}
    		return true;
    	default:
    		return super.onOptionsItemSelected(item);
    	}
    }  
    
    /** Called whenever activity comes (or returns) to foreground. */
    @Override
    public void onStart() {
            super.onStart();
            getSettings();  // makes sure user prefs are applied
    }
    
    /** Called by onStart(), to make sure prefs are loaded whenever we return
     *  to the main AKA activity (e.g. after backing out of the 
     *  Preferences activity)
     */
    private void getSettings() {
    		Log.i(TAG, "Loading saved preferences");
             SharedPreferences prefs = 
                     PreferenceManager.getDefaultSharedPreferences(getBaseContext());
             hertz = prefs.getInt("sidetone", 800);
             speed = prefs.getInt("wpm", 15);
             darkness = prefs.getInt("hellTiming", 0);
             beacon_interval = prefs.getString("beacon_interval", "15");
             beacon_text = xmlImport(prefs.getString("beacon_text", "QTH # DE @/B @/B"));
             suppress_other_sound = prefs.getBoolean("suppress_other_sound",true);
             callsign = xmlImport(prefs.getString("callsign","UR CALL"));
             qrss = prefs.getBoolean("qrss", false);
             qrss_rate = prefs.getString("qrss_rate", "10");
             beaconOn = prefs.getBoolean("beacon_status", false);
    }
    
    
	// Play sound on separate thread from main UI thread.
    void startMessage() {
    	
    	//Instantiate the appropriate player, when needed
    	if(cwMode) {
    		if(null == player) {
    		   Log.i(TAG,"Instantiating a new morse player");
    		   player = new MorsePlayer(hertz,speed);
    		}
    	}
    	else {
    		if(null == hplayer) {
    			   Log.i(TAG,"Instantiating a hell player.");
    			   hplayer = new HellPlayer(darkness);
    		}
    	}
    	
    	// LOOK UPON MY NESTED RUNNABLES, YE MIGHTY, AND DESPAIR
    	
         soundThread = new Thread(new Runnable() {  // old soundThread gets garbage collected
              @Override
              public void run() {
            	   // plays message once then dies
            	   if(cwMode) 
            		   player.playMorse();
            	   else
            		   hplayer.playHell();
                   playButton.post(new Runnable() {
                	   // called by the UI thread when the soundThread returns
                	   public void run() {
                		   // soundThread has returned from pushing data into audiotrack buffer;
                		   // presumably audiotrack is still playing.
                		   // Set a callback to fire when the audiotrack hits the end of data.
                			signaler.audioTrack.setNotificationMarkerPosition(signaler.msgSize / 2);
                			signaler.audioTrack.setPlaybackPositionUpdateListener(new OnPlaybackPositionUpdateListener() {
                	            @Override
                	            public void onPeriodicNotification(AudioTrack track) {
                	                // nothing to do
                	            }
                	            @Override
                	            public void onMarkerReached(AudioTrack track) {
                	                Log.i(TAG, "AudioTrack played to end of message; time to die.");
                	                stopMessage();
                	                return;
                	            }
                	        });
                			Log.i(TAG, "Set marker-callback on " + (signaler.msgSize -2));
                	   }
                   });
              }
        });

    	String playText = keyerEditText.getText().toString();
    	if (!emptyMessage(playText)) {
    		playText = expandMessage(playText);
    		keyerEditText.setText(playText);
    		Log.i(TAG, "Starting sound thread with " + (String) ((cwMode) ? "CW" : "Hell" + " message"));
    		if(cwMode) {
    			player.setMessage(playText);
    		}
    		else {
    			hplayer.setMessage(playText);
    		}
    		sound_playing = true;
        	playButton.setCompoundDrawablesWithIntrinsicBounds(null,null,null, 
        			getResources().getDrawable(android.R.drawable.ic_media_pause));
            soundThread.start();  // injects sound data once, then commits suicide
    	}
    }
    
    boolean emptyMessage(String mMsg) {
    	if (mMsg.length() == 0) {
    		Toast.makeText(getApplicationContext(), R.string.nothing_to_play,
			          Toast.LENGTH_SHORT).show();
      		Log.i(TAG,"Refused to play nothing");
      		return true;
    	}
    	else {
    		return false;
    	}
    }
    
    String expandMessage(String mMsg) {
		Log.i(TAG, "Expanding text substitutions");
		mMsg = mMsg.replaceAll("@",callsign);
		mMsg = mMsg.replaceAll("#",mGeo.currentDecimalLocation());
		mMsg = mMsg.replaceAll("!",mGeo.maidenheadGrid());
		return mMsg;
    }
    
    private void stopMessage() {
    	signaler.killAudioTrack(); 
    	sound_playing = false;
    	playButton.setCompoundDrawablesWithIntrinsicBounds(null,null,null, 
    			getResources().getDrawable(android.R.drawable.ic_media_play));
    }
    
    public void onClick(View v){
    	switch (v.getId()) {
    	case R.id.playButton:
    		Log.i(TAG, "Play/pause button clicked.");
        	if (sound_playing) {
        		Log.i(TAG, "STOP button clicked; killing audiotrack.");
        		stopMessage();
        	} else {
        		startMessage();
        	}
    		break;
    	case R.id.clearMessageButton:
    		Log.i(TAG,"Clear button clicked; wiping Edit Text.");
    		keyerEditText.setText("");
    		break;
    	case R.id.addMessageButton:
    		Log.i(TAG,"Add message button clicked; attempting to add message to list");
    		int addAt = messages.size();
        	String addText = keyerEditText.getText().toString();
        	if (addText.length() == 0) {
        		Toast.makeText(getApplicationContext(), R.string.enter_message_first,
  			          Toast.LENGTH_SHORT).show();
        		Log.i(TAG,"Refused to save an empty string to a slot");
        	}
        	else {
        	if (messages.size() == 0) {
        		emptyMessageList.setVisibility(View.INVISIBLE);
        	}
        	messages.add(addAt, addText);
        	messageList.invalidateViews();
        	Log.i(TAG, String.format("Added new message at index %d", addAt));
        	}
    		break;
    	case R.id.mode_textView:
    		Log.i(TAG,"Mode status toggled.");
    		switchMode();
    		if(cwMode){
                modeTextView.setText(R.string.toCW_label);
        	}
        	else {
        		modeTextView.setText(R.string.toHell_label);
        	}
    		break;
    	case R.id.beacon_textView:	
    		Log.i(TAG,"Beacon status toggled.");
        	if(beaconOn) {
        		beaconTextView.setText(R.string.cancel_beacon_label);
        		disarmBeacon();
        	}
        	else {
        		beaconTextView.setText(R.string.enable_beacon_label);
        		//TODO: kick off count down display to beacon firing
        		armBeacon();
        	}
    		break;
    	case R.id.bootian_imageView:
    		Log.i(TAG,"Bootian clicked, switching to straight key activity.");
    		startActivity(new Intent(this, StraightKeyActivity.class));
    		break;
    	}
    }
    
    
    /** Have our activity manage and persist dialogs, showing and hiding them */
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
        case DIALOG_EDIT_MESSAGE:
                LayoutInflater factory = LayoutInflater.from(this);
                final View textEntryView = factory.inflate(R.layout.edit_message_prompt, null);
                messageEditText = (EditText) textEntryView.findViewById(R.id.edit_message_prompt_entry);
                messageEditText.setText("initial value");
                return new AlertDialog.Builder(AndroidomaticKeyerActivity.this)
                .setTitle(R.string.edit_message_dialog_title)
                .setView(textEntryView)
                .setPositiveButton(R.string.edit_message_dialog_ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                        	messages.set(currentPick, messageEditText.getText().toString());
                        	messageList.invalidateViews();
                        	Log.i(TAG, "OK'd the message edit.");
                        }
                })
                .setNegativeButton(R.string.edit_message_dialog_cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                        	Log.i(TAG, "Nix'd the message edit.");
                        }
                })
                .create();
        default:
        	return null;
        }
	}


    /** Have our activity prepare dialogs before displaying them */
    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
        super.onPrepareDialog(id, dialog);
        switch(id) {
        case DIALOG_EDIT_MESSAGE:
        	 messageEditText.setText(messages.get(currentPick));
        	 break;
        default:
        	return;
        }
    }
    
    
    @Override
    protected void onResume() {
        super.onResume();
        // The activity has become visible (it is now "resumed").
        setMuting(suppress_other_sound);
        mGeo.locationUpdatesOn();
        if(beaconOn) mHandler.post(mUpdateTimeTask);
    }
   
    @Override
    protected void onPause() {
        super.onPause();
        // Another activity is taking focus (this activity is about to be "paused").
        //consider moving save pref and save messages here, as this is more reliably called
        //than stop
        if(suppress_other_sound) 
        	setMuting(false);
        mGeo.locationUpdatesOff();
        signaler.killAudioTrack(); 
        if (player != null) player = null;
        if (hplayer != null) hplayer = null;
        //assures a fresh player each time, with updated settings
    }
        
    @Override
    protected void onStop(){
       super.onStop();
    // The activity is no longer visible (it is now "stopped")
       savePreferences();
       saveMessages();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // The activity is about to be destroyed.
        // Kill any still running threads
        // Release all resources
    }
    
    protected String xmlExport(String str) {
    	//XML 1.0, section 2.4 recommends escaping &, < and >
    	//
    	str.replaceAll("<", "&lt;");
    	str.replaceAll(">", "&gt;");
    	str.replaceAll("&", "&amp;");
    	return str;
    }
    
    protected String xmlImport(String str){
    	str.replaceAll("&lt;","<");
    	str.replaceAll("&gt;",">");
    	str.replaceAll("&amp;","&");
    	return str;
    }
    

	private void savePreferences() {
		 Log.i(TAG, "Saving current preferences");
	       SharedPreferences prefs = 
	               PreferenceManager.getDefaultSharedPreferences(getBaseContext());
	      SharedPreferences.Editor editor = prefs.edit();
	      editor.putInt("sidetone", hertz);
	      editor.putInt("wpm", speed);
	      editor.putInt("hellTiming", darkness);
	      editor.putString("beacon_interval", beacon_interval);
	      editor.putString(xmlExport("beacon_text"), beacon_text);
	      editor.putBoolean("suppress_other_sound",suppress_other_sound);
	      editor.putString(xmlExport("callsign"), callsign);
	      editor.putBoolean("qrss", qrss);
	      editor.putString("qrss_rate",qrss_rate);
	      editor.putBoolean("beacon_status",beaconOn);
	      editor.commit();
	}


	private void saveMessages()  {
		//if there are no messages to store, delete the custom file
		if (messages.size() == 0) {
			 File file = getFileStreamPath(MESSAGE_STORE);
		        if(file.exists()) {
		        	Log.i(TAG,"Attempting to delete custom message text file");
		        	//Note: file.delete doesn't throw an IOException when it fails
		        	if(file.delete()) 
		        		Log.i(TAG,"Deleted custom message text file");
		        	else
		        		Log.i(TAG,"Unable to delete custom text file");
		        }
		}
		else {
			//if not extent, creates files/MESSAGE_STORE directory and file
			//under ...package/data/data/, otherwise overwrites it.
			Log.i(TAG, "Writing messages to internal storage");
			try {
				FileOutputStream fOut = openFileOutput(MESSAGE_STORE, MODE_WORLD_READABLE);
				BufferedWriter buf = new BufferedWriter(new OutputStreamWriter(fOut));
				for (String s : messages) {
					buf.write(s+"\n");
				}
				//closing, so no flush required.	
				buf.close();
			}
			catch (IOException e){
				Log.i(TAG,"IO Exception");
			}
		}	
	}
}



