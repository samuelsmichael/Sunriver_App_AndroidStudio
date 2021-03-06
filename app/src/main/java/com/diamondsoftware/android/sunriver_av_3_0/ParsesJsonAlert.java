package com.diamondsoftware.android.sunriver_av_3_0;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

public class ParsesJsonAlert extends ParsesJson {

	public ParsesJsonAlert() {
	}

	@Override
	protected ArrayList<Object> parse(String jsonString) throws Exception {
		ArrayList<Object> items = new ArrayList<Object>();
		JSONArray jsonArray=new JSONArray(jsonString);
		int c=jsonArray.length();
		for(int i=0;i<c;i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			ItemAlert item=new ItemAlert();
			item.setmALDescription(jsonObject.getString("ALDescription"));
			item.setmALID(jsonObject.getInt("ALID"));
			item.setmALTitle(jsonObject.getString("ALTitle"));
			item.setmIsOnAlert(jsonObject.getBoolean("isOnAlert"));
			items.add(item);
		}
		return items;
		
	}

}
