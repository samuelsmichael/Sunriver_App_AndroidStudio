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

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.xmlpull.v1.XmlPullParserException;

/**
 * Gets an input stream from the Internet, hands it to its associated ParsesXML, in order to
 * obtain an ArrayList of objects.  
 * 
 * The data is then stored into the database.
 * 
 * If the objects thus obtained implement the interface Cacheable (which
 * all SunriverDataItem objects do), then the data is written using the methods
 * implemented by the Cacheable object.
*/
public class XMLReaderFromRemotelyAcquiredXML extends XMLReader {
	private String mUrl=null;
	private List<NameValuePair> mParameters=null;
	
	public XMLReaderFromRemotelyAcquiredXML(List<NameValuePair> parameters, ParsesXML parsesXML, String url) {
		this(parsesXML,url);
		mParameters=parameters;
	}

	public XMLReaderFromRemotelyAcquiredXML(ParsesXML parsesXML, String url) {
		super(parsesXML);
		mUrl=url;
	}

	@Override
	public ArrayList<Object> parse() throws  Exception {
		ArrayList<Object> data=parse(getInputStream());
		SunriverDataItem.flushDataArrayToDatabase(data);
		return data;
	}
	
	private InputStream getInputStream() throws IOException{
		String dataString=new RemoteDataReader().getRemoteData(mUrl, mParameters);
		return new ByteArrayInputStream(dataString.getBytes("UTF-8"));
	}	
}
