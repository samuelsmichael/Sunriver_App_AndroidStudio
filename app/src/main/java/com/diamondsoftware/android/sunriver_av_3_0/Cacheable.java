package com.diamondsoftware.android.sunriver_av_3_0;

import java.util.ArrayList;
import java.util.Date;


public interface Cacheable {
	long writeItemToDatabase(); // writes an item to the database, and returns rowId
	void clearTable();
	Date getLastDateRead();
	ArrayList<Object> fetchDataFromDatabase();
	void setLastDateReadToNow();
	boolean isDataExpired();
	void forceNewFetch();
}
