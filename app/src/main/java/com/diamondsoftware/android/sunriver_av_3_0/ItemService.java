package com.diamondsoftware.android.sunriver_av_3_0;

import java.util.ArrayList;
import java.util.Date;

import android.content.ContentValues;
import android.database.Cursor;
import android.text.TextUtils;

import com.diamondsoftware.android.sunriver_av_3_0.DbAdapter.FavoriteItemType;

public class ItemService extends SunriverDataItem implements IFavoriteItem {
	public static final String DATABASE_TABLE_SERVICE = "service";
	public static final String DATE_LAST_UPDATED = "date_last_updated_service";
	public static final String KEY_SERVICE_ROWID = "_id";
	public static final String KEY_SERVICE_SERVICEID = "serviceID";
	public static final String KEY_SERVICE_SERVICENAME = "serviceName";
	public static final String KEY_SERVICE_SERVICEWEBURL = "serviceWebURL";
	public static final String KEY_SERVICE_SERVICEPICTUREURL = "servicePictureURL";
	public static final String KEY_SERVICE_SERVICEICONURL = "serviceIconURL";
	public static final String KEY_SERVICE_SERVICEDESCRIPTION = "serviceDescription";
	public static final String KEY_SERVICE_SERVICEPHONE = "servicePhone";
	public static final String KEY_SERVICE_SERVICEADDRESS = "serviceAddress";
	public static final String KEY_SERVICE_SERVICELAT = "serviceLat";
	public static final String KEY_SERVICE_SERVICELONG = "serviceLong";
	public static final String KEY_SERVICE_SERVICECATEGORYNAME = "serviceCatName";
	public static final String KEY_SERVICE_SERVICECATEGORYNUM = "serviceCatNum";
	public static final String KEY_SERVICE_SERVICECATEGORYICONURL = "serviceCatIconURL";
	public static final String KEY_SERVICE_SORTORDER = "sortOrder";
	public static String mColumnNameForWhereClause=null;
	public static String[] mColumnValuesForWhereClause=null;
	public static String mGroupBy=null;
	public static ArrayList<Object> mLastDataFetchSummary2;
	public static ArrayList<Object> mLastDataFetchDetail2;
	
	@Override
	public ArrayList<Object> fetchDataFromDatabase() {
		if(mGroupBy==null) {
			if(mLastDataFetchDetail2==null) {
				mLastDataFetchDetail2=super.fetchDataFromDatabase();
			}
			return filterIfNecessary(mLastDataFetchDetail2);
		} else {
			if(mLastDataFetchSummary2==null) {
				mLastDataFetchSummary2=super.fetchDataFromDatabase();
			}
			return mLastDataFetchSummary2;
		}
	}
	
	/*
	 * We are caching the data fetch; and here, we are filteringing
	 * it by sub-type
	 */
	public ArrayList<Object> filterIfNecessary(ArrayList<Object> fullSuite) {
		if(mColumnValuesForWhereClause==null) {
			return fullSuite;
		} else {
			ArrayList<Object> retValue=new ArrayList<Object>();
			for(Object itemService :fullSuite) {
				if( ((ItemService)itemService).getServiceCategoryName().equals(mColumnValuesForWhereClause[0])  ) {
					retValue.add(itemService);
				}
			}
			return retValue;
		}
	}
	public String getFavoritesItemIdentifierColumnName() {
		return KEY_SERVICE_SERVICEID;
	}
	
	
	public String[] getFavoritesItemIdentifierValue() {
		return new String[] {
				String.valueOf(getServiceID())
		};
	}
	
	public FavoriteItemType getFavoriteItemType() {
		return FavoriteItemType.Service;
	}
	
	public int getServiceID() {
		return serviceID;
	}

