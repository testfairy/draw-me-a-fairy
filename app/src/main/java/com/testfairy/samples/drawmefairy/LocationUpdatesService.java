package com.testfairy.samples.drawmefairy;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.testfairy.TestFairy;

public class LocationUpdatesService extends Service implements LocationListener {

    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
    private static final long MIN_TIME_BW_UPDATES = 1000 * 5 * 1;

	private final Context mContext;
    private LocationManager locationManager;

	private boolean checkGPS = false;
	private boolean checkNetwork = false;
	private boolean canGetLocation = false;

	private Location loc;
	private double latitude;
	private double longitude;

	public LocationUpdatesService(Context mContext) {
		this.mContext = mContext;
		getLocationUpdates();
	}

	public LocationUpdatesService() {
		this.mContext = this;
		getLocationUpdates();
	}

	@SuppressLint("MissingPermission")
	private Location getLocationUpdates() {
		try {
			locationManager = (LocationManager) mContext
					.getSystemService(LOCATION_SERVICE);

			// get GPS status
			checkGPS = locationManager
					.isProviderEnabled(LocationManager.GPS_PROVIDER);

			// get network provider status
			checkNetwork = locationManager
					.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

			if (!checkGPS && !checkNetwork) {
				Toast.makeText(mContext, "No Service Provider is available", Toast.LENGTH_SHORT).show();
			} else {
				this.canGetLocation = true;

				// if GPS Enabled get lat/long using GPS Services
				if (checkGPS) {
					locationManager.requestLocationUpdates(
							LocationManager.GPS_PROVIDER,
							MIN_TIME_BW_UPDATES,
							MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

					if (locationManager != null) {
						loc = locationManager
								.getLastKnownLocation(LocationManager.GPS_PROVIDER);
						if (loc != null) {
							latitude = loc.getLatitude();
							longitude = loc.getLongitude();
						}
					}
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}

		return loc;
	}

    public Location getLocation() {
        return loc;
    }

    public double getLongitude() {
		if (loc != null) {
			longitude = loc.getLongitude();
		}

		return longitude;
	}

	public double getLatitude() {
		if (loc != null) {
			latitude = loc.getLatitude();
		}

		return latitude;
	}

	public boolean canGetLocation() {
		return this.canGetLocation;
	}

	public void showSettingsAlert() {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

		alertDialog.setTitle("GPS is not Enabled!");
		alertDialog.setMessage("Do you want to turn on GPS?");

		alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				mContext.startActivity(intent);
			}
		});

		alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});

		alertDialog.show();
	}


	public void stopListener() {
		if (locationManager != null) {
			locationManager.removeUpdates(this);
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onLocationChanged(Location location) {
	    loc = location;
        Log.d("LocationUpdatesService", "New Location: " + location.toString());
        TestFairy.updateLocation(location);
	}

	@Override
	public void onStatusChanged(String s, int i, Bundle bundle) {

	}

	@Override
	public void onProviderEnabled(String s) {

	}

	@Override
	public void onProviderDisabled(String s) {

	}
}
