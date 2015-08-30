package com.diamondsoftware.android.sunriver_av_3_0;

import java.util.ArrayList;

import com.diamondsoftware.android.sunriver_av_3_0.DbAdapter.FavoriteItemType;

import android.view.View;
import android.widget.AdapterView;
/*
 * Provides support for the ListView-type Activity "Retail".  
 * Refer to documentation in the parent class AbstractActivityForListViews for descriptions of the 
 * purposes of the overridden methods.
 */
public class ActivityRetail extends ActivityThatIsASubtypeOfMaps {

	@Override
	protected ListViewAdapter getListViewAdapter() {
		mListViewAdapter=new ListViewAdapterMapSubtype(this,3);
		return mListViewAdapter;
	}
	
	@Override
	protected void childOnItemClick(AdapterView<?> parent, View view,
			int position, long id) {
		ItemLocation itemLocation=(ItemLocation)mListViewAdapter.mData.get(position);
		Popups2 mPopup = new PopupMapLocationRetail(this, itemLocation.toHashMap(),true,itemLocation);
		mPopup.createPopup();
	}
	@Override
	public boolean doYouDoFavorites() {
		return true;
	}

	@Override
	public FavoriteItemType whatsYourFavoriteItemType() {
		return DbAdapter.FavoriteItemType.Retail;
	}

}
