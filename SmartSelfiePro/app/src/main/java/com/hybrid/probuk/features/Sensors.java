package com.hybrid.probuk.features;

import com.hybrid.probuk.global.GlobalMembers;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.widget.Toast;


public class Sensors {

	//TextView textLIGHT_available, textLIGHT_reading;
	//private static Context mContext;
 public static SensorManager mySensorManager;
//	public Sensors(Context mContext){
//		this.mContext=mContext;
//	}

	public static boolean init(Context mContext){
		try{
		mySensorManager = (SensorManager)mContext.getSystemService(mContext.SENSOR_SERVICE);
		Sensor LightSensor = mySensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
		if(LightSensor != null){
			mySensorManager.registerListener(
					LightSensorListener,
					LightSensor,
					SensorManager.SENSOR_DELAY_NORMAL);

return true;
		}else{
			Toast.makeText(mContext, "Light Sensor Not Available", Toast.LENGTH_SHORT).show();
		return false;
		}
		}catch(Exception e){

			Log.e(GlobalMembers.TAG,"Error Occured in Sensors",e);
			GlobalMembers.showAlert(mContext,e);
			return false;

		}
	}

	private final static SensorEventListener LightSensorListener
	= new SensorEventListener(){
		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			// TODO Auto-generated method stub

		}
		@Override
		public void onSensorChanged(SensorEvent event) {
			if(event.sensor.getType() == Sensor.TYPE_LIGHT){
				GlobalMembers.GLOBAL_LIGHT_VALUE=Math.round(event.values[0]);
				//Log.d("ScreenBright", "Value : "+event.values[0]);
			}
		}
	};

	public static void stopSensorListner(){
		if(mySensorManager!=null){
			mySensorManager.unregisterListener(LightSensorListener);
		}
	}
}

