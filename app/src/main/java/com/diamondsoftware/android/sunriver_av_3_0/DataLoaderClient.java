package com.diamondsoftware.android.sunriver_av_3_0;

import java.util.ArrayList;

public interface DataLoaderClient {
	void decrementMCountItemsLeft();
	void anAsynchronousActionCompleted(String xname);
	void amGettingRemoteData(String name);
	void gotMyDataFromDataLoader(String name, ArrayList<Object> data);
	void incrementMCountItemsLeft(String name);
}
