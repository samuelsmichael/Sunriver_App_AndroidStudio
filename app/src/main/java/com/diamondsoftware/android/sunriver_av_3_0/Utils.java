package com.diamondsoftware.android.sunriver_av_3_0;

import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import com.esri.core.geometry.Point;
 
public class Utils {
	/*
	 * Makes the first letter of each word captialized, and all other letters lower-case
	 */
	public static String scrubResortName(String resortName) {
		return resortName.replace("ln", "lane").replace("Ln", "Lane").replace("LN", "LANE");
		
	}
	public static double round(double unrounded, int precision, int roundingMode)
	{
	    BigDecimal bd = new BigDecimal(unrounded);
	    BigDecimal rounded = bd.setScale(precision, roundingMode);
	    return rounded.doubleValue();
	}
	
	public static boolean canHandleIntent(Context context, Intent intent){
	    PackageManager packageManager = context.getPackageManager();
	    List activities = packageManager.queryIntentActivities(
	        intent, 
	        PackageManager.MATCH_DEFAULT_ONLY);
	    return activities.size() > 0;
	}	
	
    public static void CopyStream(InputStream is, OutputStream os)
    {
        final int buffer_size=1024;
        try
        {
            byte[] bytes=new byte[buffer_size];
            for(;;)
            {
              int count=is.read(bytes, 0, buffer_size);
              if(count==-1)
                  break;
              os.write(bytes, 0, count);
            }
        }
        catch(Exception ex){}
    }
    
	public static Point ToGeographic(Point point)
	{
	    double mercatorX_lon = point.getX();
	    double mercatorY_lat = point.getY();		
	    if (Math.abs(mercatorX_lon) < 180 && Math.abs(mercatorY_lat) < 90)
	        return point;

	    if ((Math.abs(mercatorX_lon) > 20037508.3427892) || (Math.abs(mercatorY_lat) > 20037508.3427892))
	        return point;

	    double x = mercatorX_lon;
	    double y = mercatorY_lat;
	    double num3 = x / 6378137.0;
	    double num4 = num3 * 57.295779513082323d;
	    double num5 = Math.floor((num4 + 180.0d) / 360.0d);
	    double num6 = num4 - (num5 * 360.0d);
	    double num7 = 1.5707963267948966 - (2.0d * Math.atan(Math.exp((-1.0d * y) / 6378137.0d)));
	    return new Point(num6,num7 * 57.295779513082323d);
	}


	public static  Point ToWebMercator(Point point )
	{
		double mercatorX_lon=point.getX();
		double mercatorY_lat=point.getY();				
	    if ((Math.abs(mercatorX_lon) > 180 || Math.abs(mercatorY_lat) > 90))
	        return point;

	    double num = mercatorX_lon * 0.017453292519943295;
	    double x = 6378137.0 * num;
	    double a = mercatorY_lat * 0.017453292519943295;

	    mercatorX_lon = x;
	    mercatorY_lat = 3189068.5 * Math.log((1.0 + Math.sin(a)) / (1.0 - Math.sin(a)));
	    return new Point(mercatorX_lon,mercatorY_lat);
	}
	public static GregorianCalendar toGregorianCalendarFromYYYYdashMMdashDDTHHcolonMMcolonSS(String s) {
		return new GregorianCalendar(
			  Integer.valueOf(s.substring(0, 3)), 
			  Integer.valueOf(s.substring(5, 6)),
			  Integer.valueOf(s.substring(8,2)),
			  Integer.valueOf(s.substring(11, 2)),
			  Integer.valueOf(s.substring(14, 2)),
			  Integer.valueOf(s.substring(17, 2)));
	}
	public static GregorianCalendar toDateFromYYYYdashMMdashDDTHHcolonMMcolonSS(String s) {
		if (s==null || s.equalsIgnoreCase("null")) {
			s="0001-01-01T00:00:00"; // the system was originally written to return null's like this
		}
		int yyyy=Integer.valueOf(s.substring(0, 4));
		int mm=Float.valueOf(s.substring(5, 7)).intValue();
		int dd=Float.valueOf(s.substring(8,10)).intValue();
		int hh=Float.valueOf(s.substring(11,13)).intValue();
		int min=Float.valueOf(s.substring(14,16)).intValue();
		int sec=Float.valueOf(s.substring(17)).intValue();
		return new GregorianCalendar(
			  yyyy, 
			  mm-1,
			  dd,hh,min,sec);
	}
	public static java.util.Calendar toDateFromMMdashDDdashYYYY(String s) throws ParseException {
		DateFormat df1 = new SimpleDateFormat("MM-dd-yyyy");
		DateFormat df2 = new SimpleDateFormat("MM/dd/yyyy");
		DateFormat df3 = new SimpleDateFormat("MM.dd.yyyy");
		Date parsed=null;
		try {
			parsed = df1.parse(s);
		} catch (Exception e) {
			try {
				parsed=df2.parse(s);
			} catch (Exception e2) {
				parsed=df3.parse(s);
			}
		}
		java.util.Calendar newCalendar = GregorianCalendar.getInstance();
		newCalendar.setTime(parsed);
		return newCalendar;
	}
	public static String objectToString(Object obj) {
		if(obj == null) {
			return "";
		} else {
			return obj.toString();
		}
	}
}