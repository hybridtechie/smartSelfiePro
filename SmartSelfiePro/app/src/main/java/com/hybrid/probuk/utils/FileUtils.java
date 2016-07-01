package com.hybrid.probuk.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.hybrid.probuk.global.GlobalMembers;

public class FileUtils {

	static List<Uri> imagesUri=new ArrayList<Uri>();

	public static ArrayList<String> listAllFiles(Context ctx){
		File sdCard = Environment.getExternalStorageDirectory();
		File dir = new File (sdCard.getAbsolutePath() + "/smartselfiepro");
		ArrayList<String> filePaths=new ArrayList<String>();
		Log.v(GlobalMembers.TAG,"listAllFiles: ");
		if(dir!=null && dir.exists()){
			File[] files = dir.listFiles();
			if(files != null){
				for(File f : files){
					String fileName = f.getName();
					if(fileName.endsWith(".jpg")){
						Uri uri=Uri.fromFile(f);
						imagesUri.add(uri);
						filePaths.add(f.getAbsolutePath());
					}
					else{
					}
				}
				Collections.sort(filePaths,Collections.reverseOrder(String.CASE_INSENSITIVE_ORDER));
				return filePaths;
			}else{
				Log.v(GlobalMembers.TAG,"Files Null ");
			}
		}else{
			Log.v(GlobalMembers.TAG,"Directory Null ");
		}
		return filePaths;

	}
}
