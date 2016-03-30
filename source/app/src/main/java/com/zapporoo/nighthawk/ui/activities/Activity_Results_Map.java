package com.zapporoo.nighthawk.ui.activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.zapporoo.nighthawk.R;
import com.zapporoo.nighthawk.model.ModCheckIn;
import com.zapporoo.nighthawk.model.ModUser;
import com.zapporoo.nighthawk.util.UtilAnimation;
import com.zapporoo.nighthawk.util.UtilDialog;
import com.zapporoo.nighthawk.util.XxUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Activity_Results_Map extends ActivityDefault implements OnMapClickListener, OnMapLongClickListener, OnMapReadyCallback {

	private static final long LOCATION_REFRESH_TIME = 2000;
	private static final float LOCATION_REFRESH_DISTANCE = 50;
	public static final String P_LAT = "P_LAT";
	public static final String P_LON = "P_LON";

	private static final String TAG = "ActivitySignupMap";
	public static List<ModUser> businessList;
	private List<ModCheckIn> checkInList;
	//MapFragment mapFragment;
	SupportMapFragment mapFragment;
	Map<Marker, ModUser> hashmap;

	private static int LIMIT_BLACK = 10;
	private static int LIMIT_BLUE = 20;
	//private static int LIMIT_RED = ;



	private boolean mActive;

	ModUser user;
	private ProgressDialog mProgressDialog;
	private boolean isCheckinReady = false, isMapReady = false;

	View llMapLegend;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_results_map);
		setupGui();
		user = Activity_Register_Business1.modBusiness;
		mProgressDialog = UtilDialog.createProgressDialogSpinning(this, null);
		hashmap = new HashMap<>();
		startCheckInQuery();
	}



	private void startCheckInQuery() {
		showProgressDialogSpinning();
		ModCheckIn.queryAllWhoAreCheckedInBusiness(businessList).findInBackground(new FindCallback<ModCheckIn>() {
			@Override
			public void done(List<ModCheckIn> objects, ParseException e) {
				dismissProgressDialog();
				if (e == null) {
					checkInList = objects;
					isCheckinReady = true;
					checkReady();
				}else
					showCloseActivity(Activity_Results_Map.this, "Server error, please try again later!");
			}
		});
	}

	private void setupGui() {
		Toolbar mToolbarMain = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(mToolbarMain);
		llMapLegend = findViewById(R.id.llMapLegend);
		llMapLegend.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

			}
		});

		// Get the ActionBar here to configure the way it behaves.
		final ActionBar ab = getSupportActionBar();
		if(ab != null) {
			ab.setDisplayShowHomeEnabled(true); // show or hide the default home button
			ab.setDisplayHomeAsUpEnabled(true);
			ab.setDisplayShowCustomEnabled(true); // enable overriding the default toolbar layout
			ab.setDisplayShowTitleEnabled(false); // disable the default title element here (for centered title)
		}

		showToolbarTitle(mToolbarMain, getString(R.string.title_activity_results_map));  // default
	}

	private ParseGeoPoint getUserGeoPoint(){
		ParseGeoPoint pgp = null;
		try{
			Bundle b = getIntent().getExtras();
			double lat = b.getDouble(P_LAT);
			double lon = b.getDouble(P_LON);

			if (lat == 0.0 && lon == 0.0)
				pgp = null;
			else
				pgp = new ParseGeoPoint(lat, lon);
			return  pgp;
		}catch (Exception e){

		}
		return pgp;
	}

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
			finish();
			UtilAnimation.startPrevoiusActivitySlide(this);
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onResume() {

		mActive = true;
		mapFragment = getMapFragment();
		mapFragment.getMapAsync(this);
		//startLogic()
		int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

		if (status == ConnectionResult.SUCCESS) {
			boolean isServiceAvailable = true;
			int playVersion = GooglePlayServicesUtil.GOOGLE_PLAY_SERVICES_VERSION_CODE;
		} else {
			int requestCode = 10;
			Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, requestCode);
			dialog.show();
		}

		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();

		mActive = false;
	}

	@Override
	public void onMapReady(GoogleMap map) {
		mProgressDialog.dismiss();
		map.setOnMapClickListener(this);
		map.setOnMapLongClickListener(this);
		map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener(){
			@Override
			public boolean onMarkerClick(com.google.android.gms.maps.model.Marker marker){
				marker.showInfoWindow();
				return true;
			}
		});
		map.setInfoWindowAdapter(new MarkerInfoWindowAdapter());
		centerMapOnUser();
		isMapReady = true;
		checkReady();
	}

	private void checkReady() {
		if (isMapReady && isCheckinReady)
			putBusinessMarkers(businessList);
	}

	private void putBusinessMarkers(List<ModUser> businessList) {
		for (ModUser business:businessList){
			ParseGeoPoint gp = business.getGeopoint();

			if (gp != null) {
				int checkInCount = countCheckIns(business);
				business.checkIns = checkInCount;
				BitmapDescriptor icon = null;

				if (checkInCount <= 10)
					icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_gps_off_edit);

				if ((10 < checkInCount) && (checkInCount) <= 20)
					icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_gps_blue_edit);

				if (checkInCount > 20)
					icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_gps_red_edit);

				Marker marker = putAddressMarker(gp.getLatitude(), gp.getLongitude(), null, icon);
				hashmap.put(marker, business);
			}
		}
	}

	private int countCheckIns(ModUser business) {
		int ret = 0;
		if (business.isGeneratedFromYelp())
			for (ModCheckIn checkIn:checkInList){
				if (business.getYelpId().equals(checkIn.getToYelp()))
					ret++;
			}else
			for (ModCheckIn checkIn:checkInList){
				if (checkIn.getTo() != null && checkIn.getTo().isEqual(business))
					ret++;
			}

		return ret;
	}

	private void centerMapOnUser() {
		ParseGeoPoint geoPoint = getUserGeoPoint();

		if (geoPoint != null){
			moveCameraToAddress(geoPoint.getLatitude(), geoPoint.getLongitude());
			putAddressMarker(geoPoint.getLatitude(), geoPoint.getLongitude(), null, BitmapDescriptorFactory.fromResource(R.drawable.ic_gps_hawk_larger));
		}else{
			if (businessList != null && businessList.size() > 0){
				geoPoint = businessList.get(0).getGeopoint();
				moveCameraToAddress(geoPoint.getLatitude(), geoPoint.getLongitude());
			}
		}
	}

	private Marker putAddressMarker(Double lat, Double lon, String title, BitmapDescriptor bitmapDescriptor) {
		Marker marker;
		LatLng point = new LatLng(lat, lon);
			if (title != null) {
				marker = mapFragment.getMap().addMarker(new MarkerOptions().position(point).icon(bitmapDescriptor).title(title));
				//.showInfoWindow();
			}else {
				marker = mapFragment.getMap().addMarker(new MarkerOptions().position(point).icon(bitmapDescriptor));
				//.showInfoWindow();
			}

		return marker;
	}

	private void moveCameraToAddress(double lat, double lon) {
		int zoom = 13 - XxUtil.getSearchRadiusIndex(this);
		mapFragment.getMap().moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lon), zoom));
	}

	@Override
	public void onMapLongClick(LatLng point) {
		//putAddressMarker(point);
	}

	@Override
	public void onMapClick(LatLng point) {
		//putAddressMarker(point.latitude, point.longitude, null);
		moveCameraToAddress(point.latitude, point.longitude);
	}


    public boolean isPermissionGranted(String permission){
//        if (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this, permission))
            return true;
//        else
//            return false;
    }

	private ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions options;

	public class MarkerInfoWindowAdapter implements GoogleMap.InfoWindowAdapter{
		public MarkerInfoWindowAdapter(){}

		@Override
		public View getInfoWindow(final Marker marker){
			if (marker.isInfoWindowShown()) {
				marker.hideInfoWindow();
				marker.showInfoWindow();
			}
			return null;
		}

		@Override
		public View getInfoContents(final Marker marker){

			View v  = getLayoutInflater().inflate(R.layout.infowindow_layout, null);
			ModUser business = hashmap.get(marker);

			if (business == null)
				return null;

			ImageView markerIcon = (ImageView) v.findViewById(R.id.marker_icon);

			String url = "";
			if(business.isGeneratedFromYelp()){
				url = business.getYelpImageUrl();
			}else{
				url = business.getParseProfileImageUrl();
			}

			int iconId = R.drawable.ic_gps_off_edit;

			if ((10 < business.checkIns) && (business.checkIns) <= 20)
				iconId = R.drawable.ic_gps_blue_edit;

			if (business.checkIns > 20)
				iconId = R.drawable.ic_gps_red_edit;

			options = new DisplayImageOptions.Builder()
					.showImageOnLoading(iconId)        //    Display Stub Image
					//.showImageForEmptyUri(R.drawable.ic_gps_off_edit)    //    If Empty image found
					.cacheInMemory(true)
					.cacheOnDisk(true)
					.bitmapConfig(Bitmap.Config.RGB_565)
					.build();

			imageLoader.displayImage(url, markerIcon, options, new ImageLoadingListener() {
						@Override
						public void onLoadingStarted(String imageUri, View view) {
							//getInfoContents(marker);
						}

						@Override
						public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
							getInfoWindow(marker);
						}

						@Override
						public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
							getInfoWindow(marker);
						}

						@Override
						public void onLoadingCancelled(String imageUri, View view) {
							//getInfoContents(marker);
						}

					});

			TextView markerLabel = (TextView)v.findViewById(R.id.marker_label);
			markerLabel.setText(business.getBusinessName());

			TextView markerDetails = (TextView)v.findViewById(R.id.another_label);
			markerDetails.setText("Ratings: " + business.getRatingString());

			return v;
		}
	}

}
