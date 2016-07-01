package com.hybrid.probuk.global;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class GlobalMembers {


	public static int GLOBAL_LIGHT_VALUE;
	public static int GLOBAL_CURRENT_BRIGHTNESS_VALUE;
	public static int GLOBAL_CURRENT_MODE;
	public static int GLOBAL_WAIT_VALUE=1000;
	public static int GLOBAL_ORIENTATION=0;
	public static String GLOBAL_EXCEPTION_MSG;
	public static Exception GLOBAL_EXCEPTION;
	public static Context GLOBAL_CONTEXT;

	public static boolean GLOBAL_FULL_FLASH=false;
	public static String TAG="SMARTSELFIEPRO";
	public static Integer CAMERAID;


	public static ArrayList<String> SELFIE_PRO_FILE_LIST;

	public static final String ERROR_ON_RESUME="Error while resuming";
	public static final String ERROR_NO_LIGHT_SENSOR="App not supported as Light Sensor not available in your device ";
	public static final String ERROR_NO_FRONT_CAMERA="App not supported as Front Camera not available in your device ";
	public static final String ERROR_UNKNOWN_REASON="Unknown Error Occured";
	public static final String ERROR_IN_SPLASH_SCREEN_THREAD="Error Occured in Splash Screen Thread";
	public static final String ERROR_IN_CAMERA_ACT_CREATE="Error Occured in Camera Activity create";
	public static final String ERROR_IN_PREVIEW_DISPLAY="Error Occured in SeTPreviewDisplay";
	public static final String ERROR_IN_SURFACE_CREATED="Error Occured in SurfaceCreated";
	public static final String ERROR_IN_VIEW_CREATED="Error Occured in ViewActivity";
	public static final String ERROR_IN_STARTING_SHARE_INTENT="Error Occured in starting share intent";
	public static final String ERROR_IN_SET_CAMERA="Error Occured in Set Camera";
	public static final String ERROR_IMAGE_FILE_COULD_NOT_BE_FOUND="Error: Image file could not be found";

	public static void sendEmail( Context context,String message){

		String s="Debug-infos:";
		s += "\n OS Version: " + System.getProperty("os.version") + "(" + android.os.Build.VERSION.INCREMENTAL + ")";
		s += "\n OS API Level: "+android.os.Build.VERSION.RELEASE + "("+android.os.Build.VERSION.SDK_INT+")";
		s += "\n Device: " + android.os.Build.DEVICE;
		s += "\n Model (and Product): " + android.os.Build.MODEL + " ("+ android.os.Build.PRODUCT + ")";

		message=message+" "+s;
		Intent i = new Intent(Intent.ACTION_SEND);
		i.setType("message/rfc822");
		i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"rapidbrowserpro@gmail.com"});
		i.putExtra(Intent.EXTRA_SUBJECT, "Debug Smart Selfie Pro");
		i.putExtra(Intent.EXTRA_TEXT   ,message);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		try {
			context.startActivity(Intent.createChooser(i, "Send mail..."));
		} catch (android.content.ActivityNotFoundException ex) {
			Toast.makeText(context, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
		}
	}

	public static void showAlert(Context context,String message){
		GLOBAL_EXCEPTION_MSG=message;
		GLOBAL_CONTEXT=context;
		Log.e(GlobalMembers.TAG,message);
		new AlertDialog.Builder(context)
		.setTitle("Send Error Cause?")
		.setMessage("App faced an error. Please send the error cause, so that our developers can solve it.")
		.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				sendEmail(GLOBAL_CONTEXT,GLOBAL_EXCEPTION_MSG);
			}
		})
		.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				((Activity)GLOBAL_CONTEXT).finish();
				return;
			}
		})
		.setIcon(android.R.drawable.ic_dialog_alert)
		.show();
	}

	public static void showAlert(Context context,Exception e){
		GLOBAL_EXCEPTION=e;
		GLOBAL_CONTEXT=context;
		if(e!=null){
			showAlert(context,e.getMessage()+" "+getStackTraceAsString(e));
		}
	}
	public static void showAlert(Context context,String message,Exception e){
		GLOBAL_EXCEPTION=e;
		GLOBAL_CONTEXT=context;
		if(e!=null){
			showAlert(context,message+" "+e.getMessage()+" "+getStackTraceAsString(e));
		}
	}

	public static String getStackTraceAsString(Exception e){
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		return sw.toString();
	}


}
