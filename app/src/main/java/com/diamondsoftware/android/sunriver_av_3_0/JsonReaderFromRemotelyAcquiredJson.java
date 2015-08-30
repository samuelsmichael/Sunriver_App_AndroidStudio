package com.diamondsoftware.android.sunriver_av_3_0;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.apache.http.NameValuePair;
import org.xmlpull.v1.XmlPullParserException;

/**
 * Gets an input stream from the Internet, hands it to its associated ParsesJson, in order to
 * obtain an ArrayList of objects.  
 * 
 * The data is then stored into the database.
 * 
 * If the objects thus obtained implement the interface Cacheable (which
 * all SunriverDataItem objects do), then the data is written using the methods
 * implemented by the Cacheable object.
*/
public class JsonReaderFromRemotelyAcquiredJson extends JsonReader {
	private String mUrl=null;
	private List<NameValuePair> mParameters=null;
	
	public JsonReaderFromRemotelyAcquiredJson(List<NameValuePair> parameters, ParsesJson parsesJson, String url) {
		this(parsesJson,url);
		mParameters=parameters;
	}

	public JsonReaderFromRemotelyAcquiredJson(ParsesJson parsesJson, String url) {
		super(parsesJson);
		mUrl=url;
	}

	@Override
	public ArrayList<Object> parse() throws  Exception {
		ArrayList<Object> data=parse(getData());
		SunriverDataItem.flushDataArrayToDatabase(data);
		return data;
	}
	
	private String getData() throws IOException{
		return new RemoteDataReader().getRemoteData(mUrl, mParameters);
	}	
}
