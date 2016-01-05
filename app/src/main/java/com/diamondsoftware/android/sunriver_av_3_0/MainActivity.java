package com.diamondsoftware.android.sunriver_av_3_0;

import java.io.File;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;

import com.diamondsoftware.android.sunriver_av_3_0.DbAdapter.FavoriteItemType;

/**
 *  Manages all of the actions at the home page.
 *  Preloads the map location items.
 *  Serves also as a central location for managing actions required of different activities, such a a
 *  static method for loading geofences once all the locations are loaded.
 *   
 */
public class MainActivity extends AbstractActivityForListViewsNonscrollingImage implements WaitingForDataAcquiredAsynchronously,DataGetter,
			DoesNewImageEvery4Or5Seconds {
	public static ArrayList<Hashtable<ItemLocation.LocationType, ArrayList<Object>>> LocationData = new ArrayList<Hashtable<ItemLocation.LocationType, ArrayList<Object>>>();
	public static ArrayList<Object> SunriverArray = null;
	private static boolean AllMapsUriLocationDataIsLoaded=false;
	private static boolean AllNonUriMapsDataIsLoaded=false;
	public static final String PREFERENCES_MAPS_POPUP_RESTAURANTS = "mapLayersRestaurants";
	public static final String PREFERENCES_MAPS_POPUP_RETAIL = "mapLayersRetail";
	public static final String PREFERENCES_MAPS_POPUP_POOLS = "mapLayersPools";
	public static final String PREFERENCES_MAPS_POPUP_TENNISCOURTS = "mapLayersTennisCourts";
	public static final String PREFERENCES_MAPS_POPUP_GAS = "mapLayersGas";
	public static final String PREFERENCES_MAPS_POPUP_PERFECTPICTURESPOTS = "mapLayersPerfectPictureSpots";
	public static final String PREFERENCES_MAPS_POPUP_BIKEPATHS = "mapLayersBikePaths";
	public final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
	public static MainActivity mSingleton;
	private static GeocodeManager mGeocodeManager;
	private static String SECOND_GENERATION_FIND_MY_HOME_INDICATOR="|~";

	public static boolean heresHowIChangeCameraFaceCleanly=false;

	private String android_id;
	private static boolean mGeocoderIsLoaded=false;
	
	/*
	 * The "Sunriver graphic item doesn't come from fetch of all the other map
	 * items. It could, but inasmuch as it is not really one of the defined
	 * types, it was treated as its own singleton. All the map items are built
	 * together in one single fetch. So, the sunriver singleton has to be
	 * inserted specially; either at the end of the building of it, or after the
	 * others
	 */
	public static synchronized void AddSunriverArrayToLocationDataIfAppropriate() {
		boolean doit = true;
		// Don't do anything if we haven't yet built the SunriverArray
		if (SunriverArray != null) {
			// Don't add it if it's already there.
			if (!LocationData.isEmpty()) {
				for (Hashtable ht : LocationData) {
					if (ht.contains(ItemLocation.LocationType.SUNRIVER)) {
						doit = false;
					}
				}
			}
		} else {
			doit = false;
		}
		if (doit) {
			Hashtable ht = new Hashtable<ItemLocation.LocationType, ArrayList<Object>>();
			ht.put(ItemLocation.LocationType.SUNRIVER, MainActivity.SunriverArray);
			MainActivity.LocationData.add(ht);
			setAllNonUriMapsDataIsLoaded();
		}
	}

	@Override
	protected int getImageId() {
		return R.id.activity_main_image;
	}
	
	// generate a random URL for pictures. The set of items to pick from is the ArrayList SplashPage.TheItemWelcomes
	public String getRandomWelcomeImageURL() {
		try {
			ArrayList<Object> qualifyingItems=new ArrayList<Object>();
			for (Object obj :((GlobalState)getApplicationContext()).TheItemWelcomes) {
				if(((ItemWelcome)obj).isInRotation() && !TextUtils.isEmpty(((ItemWelcome)obj).getWelcomeURL() )) {
					qualifyingItems.add(obj);
				}
			}
			if(qualifyingItems.size()<=0) {
				qualifyingItems.add(((GlobalState)getApplicationContext()).TheItemWelcomes.get(0));
			}
			double randomNumber=Math.random();
			double factor=1d/(double)qualifyingItems.size();
			int element=(int)(randomNumber/factor);
			return ((ItemWelcome)qualifyingItems.get(element)).getWelcomeURL();
		} catch (Exception e) {
			return "http://www.srfeed.com/res/pics/welcome/Welcome.jpg";
		}
	}

	@Override
	protected String getImageURL() {
		if(((GlobalState)getApplicationContext()).TheItemWelcomes!=null) {
			return getRandomWelcomeImageURL();
		}
		return null;
	}
	
	public static void setAllNonUriMapsDataIsLoaded() { // this means that we're all loaded, and it's time to create the GeoFences
		AllNonUriMapsDataIsLoaded=true;
		if(AllMapsUriLocationDataIsLoaded && mGeocodeManager!=null && !mGeocoderIsLoaded) {
			mGeocoderIsLoaded=true;
			// load the GeocodeManager geofences
			mGeocodeManager.enableGeocode();
		}
	}
	
	public static void setAllMapsUriLocationDataIsLoaded() {
		AllMapsUriLocationDataIsLoaded=true;
		if(AllNonUriMapsDataIsLoaded&& mGeocodeManager!=null && !mGeocoderIsLoaded) { // this means that we're all loaded, and it's time to create the GeoFences
			mGeocoderIsLoaded=true;
			// load the GeocodeManager geofences
			mGeocodeManager.enableGeocode();
		}
	}

	private void managePushNotifications() {

		String topic=getIntent().getExtras().getString("PushNotificationTopic");
		String title=getIntent().getExtras().getString("PushNotificationTitle");
		String message=getIntent().getExtras().getString("PushNotificationMessage");
		String emergencyMapURL=getIntent().getExtras().getString("PushNotificationEmergencyMapURL");

		if(topic.equals("alert") || topic.equals("alerttest")  || topic.equals("alerttestinternal")) {
			ItemAlert itemAlert=new ItemAlert( new Date().getTime(),title,message);
			itemAlert.setmIsOnAlert(true);
			if(topic.equals("alerttest")) {
				((GlobalState)getApplicationContext()).theItemAlert=itemAlert;
			}
			new PopupAlert(this,itemAlert).createPopup();
		}
		if(topic.equals("newsfeed") || topic.equals("newsfeedtest")  || topic.equals("newsfeedtestinternal")) {
			ItemNewsFeed itemNewsFeed=new ItemNewsFeed(new Date().getTime(),title,message);
			itemNewsFeed.setIsOnNewsFeedAlert(true);
			if(topic.equals("newsfeedtest")) {
				((GlobalState)getApplicationContext()).theItemNewsFeed=itemNewsFeed;
			}
			new PopupNewsFeed(this, itemNewsFeed).createPopup();
		}
		if(topic.equals("emergency") || topic.equals("emergencytest") || topic.equals("emergencytestinternal")) {
			ItemEmergency itemEmergency=new ItemEmergency();
			itemEmergency.setEmergencyAlert(true);
			itemEmergency.setEmergencyDescription(message);
			itemEmergency.setEmergencyId(Math.abs((int)new Date().getTime()));
			itemEmergency.setEmergencyTitle(title);
			if(emergencyMapURL!=null && !emergencyMapURL.trim().equals("")) {
				itemEmergency.setHasMap(true);
				itemEmergency.addMapURL(emergencyMapURL);
			} else {
				itemEmergency.setHasMap(false);
			}
			if(topic.equals("emergencytest") || topic.equals("emergencytestinternal")) {
				if (((GlobalState) getApplicationContext()).TheItemsEmergency==null) {
					((GlobalState) getApplicationContext()).TheItemsEmergency=new ArrayList<Object>();
				}
				((GlobalState) getApplicationContext()).TheItemsEmergency.add(itemEmergency);
			}
			new PopupEmergency(this, itemEmergency).createPopup();
		}



	}

	private boolean doTips=true;
	@Override
	protected void childOnCreate(Bundle savedInstanceState) {
		if(getIntent()!=null && getIntent().getAction() != null &&  getIntent().getAction().equals("PushNotification")) {
			managePushNotifications();
			doTips=false;
		}
		/* Start Push Notification services */
		Intent jdIntent3=new Intent(this,PushNotificationsManager.class);
		startService((jdIntent3));

		((GlobalState)getApplicationContext()).gaSendView("Sunriver Navigator - Home Page");
		android_id=Secure.getString(getContentResolver(),
                Secure.ANDROID_ID);
		mGeocodeManager = new GeocodeManager(this);
		if(AllNonUriMapsDataIsLoaded) {
			if(!mGeocoderIsLoaded) {
				mGeocoderIsLoaded=true;
				mGeocodeManager.enableGeocode();
			}
		}
		if(AllMapsUriLocationDataIsLoaded) {
			if(!mGeocoderIsLoaded) {
				mGeocoderIsLoaded=true;
				mGeocodeManager.enableGeocode();
			}
		}
		mSingleton=this;
        String imageURL=getImageURL();
		if(imageURL!=null && getImageId()!=0) {
			ImageLoader imageLoader=new ImageLoaderRemote(this,true,1f);
			imageLoader.displayImage(imageURL,mImageView);
		}
		if(mSharedPreferences.getBoolean("show_home_page_tips", true) && doTips) {
			Intent intent=new Intent(MainActivity.this,ActivityTipsHomePage.class);
			startActivity(intent);
		}

 	}

	@Override
	protected ListViewAdapter getListViewAdapter() {
		return new ListViewAdapterForLandingPage(this);
	}

	@Override
	protected int getListViewId() {
		return R.id.list;
	}

	@Override
	protected int getViewId() {
		return R.layout.activity_main;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.diamondsoftware.android.sunriver_av_3_0.AbstractActivityForListViews#childOnItemClick(android.widget.AdapterView, android.view.View, int, long)
	 * 
	 * If id==9, re-start this activity, having set the bike paths to true; otherwise
	 * start the activity associated with the line selected.
	 */
	
	@Override
	protected void childOnItemClick(AdapterView<?> parent, View view,
			int position, long id) {
		if (id == 100) {
			((GlobalState)getApplicationContext()).gaSendView("Sunriver Navigator - Maps");

			File dir=new File(android.os.Environment.getExternalStorageDirectory(),"/sunriver"); // no need to popup the disclaimer if the user checked "do not show again"
			File donotshowagain=new File(dir, PopupDisclaimer.DISCLAIMER_MAPS_FILE);
			Intent intent = new Intent(this, Maps.class);
			if(!donotshowagain.exists()) {
				new PopupDisclaimer(this,PopupDisclaimer.DISCLAIMER_MAPS_FILE,intent).createPopup();
			} else {
				startActivity(intent);
			}
		}
		if(id==900) {
			Editor edit=mSharedPreferences.edit();
			edit.putBoolean(MainActivity.PREFERENCES_MAPS_POPUP_BIKEPATHS, true);
			edit.commit();
			File dir=new File(android.os.Environment.getExternalStorageDirectory(),"/sunriver"); // no need to popup the disclaimer if the user checked "do not show again"
			File donotshowagain=new File(dir, PopupDisclaimer.DISCLAIMER_BIKEPATHS_FILE);
			Intent intent = new Intent(this, Maps.class);
			if(!donotshowagain.exists()) {
				new PopupDisclaimer(this,PopupDisclaimer.DISCLAIMER_BIKEPATHS_FILE,intent).createPopup();
			} else {
				startActivity(intent);
			}
		}
		if (id == 200) {
		    android.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
		    android.app.Fragment prev = getFragmentManager().findFragmentByTag("weatherpagechooser");
		    if (prev != null) {
		        ft.remove(prev);
		    }
		    ft.addToBackStack(null);
		    SelectWeatherPage selectWeatherPage=new SelectWeatherPage();
			selectWeatherPage.show(ft,"weatherpagechooser");
		}
		if (id == 120) {
			((GlobalState)getApplicationContext()).gaSendView("Sunriver Navigator - Where to Stay");
			Intent intent = new Intent(MainActivity.this, ActivityHospitality.class);
			startActivity(intent);
		}
		if(id==1001) {
			
			int x=0;
			int y=3;
			int z=y/x;
			
		}
		if (id == 300) {
			((GlobalState)getApplicationContext()).gaSendView("Sunriver Navigator - Events");
			Intent intent = new Intent(MainActivity.this, CalendarActivitySummary.class);
			startActivity(intent);
		}
		if (id == 400) {
			((GlobalState)getApplicationContext()).gaSendView("Sunriver Navigator - Activity Planner");
			Intent intent = new Intent(MainActivity.this, ActivitiesActivity.class);
			startActivity(intent);
		}
		if (id == 500) {
			((GlobalState)getApplicationContext()).gaSendView("Sunriver Navigator - QR Code Reader");			
			IntentIntegrator integrator = new IntentIntegrator(
					MainActivity.this);
			integrator.initiateScan();
		}
		if (id == 600) {
			((GlobalState)getApplicationContext()).gaSendView("Sunriver Navigator - Eats & Treats");
			Intent intent = new Intent(MainActivity.this,EatsAndTreatsActivity.class);
			startActivity(intent);
		}
		if (id == 110) {
			((GlobalState)getApplicationContext()).gaSendView("Sunriver Navigator - Shopping");
			Intent intent = new Intent(MainActivity.this,ActivityRetail.class);
			startActivity(intent);
		}
		if (id == 700) {
			((GlobalState)getApplicationContext()).gaSendView("Sunriver Navigator - Services");
			Intent intent = new Intent(MainActivity.this,ServicesActivity.class);
			startActivity(intent);
		}
		if(id==800) {
			((GlobalState)getApplicationContext()).gaSendView("Sunriver Navigator - Selfie");
			Intent intentCamera=new Intent(MainActivity.this,AndroidCamera.class);
			startActivity(intentCamera);
		}
		if( id==99) {
			((GlobalState)getApplicationContext()).gaSendView("Sunriver Navigator - Alert");
			new PopupAlert(this, ((GlobalState)getApplicationContext()).theItemAlert).createPopup();
		}
		if( id==98) {
			((GlobalState)getApplicationContext()).gaSendView("Sunriver Navigator - News Item");
			new PopupNewsFeed(this, ((GlobalState)getApplicationContext()).theItemNewsFeed).createPopup();
		}
		if(id>=100000) { // Emergency
			ItemEmergency itemEmergency=GlobalState.getEmergencyItemWhoseIdIs((int)(id-100000));
			if(itemEmergency!=null) {
				((GlobalState)getApplicationContext()).gaSendView("Sunriver Navigator - Emergency");
				new PopupEmergency(this, itemEmergency).createPopup();
			}
		}
		if(id==1000) {
			((GlobalState)getApplicationContext()).gaSendView("Sunriver Navigator - Find My Home");
		    android.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
		    android.app.Fragment prev = getFragmentManager().findFragmentByTag("findhome");
		    if (prev != null) {
		        ft.remove(prev);
		    }
		    ft.addToBackStack(null);
		    FindHomeDialog findHomeDialog=FindHomeDialog.newInstance(this);
			findHomeDialog.show(ft,"findhome");
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if(mGeocodeManager!=null) {
			// Register the broadcast receiver to receive status updates
			LocalBroadcastManager.getInstance(this).registerReceiver(
					mGeocodeManager.getBroadcastReceiver(),
					mGeocodeManager.getIntentFilter());
		}
		if(heresHowIChangeCameraFaceCleanly) {
			heresHowIChangeCameraFaceCleanly=false;
			Intent intentCamera=new Intent(this,AndroidCamera.class);
			startActivity(intentCamera);
		}
		if(GlobalState.homePageNeedsRefreshing) {
			((ListViewAdapterLocalData)mAdapter).performDataFetch();
			GlobalState.homePageNeedsRefreshing=false;
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		switch (requestCode) {
		/*
		 * This code is to handle the QR Code Reader
		 */
		case IntentIntegrator.REQUEST_CODE:

			IntentResult scanResult = IntentIntegrator.parseActivityResult(
					requestCode, resultCode, intent);
			if (scanResult != null) {
				final String url = scanResult.getContents();

				AlertDialog.Builder builder = new AlertDialog.Builder(
						MainActivity.this);
				builder.setMessage(url);
				builder.setTitle("Open URL?");
				// Add the buttons
				builder.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								Intent intent = new Intent(MainActivity.this,
										Website.class).putExtra("url", url);
								startActivity(intent);
							}
						});
				builder.setNegativeButton("No",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// User cancelled the dialog
							}
						});
				// Set other dialog properties

				// Create the AlertDialog
				AlertDialog dialog = builder.create();
				dialog.show();

			}
			break;
		// If the request code matches the code sent in onConnectionFailed
		case GeofenceUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST:

			switch (resultCode) {
			// If Google Play services resolved the problem
			case Activity.RESULT_OK:

				// If the request was to add geofences
				if (GeofenceUtils.REQUEST_TYPE.ADD == mGeocodeManager.getRequestType()) {

					// Toggle the request flag and send a new request
					mGeocodeManager.getGeofenceRequester().setInProgressFlag(
							false);

					// Restart the process of adding the current geofences
					mGeocodeManager.getGeofenceRequester().addGeofences(
							mGeocodeManager.getCurrentGeofences());

					// If the request was to remove geofences
				} else if (GeofenceUtils.REQUEST_TYPE.REMOVE == mGeocodeManager
						.getRequestType()) {

					// Toggle the removal flag and send a new removal request
					mGeocodeManager.getGeofenceRemover().setInProgressFlag(
							false);

					// If the removal was by Intent
					if (GeofenceUtils.REMOVE_TYPE.INTENT == mGeocodeManager
							.getRemoveType()) {

						// Restart the removal of all geofences for the
						// PendingIntent
						mGeocodeManager.getGeofenceRemover()
								.removeGeofencesByIntent(
										mGeocodeManager.getGeofenceRequester()
												.getRequestPendingIntent());

						// If the removal was by a List of geofence IDs
					} else {

						// Restart the removal of the geofence list
						mGeocodeManager.getGeofenceRemover()
								.removeGeofencesById(
										mGeocodeManager
												.getGeofenceIdsToRemove());
					}
				}
				break;
			default:
				break;

			}
		}
	}

	public ItemLocation findFirstItemLocationWhoseIdIs(String id) {
		
		for(Hashtable ht :LocationData) {
			for (Object al: ht.values()) {
				ArrayList<Object> aroo = (ArrayList<Object>)al;
				for (Object theElement :aroo) {
					ItemLocation location=(ItemLocation)theElement;
					if(String.valueOf(location.getmId()).equals(id)) {
						return location;
					}
				}
			}
		}
		return null;
	}
	public void resetCurrentPoppedUp() {
		if(mGeocodeManager!=null) {
			GeocodeManager._CurrentlyPoppedUp =false;
		}
	}
	
	@Override
	protected void hookDoSomethingWithTheDataIfYouWant(ArrayList<Object> data) {
		if( 
				(((GlobalState)getApplicationContext()).theItemNewsFeed!=null && 
						((GlobalState)getApplicationContext()).theItemNewsFeed.getnewsFeedTitle()!=null && 
					!((GlobalState)getApplicationContext()).theItemNewsFeed.getnewsFeedTitle().trim().isEmpty())
				|| 
				(((GlobalState)getApplicationContext()).theItemNewsFeed!=null && 
						((GlobalState)getApplicationContext()).theItemNewsFeed.getnewsFeedDescription()!=null && 
					!((GlobalState)getApplicationContext()).theItemNewsFeed.getnewsFeedDescription().trim().isEmpty())) 
			{
				ItemLandingPage newsFeedItem=new ItemLandingPage();
				newsFeedItem.setDescription(((GlobalState)getApplicationContext()).theItemNewsFeed.getnewsFeedTitle());
				newsFeedItem.setmIsStyleMarquee(false);
				newsFeedItem.setName("Sunriver News Bites");
				newsFeedItem.setId(98);
				newsFeedItem.setIconName("srnewsbites");
				data.add(0,newsFeedItem);
			}
		if( 
			(((GlobalState)getApplicationContext()).theItemAlert!=null && 
					((GlobalState)getApplicationContext()).theItemAlert.getmALTitle()!=null && 
				!((GlobalState)getApplicationContext()).theItemAlert.getmALTitle().trim().isEmpty())
			|| 
			(((GlobalState)getApplicationContext()).theItemAlert!=null && 
					((GlobalState)getApplicationContext()).theItemAlert.getmALDescription()!=null && 
				!((GlobalState)getApplicationContext()).theItemAlert.getmALDescription().trim().isEmpty())) 
		{
			ItemLandingPage alertItem=new ItemLandingPage();
			alertItem.setDescription(((GlobalState)getApplicationContext()).theItemAlert.getmALTitle());
			alertItem.setmIsStyleMarquee(false);
			alertItem.setName("Alert");
			alertItem.setId(99);
			alertItem.setIconName("alertnew");
			data.add(0,alertItem);
		}
		if(GlobalState.TheItemsEmergency!=null) {
			for(Object itemEmergency: GlobalState.TheItemsEmergency) {
				ItemLandingPage emergencyItem=new ItemLandingPage();
				emergencyItem.setDescription(((ItemEmergency)itemEmergency).getEmergencyDescription());
				emergencyItem.setmIsStyleMarquee(true);
				emergencyItem.setId((long)100000+((ItemEmergency)itemEmergency).getEmergencyId()); // I'll know which EmergencyItem to display using this technique (id-100000)
				emergencyItem.setIconName("alertnew"); //TODO: get icon 
				emergencyItem.setName(((ItemEmergency)itemEmergency).getEmergencyTitle());
				emergencyItem.setmOtherInfo("EmergencyId:"+((ItemEmergency)itemEmergency).getEmergencyId());
				data.add(0,emergencyItem);
			}
		}
	}
	
	/**
	 * Popup dialog to select Weather page
	 */
	public static class SelectWeatherPage extends DialogFragment {
		public SelectWeatherPage() {}
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			LayoutInflater inflater = getActivity().getLayoutInflater();
			View view = inflater.inflate(R.layout.popup_weather_function_chooser, null);
			builder.setView(view);
	        builder.setMessage(R.string.weatherchoosertitle);
	        Button forecast=(Button)view.findViewById(R.id.btn_weather_forecast);
	        Button webcams=(Button)view.findViewById(R.id.btn_webcams);
	        Button cancel = (Button)view.findViewById(R.id.btn_weather_chooser_cancel);
	        cancel.setOnClickListener(new View.OnClickListener() {				
				@Override
				public void onClick(View v) {
					SelectWeatherPage.this.getDialog().cancel();
				}
			});
	        forecast.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					((GlobalState)getActivity().getApplicationContext()).gaSendView("Sunriver Navigator - Weather forecast");
					Intent intent = new Intent(getActivity(), Weather2.class);
					startActivity(intent);
					SelectWeatherPage.this.getDialog().cancel();
				}
			});
	        webcams.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					((GlobalState)getActivity().getApplicationContext()).gaSendView("Sunriver Navigator - Weather web cams");
					Intent intent = new Intent(getActivity(), ActivityWebcams.class);
					startActivity(intent);
					SelectWeatherPage.this.getDialog().cancel();
				}
			});
	     // Create the AlertDialog object and return it
	     AlertDialog dialog= builder.create();
	     return dialog;

		}
	}
	/**
	 * 
	 * @author Diamond
	 * Popup dialog to enter Sunriver address
	 */
	public static class FindHomeDialog extends DialogFragment {
		public static FindHomeDialog newInstance(MainActivity mainActivity) {
			FindHomeDialog fhd = new FindHomeDialog();
			fhd.mMainActivity=mainActivity;
			return fhd;
		}
		MainActivity mMainActivity;

		public FindHomeDialog () {}
	    @Override
	    public Dialog onCreateDialog(Bundle savedInstanceState) {
	        // Use the Builder class for convenient dialog construction
	    	if(mMainActivity==null) {mMainActivity=MainActivity.mSingleton;}
	    	if(mMainActivity!=null) {
		        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		        // Get the layout inflater
		        LayoutInflater inflater = getActivity().getLayoutInflater();
		        View view = inflater.inflate(R.layout.find_home2, null);
		        EditText textView = (EditText) view.findViewById(R.id.resortlane2);
		        String[] lanes=null;
			     // Get the string array
		        	if(GlobalState.mSingleton.TheItemsLaneStrings==null) {
		        		ArrayList<String> stringLanes=new ArrayList<String>();
		        		for(Object itemLane: GlobalState.mSingleton.TheItemsLane) {
		        			stringLanes.add(((ItemLane)itemLane).getSrLane());
		        		}
		        		lanes=new String[stringLanes.size()];
		        		int i=0;
		        		for(String stringLane:stringLanes) {
		        			lanes[i++]=stringLane;
		        		}
		        		GlobalState.mSingleton.TheItemsLaneStrings=lanes;
		        	} else {
		        		lanes=GlobalState.mSingleton.TheItemsLaneStrings;
		        	}
				     // Create the adapter and set it to the AutoCompleteTextView 
//				     ArrayAdapter<String> adapter = 
	//			             new ArrayAdapter<String>(mMainActivity, android.R.layout.simple_list_item_1, (String[])lanes);
//				     textView.setAdapter(adapter);
		        builder.setView(view);
	
		        // Inflate and set the layout for the dialog
		        // Pass null as the parent view because its going in the dialog layout
//		        builder.setView(inflater.inflate(R.layout.find_home, null));
		        
		        builder.setMessage(R.string.key_in_your_resort_address)
		               .setPositiveButton(R.string.btn_continue, new DialogInterface.OnClickListener() {
		                   public void onClick(DialogInterface dialog, int id) {
		                	   final EditText resortLane=(EditText)FindHomeDialog.this.getDialog().findViewById(R.id.resortlane2);
//		                	   final EditText resortLot=(EditText)FindHomeDialog.this.getDialog().findViewById(R.id.resortlot);
		                	   new AcquireDataRemotelyAsynchronously(ascertainLot(resortLane.getText().toString())+"~"+ascertainLane(resortLane.getText().toString()), mMainActivity, mMainActivity);
		           			   mMainActivity.pd = ProgressDialog.show(mMainActivity,"Searching ...","Searching for "+resortLane.getText().toString(),true,false,null);
		                	   
		                  }
		               })
		               .setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
		                   public void onClick(DialogInterface dialog, int id) {
		                	   FindHomeDialog.this.getDialog().cancel();
		                   }
		               });
		        // Create the AlertDialog object and return it
		        AlertDialog dialog= builder.create();
		        return dialog;
	    	} else {
	    		return null;
	    	}
	    }
		private String ascertainLane(String address) {
			try {
				int indexOfBlank=address.indexOf(" ");
				String lane=address.substring(0,indexOfBlank);
				int i=Integer.valueOf(lane);
				String retValue = address.substring(indexOfBlank+1);
				return retValue;
			} catch (Exception e) {
				return address;
			}		
		}
		private String ascertainLot(String address) {
			try {
				int indexOfBlank=address.indexOf(" ");
				String lane=address.substring(0,indexOfBlank);
				int i=Integer.valueOf(lane);
				String retValue=String.valueOf(i);
				return retValue;
			} catch (Exception e) {
				return "";
			}		
		}
	}
	ProgressDialog pd;
	String nameFinding=null;
	boolean reShowResortSearch=false;
	@Override
	public ArrayList<Object> getRemoteData(String name) {
		try {
				nameFinding=name;
				// Add your data
//		        List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(1);
//		        nameValuePair.add(new BasicNameValuePair("resortAddress", name));
				String defaultValue=getResources().getString(R.string.urlfindhomejson);
				
				
				/*TODO PUBLISH*/
				/* Use this when you've published 7/20/2015 version, or later, of the web app 	String uri=getResources().getString(R.string.urlfindhomejson);*/
				/*  Use this when you're still using my web site */  String uri=getResources().getString(R.string.urlfindhomejsontestremote);
				/* This one is for my testing in my office		String uri=getResources().getString(R.string.urlfindhomejsontestlocal); */  

				ArrayList<Object> data = new JsonReaderFromRemotelyAcquiredJson(
//					nameValuePair,
					new ParsesJsonFindHome(name), 
					uri+"?resortAddress="+SECOND_GENERATION_FIND_MY_HOME_INDICATOR+URLEncoder.encode(name)+"~"+android_id).parse();
				return data;
			} catch (Exception e) {
				int bkhere1=3;
				int bkhere2=bkhere1;
			} finally {
			}			
		return null;
	}
	@Override
	public void gotMyData(String name, ArrayList<Object> data) {		
		if(name==null) {
			super.gotMyData(name,data);
		} else {
			pd.dismiss();
			String countyAddress=null;
			String sunriverAddress=null;
			if(data!=null && data.size()>0) {
				ItemFindHome itemFindHome=(ItemFindHome)data.get(0);
				countyAddress=itemFindHome.getmDC_Address();
				sunriverAddress=itemFindHome.getmSRAddress();
			}
			manageGetMyHomeAddress(countyAddress, sunriverAddress);
		}
	}
	private void manageGetMyHomeAddress(String countyAddress, String sunriverAddress) {
		reShowResortSearch=false;
		String alertMsg=null;
		if(countyAddress!=null && sunriverAddress!=null) {
			Geocoder geocoder = new Geocoder(this);  
			List<Address> addresses=null;
			try {
				addresses = geocoder.getFromLocationName(countyAddress, 1);
			} catch (Exception e) {
				alertMsg="Failed trying to find address for "+nameFinding.replace('~', ' ')+". Msg: " + e.getMessage();
			}
			if(addresses!=null&&addresses.size() > 0) {
			    double latitude= addresses.get(0).getLatitude();
			    double longitude= addresses.get(0).getLongitude();
				Intent intent=new Intent(MainActivity.this,Maps.class)
				.putExtra("GoToLocationLatitude", latitude)
				.putExtra("GoToLocationLongitude", longitude)
				.putExtra("HeresYourIcon", R.drawable.route_destination)
				.putExtra("GoToLocationTitle", sunriverAddress.replace('~', ' '))
				.putExtra("GoToLocationSnippet", countyAddress)
				.putExtra("GoToLocationURL", "");
				MainActivity.this.startActivity(intent);				    
			} else {
				alertMsg="Unable to find address for "+nameFinding.replace('~', ' ')+".";
			}
		} else {
			alertMsg="The resort name " + nameFinding.replace('~', ' ') + " wasn't found in our database.  Please try again.";
			reShowResortSearch=true;
		}
		if(alertMsg!=null) {
			AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
			builder.setMessage(alertMsg)
		       .setTitle("Find My Home")
		   
		       .setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface di, int something) {
				}
			});
			if(reShowResortSearch) {
				builder.setPositiveButton(R.string.btn_tryagain, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface di, int something) {
					    android.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
					    android.app.Fragment prev = getFragmentManager().findFragmentByTag("findhome");
					    if (prev != null) {
					        ft.remove(prev);
					    }
					    ft.addToBackStack(null);
					    FindHomeDialog findHomeDialog=FindHomeDialog.newInstance(MainActivity.this);
						findHomeDialog.show(ft,"findhome");
					}
				});
			}
			AlertDialog dialog=builder.create();
			dialog.show();
		}

	}
	@Override
	public boolean doYouDoFavorites() {
		return true;
	}

	@Override
	public FavoriteItemType whatsYourFavoriteItemType() {
		return DbAdapter.FavoriteItemType.Unknown;
	}
}
