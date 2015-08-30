package com.diamondsoftware.android.sunriver_av_3_0;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class PopupCalendarDetail extends Popups2 {
	private ItemCalendar mItemCalendar;
	
	private ImageView mImageUrl;
	private TextView mName;
	private TextView mAddress;
	private TextView mDateAndDuration;
	private TextView mMoreInfo;
	private Button mShare;
	private Button mMap;
	private TextView mDescription;
	private TextView mWebUrl;

	public double mLatitude;
	public double mLongitude;
	public String mCalendarName;
	public String mWebAddress;

	private ImageLoader mImageLoader=null;	
	private SimpleDateFormat mSimpleFormatter;
	private boolean mIsFavorite;
	private ImageView mFavorite;

	

	public PopupCalendarDetail(Activity activity, ItemCalendar itemCalendar) {
		super(activity);
		mItemCalendar=itemCalendar;
        mSimpleFormatter = new SimpleDateFormat("EEEE, MMM d", Locale.getDefault());

	}
	private void flipFavorite() {
		if(mIsFavorite) {
			mIsFavorite=false;
			mFavorite.setImageResource(R.drawable.favoriteoff);
			mItemCalendar.putIsFavorite(false);
			if(!mDbAdapter.areThereAnyFavoritesForThisCategory(DbAdapter.FavoriteItemType.Calendar)) {
				AbstractActivityForListViews.mSingleton.setImViewingFavorites(false);
			}
			AbstractActivityForListViews.mSingleton.rebuildListBasedOnFavoritesSetting();
			
		} else {
			mIsFavorite=true;
			mFavorite.setImageResource(R.drawable.favoriteon);
			mItemCalendar.putIsFavorite(true);			
		}
		mActivity.invalidateOptionsMenu();
	}
	@Override
	protected void childPerformCloseActions() {
	}

	private String formatDateAndDuration() {
        mSimpleFormatter.setCalendar(mItemCalendar.getSrCalDate());
        Date dt=mItemCalendar.getSrCalDate().getTime();
        String str=dt.toLocaleString();
        String retValue=mSimpleFormatter.format(dt);
        retValue+=mItemCalendar.getSrCalDuration()==null?"":"   "+mItemCalendar.getSrCalDuration();
        return retValue;
	}
	
	@Override
	protected void loadView(ViewGroup popup) {
		mLatitude=mItemCalendar.getSrCalLat();
		mLongitude=mItemCalendar.getSrCalLong();
		mCalendarName=mItemCalendar.getSrCalName();
		mImageUrl=(ImageView)popup.findViewById(R.id.calendar_popup_image);
		mName=(TextView)popup.findViewById(R.id.calendar_popup_name);
		mName.setText(mItemCalendar.getSrCalName());
		mDateAndDuration=(TextView)popup.findViewById(R.id.calendar_popup_date_and_duration);
		mDateAndDuration.setText(formatDateAndDuration());
		mAddress=(TextView)popup.findViewById(R.id.calendar_popup_address);
		mDescription=(TextView)popup.findViewById(R.id.calendar_popup_description);
		mWebUrl=(TextView)popup.findViewById(R.id.calendar_popup_weburl);
		
		mFavorite=(ImageView)popup.findViewById(R.id.ibtn_calendar_favorite);
		mFavorite.setVisibility(View.VISIBLE);
		if(mItemCalendar.getIsFavorite()) {
			mFavorite.setImageResource(R.drawable.favoriteon);
			mIsFavorite=true;
		} else {
			mFavorite.setImageResource(R.drawable.favoriteoff);
			mIsFavorite=false;
		}

		mFavorite.setOnClickListener(new View.OnClickListener() {				
			@Override
			public void onClick(View v) {
				PopupCalendarDetail.this.flipFavorite();
			}
		});


		
		mShare=(Button)popup.findViewById(R.id.calendar_popup_share_button);		
		mMap=(Button)popup.findViewById(R.id.calendar_popup_map_button);

		mAddress.setText(mItemCalendar.getSrCalAddress());
		mDescription.setText(mItemCalendar.getSrCalDescription());
		/* If they don't have a web site, don't display this part */
		mWebAddress=mItemCalendar.getSrCalLinks();
		if(mWebAddress==null || mWebAddress.trim().length()==0) {
			mWebUrl.setText("");
			mMoreInfo=(TextView)popup.findViewById(R.id.calendar_popup_for_more_information);
			mMoreInfo.setText("");
		}
		
		mWebAddress=mItemCalendar.getSrCalLinks();
		if(mWebAddress!=null && mWebAddress.trim().length()>0) {
			mWebUrl.setOnTouchListener(new OnTouchListener() {
			    @Override
			    public boolean onTouch(View v, MotionEvent event) {			    	
			        if (event.getAction() == MotionEvent.ACTION_UP) {
			        	removeView();
				        Tracker t = ((GlobalState) mActivity.getApplication()).getTracker(
					            GlobalState.TrackerName.APP_TRACKER);
					        // Build and send an Event.
					        t.send(new HitBuilders.EventBuilder()
					            .setCategory("Item Detail Action")
					            .setAction("Visit website")
					            .setLabel(mItemCalendar.getSrCalName())
					            .build());

			        	Intent intent=new Intent(mActivity,Website.class).
			        		putExtra("url",(mWebAddress.indexOf("http")==-1?"http://":"")+mWebAddress);
			        	mActivity.startActivity(intent);
			        }
		        	return true;
			    }
			});
		}

		mShare.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				removeView();
		        Tracker t = ((GlobalState) mActivity.getApplication()).getTracker(
			            GlobalState.TrackerName.APP_TRACKER);
			        // Build and send an Event.
			        t.send(new HitBuilders.EventBuilder()
			            .setCategory("Item Detail Action")
			            .setAction("Share")
			            .setLabel(mItemCalendar.getSrCalName())
			            .build());

				Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
				sharingIntent.setType("text/plain");
				sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, mItemCalendar.getSrCalName());
				sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, mItemCalendar.toString());
				mActivity.startActivity(Intent.createChooser(sharingIntent, "Share via"));
			}
		});
		mMap.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				removeView();
				
				String uriBegin = "geo:"+mLatitude+","+mLongitude;
				String query =  mLatitude+","+mLongitude +"(" + mCalendarName + ")";
				String encodedQuery = Uri.encode( query  );
				String uriString = uriBegin + "?q=" + encodedQuery;
				Uri uri = Uri.parse( uriString );
				Intent intent = new Intent(android.content.Intent.ACTION_VIEW, uri );
				mActivity.startActivity( intent );
		        Tracker t = ((GlobalState) mActivity.getApplication()).getTracker(
			            GlobalState.TrackerName.APP_TRACKER);
			        // Build and send an Event.
			        t.send(new HitBuilders.EventBuilder()
			            .setCategory("Item Detail Action")
			            .setAction("Show location on map")
			            .setLabel(mItemCalendar.getSrCalName())
			            .build());

				
				
			}
		});

		// Is it local, or remote
		String imageString=mItemCalendar.getSrCalUrlImage();
		if(imageString==null || imageString.trim().length()==0) {
			imageString="sunriverlogoopaque";
		}
		// for testing purposes: imageString="http://www.srfeed.com/res/pics/welcome/Welcome.jpg";
		if(imageString.indexOf("/")>=0) {
			mImageLoader=new ImageLoaderRemote(mActivity,true,.40f);
		} else {
			mImageLoader=new ImageLoaderLocal(mActivity,true);
		}
		mImageLoader.displayImage(imageString,mImageUrl);	

	}

	@Override
	protected int getResourceId() {
		return R.layout.calendar_popup2;
	}

	@Override
	protected boolean getDoesPopupNeedToRunInBackground() {
		return false;
	}

	@Override
	protected int getClosePopupId() {
		return R.id.closePopup;
	}

	@Override
	protected String getGoogleAnalyticsAction() {
		return "Events";
	}

	@Override
	protected String getGoogleAnalyticsLabel() {
		return mItemCalendar.getSrCalName();
	}

}
