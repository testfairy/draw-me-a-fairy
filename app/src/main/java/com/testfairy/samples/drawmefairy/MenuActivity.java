package com.testfairy.samples.drawmefairy;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;
import draw.me.fairy.R;


public class MenuActivity extends Activity {

	private static final int SELECT_PICTURE_REQUEST_CODE = 1;
	private static int menuOpenedCount = 0;
	private final String TAG = getClass().getSimpleName();
	private LocationListener locationListener = new LocationListener() {
		@Override
		public void onLocationChanged(Location location) {
			Log.i(TAG, "onLocationChanged " + location.getLongitude() + " , " + location.getLatitude());
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			Log.i(TAG, "onStatusChanged");
		}

		@Override
		public void onProviderEnabled(String provider) {
			Log.i(TAG, "onProviderEnabled");
		}

		@Override
		public void onProviderDisabled(String provider) {
			Log.i(TAG, "onProviderDisabled");
		}
	};
	private final int MENU_CLOCK = 1;
	private final int MENU_BLOG = 2;
	private View.OnClickListener onClickFromGallery = new View.OnClickListener() {
		@Override
		public void onClick(View view) {

			Intent intent = new Intent(MenuActivity.this, SelectPhotoActivity.class);
			startActivity(intent);
//			intent.setType("image/jpeg");
//			intent.setAction(Intent.ACTION_GET_CONTENT);
//			startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE_REQUEST_CODE);
		}
	};
	private View.OnLongClickListener onLongClickMenuLogo = new View.OnLongClickListener() {
		@Override
		public boolean onLongClick(View v) {
			Intent intent = new Intent(MenuActivity.this, SecretActivity.class);
			startActivity(intent);
			return false;
		}
	};

	public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
		Cursor cursor = null;
		final String column = "_data";
		final String[] projection = {
		    column
		};

