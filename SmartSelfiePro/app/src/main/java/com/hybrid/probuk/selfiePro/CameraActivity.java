package com.hybrid.probuk.selfiePro;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.ErrorCallback;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.hybrid.probuk.utils.AsyncFileLoader;
import com.hybrid.probuk.utils.CameraUtils;
import com.hybrid.probuk.utils.FileLoaderListner;
import com.hybrid.probuk.features.ScreenBright;
import com.hybrid.probuk.global.GlobalMembers;
import com.hybrid.probuk.views.Preview;
import com.hybrid.probuk.views.TappableSurfaceView;


public class CameraActivity extends Activity {
	private Preview preview;
	public static Camera camera;
	private Context ctx;
	private TappableSurfaceView surfaceView;
	private View bottomPanel=null;
	private ImageView thumbView,clickButton,backBtn;
	private String imageFilePath=null;
	private OrientationEventListener orientaionListner;
	private boolean cameraClicked=false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ctx = this;
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.camera_layout);
		try{
			initializeAllViews();
			registerClickForCameraButton();
			registerOrientationSensorListener();
			registerClickForThumbView();
			registerClickForBackBtn();
			thumbView.setTag("Loading");
			listAllFiles();
			//files=FileUtils.listAllFiles(this);
			//Collections.sort(files,Collections.reverseOrder(String.CASE_INSENSITIVE_ORDER));
		}catch(Exception e){
			Log.e(GlobalMembers.TAG,GlobalMembers.ERROR_IN_CAMERA_ACT_CREATE,e);
			GlobalMembers.showAlert(ctx,GlobalMembers.ERROR_IN_CAMERA_ACT_CREATE,e);
		}
	}


	public void listAllFiles(){
		new AsyncFileLoader(this,new FileLoaderListner() {
			@Override
			public void setThumbViewFromLasTImage() {
				setThumbView();
			}
		}).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	ErrorCallback cameraErrorHandler=new ErrorCallback() {
		@Override
		public void onError(int error, Camera camera) {
			Log.e(GlobalMembers.TAG,"Camera Error CallBack :"+error);
			if(camera != null) {
				camera.stopPreview();
				preview.setCamera(null);
				camera.release();
			}
		}
	};


	public void initializeAllViews(){
		surfaceView=(TappableSurfaceView)findViewById(R.id.surfaceView);
		surfaceView.addTapListener(onTap);
		preview = new Preview(this, surfaceView);
		bottomPanel=findViewById(R.id.bottom_panel);
		thumbView=(ImageView)findViewById(R.id.thumbView);
		backBtn=(ImageView)findViewById(R.id.backBtn);
		clickButton=(ImageView)findViewById(R.id.clickButton);
		preview.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		((FrameLayout) findViewById(R.id.layout)).addView(preview);
		preview.setKeepScreenOn(true);
	}

	public void registerClickForBackBtn(){
		backBtn.setOnClickListener(new ImageView.OnClickListener() {
			@Override
			public void onClick(View v) {
				v.startAnimation(AnimationUtils.loadAnimation(ctx, R.anim.shutter_click));
				finish();
			}
		});
	}

	public void registerClickForThumbView(){
		thumbView.setOnClickListener(new ImageView.OnClickListener() {
			@Override
			public void onClick(View v) {
				v.startAnimation(AnimationUtils.loadAnimation(ctx, R.anim.shutter_click));
				if(!thumbView.getTag().toString().equalsIgnoreCase("Loading")){
//					if(camera != null) {
//						//camera.stopPreview();
//						//preview.setCamera(null);
//						camera.release();
//						//camera = null;
//					}
					Intent intent = new Intent(ctx, ViewActivity.class);
					//intent.putExtra("imageFilePath", imageFilePath);
					startActivity(intent);
				}else{
					Toast.makeText(ctx,"No Image Available", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}



	public void registerClickForCameraButton(){

		clickButton.setOnClickListener(new ImageView.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.v(GlobalMembers.TAG,"shutter clicked");
				//v.startAnimation(AnimationUtils.loadAnimation(ctx, R.anim.shutter_click));
				if(!cameraClicked){
					if(GlobalMembers.GLOBAL_FULL_FLASH){
						ScreenBright.changeBrightnessToMax(ctx);
					}
					else{
						ScreenBright.changeBrightnessToValue(GlobalMembers.GLOBAL_LIGHT_VALUE,ctx);
					}
					bottomPanel.setVisibility(View.VISIBLE);
					try {
						Thread.sleep(GlobalMembers.GLOBAL_WAIT_VALUE);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						GlobalMembers.showAlert(ctx,e);
					}
					orientaionListner.enable();
					cameraClicked=true;
					camera.takePicture(shutterCallback, rawCallback, jpegCallback);

				}
				else{
					Toast.makeText(ctx, "Please Wait", Toast.LENGTH_LONG).show();
				}

			}
		});
	}

	public void registerOrientationSensorListener(){
		orientaionListner=new OrientationEventListener(this) {
			@Override
			public void onOrientationChanged(int orientation) {
				//Log.d("Orientation ","Orientation : "+orientation);

				if(orientation>=45 && orientation<135){
					GlobalMembers.GLOBAL_ORIENTATION=3;
				}
				else if( orientation>=135 && orientation <225){
					GlobalMembers.GLOBAL_ORIENTATION=2;
				}
				else if( orientation>=225 && orientation <315){
					GlobalMembers.GLOBAL_ORIENTATION=1;
				}
				else{
					GlobalMembers.GLOBAL_ORIENTATION=0;
				}
			}
		};
	}


	@Override
	protected void onResume() {
		super.onResume();
		Log.v(GlobalMembers.TAG,"Camera Activity OnResume Called");
		thumbView.setTag("Loading");
		listAllFiles();
		int numCams = Camera.getNumberOfCameras();
		if(numCams > 0){
			try{
				GlobalMembers.CAMERAID=CameraUtils.getCameraId();
				if(GlobalMembers.CAMERAID!=null){
					camera = Camera.open(GlobalMembers.CAMERAID);
					camera.setErrorCallback(cameraErrorHandler);
					camera.startPreview();
					preview.setCamera(camera);
				}
				else{
					GlobalMembers.showAlert(this,GlobalMembers.ERROR_NO_FRONT_CAMERA);
				}
			} catch (RuntimeException ex){
				Toast.makeText(ctx, getString(R.string.camera_not_found), Toast.LENGTH_LONG).show();
				GlobalMembers.showAlert(ctx,GlobalMembers.ERROR_NO_FRONT_CAMERA,ex);
			}
		}
		else{
			Toast.makeText(ctx, "Error on resume", Toast.LENGTH_LONG).show();
			GlobalMembers.showAlert(ctx,GlobalMembers.ERROR_ON_RESUME);
			finish();
		}
	}

	@Override
	protected void onPause() {
		Log.v(GlobalMembers.TAG,"onPause Called");
		//thumbView.setTag("Loading");
		if(camera != null) {
			camera.stopPreview();
			preview.setCamera(null);
			camera.release();
			camera = null;
		}
		super.onPause();
	}


	private TappableSurfaceView.TapListener onTap=
			new TappableSurfaceView.TapListener() {
		public void onTap(MotionEvent event) {
			Log.d(GlobalMembers.TAG, "TappableSurfaceView - listener");

		}
	};

	private void resetCam() {
		Log.v(GlobalMembers.TAG,"resetCam Called");
		try{
			if(camera != null) {
				camera.startPreview();
				preview.setCamera(camera);
			}
			else{
				camera = Camera.open(GlobalMembers.CAMERAID);
				camera.setErrorCallback(cameraErrorHandler);
				camera.startPreview();
				preview.setCamera(camera);
			}
		}catch(Exception e){
			Log.e(GlobalMembers.TAG,"Reset Cam Error",e);
		}
	}

	private void refreshGallery(File file) {
		Log.v(GlobalMembers.TAG,"inside refreshGallery ");
		Intent mediaScanIntent = new Intent( Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
		mediaScanIntent.setData(Uri.fromFile(file));
		sendBroadcast(mediaScanIntent);
	}

	ShutterCallback shutterCallback = new ShutterCallback() {
		public void onShutter() {
			Log.v(GlobalMembers.TAG, "onShutter'd");
		}
	};

	PictureCallback rawCallback = new PictureCallback() {
		public void onPictureTaken(byte[] data, Camera camera) {
			Log.v(GlobalMembers.TAG, "onPictureTaken - raw");
			ScreenBright.changeBrightnessToNormal(ctx);
			bottomPanel.setVisibility(View.GONE);
		}
	};

	PictureCallback jpegCallback = new PictureCallback() {
		public void onPictureTaken(byte[] data, Camera camera) {
			try{
				orientaionListner.disable();
				cameraClicked=false;
				camera.startPreview();
				//resetCamera();
				Log.v(GlobalMembers.TAG, "onPictureTaken - jpeg");
				if(data!=null){
					byte[] rotatedData=rotateImageBeforeSaving(data);
					setThumbView(rotatedData);
					new SaveImageTask().execute(rotatedData);
				}
			}catch(Exception e){
				Log.e(GlobalMembers.TAG," Error Occured onPictureCallBack ");
				resetCam();
			}
		}
	};


	public byte[] rotateImageBeforeSaving(byte[] data){
		try {
			InputStream is = new ByteArrayInputStream(data);
			Bitmap bmp = BitmapFactory.decodeStream(is);
			int w = bmp.getWidth();
			int h = bmp.getHeight();
			Log.d(GlobalMembers.TAG,"Width : "+w+" Height : "+h);
			Matrix mtx = new Matrix();
			mtx.postRotate(calculateRotationBeforeSaving());
			Bitmap rotatedBMP = Bitmap.createBitmap(bmp, 0, 0, w, h, mtx, true);
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			rotatedBMP.compress(Bitmap.CompressFormat.PNG, 100, stream);
			byte[] byteArray = stream.toByteArray();
			return byteArray;

		} catch (Exception e) {
			e.printStackTrace();
			Log.e(GlobalMembers.TAG,"Error Occured in CameraActivity",e);
			GlobalMembers.showAlert(ctx,e);
			return null;
		} finally {

		}
	}

	private int calculateRotationBeforeSaving() {
		Log.d(GlobalMembers.TAG,"calculateRotationBeforeSaving based on Orientation Sensor : "+GlobalMembers.GLOBAL_ORIENTATION);
		switch(GlobalMembers.GLOBAL_ORIENTATION){
		case 0:
		{
			return 270;
		}
		case 1:{
			return 0;
		}
		case 2:{
			return 90;
		}
		case 3:{
			return 180;
		}
		}
		return 0;
	}

	public void setThumbView(){
		Log.v(GlobalMembers.TAG,"inside setThumbView ");
		Bitmap resized=null;
		try{
			String tag="Loading";
			Log.d(GlobalMembers.TAG,"setThumbView(), width of thumbView= "+thumbView.getWidth());
			if(GlobalMembers.SELFIE_PRO_FILE_LIST==null || GlobalMembers.SELFIE_PRO_FILE_LIST.size()==0){
				Log.v(GlobalMembers.TAG,"SELFIE_PRO_FILE_LIST size "+null);
				resized= BitmapFactory.decodeResource(ctx.getResources(),
						R.drawable.loading);
			}
			else{
				Log.v(GlobalMembers.TAG,"SELFIE_PRO_FILE_LIST size "+GlobalMembers.SELFIE_PRO_FILE_LIST.size());
				resized= ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(GlobalMembers.SELFIE_PRO_FILE_LIST.get(0)), 60, 60);
				if(resized==null){
					resized= BitmapFactory.decodeResource(ctx.getResources(),
							R.drawable.loading);
				}
				else{
					tag="Image";
				}
				thumbView.setTag(tag);
			}
		}catch(Exception e){
			e.printStackTrace();
			resized= BitmapFactory.decodeResource(ctx.getResources(),
					R.drawable.loading);

		}finally{
			thumbView.setImageBitmap(resized);
		}
	}
	public void setThumbView(byte[] data){
		Log.v(GlobalMembers.TAG,"inside setThumbView with data");
		Bitmap resized=null;
		String tag="Loading";
		Log.d(GlobalMembers.TAG,"setThumbView(byte), width of thumbView= "+thumbView.getWidth());
		try{
			resized= ThumbnailUtils.extractThumbnail(BitmapFactory.decodeByteArray(data,0,data.length), thumbView.getWidth(), thumbView.getHeight());
			if(resized==null){
				resized= BitmapFactory.decodeResource(ctx.getResources(),
						R.drawable.loading);
			}
			else{
				tag="Image";
			}
		}catch(Exception e){
			e.printStackTrace();
			resized= BitmapFactory.decodeResource(ctx.getResources(),
					R.drawable.loading);
		}finally{
			thumbView.setTag(tag);
			thumbView.setImageBitmap(resized);
		}
	}

	private class SaveImageTask extends AsyncTask<byte[], Void, String> {

		@Override
		protected void onPostExecute(String imageFilePath) {
			super.onPostExecute(imageFilePath);
			//setThumbView();
		}

		@Override
		protected String doInBackground(byte[]... data) {
			FileOutputStream outStream = null;
			try {

				File sdCard = Environment.getExternalStorageDirectory();
				File dir = new File (sdCard.getAbsolutePath() + "/smartselfiepro");
				dir.mkdirs();
				String fileName = "selfie_"+String.format("%d.jpg", System.currentTimeMillis());
				File outFile = new File(dir, fileName);
				imageFilePath=dir+File.separator+fileName;
				Log.d(GlobalMembers.TAG,"ImageFilePath "+imageFilePath);
				outStream = new FileOutputStream(outFile);
				outStream.write(data[0]);
				outStream.flush();
				outStream.close();
				Log.d(GlobalMembers.TAG, "onPictureTaken - wrote bytes: " + data.length + " to " + outFile.getAbsolutePath());
				refreshGallery(outFile);
				return imageFilePath;
			} catch (FileNotFoundException e) {
				Log.e(GlobalMembers.TAG,"Error Occured in CameraActivity",e);
				GlobalMembers.showAlert(ctx,e);
			} catch (IOException e) {
				Log.e(GlobalMembers.TAG,"Error Occured in CameraActivity",e);
				GlobalMembers.showAlert(ctx,e);
			} finally {
			}
			return null;
		}

	}



}


