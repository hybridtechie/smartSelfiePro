package com.hybrid.probuk.views;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;

import com.hybrid.probuk.utils.CameraUtils;
import com.hybrid.probuk.global.GlobalMembers;

public class Preview extends ViewGroup implements SurfaceHolder.Callback {
	public static TappableSurfaceView mSurfaceView;
	SurfaceHolder mHolder;
	Size mPreviewSize;
	public static Camera mCamera;
	Context ctx;
	List<String> focusModes;

	@SuppressWarnings("deprecation")
	public 	Preview(Context context, TappableSurfaceView sv) {
		super(context);
		ctx=context;
		mSurfaceView = sv;
		mHolder = mSurfaceView.getHolder();
		mHolder.addCallback(this);
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	public void setCamera(Camera camera) {
		Log.v(GlobalMembers.TAG,"Inside setCamera");
		mCamera = camera;
		try{
			if (mCamera != null) {
				requestLayout();
				Camera.Parameters parameters = mCamera.getParameters();
				focusModes = parameters.getSupportedFocusModes();
				setSizeParametersForCamera(parameters,null,null);
			}
		}catch(Exception e){
			GlobalMembers.showAlert(ctx,GlobalMembers.ERROR_IN_SET_CAMERA,e);
		}
	}

	public void setSizeParametersForCamera(Camera.Parameters parameters,Integer w, Integer h){
		try{
			Log.v(GlobalMembers.TAG,"Inside setSizeParametersForCamera");
			if (focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
				parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
				mCamera.setParameters(parameters);
			}
			if(w!=null){
				mPreviewSize=CameraUtils.getOptimalPreviewSize(parameters.getSupportedPreviewSizes(), w, h);
				if(mPreviewSize!=null){
					Log.d(GlobalMembers.TAG,"Preview Size Width:"+mPreviewSize.width);
					parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
				}
				else{
					Log.e(GlobalMembers.TAG,"No Preview Size Found-1");
				}
			}
			else{
				Log.e(GlobalMembers.TAG,"Not setting preview size as call from setCamera");
			}
			setRotationParameter((Activity)ctx,parameters);
			Camera.Size pictureSize=CameraUtils.getBestPictureSize(parameters);
			if(pictureSize!=null){
				parameters.setPictureSize(pictureSize.width,pictureSize.height);
			}
			else{
				Log.e(GlobalMembers.TAG,"No picture Size Found");
			}
			mCamera.setParameters(parameters);
		}catch( Exception e){
			Log.e(GlobalMembers.TAG,"Error setSizeParametersForCamera");
		}
	}


	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		Log.v(GlobalMembers.TAG, "onMeasure Called");
		final int width = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
		final int height = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);
		setMeasuredDimension(width, height);
		//mPreviewSize=CameraUtils.getOptimalPreviewSize(sizes, w, h)
		//		if (mSupportedPreviewSizes != null) {
		//			mPreviewSize = getOptimalPreviewSize(mSupportedPreviewSizes, width, height);
		//		}
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		Log.v(GlobalMembers.TAG, "onLayout Called");
		if (changed && getChildCount() > 0) {
			final View child = getChildAt(0);

			final int width = r - l;
			final int height = b - t;

			int previewWidth = width;
			int previewHeight = height;
			if (mPreviewSize != null) {
				previewWidth = mPreviewSize.width;
				previewHeight = mPreviewSize.height;
			}
			if (width * previewHeight > height * previewWidth) {
				final int scaledChildWidth = previewWidth * height / previewHeight;
				child.layout((width - scaledChildWidth) / 2, 0,
						(width + scaledChildWidth) / 2, height);
			} else {
				final int scaledChildHeight = previewHeight * width / previewWidth;
				child.layout(0, (height - scaledChildHeight) / 2,
						width, (height + scaledChildHeight) / 2);
			}
		}
	}

	public void surfaceCreated(SurfaceHolder holder) {
		try {
			Log.v(GlobalMembers.TAG, "surfaceCreated Called");
			if (mCamera == null) {
				mCamera = Camera.open(GlobalMembers.CAMERAID);
			}
			mCamera.setPreviewDisplay(holder);
		} catch (Exception e) {
			Log.e(GlobalMembers.TAG, GlobalMembers.ERROR_IN_PREVIEW_DISPLAY, e);
			GlobalMembers.showAlert(ctx, GlobalMembers.ERROR_IN_PREVIEW_DISPLAY, e);

		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		Log.v(GlobalMembers.TAG, "On Draw Called");
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.v(GlobalMembers.TAG, "surfaceDestroyed Called");
		if (mCamera != null) {
			mCamera.stopPreview();
		}
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
		try{
			if(mCamera != null) {
				Log.d(GlobalMembers.TAG+"*******************","OnSurfaceChanged");
				Camera.Parameters parameters = mCamera.getParameters();
				setSizeParametersForCamera(parameters,w,h);
				mCamera.startPreview();
			}
		}catch(Exception e){
			GlobalMembers.showAlert(ctx, GlobalMembers.ERROR_IN_SURFACE_CREATED, e);
		}
	}

//	public boolean isPortrait() {
//		return (((Activity)ctx).getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT);
//	}

	public  void setRotationParameter(Activity activity, Camera.Parameters param) {
		int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
		int toRotate=0;
		switch(rotation){
		case 0:{
			toRotate=90;
		}
		break;
		case 1:{
			toRotate=0;
		}
		break;
		case 2:{
			toRotate=180;
		}
		break;
		case 3:{
			toRotate=180;
		}
		break;
		}
		Log.d(GlobalMembers.TAG,"Preview setRotationParameter for size : "+rotation);
		mCamera.setDisplayOrientation(toRotate);
	}

}
