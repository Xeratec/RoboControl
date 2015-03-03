package com.example.robocontrol;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.robocontrol.util.XBeeConnection;
import com.example.robocontrol.util.XBeeConnection.OnConnectListener;
import com.thalmic.myo.AbstractDeviceListener;
import com.thalmic.myo.Arm;
import com.thalmic.myo.DeviceListener;
import com.thalmic.myo.Hub;
import com.thalmic.myo.Myo;
import com.thalmic.myo.Pose;
import com.thalmic.myo.Quaternion;
import com.thalmic.myo.XDirection;
import com.thalmic.myo.scanner.ScanActivity;

public class MyoControlActivity extends Activity {
	
	private ImageView lock,imgDoubleTap,imgFist,imgWaveIn,imgWaveOut,imgSpreadFingers;
	private Activity currentActivity;
	private TextView textRoll,textPitch,textYaw;
	private TextView textMotor,textWheel;
	private float roll,pitch,yaw;
	private int perRoll,perPitch,perYaw;
	private  int relRoll,relPitch,relYaw;
	protected Myo myo;
	public boolean active;
	public boolean connect = true;
	
	private Hub hub;
    private XBeeConnection xbee;
    private Runnable Thread1;
    
    private static int motorAxis , motorAxisMin , motorAxisMax;
    private static int wheelAxis , wheelAxisMin , wheelAxisMax;
    private static boolean invertMotorAxis, invertWheelAxis, stopMotor;
    
