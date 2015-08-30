package com.diamondsoftware.android.sunriver_av_3_0;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import com.diamondsoftware.android.sunriver_av_3_0.DbAdapter.FavoriteItemType;

import android.content.ContentValues;
import android.database.Cursor;


public class ItemHospitality extends SunriverDataItem implements IFavoriteItem {
	
	public static final String KEY_HOSPITALITY_ROWID = "_id";
	public static final String KEY_HOSPITALITY_ID = "srHospitalityID";
	public static final String KEY_HOSPITALITY_NAME = "srHospitalityName";
	public static final String KEY_HOSPITALITY_DESCRIPTION = "srHospitalityDescription";
	public static final String KEY_HOSPITALITY_PHONE = "srHospitalityPhone";
	public static final String KEY_HOSPITALITY_URLWEBSITE = "srHospitalityUrlWebsite";
	public static final String KEY_HOSPITALITY_URLIMAGE = "srHospitalityUrlImage";
	public static final String KEY_HOSPITALITY_ADDRESS = "srHospitalityAddress";
	public static final String KEY_HOSPITALITY_LAT = "srHospitalityLat";
	public static final String KEY_HOSPITALITY_LONG = "srHospitalityLong";
	public static final String DATABASE_TABLE_HOSPITALITY = "hospitality";	
	public static final String DATE_LAST_UPDATED="date_last_updated_hospitality";
	private SimpleDateFormat simpleFormatter;
	public static ArrayList<Object> mLastDataFetch;
	
	@Override
	public ArrayList<Object> fetchDataFromDatabase() {
		if(mLastDataFetch==null) {
			mLastDataFetch=super.fetchDataFromDatabase();
		}
		return mLastDataFetch;
	}
	

	public int getSrHospitalityID() {
		return srHospitalityID;
	}

	public void setSrHospitalityID(int srHospitalityID) {
		this.srHospitalityID = srHospitalityID;
	}

	public String getSrHospitalityName() {
		return srHospitalityName;
	}

	public void setSrHospitalityName(String srHospitalityName) {
		this.srHospitalityName = srHospitalityName;
	}

	public String getSrHospitalityDescription() {
		return srHospitalityDescription;
	}

	public void setSrHospitalityDescription(String srHospitalityDescription) {
		this.srHospitalityDescription = srHospitalityDescription;
	}

	public String getSrHospitalityPhone() {
		return srHospitalityPhone;
	}

	public void setSrHospitalityPhone(String srHospitalityPhone) {
		this.srHospitalityPhone = srHospitalityPhone;
	}

	public String getSrHospitalityUrlWebsite() {
		return srHospitalityUrlWebsite;
	}

	public void setSrHospitalityUrlWebsite(String srHospitalityUrlWebsite) {
		this.srHospitalityUrlWebsite = srHospitalityUrlWebsite;
	}

	public String getSrHospitalityUrlImage() {
		return srHospitalityUrlImage;
	}

	public void setSrHospitalityUrlImage(String srHospitalityUrlImage) {
		this.srHospitalityUrlImage = srHospitalityUrlImage;
	}

	public String getSrHospitalityAddress() {
		return srHospitalityAddress;
	}

	public void setSrHospitalityAddress(String srHospitalityAddress) {
		this.srHospitalityAddress = srHospitalityAddress;
	}

	public double getSrHospitalityLat() {
		return srHospitalityLat;
	}

	public void setSrHospitalityLat(double srHospitalityLat) {
		this.srHospitalityLat = srHospitalityLat;
	}

	public double getSrHospitalityLong() {
		return srHospitalityLong;
	}

	public void setSrHospitalityLong(double srHospitalityLong) {
		this.srHospitalityLong = srHospitalityLong;
	}

	private int srHospitalityID;
	private String srHospitalityName;
	private String srHospitalityDescription;
	private String srHospitalityPhone;
	private String srHospitalityUrlWebsite;
	private String srHospitalityUrlImage;
	private String srHospitalityAddress;
	private double srHospitalityLat;
	private double srHospitalityLong;

	public ItemHospitality() {
	}
	
