package com.diamondsoftware.android.sunriver_av_3_0;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Hashtable;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;

/**
 * Provides the base functions for Data Items (e.g. ItemActivity, ItemDidYouKnow, and so forth) to
 * interface with the database.  This includes reading and writing to the database, as well as 
 * ascertaining whether or not data is stale, and has to be fetched from the Internet.
 * @author Diamond
 *
 */
public abstract class SunriverDataItem implements Cacheable {

	public SunriverDataItem() {

	}
	protected abstract String getTableName();
	protected abstract String getDateLastUpdatedKey();
	protected abstract void loadWriteItemToDatabaseContentValuesTo(ContentValues values);
	protected abstract String[] getProjectionForFetch();
	protected abstract SunriverDataItem itemFromCursor(Cursor cursor);
	protected abstract String getColumnNameForWhereClause();
	protected abstract String[] getColumnValuesForWhereClause();
	protected abstract String getGroupBy();
	protected abstract String getOrderBy();
	
	@Override
	public void forceNewFetch() {
		SharedPreferences sharedPreferences=GlobalState.sharedPreferences;
		Editor edit=sharedPreferences.edit();
		edit.remove(getDateLastUpdatedKey());
		edit.commit();
	}
	
	@Override
	public void clearTable() {
		try {
			String sql="DELETE FROM " + getTableName();
			GlobalState.getDbAdapter().exec(sql);
			} catch (Exception e) { // maybe the table doesn't exist yet

		}
	}

	protected boolean tableExists() {
		return GlobalState.getDbAdapter().existsTable(this.getTableName());
	}
	
	@Override
	public void setLastDateReadToNow() {
		SharedPreferences sharedPreferences=GlobalState.sharedPreferences;
		Editor edit=sharedPreferences.edit();
		edit.putString(getDateLastUpdatedKey(), DbAdapter.mDateFormat.format(new GregorianCalendar().getTime()));
		edit.commit();
	}
	
	@Override
	public long writeItemToDatabase() {
		ContentValues values=new ContentValues();
		loadWriteItemToDatabaseContentValuesTo(values);
		return GlobalState.getDbAdapter().insert(getTableName(), values);
	}

	@Override
	public Date getLastDateRead() {
		SharedPreferences sharedPreferences=GlobalState.sharedPreferences;
		String date= sharedPreferences.getString(getDateLastUpdatedKey(), null);
		if(date==null) {
			return null;
		} else {
			try {
				Date theDate=DbAdapter.mDateFormat.parse(date);
				return theDate;
			} catch (Exception e) {
				return null;
			}
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.diamondsoftware.android.sunriver_av_3_0.Cacheable#fetchDataFromDatabase()
	 * 
	 * With services, we have two levels: the summary (first page) and the detail (chosen category).
	 * We use the Where clause in the detail page in order to only fetch items of the chosen category.
	 * We use the Group By clause in the summary page so that only 1 record of each category is chosen. 
	 */
	
	@Override
	public ArrayList<Object> fetchDataFromDatabase() {
		ArrayList<Object> array= new ArrayList<Object>();
		String[] projection=getProjectionForFetch();
		String columnNameForWhereClause=getColumnNameForWhereClause();
		String[] columnValuesForWhereClause=getColumnValuesForWhereClause();
		Cursor cursor=GlobalState.getDbAdapter().get(getTableName(), projection, 
				columnNameForWhereClause, columnValuesForWhereClause, getGroupBy(), getOrderBy());
		if(cursor.moveToFirst()) {
			do  {
				SunriverDataItem item=itemFromCursor(cursor);
				if(item!=null) {
					array.add(this.itemFromCursor(cursor));
				}
			} while(cursor.moveToNext());
		}
		cursor.close();
		return array;
	}

	/******   IFavoriteItem implementation   ******/
	public boolean getIsFavorite() {
		return GlobalState.getDbAdapter().getIsFavorite(
				((IFavoriteItem)this).getFavoriteItemType(), 
				Integer.valueOf(((IFavoriteItem)this).getFavoritesItemIdentifierValue()[0]));
	}

	public void putIsFavorite(boolean isFavorite) {
		try { // in case this somehow gets called by a non IFavoriteItem implementer
			if(isFavorite) {
				if(!getIsFavorite()) {
					ContentValues values=new ContentValues();
					values.put(DbAdapter.KEY_FAVORITES_ITEM_ID, ((IFavoriteItem)this).getFavoritesItemIdentifierValue()[0]);
					values.put(DbAdapter.KEY_FAVORITES_ITEM_TYPE,String.valueOf(((IFavoriteItem)this).getFavoriteItemType().ordinal()));
		
					GlobalState.getDbAdapter().insert(
								DbAdapter.DATABASE_TABLE_FAVORITES,
						         values);
				}
			} else {
				if(getIsFavorite()) {
					String whereClause=DbAdapter.KEY_FAVORITES_ITEM_ID+"=? AND "+DbAdapter.KEY_FAVORITES_ITEM_TYPE+"=?";
					GlobalState.getDbAdapter().delete(DbAdapter.DATABASE_TABLE_FAVORITES, 
							whereClause, 
							new String[] {
								((IFavoriteItem)this).getFavoritesItemIdentifierValue()[0],
								String.valueOf(((IFavoriteItem)this).getFavoriteItemType().ordinal())
						}
					);
				}
			}
		} catch (Exception e) {
			StackTraceElement[] stea=e.getStackTrace();
			e=e;
			
		}
	}
	
	public static void flushDataArrayToDatabase(ArrayList<Object> data) {
		if(data.size()>0) { // write data to database
			if(data.get(0) instanceof Hashtable) { // for Sunriver locations
				for (Object objht: data) {
					Hashtable ht=(Hashtable)objht;
					for (Object objArray : ht.values()) {
						ArrayList<Object> aroo = (ArrayList<Object>)objArray;
						for (Object theElement :aroo) {
							((ItemLocation)theElement).writeItemToDatabase();
						}
					}
				}
			} else {
				if(data.get(0) instanceof Cacheable) { // for SunriverDataItems
					((Cacheable)data.get(0)).clearTable();
					for(Object obj: data) {
						((Cacheable)obj).writeItemToDatabase();
					}
				}
			}
		}

	}
}