    private int powerX,powerY;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		if (XBeeConnection.debug()){
			this.setTitle(this.getTitle()+" (Debug)");
		}
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_myo_control);
		
		lock = (ImageView) findViewById(R.id.imgConnect);
		imgDoubleTap = (ImageView) findViewById(R.id.imgDoubeTap);
		imgFist = (ImageView) findViewById(R.id.imgFist);
		imgWaveIn = (ImageView) findViewById(R.id.imgWaveIn);
		imgWaveOut = (ImageView) findViewById(R.id.imgWaveOut);
		imgSpreadFingers = (ImageView) findViewById(R.id.imgSpreadFingers);
		textRoll = (TextView) findViewById(R.id.textRoll);
		textPitch = (TextView) findViewById(R.id.textPitch);
		textYaw = (TextView) findViewById(R.id.textYaw);
		textMotor = (TextView) findViewById(R.id.textMotor);
		textWheel = (TextView) findViewById(R.id.textWheel);
		relRoll=relPitch=relYaw=0;
		currentActivity = this;
		
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		hub = Hub.getInstance();
		if (!hub.init(this)){
			Toast.makeText(this, getString(R.string.hub_error), Toast.LENGTH_SHORT).show();
			if (!XBeeConnection.debug()){
				 finish();
			}
		}else{
		xbee = new XBeeConnection(this, (ProgressBar) findViewById(R.id.progressBar1));
		
		xbee.setOnConnectListener(new OnConnectListener() {
				@Override
				public void onConnect(boolean conntected) {
					if (conntected){ 
						   Thread1 = new Runnable() {			
								public void run() {
									float mMotor = (float) (motorAxisMax-motorAxisMin)/100;
									float mWheel = (float) (wheelAxisMax-wheelAxisMin)/100;
									powerX=powerY=0;
									do  {
										try {						
											switch (motorAxis){
												case 1:
													if (Math.abs(perRoll) <= motorAxisMin){
														powerX = 0;
													}else if (Math.abs(perRoll) >= motorAxisMax){
														powerX = (int) (100*Math.signum(perRoll));
													}else {
														powerX = Math.round((float) (Math.abs(perRoll)-motorAxisMin)/mMotor*Math.signum(perRoll));
													}
													break;
												case 2:
													if (Math.abs(perPitch) <= motorAxisMin){
														powerX = 0;
													}else if (Math.abs(perPitch) >= motorAxisMax){
														powerX = (int) (100*Math.signum(perPitch));
													}else {
														powerX = Math.round((float) (Math.abs(perPitch)-motorAxisMin)/mMotor*Math.signum(perPitch));
													}
													break;
												case 3:
													if (Math.abs(perYaw) <= motorAxisMin){
														powerX = 0;
													}else if (Math.abs(perYaw) >= motorAxisMax){
														powerX = (int) (100*Math.signum(perYaw));
													}else {
														powerX = Math.round((float)(Math.abs(perYaw)-motorAxisMin)/mMotor*Math.signum(perYaw));
													}
													break;
											}
											switch (wheelAxis) {
												case 1:
													if (Math.abs(perRoll) <= wheelAxisMin){
														powerY = 0;
													}else if (Math.abs(perRoll) >= wheelAxisMax){
														powerY = (int) (100*Math.signum(perRoll));
													}else {
														powerY = Math.round((float)(Math.abs(perRoll)-wheelAxisMin)/mWheel*Math.signum(perRoll));
													}
													break;
												case 2:
													if (Math.abs(perPitch) <= wheelAxisMin){
														powerY = 0;
													}else if (Math.abs(perPitch) >= wheelAxisMax){
														powerY = (int) (100*Math.signum(perPitch));
													}else {
														powerY = Math.round((float)(Math.abs(perPitch)-wheelAxisMin)/mWheel*Math.signum(perPitch));
													}
													break;
												case 3:
													if (Math.abs(perYaw) <= wheelAxisMin){
														powerY = 0;
													}else if (Math.abs(perYaw) >= wheelAxisMax){
														powerY = (int) (100*Math.signum(perYaw));
													}else {
														powerY = Math.round((float)(Math.abs(perYaw)-wheelAxisMin)/mWheel*Math.signum(perYaw));
													}
													break;
											}
											
											if (invertMotorAxis) powerX*=-1;
											if (invertWheelAxis) powerY*=-1;
											
											//Bug Fix
											if (powerY>100) powerY=100;
											if (powerX>100) powerX=100;
											if (powerY<-100) powerY=-100;
											if (powerX<-100) powerX=-100;
	
											if (stopMotor) powerX=0;
											
											if (connect && active){
												xbee.sendData("ST;");
												Thread.sleep(50);
												xbee.sendData(Integer.toString(powerY)+":"+Integer.toString(powerX)+";");
												Thread.sleep(50);
												runOnUiThread(new Runnable(){
												     public void run() {
												    	textMotor.setText(Integer.toString(powerX));
														textWheel.setText(Integer.toString(powerY));
												     }
												});
											}
										} catch (InterruptedException e) {
											e.printStackTrace();
										}
									}while (!currentActivity.isFinishing());
								}
							};
						onScanActionSelected();
						hub.addListener(mListener);	
				        Hub.getInstance().setLockingPolicy(Hub.LockingPolicy.STANDARD);
						// Upon interacting with UI controls, delay any scheduled hide()
						// operations to prevent the jarring behavior of controls going away
				       
				        
				        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(currentActivity);
						      
				        motorAxis = Integer.parseInt(sharedPrefs.getString("motorAxis", "2"));
				        motorAxisMin = Integer.parseInt(sharedPrefs.getString("motorAxisMin", "15"));
				        motorAxisMax = Integer.parseInt(sharedPrefs.getString("motorAxisMax", "40"));
				        wheelAxis = Integer.parseInt(sharedPrefs.getString("wheelAxis", "1"));
				        wheelAxisMin = Integer.parseInt(sharedPrefs.getString("wheelAxisMin", "15"));
				        wheelAxisMax = Integer.parseInt(sharedPrefs.getString("wheelAxisMax", "40"));
						invertMotorAxis = sharedPrefs.getBoolean("invertMotorAxis", false);
				        invertWheelAxis = sharedPrefs.getBoolean("invertWheelAxis", false);
				        stopMotor = sharedPrefs.getBoolean("stopMotor", false);
				        
						new Thread(Thread1).start();
						
					}
					
				}
	        });
		}
	}

	 @Override
    protected void onDestroy() {
		active = false;
		connect = false;
		if (xbee!=null) {
			xbee.sendData("0:0");
			xbee.onPause();
		}
		
        super.onDestroy();
        // We don't want any callbacks when the Activity is gone, so unregister the listener.

        if (hub.init(this)){       
        	Hub.getInstance().removeListener(mListener);
            if (isFinishing()) {
            // The Activity is finishing, so shutdown the Hub. This will disconnect from the Myo.
            Hub.getInstance().shutdown();
        }
        }

       
    }
	 
	@Override
	protected void onPause() {
		super.onDestroy();

	}
 
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.myo_control, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.layout1){
			onScanActionSelected();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private DeviceListener mListener = new AbstractDeviceListener() {

        // onConnect() is called whenever a Myo has been connected.
        @Override
        public void onConnect(Myo myoc, long timestamp) {
        	Toast.makeText(currentActivity, getString(R.string.action_connect), Toast.LENGTH_SHORT).show();
        	myo = myoc;
        	myo.lock();
        }

        // onDisconnect() is called whenever a Myo has been disconnected.
        @Override
        public void onDisconnect(Myo myo, long timestamp) {
        	Toast.makeText(currentActivity, getString(R.string.action_disconnect), Toast.LENGTH_SHORT).show();
			xbee.sendData("0:0");
        }

        // onArmSync() is called whenever Myo has recognized a Sync Gesture after someone has put it on their
        // arm. This lets Myo know which arm it's on and which way it's facing.
        @Override
        public void onArmSync(Myo myo, long timestamp, Arm arm, XDirection xDirection) {
            Toast.makeText(currentActivity, getString(R.string.action_sync), Toast.LENGTH_SHORT).show();
            
        }

        // onArmUnsync() is called whenever Myo has detected that it was moved from a stable position on a person's arm after
        // it recognized the arm. Typically this happens when someone takes Myo off of their arm, but it can also happen
        // when Myo is moved around on the arm.
        @Override
        public void onArmUnsync(Myo myo, long timestamp) {
           Toast.makeText(currentActivity, getString(R.string.action_unsync), Toast.LENGTH_SHORT).show();
           lock.setImageResource(R.drawable.myo_inactive);
           xbee.sendData("0:0");
           active = false;
        }

        // onUnlock() is called whenever a synced Myo has been unlocked. Under the standard locking
        // policy, that means poses will now be delivered to the listener.
        @Override
        public void onUnlock(Myo myo, long timestamp) {
        	lock.setImageResource(R.drawable.myo_passiv);
        	active = false;
        }

        // onLock() is called whenever a synced Myo has been locked. Under the standard locking
        // policy, that means poses will no longer be delivered to the listener.
        @Override
        public void onLock(Myo myo, long timestamp) {
        	lock.setImageResource(R.drawable.myo_inactive);
        	active = false;
	    	imgSpreadFingers.setBackgroundColor(Color.TRANSPARENT);
			imgFist.setBackgroundColor(Color.TRANSPARENT);
	    	imgWaveIn.setBackgroundColor(Color.TRANSPARENT);
	    	imgWaveOut.setBackgroundColor(Color.TRANSPARENT);
	    	imgDoubleTap.setBackgroundColor(Color.TRANSPARENT);	
			xbee.sendData("0:0");
        }

        // onOrientationData() is called whenever a Myo provides its current orientation,
        // represented as a quaternion.
        @Override
        public void onOrientationData(Myo myo, long timestamp, Quaternion rotation) {
            // Calculate Euler angles (roll, pitch, and yaw) from the quaternion.
            roll = (float) Math.toDegrees(Quaternion.roll(rotation));
            pitch = (float) Math.toDegrees(Quaternion.pitch(rotation));
            yaw = (float) Math.toDegrees(Quaternion.yaw(rotation));

            // Adjust roll and pitch for the orientation of the Myo on the arm.
            if (myo.getXDirection() == XDirection.TOWARD_WRIST) {
                pitch *= -1;
                yaw *= -1;
            }
            perRoll = Math.round(roll)-relRoll;
            if (perRoll<-180){
            	perRoll+=360;
            }
            
            perPitch = Math.round(pitch)-relPitch;
            if (perPitch<-90){
            	perPitch += 180;
            }
     
            perYaw = Math.round(yaw)-relYaw;
            if (perYaw<-180){
            	perYaw += 360;
            }
     

			textRoll.setText(Integer.toString(perRoll));
			textPitch.setText(Integer.toString(perPitch));
			textYaw.setText(Integer.toString(perYaw));
            // Next, we apply a rotation to the text view using the roll, pitch, and yaw.
        }

        // onPose() is called whenever a Myo provides a new pose.
        @Override
        public void onPose(Myo myo, long timestamp, Pose pose) {
            // Handle the cases of the Pose enumeration, and change the text of the text view
            // based on the pose we receive.
            switch (pose) {
                case UNKNOWN:
                    break;
                case REST:
                case DOUBLE_TAP:   
                    switch (myo.getArm()) {
                        case LEFT:
                            break;
                        case RIGHT:
                            break;
						case UNKNOWN:
							break;
                    }
                    break;
                case FIST:
            		myo.unlock(Myo.UnlockType.HOLD);
            		if (!active) setRelativeOrientationData();
                	active = true;
                	lock.setImageResource(R.drawable.myo_acitve);
                    break;
                case WAVE_IN:
                	myo.lock();
                    break;
                case WAVE_OUT:
                    break;
                case FINGERS_SPREAD:
                	myo.unlock(Myo.UnlockType.HOLD);
                	if (!active) setRelativeOrientationData();
                	active = true;
                	lock.setImageResource(R.drawable.myo_acitve);
                    break;
            }
            setPoseActive(pose);
      
        }
    };
    
    private void onScanActionSelected() {
        // Launch the ScanActivity to scan for Myos to connect to.
        Intent intent = new Intent(this, ScanActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("keep", true);
        startActivity(intent);
    }
    
    public void lock(View view){
    	if (myo!=null) myo.lock();
    }
    
    private void setRelativeOrientationData(){
    	myo.vibrate(Myo.VibrationType.SHORT);
        relRoll = Math.round(roll);
        relPitch = Math.round(pitch);
        relYaw = Math.round(yaw);    	
    }
    
    private void setPoseActive(Pose pose){
    	switch (pose) {
		    case DOUBLE_TAP:   
		    	imgDoubleTap.setBackgroundResource(R.drawable.pose_acitve);
		    	imgFist.setBackgroundColor(Color.TRANSPARENT);
		    	imgWaveIn.setBackgroundColor(Color.TRANSPARENT);
		    	imgWaveOut.setBackgroundColor(Color.TRANSPARENT);
		    	imgSpreadFingers.setBackgroundColor(Color.TRANSPARENT);
		        break;
		    case FIST:
		    	imgFist.setBackgroundResource(R.drawable.pose_acitve);
		    	imgSpreadFingers.setBackgroundColor(Color.TRANSPARENT);
		    	imgWaveIn.setBackgroundColor(Color.TRANSPARENT);
		    	imgWaveOut.setBackgroundColor(Color.TRANSPARENT);
		    	imgDoubleTap.setBackgroundColor(Color.TRANSPARENT);
		        break;
		    case WAVE_IN:
		    	imgWaveIn.setBackgroundResource(R.drawable.pose_acitve);
		    	imgFist.setBackgroundColor(Color.TRANSPARENT);
		    	imgDoubleTap.setBackgroundColor(Color.TRANSPARENT);
		    	imgWaveOut.setBackgroundColor(Color.TRANSPARENT);
		    	imgSpreadFingers.setBackgroundColor(Color.TRANSPARENT);
		        break;
		    case WAVE_OUT:
		    	imgWaveOut.setBackgroundResource(R.drawable.pose_acitve);
		    	imgFist.setBackgroundColor(Color.TRANSPARENT);
		    	imgWaveIn.setBackgroundColor(Color.TRANSPARENT);
		    	imgDoubleTap.setBackgroundColor(Color.TRANSPARENT);
		    	imgSpreadFingers.setBackgroundColor(Color.TRANSPARENT);
		        break;
		    case FINGERS_SPREAD:
		    	imgSpreadFingers.setBackgroundResource(R.drawable.pose_acitve);
		    	imgFist.setBackgroundColor(Color.TRANSPARENT);
		    	imgWaveIn.setBackgroundColor(Color.TRANSPARENT);
		    	imgWaveOut.setBackgroundColor(Color.TRANSPARENT);
		    	imgDoubleTap.setBackgroundColor(Color.TRANSPARENT);
		        break;	
		    case REST:  
			    imgSpreadFingers.setBackgroundColor(Color.TRANSPARENT);
				imgFist.setBackgroundColor(Color.TRANSPARENT);
		    	imgWaveIn.setBackgroundColor(Color.TRANSPARENT);
		    	imgWaveOut.setBackgroundColor(Color.TRANSPARENT);
		    	imgDoubleTap.setBackgroundColor(Color.TRANSPARENT);	
		    	break;
		    case UNKNOWN:
		    	imgSpreadFingers.setBackgroundColor(Color.TRANSPARENT);
				imgFist.setBackgroundColor(Color.TRANSPARENT);
		    	imgWaveIn.setBackgroundColor(Color.TRANSPARENT);
		    	imgWaveOut.setBackgroundColor(Color.TRANSPARENT);
		    	imgDoubleTap.setBackgroundColor(Color.TRANSPARENT);	
		    	break;
		}
    }
}
