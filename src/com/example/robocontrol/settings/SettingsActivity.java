package com.example.robocontrol.settings;

import java.util.Map;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.robocontrol.R;
import com.example.robocontrol.util.XBeeConnection;

public class SettingsActivity extends Activity {
	protected static EditTextPreference ip;
	protected static EditTextPreference port;
	protected static Activity currentActivity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState == null) {
			  getFragmentManager().beginTransaction()
              .replace(android.R.id.content, new SettingsFragment())
              .commit();
		}
		currentActivity = this;
		if (XBeeConnection.debug()){
			this.setTitle(this.getTitle()+" (Debug)");
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class SettingsFragment  extends PreferenceFragment implements OnSharedPreferenceChangeListener {

		public SettingsFragment() {
		}

		@Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	       
	        // Load the preferences from an XML resource
	        addPreferencesFromResource(R.xml.preferences);
	        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this); 
	        
	        Preference resetDialogPreference = (Preference) this.getPreferenceManager().findPreference("resetDialog");
	        if (resetDialogPreference != null) {
	        	 resetDialogPreference.setOnPreferenceChangeListener(new OnPreferenceChangeListener()
	             {  
	                 @Override  
	                 public boolean onPreferenceChange(Preference preference, Object newValue)   
	                 {  
	                	 Intent intent = currentActivity.getIntent();
	                	 currentActivity.overridePendingTransition(0, 0);
	                	 intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
	                	 currentActivity.finish();
	                	 currentActivity.overridePendingTransition(0, 0);
	                	 currentActivity.startActivity(intent);
	                    return false;
	                 }  
	             });     
	        }
	        		
	        ip = ((EditTextPreference) getPreferenceScreen().findPreference("ipAddress"));
	        port = ((EditTextPreference) getPreferenceScreen().findPreference("port"));
	    }

	   @Override
	   public void onResume() {
	      super.onResume();
	      Map<String, ?> pref = this.getPreferenceManager().getSharedPreferences().getAll();
	      for ( String key : pref.keySet()){
	    	  updatePreference(findPreference(key));
	      }
	    }

	    @Override
	    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
	      updatePreference(findPreference(key));
	      String numbers[] = sharedPreferences.getString("ipAddress","0").split("\\.");
	      if (numbers.length == 4) {
	           return;
	      }else{
	    	  Toast.makeText(currentActivity, "Should be a IP-Adress", Toast.LENGTH_SHORT).show();
	           ip.setText("192.168.1.10");    
	      }
	    }

	    private void updatePreference(Preference preference) {
	    	if (preference instanceof ListPreference) {
	    		ListPreference listPreference = (ListPreference) preference;
	    		listPreference.setSummary(listPreference.getEntry());
	    	}
	    	if (preference instanceof EditTextPreference) {
	            EditTextPreference editTextPref = (EditTextPreference) preference;
	            if (preference.getTitle().toString().contains("assword"))
	            {
	            	preference.setSummary("******");
	            } else {
	            	preference.setSummary(editTextPref.getText());
	            }
	        }
	    	if (preference instanceof MultiSelectListPreference) {
	    	  EditTextPreference editTextPref = (EditTextPreference) preference;
	    	  preference.setSummary(editTextPref.getText());
	    	}
	    }
	    @Override
		public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference    preference) {
		super.onPreferenceTreeClick(preferenceScreen, preference);

		// If the user has clicked on a preference screen, set up the action bar
		if (preference instanceof PreferenceScreen) {
		    initializeActionBar((PreferenceScreen) preference);
		}

		return false;
		}
		
		/** Sets up the action bar for an {@link PreferenceScreen} */
	    public static void initializeActionBar(PreferenceScreen preferenceScreen) {
	    final Dialog dialog = preferenceScreen.getDialog();

	    if (dialog != null) {
	        // Inialize the action bar
	        dialog.getActionBar().setDisplayHomeAsUpEnabled(true);

	        // Apply custom home button area click listener to close the PreferenceScreen because PreferenceScreens are dialogs which swallow
	        // events instead of passing to the activity
	        // Related Issue: https://code.google.com/p/android/issues/detail?id=4611
	        View homeBtn = dialog.findViewById(android.R.id.home);

	        if (homeBtn != null) {
	            OnClickListener dismissDialogClickListener = new OnClickListener() {
	                @Override
	                public void onClick(View v) {
	                    dialog.dismiss();
	                }
	            };

	            // Prepare yourselves for some hacky programming
	            ViewParent homeBtnContainer = homeBtn.getParent();

	            // The home button is an ImageView inside a FrameLayout
	            if (homeBtnContainer instanceof FrameLayout) {
	                ViewGroup containerParent = (ViewGroup) homeBtnContainer.getParent();

	                if (containerParent instanceof LinearLayout) {
	                    // This view also contains the title text, set the whole view as clickable
	                    ((LinearLayout) containerParent).setOnClickListener(dismissDialogClickListener);
	                } else {
	                    // Just set it on the home button
	                    ((FrameLayout) homeBtnContainer).setOnClickListener(dismissDialogClickListener);
	                }
	            } else {
	                // The 'If all else fails' default case
	                homeBtn.setOnClickListener(dismissDialogClickListener);
	            }
	        }    
	    }
	    }
		 
	}
}
