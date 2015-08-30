package com.diamondsoftware.android.sunriver_av_3_0;

import java.util.Date;

import android.content.ContentValues;
import android.database.Cursor;

public class ItemWelcome extends SunriverDataItem {
	private int welcomeID;
	private String welcomeURL;
	private boolean isInRotation;

	/**
	 * @return the isInRotation
	 */
	public boolean isInRotation() {
		return isInRotation;
	}
	/**
	 * @param isInRotation the isInRotation to set
	 */
	public void setInRotation(boolean isInRotation) {
		this.isInRotation = isInRotation;
	}
	public static final String DATABASE_TABLE_WELCOME = "welcome";
	public static final String DATE_LAST_UPDATED = "date_last_updated_welcome";
	public static final String KEY_WELCOME_ROWID = "_id";
	public static final String KEY_WELCOME_WELCOMEID = "welcomeID";
	public static final String KEY_WELCOME_WELCOMEURL = "welcomeURL";
	public static final String KEY_WELCOME_ISINROTATION = "isInRotation";
	
	
	public ItemWelcome() {
	}
	protected ItemWelcome(Cursor cursor) {
		this.setWelcomeID(cursor.getInt(cursor.getColumnIndex(KEY_WELCOME_WELCOMEID)));
		setWelcomeURL(cursor.getString(cursor.getColumnIndex(KEY_WELCOME_WELCOMEURL)));
		this.setInRotation(cursor.getInt(cursor.getColumnIndex(KEY_WELCOME_ISINROTATION))!=0);
	}

	public int getWelcomeID() {
		return welcomeID;
	}
	public void setWelcomeID(int welcomeID) {
		this.welcomeID = welcomeID;
	}
	public String getWelcomeURL() {
		return welcomeURL;
	}
	public void setWelcomeURL(String welcomeURL) {
		this.welcomeURL = welcomeURL;
	}

	@Override
	public boolean isDataExpired() {
		Date lastTimeWeveFetchedData=getLastDateRead();
		return (
				GlobalState.TheItemUpdate==null || lastTimeWeveFetchedData==null || GlobalState.TheItemUpdate.getUpdateWelcome()==null ||
						GlobalState.TheItemUpdate.getUpdateWelcome().getTime().after(lastTimeWeveFetchedData)
		);
	}

	@Override
	protected String getTableName() {
		return DATABASE_TABLE_WELCOME;
	}

	@Override
	protected String getDateLastUpdatedKey() {
		return DATE_LAST_UPDATED;
	}

	@Override
	protected void loadWriteItemToDatabaseContentValuesTo(ContentValues values) {
		values.put(KEY_WELCOME_WELCOMEID, this.getWelcomeID());
		values.put(KEY_WELCOME_WELCOMEURL, getWelcomeURL());
		values.put(KEY_WELCOME_ISINROTATION, isInRotation());
	}

	@Override
	protected String[] getProjectionForFetch() {
		String[] projection = {KEY_WELCOME_WELCOMEID, KEY_WELCOME_WELCOMEURL, KEY_WELCOME_ISINROTATION };
		return projection;
	}

	@Override
	protected SunriverDataItem itemFromCursor(Cursor cursor) {
		return new ItemWelcome(cursor);
	}
	@Override
	protected String getColumnNameForWhereClause() {
		return null;
	}
	@Override
	protected String[] getColumnValuesForWhereClause() {
		return null;
	}
	@Override
	protected String getGroupBy() {
		return null;
	}
	@Override
	protected String getOrderBy() {
		return null;
	}
	
}
