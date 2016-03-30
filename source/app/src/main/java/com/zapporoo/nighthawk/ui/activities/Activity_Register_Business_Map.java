package com.zapporoo.nighthawk.ui.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.ParseGeoPoint;
import com.zapporoo.nighthawk.R;
import com.zapporoo.nighthawk.model.ModUser;
import com.zapporoo.nighthawk.util.UtilAnimation;
import com.zapporoo.nighthawk.util.UtilDialog;
import com.zapporoo.nighthawk.util.XxUtil;


import java.io.IOException;

public class Activity_Register_Business_Map extends FragmentActivity implements OnMapClickListener, OnMapLongClickListener, OnMapReadyCallback {

	private static final long LOCATION_REFRESH_TIME = 2000;
	private static final float LOCATION_REFRESH_DISTANCE = 50;
	private static final String TAG = "ActivitySignupMap";
	//MapFragment mapFragment;
	SupportMapFragment mapFragment;
	Address address;
	EditText etAddress;
	Button btnSearch;
	Geocoder geocoder;
	private LocationManager mLocationManager;

	String address_return;
	int GPS_ACCURACY = 60;

	ModUser user;
	private ProgressDialog mProgressDialog;
	private ParseGeoPoint userGeoPoint;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_signup_map);
		user = Activity_Register_Business1.modBusiness;
		mProgressDialog = UtilDialog.createProgressDialogSpinning(this, null);

//		if (ModUser.getModUser() == null)
//			user = IcApp.user;
//		else
//			user = ModUser.getModUser();

		setupGui();
	}