		try {
			cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
			if (cursor != null && cursor.moveToFirst()) {
				final int index = cursor.getColumnIndexOrThrow(column);
				return cursor.getString(index);
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return null;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate");
		super.onCreate(savedInstanceState);

		Log.d(TAG, "onCreate " + TAG);
		// hide title bar
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.main_layout);

		Button fromGalleryButton = (Button) findViewById(R.id.from_gallery);
		Button blankCanvasButton = (Button) findViewById(R.id.blank_canvas);
		Button aboutButton = (Button) findViewById(R.id.about_app);
		Button crashButton = (Button) findViewById(R.id.crash_button);
		View menuLogo = findViewById(R.id.menu_logo);

		fromGalleryButton.setOnClickListener(new OnClickStartActivity(SelectPhotoActivity.class));
		blankCanvasButton.setOnClickListener(new OnClickStartActivity(DrawingActivity.class));
		aboutButton.setOnClickListener(new OnClickStartActivity(AboutActivity.class));
		crashButton.setOnClickListener(new OnClickStartActivity(CrashActivity.class));
		menuLogo.setOnLongClickListener(onLongClickMenuLogo);



	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.i(TAG, "on Activity Result called");

		if (requestCode == SELECT_PICTURE_REQUEST_CODE && resultCode == RESULT_OK && null != data) {
			String imagePath = getPath(this, data.getData());
			Log.d(TAG, "imagePath = " + imagePath);
			Intent intent = new Intent(MenuActivity.this, DrawingActivity.class);
			intent.putExtra(DrawingActivity.EXTRA_PICTURE_PATH, imagePath);
			startActivity(intent);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
//		startLocationService();
	}

	@Override
	protected void onPause() {
		super.onPause();
		LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		locationManager.removeUpdates(this.locationListener);
	}

	@Override
	public void onBackPressed() {
		finish();
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is ExternalStorageProvider.
	 */
	private boolean isExternalStorageDocument(Uri uri) {
		return "com.android.externalstorage.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is DownloadsProvider.
	 */
	private boolean isDownloadsDocument(Uri uri) {
		return "com.android.providers.downloads.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is MediaProvider.
	 */
	private boolean isMediaDocument(Uri uri) {
		return "com.android.providers.media.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is Google Photos.
	 */
	private boolean isGooglePhotosUri(Uri uri) {
		return "com.google.android.apps.photos.content".equals(uri.getAuthority());
	}

	@TargetApi(Build.VERSION_CODES.KITKAT)
	private String getPath(final Context context, final Uri uri) {

		final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
		Log.d(TAG, "URI: " + uri);

		// DocumentProvider
		if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
			// ExternalStorageProvider
			if (isExternalStorageDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];

				if ("primary".equalsIgnoreCase(type)) {
					return Environment.getExternalStorageDirectory() + "/" + split[1];
				}

				// TODO handle non-primary volumes
			}
			// DownloadsProvider
			else if (isDownloadsDocument(uri)) {

				final String id = DocumentsContract.getDocumentId(uri);
				final Uri contentUri = ContentUris.withAppendedId(
				    Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

				return getDataColumn(context, contentUri, null, null);
			}
			// MediaProvider
			else if (isMediaDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];

				Uri contentUri = null;
				if ("image".equals(type)) {
					contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				} else if ("video".equals(type)) {
					contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
				} else if ("audio".equals(type)) {
					contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
				}

				final String selection = "_id=?";
				final String[] selectionArgs = new String[]{
				    split[1]
				};

				return getDataColumn(context, contentUri, selection, selectionArgs);
			}
		}
		// MediaStore (and general)
		else if ("content".equalsIgnoreCase(uri.getScheme())) {

			// Return the remote address
			if (isGooglePhotosUri(uri)) {
				Log.d(TAG, "Opening Google photos uri " + uri);
				return uri.getLastPathSegment();
			}

			return getDataColumn(context, uri, null, null);
		}
		// File
		else if ("file".equalsIgnoreCase(uri.getScheme())) {
			return uri.getPath();
		}

		return null;
	}

	/**
	 * start location service, so TestFairy can demonstrate Geolocation
	 */
	private void startLocationService() {
		LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

		Criteria locationCriteria = new Criteria();
		locationCriteria.setAccuracy(Criteria.ACCURACY_COARSE);
		locationCriteria.setAltitudeRequired(false);
		locationCriteria.setBearingRequired(false);
		locationCriteria.setCostAllowed(true);
		locationCriteria.setPowerRequirement(Criteria.NO_REQUIREMENT);

		String providerName = locationManager.getBestProvider(locationCriteria, true);

//		if (providerName != null && locationManager.isProviderEnabled(providerName)) {
		if (locationManager.isProviderEnabled(providerName)) {
			// Provider is enabled
			locationManager.requestLocationUpdates(providerName, 20000, 100, locationListener);
		} else {
			// Provider not enabled, prompt user to enable it
			Toast.makeText(this, R.string.please_turn_on_gps, Toast.LENGTH_LONG).show();
			Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			this.startActivity(myIntent);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(1, MENU_CLOCK, 1, "Clock");
		menu.add(1, MENU_BLOG, 1, "TestFairy Blog");

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onMenuOpened(int featureId, Menu menu) {
		menuOpenedCount++;
		Log.v("testfairy-checkpoint", "Main Menu Opened " + menuOpenedCount + " times");
		return super.onMenuOpened(featureId, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		Intent intent;
		switch (item.getItemId()) {
			case MENU_CLOCK:
				intent = new Intent(MenuActivity.this, ClockActivity.class);
				startActivity(intent);
				break;

			case MENU_BLOG:
				intent = new Intent(MenuActivity.this, TestFairyBlogActivity.class);
				startActivity(intent);
				break;

			default:
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	private class OnClickStartActivity implements View.OnClickListener {

		private Class<?> clr;

		private OnClickStartActivity(Class<?> clr) {

			this.clr = clr;
		}

		@Override
		public void onClick(View v) {
			Intent intent = new Intent(MenuActivity.this, clr);
			startActivity(intent);
		}
	}
}
