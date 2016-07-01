package com.hybrid.probuk.features;


import com.hybrid.probuk.global.GlobalMembers;

import android.content.ContentResolver;
import android.content.Context;
import android.provider.Settings;
import android.util.Log;

public class ScreenBright {



	private static ContentResolver cResolver;


	public static void init(Context mContext){
		try{
			cResolver = mContext.getContentResolver();
			GlobalMembers.GLOBAL_CURRENT_MODE = Settings.System.getInt(cResolver,Settings.System.SCREEN_BRIGHTNESS_MODE);
			GlobalMembers.GLOBAL_CURRENT_BRIGHTNESS_VALUE = android.provider.Settings.System.getInt(cResolver, Settings.System.SCREEN_BRIGHTNESS);
			Log.d(GlobalMembers.TAG,"GlobalMembers.GLOBAL_CURRENT_MODE : "+GlobalMembers.GLOBAL_CURRENT_MODE);
			Log.d(GlobalMembers.TAG,"GlobalMembers.GLOBAL_CURRENT_BRIGHTNESS_VALUE : "+GlobalMembers.GLOBAL_CURRENT_BRIGHTNESS_VALUE);
		}catch(Exception e){
			Log.e("Screen Error","Error Occured");
			e.printStackTrace();
			GlobalMembers.showAlert(mContext,e);
		}
	}

	public static void changeBrightnessToMax(Context mContext){
		try{
			Log.d(GlobalMembers.TAG,"changeBrightnessToMax Mode: "+Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
			Log.d(GlobalMembers.TAG,"changeBrightnessToMax Value : "+"240");

			Settings.System.putInt(cResolver,Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
			android.provider.Settings.System.putInt(cResolver, Settings.System.SCREEN_BRIGHTNESS,240);
		}catch(Exception e){
			GlobalMembers.showAlert(mContext,e);
		}
	}
	public static void changeBrightnessToValue(int value,Context mContext){
		try{
			int brightness=GlobalMembers.GLOBAL_CURRENT_BRIGHTNESS_VALUE;

			if(value<50){
				brightness=240;
			}
			else if(value>=50 && value < 100){
				brightness=200;
			}
			else if(value>=100 && value < 150){
				brightness=180;
			}
			else if(value>=150 && value < 200){
				brightness=160;
			}
			else if(value>=200){
				brightness=130;
			}


			Log.d(GlobalMembers.TAG,"changeBrightnessToValue Mode: "+Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
			Log.d(GlobalMembers.TAG,"changeBrightnessToValue Value : "+brightness);
			Settings.System.putInt(cResolver,Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
			android.provider.Settings.System.putInt(cResolver, Settings.System.SCREEN_BRIGHTNESS,brightness);



		}catch(Exception e){
			GlobalMembers.showAlert(mContext,e);
		}
	}
	public static void changeBrightnessToNormal(Context mContext){
		try{

			Log.d(GlobalMembers.TAG,"changeBrightnessToNormal Mode: "+GlobalMembers.GLOBAL_CURRENT_MODE);
			Log.d(GlobalMembers.TAG,"changeBrightnessToNormal Value : "+GlobalMembers.GLOBAL_CURRENT_BRIGHTNESS_VALUE);
			Settings.System.putInt(cResolver,Settings.System.SCREEN_BRIGHTNESS_MODE, GlobalMembers.GLOBAL_CURRENT_MODE);
			android.provider.Settings.System.putInt(cResolver, Settings.System.SCREEN_BRIGHTNESS,GlobalMembers.GLOBAL_CURRENT_BRIGHTNESS_VALUE);
		}
		catch(Exception e){
			GlobalMembers.showAlert(mContext,e);
		}
	}

}