	@Override
	public String toString() {
		return "Name: "
				+ ((srHospitalityName == null || srHospitalityName.trim().length() == 0) ? "None provided"
						: srHospitalityName)
				+ "\n"
				+ "Description: "
				+ ((srHospitalityDescription == null || srHospitalityDescription.trim()
						.length() == 0) ? "None provided" : srHospitalityDescription)
				+ "\n"
				+ "Location :"
				+ ((srHospitalityAddress == null || srHospitalityAddress.trim().length() == 0) ? "None provided"
						: srHospitalityAddress)
				+ "\n"
				+ "Phone: "
				+ ((srHospitalityPhone == null) ? "None provided" : srHospitalityPhone)
				+ "\n"
				+ "Link:  "
				+ ((srHospitalityUrlWebsite == null || srHospitalityUrlWebsite.trim().length() == 0) ? "None provided"
						: srHospitalityUrlWebsite);
	}
	
	
	public ItemHospitality (Cursor cursor) {
		this.setSrHospitalityAddress(cursor.getString(cursor.getColumnIndex(KEY_HOSPITALITY_ADDRESS)));
		this.setSrHospitalityDescription(cursor.getString(cursor.getColumnIndex(KEY_HOSPITALITY_DESCRIPTION)));
		this.setSrHospitalityPhone(cursor.getString(cursor.getColumnIndex(KEY_HOSPITALITY_PHONE)));
		this.setSrHospitalityID(cursor.getInt(cursor.getColumnIndex(KEY_HOSPITALITY_ID)));
		this.setSrHospitalityLat(Double.parseDouble(cursor.getString(cursor.getColumnIndex(KEY_HOSPITALITY_LAT))));
		this.setSrHospitalityUrlWebsite(cursor.getString(cursor.getColumnIndex(KEY_HOSPITALITY_URLWEBSITE)));
		this.setSrHospitalityLong(Double.parseDouble(cursor.getString(cursor.getColumnIndex(KEY_HOSPITALITY_LONG))));
		this.setSrHospitalityName(cursor.getString(cursor.getColumnIndex(KEY_HOSPITALITY_NAME)));
		this.setSrHospitalityUrlImage(cursor.getString(cursor.getColumnIndex(KEY_HOSPITALITY_URLIMAGE)));
	}
	

	@Override
	protected String getTableName() {
		return DATABASE_TABLE_HOSPITALITY;
	}

	@Override
	protected String getDateLastUpdatedKey() {
		return DATE_LAST_UPDATED;
	}
	
	@Override
	public String getFavoritesItemIdentifierColumnName() {
		return KEY_HOSPITALITY_ID;
	}
	
	@Override
	public String[] getFavoritesItemIdentifierValue() {
		return new String[] {
				String.valueOf(getSrHospitalityID())
		};
	}
	@Override
	public FavoriteItemType getFavoriteItemType() {
		return FavoriteItemType.Hospitality;
	}

	
	@Override
	protected void loadWriteItemToDatabaseContentValuesTo(ContentValues values) {
		values.put(KEY_HOSPITALITY_ID, this.getSrHospitalityID());
		values.put(KEY_HOSPITALITY_NAME,this.getSrHospitalityName());
		values.put(KEY_HOSPITALITY_DESCRIPTION,getSrHospitalityDescription());
		values.put(KEY_HOSPITALITY_PHONE, getSrHospitalityPhone() );
		values.put(KEY_HOSPITALITY_URLWEBSITE, getSrHospitalityUrlWebsite());
		values.put(KEY_HOSPITALITY_URLIMAGE, getSrHospitalityUrlImage());
		values.put(KEY_HOSPITALITY_ADDRESS, getSrHospitalityAddress());
		values.put(KEY_HOSPITALITY_LAT, getSrHospitalityLat());
		values.put(KEY_HOSPITALITY_LONG, getSrHospitalityLong());
	}
	
	@Override
	protected String[] getProjectionForFetch() {
		String[] projection = {
				KEY_HOSPITALITY_ROWID,
				KEY_HOSPITALITY_ID,
				KEY_HOSPITALITY_NAME,
				KEY_HOSPITALITY_DESCRIPTION,
				KEY_HOSPITALITY_PHONE,
				KEY_HOSPITALITY_URLWEBSITE,
				KEY_HOSPITALITY_URLIMAGE,
				KEY_HOSPITALITY_ADDRESS,
				KEY_HOSPITALITY_LAT,
				KEY_HOSPITALITY_LONG
		};
		return projection;
	}
	@Override
	protected SunriverDataItem itemFromCursor(Cursor cursor) {
		return new ItemHospitality(cursor);
	}
	@Override
	public boolean isDataExpired() {
		Date lastDateRead=getLastDateRead();
		return (
				GlobalState.TheItemUpdate==null || lastDateRead==null || GlobalState.TheItemUpdate.getUpdateHospitality()==null ||
				GlobalState.TheItemUpdate.getUpdateHospitality().getTime().after(lastDateRead)
		);
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

	@Override
	public SunriverDataItem findItemHavingId(int id) {
		for(Object item: fetchDataFromDatabase()) {
			if(((ItemHospitality)item).getSrHospitalityID()==id) {
				return (SunriverDataItem)item;
			}
		}
		return null;
	}	
	@Override
	public int getOrdinalForFavorites() {return 6;}
	@Override
	public int getId() {return getSrHospitalityID();}
}
