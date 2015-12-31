package com.diamondsoftware.android.sunriver_av_3_0;

public class ItemAlert {
    private long mALID;
    private String mALTitle;
    private String mALDescription;
    private boolean mIsOnAlert;
	public boolean ismIsOnAlert() {
		return mIsOnAlert;
	}
	public void setmIsOnAlert(boolean mIsOnAlert) {
		this.mIsOnAlert = mIsOnAlert;
	}
	public ItemAlert( long alid, String altitle, String alDescription) {
		mALID=alid;
		mALTitle=altitle;
		mALDescription=alDescription;
	}
	public ItemAlert() {
	}
	public void setmALID(int mALID) {
		this.mALID = mALID;
	}
	public void setmALTitle(String mALTitle) {
		this.mALTitle = mALTitle;
	}
	public void setmALDescription(String mALDescription) {
		this.mALDescription = mALDescription;
	}
	public long getmALID() {
		return mALID;
	}
	public String getmALTitle() {
		return mALTitle;
	}
	public String getmALDescription() {
		return mALDescription;
	}
}
