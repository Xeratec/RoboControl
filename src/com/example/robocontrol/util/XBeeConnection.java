package com.example.robocontrol.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.robocontrol.R;

public class XBeeConnection{	
	
	private static boolean debug = false;
	
    public static final int MESSAGE_DATA_RECEIVE = 0;

    private Boolean task_state = false;	
	private Socket s;
	private String ip;
	private int port;
	private int timeout;

	
	private Context context;
	private Activity activity;
    private ProgressBar mProgress;
    
    private OnConnectListener onConnectListener;
    
	public XBeeConnection(Activity mactivity, ProgressBar mprogressBar) {
		this.activity = mactivity;
		this.context = activity.getApplicationContext();
		this.mProgress = mprogressBar;
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
		
		ip = sharedPrefs.getString("ipAddress", "192.168.1.10");	
		port = Integer.parseInt(sharedPrefs.getString("port", "9750"));
		timeout = Integer.parseInt(sharedPrefs.getString("timeout", "2000"));		
	
		Runnable readThread = new Runnable() {			
			public void run() {
				try {
					s = new Socket();
					activity.runOnUiThread (new Thread(new Runnable() { 
				         public void run() {
				        	if (XBeeConnection.debug()){
				        		 mProgress.setVisibility(View.GONE);
				        	}else {
				        		mProgress.setVisibility(View.VISIBLE);				        
				        	}
				         }
				     }));
					s.connect((new InetSocketAddress(InetAddress.getByName(ip), port)), timeout);
					activity.runOnUiThread (new Thread(new Runnable() { 
				         public void run() {
				             mProgress.setVisibility(View.GONE);
				         }
				     }));
					if (onConnectListener != null) onConnectListener.onConnect(true);
					task_state = true;
					while(task_state) {
						try {
							BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
							String strRead = in.readLine();
						} catch (NumberFormatException e) {
							e.printStackTrace();
						} catch (UnknownHostException e) {
							task_state = false;
							if (!debug) activity.finish();
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				} catch (NumberFormatException e) {
					e.printStackTrace();
				} catch (UnknownHostException e) {
					task_state = false;
					if (!debug){
						Toast(context.getString(R.string.action_wifiError)+"\n"+ip+":"+port, Toast.LENGTH_SHORT);
						activity.finish();
					}
					e.printStackTrace();
				} catch (IOException e) {
					task_state = false;
					if (!debug){
						Toast(context.getString(R.string.action_wifiError)+"\n"+ip+":"+port, Toast.LENGTH_SHORT);
						activity.finish();
					}
					e.printStackTrace();
				}
			}			
		};
		new Thread(readThread).start();
	}
	
	public void onPause() {
		task_state = false;
		try {
			s.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}
	
	synchronized public boolean sendData(String str) {
		try {
			OutputStream out = s.getOutputStream();
			out.write(str.getBytes());
			out.flush();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} catch (NullPointerException e) {
			task_state = false;
			if (!debug){
				Toast("Please check your WiFi Connection", Toast.LENGTH_SHORT);
				activity.finish();
			}
			return false;
		}
	}
	
	synchronized private void Toast(final String msg, final int duration){
		 activity.runOnUiThread(new Runnable() {
		     @Override
		     public void run() {
		    	 Toast.makeText(context, msg, duration).show();
		     }
		 });
	}
	
	public void setOnConnectListener(OnConnectListener listener) {
		this.onConnectListener = listener;
	}
	
	public static interface OnConnectListener {
		public void onConnect(boolean conntected);
	}
	
	public static boolean debug(){
		return debug;
	}
	
	public static boolean debug(boolean d){
		debug = d;
		return debug;
	}
}

