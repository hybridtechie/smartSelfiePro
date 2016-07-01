package com.hybrid.probuk.selfiePro;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.hybrid.probuk.utils.FileUtils;
import com.hybrid.probuk.global.GlobalMembers;
import com.hybrid.probuk.views.TouchImageView;

public class ViewActivity extends Activity {

	private String imageFilePath;
	private boolean shareClicked=false;
	private Context ctx;
	private boolean actionBarStatus=false;
	private FullScreenImageAdapter adapter;
	private ViewPager viewPager;
	public ArrayList<String> files;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_ACTION_BAR);
		setContentView(R.layout.activity_view);
		viewPager = (ViewPager) findViewById(R.id.pager);
		Intent i = getIntent();
		int position = i.getIntExtra("position", 0);
		ctx=this;
		try{
			shareClicked=false;
			files=FileUtils.listAllFiles(this);
			imageFilePath=files.get(0);
			adapter = new FullScreenImageAdapter(this);
			viewPager.setAdapter(adapter);
			viewPager.setCurrentItem(position);
			viewPager.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Log.d(GlobalMembers.TAG,"ViewPager Clicked");
					if(actionBarStatus){
						getActionBar().hide();
						actionBarStatus=false;
					}
					else{
						getActionBar().show();
						actionBarStatus=true;
					}
				}
			});
		}catch(Exception e){
			Log.e(GlobalMembers.TAG,"Error in ViewActivity",e);
			GlobalMembers.showAlert(this,GlobalMembers.ERROR_IN_VIEW_CREATED,e);
		}
	}
	public void broadcastScanFile(File f) {
		try{
			Intent intentNotifyImgDeleted = new Intent();
			intentNotifyImgDeleted.setType("image/*");
			intentNotifyImgDeleted.setAction(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
			intentNotifyImgDeleted.setData(Uri.fromFile(f));
			sendBroadcast(intentNotifyImgDeleted);
		}catch(Exception e){
			Log.e(GlobalMembers.TAG," Delete Broadcast failed");
		}
	}

	public boolean deleteTheImage(String fileName){
		boolean deleted =false;
		try{
			File file;
			if(fileName==null){
				file= new File(imageFilePath);
			}
			else{
				file= new File(fileName);
			}
			if (file.exists()) {
				deleted = file.delete();
				broadcastScanFile(file);
				if(deleted){
					files.remove(imageFilePath);
					Log.d(GlobalMembers.TAG,"Removed Image = "+imageFilePath);
					Toast.makeText(ctx, "Image Deleted", Toast.LENGTH_SHORT).show();
//					if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
//						MediaScannerConnection.scanFile(ctx, new String[] { Environment.getExternalStorageDirectory().toString() }, null, new MediaScannerConnection.OnScanCompletedListener() {
//							public void onScanCompleted(String path, Uri uri)
//							{
//								Log.i("ExternalStorage", "Scanned " + path + ":");
//								Log.i("ExternalStorage", "-> uri=" + uri);
//							}
//						});
//					}
//					else{
//						sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
//								Uri.parse("file://" +  Environment.getExternalStorageDirectory())));
//					}
					//finish();
				}
				else{
					Toast.makeText(ctx, "Image Could Not be Deleted", Toast.LENGTH_SHORT).show();
				}
			}
		}catch(Exception e){
			GlobalMembers.showAlert(ctx, e);
		}
		return deleted;

	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Log.i(GlobalMembers.TAG , "back button pressed ");
			if(shareClicked){
				shareClicked=false;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	private Intent createShareIntent(String fileName) {
		Log.d(GlobalMembers.TAG,"Share intent Called : "+fileName);
		try{
			Intent shareIntent = new Intent(Intent.ACTION_SEND);
			shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
			shareIntent.setType("image/*");
			Uri uri = Uri.parse(fileName);
			shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
			return shareIntent;
		}catch(Exception e){
			GlobalMembers.showAlert(ctx,GlobalMembers.ERROR_IN_STARTING_SHARE_INTENT,e);
			return null;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.view_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
		//		case R.id.action_share:
		//			if(!shareClicked){
		//				startActivity(Intent.createChooser(createShareIntent(imageFilePath), "How do you want to share?"));
		//				shareClicked=true;
		//			}
		//			else{
		//				Toast.makeText(ctx, "Please Wait", Toast.LENGTH_LONG).show();
		//			}
		//			return true;
		//		case R.id.action_delete:
		//			try{
		//				boolean deleted=deleteTheImage(null);
		//				if(deleted){
		//					//finish();
		//				}
		//			}catch(Exception e){
		//				GlobalMembers.showAlert(ctx, e);
		//			}
		//			return true;
		case R.id.action_settings:
			Intent i=new Intent(this,SettingsActivity.class);
			startActivity(i);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public class FullScreenImageAdapter extends PagerAdapter {

		private Activity _activity;
		private LayoutInflater inflater;

		public FullScreenImageAdapter(Activity activity) {
			this._activity = activity;
		}

		@Override
		public int getCount() {
			return files.size();
		}
		@Override
		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == ((RelativeLayout) object);
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			try{

				Log.d(GlobalMembers.TAG,"instantiateItem");
				imageFilePath=files.get(position);
				Log.d(GlobalMembers.TAG,"Imagefile Position : "+position+" File Path :"+imageFilePath);
				TouchImageView imgDisplay;
				Button deleteButton,shareButton;

				inflater = (LayoutInflater) _activity
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				View viewLayout = inflater.inflate(R.layout.layout_fullscreen_image, container,
						false);
				imgDisplay = (TouchImageView) viewLayout.findViewById(R.id.imgDisplay);
				deleteButton=(Button)viewLayout.findViewById(R.id.deleteButton);
				shareButton=(Button)viewLayout.findViewById(R.id.shareButton);

				final int curPosition=position;
				deleteButton.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Log.d(GlobalMembers.TAG,"deleet Button Clicked");
						viewPager.setAdapter(null);
						deleteTheImage(files.get(curPosition));
						viewPager.setAdapter(adapter);
						Log.d(GlobalMembers.TAG,"getCount(): "+getCount());
						if(getCount()==0){
							Log.d(GlobalMembers.TAG,"getCount()==0");
							Toast.makeText(ctx,"No Image to Show",Toast.LENGTH_SHORT).show();
							finish();
						}
						if(curPosition==0){
							viewPager.setCurrentItem(curPosition+1);
						}
						else {
							viewPager.setCurrentItem(curPosition-1);
						}
						notifyDataSetChanged();
						finish();
					}
				});
				shareButton.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Log.d(GlobalMembers.TAG,"shareClicked ");
						if(!shareClicked){
							startActivity(Intent.createChooser(createShareIntent(files.get(curPosition)), "How do you want to share?"));
							shareClicked=true;
						}
						else{
							Toast.makeText(ctx, "Please Wait", Toast.LENGTH_LONG).show();
						}
					}
				});

				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inPreferredConfig = Bitmap.Config.ARGB_8888;
				Bitmap bitmap = BitmapFactory.decodeFile(files.get(position), options);
				if(bitmap!=null){
					Log.d(GlobalMembers.TAG,"bitmap not null ");
					imgDisplay.setImageBitmap(bitmap);
					((ViewPager) container).addView(viewLayout);
				}
				else{
					Log.d(GlobalMembers.TAG,"bitmap null ");
					viewPager.setAdapter(null);
					viewPager.setAdapter(adapter);
					notifyDataSetChanged();
					return null;
				}

				return viewLayout;}
			catch(Exception e){
				Log.e(GlobalMembers.TAG,"Error in instatiating");
				finish();
			}
			return null;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			((ViewPager) container).removeView((RelativeLayout) object);

		}

	}
}
