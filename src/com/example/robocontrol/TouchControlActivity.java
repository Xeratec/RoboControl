package com.example.robocontrol;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.robocontrol.util.SystemUiHider;
import com.example.robocontrol.util.XBeeConnection;
import com.example.robocontrol.util.XBeeConnection.OnConnectListener;
import com.zerokol.views.JoystickView;
import com.zerokol.views.JoystickView.OnJoystickMoveListener;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
public class TouchControlActivity extends Activity {
	/**
	 * Whether or not the system UI should be auto-hidden after
	 * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
	 */
	private static final boolean AUTO_HIDE = false;

	/**
	 * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
	 * user interaction before hiding the system UI.
	 */
	private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

	/**
	 * If set, will toggle the system UI visibility upon interaction. Otherwise,
	 * will show the system UI visibility upon interaction.
	 */
	private static final boolean TOGGLE_ON_CLICK = false;

	/**
	 * The flags to pass to {@link SystemUiHider#getInstance}.
	 */
	private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;

	/**
	 * The instance of the {@link SystemUiHider} for this activity.
	 */
	private SystemUiHider mSystemUiHider;
	
    private TextView powerTextView;
    private JoystickView joystick;
    private TextView powerTextView1;
    private JoystickView joystick1;
    private XBeeConnection xbee;
    private Runnable Thread1;
    private boolean connected = true;
    private static boolean invertMotorAxis, invertWheelAxis;
    private static boolean stopMotor = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_touch_control);
		setupActionBar();
	
		if (XBeeConnection.debug()){
			this.setTitle(this.getTitle()+" (Debug)");
		}

		final View controlsView = findViewById(R.id.fullscreen_content_controls);
		final View contentView = findViewById(R.id.fullscreen_content);
		xbee = new XBeeConnection(this, (ProgressBar) findViewById(R.id.progressBar1));
		
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		invertMotorAxis = sharedPrefs.getBoolean("invertMotorAxis", false);
        invertWheelAxis = sharedPrefs.getBoolean("invertWheelAxis", false);
        stopMotor = sharedPrefs.getBoolean("stopMotor", false);
        
		// Set up an instance of SystemUiHider to control the system UI for
		// this activity.
		mSystemUiHider = SystemUiHider.getInstance(this, contentView,
				HIDER_FLAGS);
		mSystemUiHider.setup();
		mSystemUiHider
				.setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener() {
					// Cached values.
					int mControlsHeight;
					int mShortAnimTime;

					@SuppressWarnings("unused")
					@Override
					@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
					public void onVisibilityChange(boolean visible) {
						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
							// If the ViewPropertyAnimator API is available
							// (Honeycomb MR2 and later), use it to animate the
							// in-layout UI controls at the bottom of the
							// screen.
							if (mControlsHeight == 0) {
								mControlsHeight = controlsView.getHeight();
							}
							if (mShortAnimTime == 0) {
								mShortAnimTime = getResources().getInteger(
										android.R.integer.config_shortAnimTime);
							}
							controlsView
									.animate()
									.translationY(visible ? 0 : mControlsHeight)
									.setDuration(mShortAnimTime);
						} else {
							// If the ViewPropertyAnimator APIs aren't
							// available, simply show or hide the in-layout UI
							// controls.
							controlsView.setVisibility(visible ? View.VISIBLE
									: View.GONE);
						}

						if (visible && AUTO_HIDE) {
							// Schedule a hide().
							delayedHide(AUTO_HIDE_DELAY_MILLIS);
						}
					}
				});

		// Set up the user interaction to manually show or hide the system UI.
		contentView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (TOGGLE_ON_CLICK) {
					mSystemUiHider.toggle();
				} else {
					mSystemUiHider.show();
				}
			}
		});
		
        powerTextView = (TextView) findViewById(R.id.powerTextView);
        //Referencing also other views
        joystick = (JoystickView) findViewById(R.id.joystickView);
        powerTextView1 = (TextView) findViewById(R.id.powerTextView1);
        joystick1 = (JoystickView) findViewById(R.id.JoystickView1);
        
		if (XBeeConnection.debug()){
	        //Event listener that always returns the variation of the angle in degrees, motion power in percentage and direction of movement
	        joystick.setOnJoystickMoveListener(new OnJoystickMoveListener() {
	
	            @Override
	            public void onValueChanged(int angle, int power, int direction) {
	                powerTextView.setText(" " + String.valueOf(power) + "%");
	 
	            }
	        }, JoystickView.DEFAULT_LOOP_INTERVAL);
	        
	       
	        
	        joystick1.setOnJoystickMoveListener(new OnJoystickMoveListener() {
	
	            @Override
	            public void onValueChanged(int angle, int power, int direction) { 
	                powerTextView1.setText(" " + String.valueOf(power) + "%");
	 
	            }
	        }, JoystickView.DEFAULT_LOOP_INTERVAL);
		}
        
		// Upon interacting with UI controls, delay any scheduled hide()
		// operations to prevent the jarring behavior of controls going away
		// while interacting with the UI.
		findViewById(R.id.powerTextView).setOnTouchListener(mDelayHideTouchListener);
		findViewById(R.id.joystickView).setOnTouchListener(mDelayHideTouchListener);
		xbee.setOnConnectListener(new OnConnectListener() {
			@Override
			public void onConnect(boolean conntected) {
				Thread1 = new Runnable() {			
					public void run() {
						int powerX,powerY;
						powerX=powerY=0;
						do  {
							try {
								powerY=((JoystickView) joystick).getPower();
								powerX=((JoystickView) joystick1).getPower();
								
			
								
								if (invertMotorAxis) powerX*=-1;
								if (invertWheelAxis) powerY*=-1;
								
								//Bug Fix
								if (powerY>100) powerY=100;
								if (powerX>100) powerX=100;
								if (powerY<-100) powerY=-100;
								if (powerX<-100) powerX=-100;
								
								if (stopMotor) powerX=0;					
								
								if (!XBeeConnection.debug()){
									powerTextView1.setText(" " + String.valueOf(powerX) + "%");
									powerTextView.setText(" " + String.valueOf(powerY) + "%");
								}
								
								if (connected){
									xbee.sendData("ST;");
									Thread.sleep(50);
									xbee.sendData(Integer.toString(powerY)+":"+Integer.toString(powerX)+";");
									Thread.sleep(50);
								}
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}while (connected);
					}
				};
				new Thread(Thread1).start();
			}
		});	
	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			// Show the Up button in the action bar.
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == android.R.id.home) {
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Touch listener to use for in-layout UI controls to delay hiding the
	 * system UI. This is to prevent the jarring behavior of controls going away
	 * while interacting with activity UI.
	 */
	View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
		@SuppressLint("ClickableViewAccessibility")
		@Override
		public boolean onTouch(View view, MotionEvent motionEvent) {
			if (AUTO_HIDE) {
				delayedHide(AUTO_HIDE_DELAY_MILLIS);
			}
			return false;
		}
	};

	Handler mHideHandler = new Handler();
	Runnable mHideRunnable = new Runnable() {
		@Override
		public void run() {
			mSystemUiHider.hide();
		}
	};

	/**
	 * Schedules a call to hide() in [delay] milliseconds, canceling any
	 * previously scheduled calls.
	 */
	private void delayedHide(int delayMillis) {
		mHideHandler.removeCallbacks(mHideRunnable);
		mHideHandler.postDelayed(mHideRunnable, delayMillis);
	}

	@Override
	protected void onDestroy() {
		connected = false;
		xbee.sendData("0:0");
		xbee.onPause();
		super.onDestroy();

		
	}

	@Override
	protected void onPause() {
		connected = false;
		xbee.sendData("0:0");
		xbee.onPause();
		super.onDestroy();

	}
}