	public void setServiceID(int serviceID) {
		this.serviceID = serviceID;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getServiceWebURL() {
		return serviceWebURL;
	}

	public void setServiceWebURL(String serviceWebURl) {
		this.serviceWebURL = serviceWebURl;
	}

	public String getServicePictureURL() {
		return servicePictureURL;
	}

	public void setServicePictureURL(String servicePictureURL) {
		this.servicePictureURL = servicePictureURL;
	}

	public String getServiceIconURL() {
		return serviceIconURL;
	}

	public void setServiceIconURL(String serviceIconURL) {
		this.serviceIconURL = serviceIconURL;
	}

	public String getServiceDescription() {
		return serviceDescription;
	}

	public void setServiceDescription(String serviceDescription) {
		this.serviceDescription = serviceDescription;
	}

	public String getServicePhone() {
		return servicePhone;
	}

	public void setServicePhone(String servicePhone) {
		this.servicePhone = servicePhone;
	}

	public String getServiceAddress() {
		return serviceAddress;
	}

	public void setServiceAddress(String serviceAddress) {
		this.serviceAddress = serviceAddress;
	}

	public double getServiceLat() {
		return serviceLat;
	}

	public void setServiceLat(double serviceLat) {
		this.serviceLat = serviceLat;
	}

	public double getServiceLong() {
		return serviceLong;
	}

	public void setServiceLong(double serviceLong) {
		this.serviceLong = serviceLong;
	}

	int serviceID;
    String serviceName;
    String serviceWebURL;
    String servicePictureURL;
    String serviceIconURL;
    String serviceDescription;
    String servicePhone;
    String serviceAddress;
    double serviceLat;
    double serviceLong;	
	String serviceCategoryName;
    int serviceCategory;
    int sortOrder;
    
    public int getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(int sortOrder) {
		this.sortOrder = sortOrder;
	}

	public String getServiceCategoryIconURL() {
		return serviceCategoryIconURL;
	}

	public void setServiceCategoryIconURL(String serviceCategoryIconURL) {
		this.serviceCategoryIconURL = serviceCategoryIconURL;
	}

	String serviceCategoryIconURL;
    
    public String getServiceCategoryName() {
		return serviceCategoryName;
	}

	public void setServiceCategoryName(String serviceCategoryName) {
		this.serviceCategoryName = serviceCategoryName;
	}

	public int getServiceCategory() {
		return serviceCategory;
	}

	public void setServiceCategory(int serviceCategory) {
		this.serviceCategory = serviceCategory;
	}
    
	public ItemService() {
	}
	protected ItemService(Cursor cursor) throws Exception {
		if(mGroupBy==null) {
			this.setServiceID(cursor.getInt(cursor.getColumnIndex(KEY_SERVICE_SERVICEID)));
			setServiceName(cursor.getString(cursor.getColumnIndex(KEY_SERVICE_SERVICENAME)));
			setServiceWebURL(cursor.getString(cursor.getColumnIndex(KEY_SERVICE_SERVICEWEBURL)));
			setServicePictureURL(cursor.getString(cursor.getColumnIndex(KEY_SERVICE_SERVICEPICTUREURL)));
			setServiceIconURL(cursor.getString(cursor.getColumnIndex(KEY_SERVICE_SERVICEICONURL)));
			setServiceDescription(cursor.getString(cursor.getColumnIndex(KEY_SERVICE_SERVICEDESCRIPTION)));
			setServicePhone(cursor.getString(cursor.getColumnIndex(KEY_SERVICE_SERVICEPHONE)));
			setServiceAddress(cursor.getString(cursor.getColumnIndex(KEY_SERVICE_SERVICEADDRESS)));
			setServiceLat(Double.parseDouble(cursor.getString(cursor.getColumnIndex(KEY_SERVICE_SERVICELAT))));
			setServiceLong(Double.parseDouble(cursor.getString(cursor.getColumnIndex(KEY_SERVICE_SERVICELONG))));
			setServiceCategoryName(cursor.getString(cursor.getColumnIndex(KEY_SERVICE_SERVICECATEGORYNAME)));
			setServiceCategoryIconURL(cursor.getString(cursor.getColumnIndex(KEY_SERVICE_SERVICECATEGORYICONURL)));
			setServiceCategory(cursor.getInt(cursor.getColumnIndex(KEY_SERVICE_SERVICECATEGORYNUM)));
		} else {
			int categoryNum=cursor.getInt(cursor.getColumnIndex(KEY_SERVICE_SERVICECATEGORYNUM));
			setServiceCategory(categoryNum);
			this.setServiceCategoryIconURL(getServiceCatIconURLWhoseCategoryNumEqual(categoryNum));
			this.setServiceCategoryName(getServiceCatNameWhoseCategoryNumEqual(categoryNum));
		}
	}

	private String getServiceCatNameWhoseCategoryNumEqual(int categoryNum) {
		String remGroupBy=mGroupBy;
		mGroupBy=null;
		String retValue="";
		ArrayList<Object> data= fetchDataFromDatabase();
		for(Object itemService: data) {
			if( ((ItemService)itemService).getServiceCategory()==categoryNum &&  !TextUtils.isEmpty( ((ItemService)itemService).getServiceCategoryName())) {
				retValue=((ItemService)itemService).getServiceCategoryName();
				break;
			}
		}
		mGroupBy=remGroupBy;
		return retValue;
	}
	private String getServiceCatIconURLWhoseCategoryNumEqual(int categoryNum) {
		String remGroupBy=mGroupBy;
		mGroupBy=null;
		String retValue="";
		ArrayList<Object> data= fetchDataFromDatabase();
		for(Object itemService: data) {
			if( ((ItemService)itemService).getServiceCategory()==categoryNum &&  !TextUtils.isEmpty( ((ItemService)itemService).getServiceCategoryIconURL())) {
				retValue=((ItemService)itemService).getServiceCategoryIconURL();
				break;
			}
		}
		mGroupBy=remGroupBy;
		return retValue;
	}
	@Override
	public boolean isDataExpired() {
		Date lastTimeWeveFetchedData=getLastDateRead();
		return (
				GlobalState.TheItemUpdate==null || lastTimeWeveFetchedData==null || GlobalState.TheItemUpdate.getUpdateServices()==null ||
						GlobalState.TheItemUpdate.getUpdateServices().getTime().after(lastTimeWeveFetchedData)
		);	
	}

	@Override
	protected String getTableName() {
		return DATABASE_TABLE_SERVICE;
	}

	@Override
	protected String getDateLastUpdatedKey() {
		return DATE_LAST_UPDATED;
	}

	@Override
	protected void loadWriteItemToDatabaseContentValuesTo(ContentValues values) {
			values.put(KEY_SERVICE_SERVICEID, this.getServiceID());
			values.put(KEY_SERVICE_SERVICENAME, getServiceName());
			values.put(KEY_SERVICE_SERVICEWEBURL, getServiceWebURL());
			values.put(KEY_SERVICE_SERVICEPICTUREURL,getServicePictureURL());
			values.put(KEY_SERVICE_SERVICEICONURL, getServiceIconURL());
			values.put(KEY_SERVICE_SERVICEDESCRIPTION, getServiceDescription());
			values.put(KEY_SERVICE_SERVICEPHONE, getServicePhone());
			values.put(KEY_SERVICE_SERVICEADDRESS, getServiceAddress());
			values.put(KEY_SERVICE_SERVICELAT, getServiceLat());
			values.put(KEY_SERVICE_SERVICELONG, getServiceLong());
			values.put(KEY_SERVICE_SERVICECATEGORYNAME, this.getServiceCategoryName());
			values.put(KEY_SERVICE_SERVICECATEGORYICONURL, this.getServiceCategoryIconURL());
			values.put(KEY_SERVICE_SORTORDER, this.getSortOrder());
			values.put(KEY_SERVICE_SERVICECATEGORYNUM, this.getServiceCategory());
	}	

	/*
	 * (non-Javadoc)
	 * @see com.diamondsoftware.android.sunriver_av_3_0.SunriverDataItem#getProjectionForFetch()
	 * 
	 * With services, we have two levels: the summary (first page) and the detail (chosen category)
	 */
	@Override
	protected String[] getProjectionForFetch() {
		String[] projectionDetail={ KEY_SERVICE_SERVICEID, KEY_SERVICE_SERVICENAME,
				KEY_SERVICE_SERVICEWEBURL,KEY_SERVICE_SERVICEPICTUREURL, KEY_SERVICE_SERVICEICONURL,
				KEY_SERVICE_SERVICEDESCRIPTION, KEY_SERVICE_SERVICEPHONE,
				KEY_SERVICE_SERVICEADDRESS, KEY_SERVICE_SERVICELAT, KEY_SERVICE_SERVICELONG,
				KEY_SERVICE_SERVICECATEGORYNAME, KEY_SERVICE_SERVICECATEGORYICONURL, KEY_SERVICE_SERVICECATEGORYNUM };
		String[] projectionSummary={ KEY_SERVICE_SERVICECATEGORYNUM };
		if(mGroupBy==null) {
			return projectionDetail;
		} else {
			return projectionSummary;
		}
	}

	@Override
	protected SunriverDataItem itemFromCursor(Cursor cursor) {
		try {
			return new ItemService(cursor);
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	protected String getColumnNameForWhereClause() {
		return mColumnNameForWhereClause;
	}

	@Override
	protected String[] getColumnValuesForWhereClause() {
		return mColumnValuesForWhereClause;
	}

	@Override
	protected String getGroupBy() {
		return mGroupBy;
	}

	@Override
	protected String getOrderBy() {
		return KEY_SERVICE_SORTORDER;
	}
	//Emergency, Medical, Grocery, Gas, Churches, Extras
	public static int deriveSortOrder(String category) {
		if(category.equalsIgnoreCase("emergency")) {
			return 0;
		} else {
			if(category.equalsIgnoreCase("medical")) {
				return 1;
			} else {
				if(category.equalsIgnoreCase("grocery")) {
					return 2;
				} else {
					if(category.equalsIgnoreCase("gas")) {
						return 3;
					} else {
						if(category.equalsIgnoreCase("churches")) {
							return 4;
						} else {
							if(category.equalsIgnoreCase("extras")) {
								return 5;
							} else {
								return 99;
							}
						}
					}
				}
			}
		}
	}


	@Override
	public SunriverDataItem findItemHavingId(int id) {
		for(Object item: fetchDataFromDatabase()) {
			if(((ItemService)item).getServiceID()==id) {
				return (SunriverDataItem)item;
			}
		}
		return null;
	}	
	@Override
	public int getOrdinalForFavorites() {return 5;}
	@Override
	public int getId() {return getServiceID();}
}
