package com.diamondsoftware.android.sunriver_av_3_0;

import java.util.ArrayList;

import com.diamondsoftware.android.sunriver_av_3_0.DbAdapter.FavoriteItemType;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

/*
 * Many activities in Sunriver produce list views.  This is the base abstract class for these items.
 * 
 * Most of these activities need to potentially access data remotely (depending on
 * whether the data is outdated).  The interface WaitingForDataAcquiredAsynchronously provides the means of allowing 
 * this data to be accessed on a separate thread.  This is required because our data is accessed via calls to the web,
 * and such calls must be made on a thread other than the UI thread.  Once the data is acquired, then the AdapterList (mList)
 * is set; and that triggers the building of the actual list items.
 * 
 * 
 */
public abstract class AbstractActivityForListViews extends AbstractActivityForMenu  implements WaitingForDataAcquiredAsynchronously {
	protected ListView mList;
	protected ListViewAdapter mAdapter;
	protected Popups2 mPopup;
	protected ImageView mImageView;
	public static AbstractActivityForListViews mSingleton;
	
	// get the layout id of the associated ListView
	protected abstract int getListViewId();
	// get the layout id of the Activity
	protected abstract int getViewId();
	// get the ListViewAdapter class
	protected abstract ListViewAdapter getListViewAdapter();
	// What to do when they click on a listview item
	protected abstract void childOnItemClick(AdapterView<?> parent, View view,
            int position, long id);
	// Perform any subclass-specific OnCreate functions.  Note: must call super.childOnCreate();
	protected abstract void childOnCreate(Bundle savedInstanceState);
	/*
	 * Get the id of the ImageView where a picture is going to be placed, if any
	 */
	protected abstract int getImageId();
	/*
	 * Get the URL that fetches the picture that's going to be placed, if any
	 */
	protected abstract String getImageURL();
	// After we've fetched the data, give subclasses the ability to massage it.  E.G. - home page needs to insert Alert programatically
	protected abstract void hookDoSomethingWithTheDataIfYouWant(ArrayList<Object> data);
	
	public AbstractActivityForListViews() {
	}

	
	// generate a random URL for pictures. The set of items to pick from is the ArrayList SplashPage.TheItemsDidYouKnow
	public String getRandomImageURL() {
		try {
			double randomNumber=Math.random();
			double factor=1d/(double)((GlobalState)getApplicationContext()).TheItemsDidYouKnow.size();
			int element=(int)(randomNumber/factor);
			return ((ItemDidYouKnow)((GlobalState)getApplicationContext()).TheItemsDidYouKnow.get(element)).getDidYouKnowURL();
		} catch (Exception e) {
			return "http://www.srfeed.com/res/pics/welcome/Welcome.jpg";
		}
	}
	
	public ImageView getImageView() {
		return mImageView;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mSingleton=this;
		// check to see that we've gone Internet Connectivity
	    ConnectivityManager connectivityManager 
	          = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    ((GlobalState)getApplicationContext()).gotInternet= activeNetworkInfo != null && activeNetworkInfo.isConnected();
		
		setContentView(getViewId());
		mImageView=(ImageView)this.findViewById(getImageId());
		childOnCreate(savedInstanceState);
        mList=(ListView)findViewById(getListViewId());
        
        /*
         * The ListViewAdaper may have to fetch data asynchronously.  Creating it starts 
         * the fetch, and then gotMyData (below) - which is called asynchronously -- assigns
         * the ListView to the adapter.
         */
        
        // Click event for single list row
        mList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
            	if(AbstractActivityForListViews.this instanceof AbstractActivityForListViewsScrollingImage) {
            		childOnItemClick(parent,view,position-1,id);         		
            	} else {
            		childOnItemClick(parent,view,position,id);
            	}
           	}
        });

        mAdapter=getListViewAdapter();
        if(mAdapter instanceof ListViewAdapterLocalData ) {
        	((ListViewAdapterLocalData)mAdapter).performDataFetch();
        }
 
	}
	@Override
	public void rebuildListBasedOnFavoritesSetting() {
		if(getImViewingFavorites()) {
			filterDataForFavorites();
		} else {
			mAdapter.causeDataToBeRebuilt();
		}
		mAdapter.notifyDataSetChanged();
	}
	private void filterDataForFavorites() {
		if(this.doYouDoFavorites()) {
			ArrayList<Object> newData=new ArrayList<Object>();
			ArrayList<Integer> favorites=new ArrayList<Integer>();
			Cursor cursor=GlobalState.getDbAdapter().getItemsInFavoritesForThisCategory(whatsYourFavoriteItemType());
			while(cursor.moveToNext()) {
				favorites.add(cursor.getInt(cursor.getColumnIndex(DbAdapter.KEY_FAVORITES_ITEM_ID)));
			}
			cursor.close();
			ArrayList<Object> items=mAdapter.getData();
			for(Object item :items) {
				FavoriteItemType itemsFavoriteItemType=((IFavoriteItem)item).getFavoriteItemType();
				FavoriteItemType viewsFavoriteItemType=whatsYourFavoriteItemType();
				int thisItemsId=Integer.valueOf(((IFavoriteItem)item).getFavoritesItemIdentifierValue()[0]);
				if(itemsFavoriteItemType==viewsFavoriteItemType &&  favorites.contains(thisItemsId)) {
					newData.add(item);
				}
			}
			mAdapter.mData=newData;
		}
	}
	/*
	 * gotMyData() is called when the ListViewAdapter has finished fetching data.  If we're dealing with a ListViewAdapterLocalData,
	 * then the data is fetched as part of the constructor.  With a ListViewAdapterRemoteData (one that fetches its data
	 * off of a webservice (like Calendar, Maps, and Activities), then the data fetch is done asynchronously (per Android's
	 * requirement), and then this method is called (using an AsyncTask, so that it will be called on the UI thread).
	 */
	@Override
	public void gotMyData(String name, ArrayList<Object> data) {
		hookDoSomethingWithTheDataIfYouWant(data);
		if(getImViewingFavorites()) {
			if(((GlobalState)getApplication()).getDbAdapter().areThereAnyFavoritesForThisCategory(whatsYourFavoriteItemType())) {
				filterDataForFavorites();
			} else {
				this.setImViewingFavorites(false);
			}
		}
        mList.setAdapter(mAdapter);
	}
	
	@Override
	protected void onDestroy() {
		if(mPopup!=null) {
			mPopup.removeView();
		}
		super.onDestroy();
	}
	


}
