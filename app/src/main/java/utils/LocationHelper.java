package utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.widget.Toast;

import com.testfairy.samples.drawmefairy.LocationUpdatesService;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class LocationHelper {

	private Activity activity;

	private List<String> permissionsToRequest;
	private List<String> permissionsRejected = new ArrayList<>();
	private List<String> permissions = new ArrayList<>();

	private final static int ALL_PERMISSIONS_RESULT = 101;
	private LocationUpdatesService locationTrack;

	public LocationHelper() {}

	private void startListening() {
		locationTrack = new LocationUpdatesService(activity);

		if (locationTrack.canGetLocation()) {
			double longitude = locationTrack.getLongitude();
			double latitude = locationTrack.getLatitude();

			Toast.makeText(activity.getApplicationContext(), String.format("Longitude:%s\nLatitude:%s", longitude, latitude), Toast.LENGTH_SHORT).show();
		} else {

			locationTrack.showSettingsAlert();
		}
	}

	private List<String> findPermissions(List<String> wanted) {
		List<String> result = new ArrayList<>();

		for (String perm : wanted) {
			if (!hasPermission(perm)) {
				result.add(perm);
			}
		}

		return result;
	}

	private boolean hasPermission(String permission) {
		if (supportsRuntimePermissions()) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
				return (activity.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
			}
		}

		return true;
	}

	private boolean supportsRuntimePermissions() {
		return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
	}

	private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
		new AlertDialog.Builder(activity)
				.setMessage(message)
				.setPositiveButton("OK", okListener)
				.setNegativeButton("Cancel", null)
				.create()
				.show();
	}

	public void onCreate(Activity activity) {
		this.activity = activity;

		permissions.add(ACCESS_FINE_LOCATION);
		permissionsToRequest = findPermissions(permissions);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			if (permissionsToRequest.size() > 0) {
				activity.requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
			} else {
				startListening();
			}
		}
	}

	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		switch (requestCode) {
			case ALL_PERMISSIONS_RESULT:
				for (String perms : permissionsToRequest) {
					if (!hasPermission(perms)) {
						permissionsRejected.add(perms);
					}
				}

				if (permissionsRejected.size() > 0) {
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
						if (activity.shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
							showMessageOKCancel("These permissions are mandatory for this feature to work. Please allow access.",
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int which) {
											if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
												activity.requestPermissions(permissionsRejected.toArray(new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
											}
										}
									});
							return;
						}
					}
				} else {
					startListening();
				}

				break;
		}
	}

	public void onDestroy() {
		locationTrack.stopListener();
	}

	public Location getLocation() {
		if (locationTrack != null) {
			return locationTrack.getLocation();
		}

		return null;
	}
}
