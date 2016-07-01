package com.hybrid.probuk.utils;

import com.hybrid.probuk.global.GlobalMembers;

import android.content.Context;
import android.os.AsyncTask;

public class AsyncFileLoader extends AsyncTask<Void, Void, Void> {

	public Context ctx;
	public FileLoaderListner fileLoaderListner;
	public AsyncFileLoader(Context context,FileLoaderListner fileLoaderListner) {
		this.ctx=context;
		this.fileLoaderListner=fileLoaderListner;
	}
	public AsyncFileLoader(Context context) {
		this.ctx=context;
		this.fileLoaderListner=null;
	}
	@Override
	protected void onPostExecute(Void data) {
		super.onPostExecute(data);
		if(fileLoaderListner!=null){
			fileLoaderListner.setThumbViewFromLasTImage();
		}
		//setThumbView();
	}

	@Override
	protected Void doInBackground(Void...obj) {
		GlobalMembers.SELFIE_PRO_FILE_LIST=FileUtils.listAllFiles(ctx);
		return null;

	}

}

