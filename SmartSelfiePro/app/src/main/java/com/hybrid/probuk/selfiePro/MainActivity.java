package com.hybrid.probuk.selfiePro;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.ProgressBar;

import com.hybrid.probuk.features.ScreenBright;
import com.hybrid.probuk.features.Sensors;
import com.hybrid.probuk.global.GlobalMembers;


public class MainActivity extends Activity {

	protected boolean mbActive;
	protected static final int TIMER_RUNTIME = 700;
	protected ProgressBar mProgressBar;
	static Sensors sensorControl;
	Context ctx;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		ctx=this;
		try{
		if(!Sensors.init(this)){
			GlobalMembers.showAlert(this, GlobalMembers.ERROR_NO_LIGHT_SENSOR);
		}
		else{
		ScreenBright.init(this);
		mProgressBar = (ProgressBar)findViewById(R.id.progressBar1);
		Thread timer = new Thread(){
			public void run(){
					//sleep(5000);
					mbActive = true;
		              try {
		            	  for(int j=0;j<2;j++){
		                  int waited = 0;
		                  while(mbActive && (waited < TIMER_RUNTIME)) {
		                      sleep(300);
		                      if(mbActive) {
		                          waited += 500;
		                      }
		                  }
		              }
		              }catch(InterruptedException e){
		            	  Log.e(GlobalMembers.TAG,"Error Occured in MainActivity",e);
		  				e.printStackTrace();
		  				}finally{
		  					Intent intent=new Intent(ctx,CameraActivity.class);
		  				startActivity(intent);

		  				}
		  			}

		  		};

		  		timer.start();
		}
		}catch(Exception e){
			Log.e(GlobalMembers.TAG,GlobalMembers.ERROR_IN_SPLASH_SCREEN_THREAD,e);
			GlobalMembers.showAlert(ctx,GlobalMembers.ERROR_IN_SPLASH_SCREEN_THREAD);
		}
		  	}

@Override
public void onDestroy() {
	  super.onDestroy();
	  Sensors.stopSensorListner();

}

@Override
protected void onResume() {
	super.onResume();
	Sensors.init(this);

}

@Override
protected void onPause() {
	super.onPause();
	Sensors.stopSensorListner();
}

}