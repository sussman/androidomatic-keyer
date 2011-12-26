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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;

import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

public class AndroidomaticKeyerActivity extends Activity {
	
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
	private Thread soundThread;
	private Button playButton;
	private Button clearMessageButton;
	private Button addMessageButton;
	private EditText keyerEditText;
	private int hertz = 800; 
	private int speed = 15;
	private int darkness = 0;
	private MorsePlayer player = new MorsePlayer(hertz, speed);
	private boolean cwMode = true;
	private ListView messageList;
	private TextView emptyMessageList;
	private ArrayList<String> messages = new ArrayList<String>();
	private int currentPick = 0;
	private EditText messageEditText;

	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "Initialize layout");
        setContentView(R.layout.main);
        LoadMessages();
        DisplayMessages();

        soundThread = new Thread(new Runnable() {
	            @Override
	            public void run() {
	        	   player.playMorse();
	            }
	        });
        
        playButton = (Button)findViewById(R.id.playButton);
        playButton.setOnClickListener(playButtonListener);
        clearMessageButton = (Button)findViewById(R.id.clearMessageButton);
        clearMessageButton.setOnClickListener(clearMessageButtonListener);
        addMessageButton = (Button)findViewById(R.id.addMessageButton);
        addMessageButton.setOnClickListener(addMessageButtonListener);
        
        keyerEditText = (EditText)findViewById(R.id.keyerEditText);
        
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
        case R.id.mode:
            switchMode();
            return true;
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
    	Log.i(TAG, "Switched to " + (String) ((cwMode) ? "Hell" : "CW" + " mode"));
    	cwMode = !cwMode;
	}

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
    	if(cwMode){
            menu
            .findItem(R.id.mode)
            .setTitle(R.string.toHell_label);
    	}
    	else {
    		menu
    		.findItem(R.id.mode)
    		.setTitle(R.string.toCW_label);
    	}
            return super.onPrepareOptionsMenu(menu);
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
    			Toast.makeText(getApplicationContext(), "Can't move up any further.",
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
    			Toast.makeText(getApplicationContext(), "Can't move down any further.",
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
    
    private OnClickListener playButtonListener = new OnClickListener() {
        public void onClick(View v) {
        	if (soundThread.isAlive()) {
        		stopMessage();
        	} else {
        		startMessage();
        	}
        }
    };
    
    
    /** Called whenever activity comes (or returns) to foreground. */
    @Override
    public void onStart() {
            super.onStart();
            getSettings();  // make sure user prefs are applied
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
    }
    
    
	// Play sound (infinite loop) on separate thread from main UI thread.
    void startMessage() {
    	if (soundThread.isAlive()) {
    		Log.i(TAG, "Trying to stop old thread first...");
    		stopMessage();
    	}
    	String playText = keyerEditText.getText().toString();
    	if (playText.length() == 0) {
    		Toast.makeText(getApplicationContext(), "Nothing to play. Enter a message first.",
			          Toast.LENGTH_SHORT).show();
      		Log.i(TAG,"Refused to play nothing");
    	}
    	else {
    		Log.i(TAG, "Starting morse thread with new message.");
        	player.setMessage(playText);
        	player.setSpeed(speed);
        	player.setTone(hertz);
        	soundThread = new Thread(new Runnable() {
    	           @Override
    	            public void run() {
    	        	   player.playMorse();
    	            }
    	        });
        	soundThread.start();
        	playButton.setCompoundDrawablesWithIntrinsicBounds(null,null,null, 
        			getResources().getDrawable(android.R.drawable.ic_media_pause));
    	}
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
    	playButton.setCompoundDrawablesWithIntrinsicBounds(null,null,null, 
    			getResources().getDrawable(android.R.drawable.ic_media_play));
    }

    
    private OnClickListener clearMessageButtonListener = new OnClickListener() {
        public void onClick(View v) {
        	keyerEditText.setText("");
        }
    };
    
    private OnClickListener addMessageButtonListener = new OnClickListener() {
        public void onClick(View v) {
        	int addAt = messages.size();
        	String addText = keyerEditText.getText().toString();
        	if (addText.length() == 0) {
        		Toast.makeText(getApplicationContext(), "Enter a message first, then save.",
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
        }
    };
    


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
                .setTitle("Edit Message")
                .setView(textEntryView)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                        	messages.set(currentPick, messageEditText.getText().toString());
                        	messageList.invalidateViews();
                        	Log.i(TAG, "OK'd the message edit.");
                        }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
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
   
    /** Called when activity stops */
    @Override
    protected void onStop(){
       super.onStop();
       savePreferences();
       saveMessages();
    }

	private void savePreferences() {
		 Log.i(TAG, "Saving current preferences");
	       SharedPreferences prefs = 
	               PreferenceManager.getDefaultSharedPreferences(getBaseContext());
	      SharedPreferences.Editor editor = prefs.edit();
	      editor.putInt("sidetone", hertz);
	      editor.putInt("wpm", speed);
	      editor.putInt("hellTiming", darkness);
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