//	private MapFragment getMapFragment() {
//	    FragmentManager fm = null;
//
//	    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
//	        NhLog.d(TAG, "using getFragmentManager");
//	        fm = getFragmentManager();
//	    } else {
//	        Log.d(TAG, "using getChildFragmentManager");
//	        fm = getFragmentManager();
//	    }
//	    return (MapFragment) fm.findFragmentById(R.id.fragmentMap);
//	}

	private SupportMapFragment getMapFragment() {
		android.support.v4.app.FragmentManager fm = null;

		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
			Log.d(TAG, "using getFragmentManager");
			fm = getSupportFragmentManager();
		} else {
			Log.d(TAG, "using getChildFragmentManager");
			fm = getSupportFragmentManager();
		}

		SupportMapFragment mySupportMapFragment = (SupportMapFragment) fm.findFragmentById(R.id.fragmentMap);
		return mySupportMapFragment;
	}

	private void startGeoDecoder(LatLng point) {
		geocoder = new Geocoder(this);
		try {
			address = geocoder.getFromLocation(point.latitude, point.longitude, 3).get(0);
			String address_title = address.getAddressLine(0);
			address_return = address_title.concat("");

			if (address.getLocality() != null) {
				address_return = address_return.concat(", " + address.getLocality());
			}
			if (address.getAdminArea() != null) {
				address_return = address_return.concat(", " + address.getAdminArea());
			}
			if (address.getPostalCode() != null)
				address_title = address_return.concat(", " + address.getPostalCode());

			etAddress.setText(address_return);
			putAddressMarker(point.latitude, point.longitude, address_title);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void setupGui() {
		etAddress = (EditText) findViewById(R.id.etMapAddress);
		etAddress.setText(user.getAddress());

		btnSearch = (Button) findViewById(R.id.btnMapAddressSearch);
		btnSearch.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					geocoder = new Geocoder(Activity_Register_Business_Map.this);
					address = geocoder.getFromLocationName(etAddress.getText().toString(), 3).get(0);
					String address_title = address.getAddressLine(0);
					address_return = address_title.concat("");

					if (address.getAdminArea() != null) {
						address_title.concat(", " + address.getAdminArea());
						address_return = address_title.concat("");
					}
					if (address.getPostalCode() != null)
						address_title.concat(", " + address.getPostalCode());
					moveCameraToAddress(address.getLatitude(), address.getLongitude());
					putAddressMarker(address.getLatitude(), address.getLongitude(), address.getAddressLine(0));
				} catch (IOException e) {
					XxUtil.showToast(Activity_Register_Business_Map.this, "Unable to find typed address. Please try different keywords.");
					e.printStackTrace();
				} catch (Exception e) {
					XxUtil.showToast(Activity_Register_Business_Map.this, "Unable to find typed address. Please try different keywords.");
				}

			}

		});

	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_map_done, menu);
		return true;
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		UtilAnimation.startPrevoiusActivitySlide(this);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();

		if (id == android.R.id.home) {
			finish();
			UtilAnimation.startPrevoiusActivitySlide(this);
			return true;
		}

		if (id == R.id.action_done) {
			if (!saveGeoDataOk())
				return false;
			finish();
			UtilAnimation.startPrevoiusActivitySlide(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private boolean saveGeoDataOk() {
		if (address != null) {
			user.setAddress(address_return);
			//user.setLattitude(String.valueOf(address.getLatitude()));
			//user.setLongitude(String.valueOf(address.getLongitude()));
			userGeoPoint.setLatitude(address.getLatitude());
			userGeoPoint.setLongitude(address.getLongitude());
			user.setGeopoint(userGeoPoint);
		} else {
			if (userGeoPoint == null) {
				XxUtil.showToast(this, "Please pick a location or enter address!");
			}
			else
				user.setGeopoint(userGeoPoint);
		}
		return true;
	}

	@Override
	protected void onPause() {
		if (mLocationManager != null)
			if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
				// TODO: Consider calling
				//    ActivityCompat#requestPermissions
				// here to request the missing permissions, and then overriding
				//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
				//                                          int[] grantResults)
				// to handle the case where the user grants the permission. See the documentation
				// for ActivityCompat#requestPermissions for more details.
				return;
			}
		mLocationManager.removeUpdates(mLocationListener);
		super.onPause();
	}

	@Override
	protected void onResume() {
		boolean isServiceAvailable = false;
		boolean isMapFragmentAvailable = false;
		int playVersion = -1;

		mapFragment = getMapFragment();
		mapFragment.getMapAsync(this);
		checkGpsState();
		//startLogic()
		startLocator();
		int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

		if (status == ConnectionResult.SUCCESS) {
			isServiceAvailable = true;
			playVersion = GooglePlayServicesUtil.GOOGLE_PLAY_SERVICES_VERSION_CODE;
		} else {
			int requestCode = 10;
			Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, requestCode);
			dialog.show();
		}

//		if (mapFragment != null)
//			isMapFragmentAvailable = true;
		//Toast.makeText(this, "Available: " + isServiceAvailable + "\nFragment: " + isMapFragmentAvailable + "\nVersion: " + playVersion, Toast.LENGTH_LONG).show();
		super.onResume();
	}

	private void startLocator() {
		if (isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION)) {
			mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
			if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
				// TODO: Consider calling
				//    ActivityCompat#requestPermissions
				// here to request the missing permissions, and then overriding
				//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
				//                                          int[] grantResults)
				// to handle the case where the user grants the permission. See the documentation
				// for ActivityCompat#requestPermissions for more details.
				return;
			}
			mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_REFRESH_TIME, LOCATION_REFRESH_DISTANCE, mLocationListener);
		}
	}

	@Override
	public void onMapReady(GoogleMap map) {
		mProgressDialog.dismiss();
		map.setOnMapClickListener(this);
		map.setOnMapLongClickListener(this);

		ParseGeoPoint geoPoint = user.getGeopoint();
		if (geoPoint != null){
			moveCameraToAddress(geoPoint.getLatitude(), geoPoint.getLongitude());
			putAddressMarker(geoPoint.getLatitude(), geoPoint.getLongitude(), user.getAddress());
		}
	}

	private void putAddressMarker(Double lat, Double lon, String title) {
		LatLng point = new LatLng(lat, lon);
		mapFragment.getMap().clear();
			if (title != null)
				mapFragment.getMap().addMarker(new MarkerOptions().position(point).title(title)).showInfoWindow();
			else
				mapFragment.getMap().addMarker(new MarkerOptions().position(point)).showInfoWindow();


		userGeoPoint = new ParseGeoPoint(lat, lon);
	}

	private void moveCameraToAddress(double lat, double lon) {
		mapFragment.getMap().moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lon), 16));
	}

	@Override
	public void onMapLongClick(LatLng point) {
		//putAddressMarker(point);
	}

	@Override
	public void onMapClick(LatLng point) {
		putAddressMarker(point.latitude, point.longitude, null);
		startGeoDecoder(point);
		moveCameraToAddress(point.latitude, point.longitude);
	}

	
	
	
	
	boolean locatedOnce = false;
	private final LocationListener mLocationListener = new LocationListener() {

		@Override
		public void onLocationChanged(final Location location) {
			float accuracy = location.getAccuracy();
			Log.e("onLocationChanged", "Accuracy:" + location.getAccuracy());
			

			if (accuracy < GPS_ACCURACY && !locatedOnce ){
				locatedOnce = true;
				startGeoDecoder(new LatLng(location.getLatitude(), location.getLongitude()));
				moveCameraToAddress(location.getLatitude(), location.getLongitude());
				
			}
			setGpsEnabled();
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {

			// SmIcLog.e(TAG, "Location StatusChanged:" + status + " Provider:" +
			// provider);

			if (LocationProvider.OUT_OF_SERVICE == status) {
				setGpsDisabled();
			}
			if (LocationProvider.TEMPORARILY_UNAVAILABLE == status) {
				// setGpsDisabled();
			}
			if (LocationProvider.AVAILABLE == status) {
				setGpsEnabled();
			}
		}

		// !!!!!!!!!!!!!!!!!!!!!!!!
		// won't be called if GPS was turned on before we started the app.
		// will be called if GPS was turned on after we started the app.
		@Override
		public void onProviderEnabled(String provider) {
			setGpsEnabled();
			// SmIcLog.e(TAG, "Location ProviderEnabled:" + provider);
		}

		// will be called only if GPS is turned off on the device
		@Override
		public void onProviderDisabled(String provider) {
			setGpsDisabled();
			// SmIcLog.e(TAG, "Location ProviderDisabled:" + provider);
		}
	};

	private boolean mGpsEnabled;

	protected void setGpsEnabled() {
		mGpsEnabled = true;

	}

	protected void setGpsDisabled() {
		mGpsEnabled = false;
	}

	private boolean checkGpsState() {
		mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
			alertDialogBuilder.setMessage("GPS is disabled in your device. Would you like to enable it?").setCancelable(false)
					.setPositiveButton("GPS Settints page", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							Intent callGPSSettingIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
							startActivity(callGPSSettingIntent);
						}
					});
			alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.cancel();
				}
			});
			AlertDialog alert = alertDialogBuilder.create();
			alert.show();
			return false;
		}
		return true;
	}

    public boolean isPermissionGranted(String permission){
//        if (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this, permission))
            return true;
//        else
//            return false;
    }
	
}
