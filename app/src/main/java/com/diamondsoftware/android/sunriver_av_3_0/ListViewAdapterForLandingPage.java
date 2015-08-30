package com.diamondsoftware.android.sunriver_av_3_0;

import java.io.IOException;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class ListViewAdapterForLandingPage extends ListViewAdapterLocalData {
	
	static class LandingPageHolder {
        TextView name; 
        TextView description; 
        ImageView thumb_image; 		
	}
	LandingPageHolder mLandingPageHolder=null;
	public ListViewAdapterForLandingPage(Activity a) {
		super(a,false);
	}
	
	@Override
	public long getItemId(int position) {
		ItemLandingPage item=(ItemLandingPage)getItem(position);
		return item.getId();
	}	

	@Override
	protected void initializeHolder(View view, int position) {
		mLandingPageHolder=new LandingPageHolder();
        mLandingPageHolder.name = (TextView)view.findViewById(R.id.name); 
        mLandingPageHolder.description = (TextView)view.findViewById(R.id.description); 
        mLandingPageHolder.thumb_image=(ImageView)view.findViewById(R.id.list_image);
        view.setTag(mLandingPageHolder);
	}
	@Override
	protected void setViewHolder(View view, int pos) {
		mLandingPageHolder=(LandingPageHolder)view.getTag();
		
	}	
	@Override
	protected void childMapData(int position, View view) throws IOException, XmlPullParserException {
        ItemLandingPage landingPageItem=(ItemLandingPage)getData().get(position);
        
        // Setting all values in listview
        mLandingPageHolder.name.setText(landingPageItem.getName());
        mLandingPageHolder.description.setText(landingPageItem.getDescription());
        mImageLoader.displayImage(((ItemLandingPage)getItem(position)).getIconName(),mLandingPageHolder.thumb_image);
        if(landingPageItem.ismIsStyleMarquee()) { 
        	mLandingPageHolder.description.setSelected(true); // believe it or not, you have to do this in order to make the marquee scroll.
        	mLandingPageHolder.description.setSingleLine(true);
        } else {
        	mLandingPageHolder.description.setSingleLine(false);
        }
		
	}

	@Override
	protected int getLayoutResource() {
		return R.layout.landingpage_listitem;
	}
	
	
	@Override
	protected ArrayList<Object> childGetData() throws Exception {
		return new XMLReaderFromAndroidAssets(mActivity, new ParsesXMLLandingPage(null), "homepage_values.xml").parse();
	}
}
