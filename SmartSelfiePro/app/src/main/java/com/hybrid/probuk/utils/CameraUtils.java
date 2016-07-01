package com.hybrid.probuk.utils;

import java.util.List;

import com.hybrid.probuk.global.GlobalMembers;

import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Size;
import android.util.Log;

public class CameraUtils {

	public static Camera.Size getBestPictureSize(Camera.Parameters parameters){
		try{
			Log.v(GlobalMembers.TAG,"Inside getBestPictureSize");
			List<Size> sizes = parameters.getSupportedPictureSizes();
			if(sizes !=null){
				Camera.Size size = sizes.get(0);
				for(int i=0;i<sizes.size();i++)
				{
					if(sizes.get(i).width > size.width)
						size = sizes.get(i);
				}
				return size;
			}
			else{
				return null;
			}
		}catch(Exception e){
			return null;
		}
	}

	public static Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
		final double ASPECT_TOLERANCE = 0.1;
		double targetRatio=(double)h / w;
		if (sizes == null) {
			Log.e(GlobalMembers.TAG,"Camera doesnt have any preview size");
			return null;
		}
		Log.d(GlobalMembers.TAG,"Camera has preview size");
		Camera.Size optimalSize = null;
		double minDiff = Double.MAX_VALUE;

		int targetHeight = h;

		for (Camera.Size size : sizes) {
			double ratio = (double) size.width / size.height;
			if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
			if (Math.abs(size.height - targetHeight) < minDiff) {
				optimalSize = size;
				minDiff = Math.abs(size.height - targetHeight);
			}
		}

		if (optimalSize == null) {
			minDiff = Double.MAX_VALUE;
			for (Camera.Size size : sizes) {
				if (Math.abs(size.height - targetHeight) < minDiff) {
					optimalSize = size;
					minDiff = Math.abs(size.height - targetHeight);
				}
			}
		}
		Log.d(GlobalMembers.TAG,"Optimal Preview Size Returned : "+optimalSize.width+" * "+optimalSize.height);
		return optimalSize;
	}


	public static Integer getCameraId(){
		try{
			for (int camNo = 0; camNo < Camera.getNumberOfCameras(); camNo++) {
				CameraInfo camInfo = new CameraInfo();
				Camera.getCameraInfo(camNo, camInfo);
				if (camInfo.facing==(Camera.CameraInfo.CAMERA_FACING_FRONT)) {
					return camNo;
				}
			}
			return null;
		} catch (RuntimeException ex){
			return null;
		}
	}


//	public static int calculateRotation(Context){
//		int rotation = this.getWindowManager().getDefaultDisplay()
//				.getRotation();
//		Log.d(GlobalMembers.TAG,"Rotation before Saving : "+rotation);
//		int toRotate=0;
//		switch(rotation){
//		case 0:{
//			toRotate=270;
//		}
//		break;
//		case 1:{
//			toRotate=270;
//		}
//		break;
//		case 2:{
//			toRotate=180;
//		}
//		break;
//		case 3:{
//			toRotate=180;
//		}
//		break;
//		}
//		return toRotate;
//	}
//

}
