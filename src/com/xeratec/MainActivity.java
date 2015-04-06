package com.xeratec;

import android.app.ActionBar.OnMenuVisibilityListener;
import android.app.Activity;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import com.xeratec.robocontrol.R;
import com.xeratec.robocontrol.settings.SettingsActivity;
import com.xeratec.robocontrol.util.XBeeConnection;


public class MainActivity extends Activity {
	private int i = 0;
	private int timer=0;
	private Activity activity;
	private Toast toast;
	public boolean isOverflowMenuOpen  = false;
	public Runnable Thread1;
	public String title;
	


	@Override
	protected void onCreate(Bundle savedInstanceState) {	

		super.onCreate(savedInstanceState);
		
		title = getString(R.string.app_name);
		if (XBeeConnection.debug()){
			this.setTitle(this.getTitle()+" (Debug)");
		}
		

		Thread1 = new Runnable() {			
			public void run() {
				do{
					try {
							timer = 0;
							Thread.sleep(1000);
							if (timer==0) i = 0;
					
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}while(!activity.isFinishing());
			}
		};
		activity = this;
		new Thread(Thread1).start();
		toast = Toast.makeText(this,"",100);

	    activity.getActionBar().addOnMenuVisibilityListener(new OnMenuVisibilityListener() {
	    @Override
	        public void onMenuVisibilityChanged(boolean isVisible) {
	            // TODO Auto-generated method stub
	            isOverflowMenuOpen = isOverflowMenuOpen ? false : true;
	            i++;
	            timer=1;
	            
	            
	            if (i>=5){
	            	if (XBeeConnection.debug()){
	            		toast.setText(getString(R.string.toast_debug_1)+" "+(10-i)+" "+getString(R.string.toast_debug_3));
	            		toast.show();
	            	}else{
	            		toast.setText(getString(R.string.toast_debug_1)+" "+(10-i)+" "+getString(R.string.toast_debug_2));
	            		toast.show();
	            	}
	            }
	            if (i==10){
	            	XBeeConnection.debug(!XBeeConnection.debug());
	            	if (XBeeConnection.debug()){
	            		toast.setText( getString(R.string.toast_debug_activated));
	            		activity.setTitle(activity.getTitle()+" (Debug)");
	            		timer = 0; i = 0;
	            		toast.show();
	            	}else{
	            		toast.setText(getString(R.string.toast_debug_deactivated));
	            		activity.setTitle(title);
	            		toast.show();
	            		timer = 0; i = 0;
	            	}

	            }
	    }    
	    });
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
		getMenuInflater().inflate(R.menu.main, menu);
	        setContentView(R.layout.activity_main);
	        
		return true;
	}
	
	

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			Intent intent = new Intent(this, SettingsActivity.class);
			startActivity(intent);	
			
		}else if (id == R.id.action_wifi){
			startActivity(new Intent(WifiManager.ACTION_PICK_WIFI_NETWORK));
		}else if (id == R.id.action_bluetooth){
			startActivity(new Intent(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS));
		}else if (id == R.id.action_help){
			startActivity(new Intent(this, HelpActivity.class));
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void myo(View view) {
		Intent intent = new Intent(this, MyoControlActivity.class);
		startActivity(intent);			
	}
	
	public void touch(View view) {
		Intent intent = new Intent(this, TouchControlActivity.class);
		startActivity(intent);			
		
	}
	
	public void settings(View view) {
		Intent intent = new Intent(this, SettingsActivity.class);
		startActivity(intent);	
	}
	
	public void wifiSettings(View view) {
		Intent intent = new Intent(WifiManager.ACTION_PICK_WIFI_NETWORK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		startActivity(intent);
	}
	public void btSettings(View view) {
		Intent intent = new Intent(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		startActivity(intent);
	}
}
