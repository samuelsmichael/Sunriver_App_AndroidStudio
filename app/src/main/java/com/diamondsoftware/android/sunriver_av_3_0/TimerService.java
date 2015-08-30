package com.diamondsoftware.android.sunriver_av_3_0;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import com.google.android.gms.location.Geofence;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

public class TimerService extends Service  implements DataGetter, WaitingForDataAcquiredAsynchronously, DataLoaderClient {
	private Timer mTimer=null;
	private Timer mRefreshTimer=null;
	private SharedPreferences mSharedPreferences=null;
	private Handler mRefreshDataAtNoonHandler=null;
	private long mRefresh;
	int mCount=0;
	Logger mLogger;
	long lNow=0;
	long lNewTime=0;
	private static int lheure=12; //12
	private static long linterval=1000*60*60*12; //=1000*60*60*24;
	

	private void doS() {
		Thread.setDefaultUncaughtExceptionHandler(new CustomExceptionHandlerTimer(
				this));
		new AcquireDataRemotelyAsynchronously("emergency",this,this);
	}
	
	private class MyRunnable implements Runnable {
		public long lNow;
		public long lNewTime;
		public MyRunnable() {
	        // If the "alarm clock" is not set, set it to go off at the next Noon hour.
	        Calendar now=Calendar.getInstance(Locale.getDefault());
	        Calendar newTime=Calendar.getInstance(Locale.getDefault());
	        int hour=now.get(Calendar.HOUR_OF_DAY);
	        int jdhour=lheure; 
	        if(hour<jdhour) {
	        	newTime.set(Calendar.HOUR_OF_DAY, jdhour);
	        	newTime.set(Calendar.MINUTE, 0);
	        } else {
	        	newTime.add(Calendar.DAY_OF_YEAR,1);
	        	newTime.set(Calendar.HOUR_OF_DAY, jdhour);
	        	newTime.set(Calendar.MINUTE, 0);
	        }
	        lNow=now.getTimeInMillis();
	        lNewTime=newTime.getTimeInMillis();
	        if(mRefreshDataAtNoonHandler==null) {
	        	mRefreshDataAtNoonHandler=new Handler();
	        }
	        mRefresh=lNewTime-lNow;
	            /* for testing mRefresh=1000*60*60;*/
	        mRefreshDataAtNoonHandler.postDelayed(this,mRefresh);
		}
		@Override
		public void run() {
			//new Logger(1,"GlobalState",TimerService.this).log("It's noon!", 9);
			if(GlobalState.mSingleton!=null) {
				DataLoader dataLoader=new DataLoader(TimerService.this,GlobalState.mSingleton);
				dataLoader.execute();
				new MyRunnable();  // Start a fresh timer.
			}
        }
	}
	
	
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Thread.setDefaultUncaughtExceptionHandler(new CustomExceptionHandlerTimer(
				this));
		//mLogger=new Logger(1,"GlobalState",this);

//		Logger logger=new Logger(0,"EmergencyTimer",this);
//		logger.log("About ready to start Timer Service",999);

		String defaultValue=getResources().getString(R.string.emergencytimerintervalinseconds);
		mSharedPreferences=getSharedPreferences(getPackageName() + "_preferences", Activity.MODE_PRIVATE);
		long interval=Long.valueOf(defaultValue);
		
		if(lNow==0) {
	        Calendar now=Calendar.getInstance(Locale.getDefault());
	        Calendar newTime=Calendar.getInstance(Locale.getDefault());
	        int hour=now.get(Calendar.HOUR_OF_DAY);
	        int jdhour=lheure; 
	        if(hour<jdhour) {
	        	newTime.set(Calendar.HOUR_OF_DAY, jdhour);
	        	newTime.set(Calendar.MINUTE, 0);
	        } else {
	        	newTime.add(Calendar.DAY_OF_YEAR,1);
	        	newTime.set(Calendar.HOUR_OF_DAY, jdhour);
	        	newTime.set(Calendar.MINUTE, 0);
	        }
	        lNow=now.getTimeInMillis();
	        lNewTime=newTime.getTimeInMillis();
	        if(mRefreshDataAtNoonHandler==null) {
	        	mRefreshDataAtNoonHandler=new Handler();
	        }
	        mRefresh=lNewTime-lNow;
			startTimer2(1000*interval,1000*interval);
			startRefreshTimer(mRefresh,linterval);
		}
//		logger.log("Timer Service started",999);

        mRefreshDataAtNoonHandler=new Handler();

      ///////////////////  new MyRunnable();

		return START_STICKY;
	}		

	@Override
	public void onDestroy() {
    	stopTimer2();		
    	stopRefreshTimer();
    	if(mRefreshDataAtNoonHandler!=null) {
    		mRefreshDataAtNoonHandler.removeCallbacksAndMessages(null);
    	}
    	super.onDestroy();
	}
	
	private void stopTimer2() {
		if (mTimer != null) {
			try {
				mTimer.cancel();
				mTimer.purge();
			} catch (Exception e) {
			}
			mTimer = null;
		}
	}	
	private void stopRefreshTimer() {
		if(mRefreshTimer != null) {
			try {
				mRefreshTimer.cancel();
				mRefreshTimer.purge();
			} catch (Exception e) {}
			mRefreshTimer=null;
		}
	}
	private void startTimer2(long trigger, long interval) {
		getTimer().schedule(new TimerTask() {
			public void run() {
				try {
					doS();

				} catch (Exception ee) {
					
				}
			}
		}, trigger, interval);
	}
	private void startRefreshTimer(long trigger, long interval) {
		getRefreshTimer().schedule(new TimerTask() {
			public void run() {
				//mLogger.log("It's noon!", 9);
				if(GlobalState.mSingleton!=null) {
					DataLoader dataLoader=new DataLoader(TimerService.this,GlobalState.mSingleton);
					dataLoader.execute();
				}
			}
		},trigger,interval);
	}

	private Timer getTimer() {
		if (mTimer == null) {
			mTimer = new Timer("SunriverEmergencyAlert");
		}
		return mTimer;
	}
    private Timer getRefreshTimer() {
    	if(mRefreshTimer==null) {
    		mRefreshTimer=new Timer("RefreshTimer");
    	}
    	return mRefreshTimer;
    }
	public TimerService() {		
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void gotMyData(String name, ArrayList<Object> data) {
		if(name.equalsIgnoreCase("emergency")) {
			String comma="";
			StringBuilder sb=new StringBuilder();
			ArrayList<Object> liveEmergencies=new ArrayList<Object>();
			if(data!=null && data.size()>0) {
				for(Object itemEmergency: data) {
					if(((ItemEmergency)itemEmergency).isEmergencyAlert()) {
						liveEmergencies.add(itemEmergency);
						// we're going to keep track of emergencies so that if it changes (in TimerService), we'll know whether or not to do a notification
						sb.append(comma+((ItemEmergency)itemEmergency).getEmergencyId());
						comma=",";
					}
				}
				if(liveEmergencies.size()>0) {
					String emergenciesFromLastFetch=mSharedPreferences.getString("EmergenciesFromLastFetch", "");
					String current=sb.toString();
					if(!emergenciesFromLastFetch.equals(current)) {
						for(Object itemEmergency: liveEmergencies) {
							sendNotification(
									((ItemEmergency)itemEmergency).getEmergencyTitle(),
									((ItemEmergency)itemEmergency).getEmergencyDescription(),
									((ItemEmergency)itemEmergency).getEmergencyId()
									);	
						}
					}
					SharedPreferences.Editor editor = mSharedPreferences.edit();
					editor.putString("EmergenciesFromLastFetch", sb.toString());
					editor.commit();
				}
			}
		}
	}
	
	private void sendNotification(String emergencyTitle, String emergencyDescription, int idOfEmergency) {

        // Create an explicit content Intent that starts the main Activity
        Intent notificationIntent =
                new Intent(this,SplashPage.class); 

        // Construct a task stack
        android.support.v4.app.TaskStackBuilder stackBuilder = android.support.v4.app.TaskStackBuilder.create(this);

        // Adds the main Activity to the task stack as the parent
        stackBuilder.addParentStack(SplashPage.class);

        // Push the content Intent onto the stack
        stackBuilder.addNextIntent(notificationIntent);

        // Get a PendingIntent containing the entire back stack
        PendingIntent notificationPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        // Get a notification builder that's compatible with platform versions >= 4
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        // Set the notification contents
        String buildingContentText="Click notification to see more details";
        if(emergencyTitle!=null && emergencyTitle!="") {
        	buildingContentText+=" for";
        }
        
        builder.setSmallIcon(R.drawable.ic_launcher)
               .setContentTitle(this.getString(R.string.emergencynotificationtitle))
               .setContentText(buildingContentText)
               .setContentIntent(notificationPendingIntent)
               .setAutoCancel(true)
               .setTicker(emergencyTitle)
               .setPriority(NotificationCompat.PRIORITY_MAX)
               .setOnlyAlertOnce(true)
               .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
               .setVibrate(new long[] {0, 1000,500,1000,500,1000,500,1000,500,1000,500,1000,500,1000});
        if(emergencyDescription!=null && emergencyDescription!="") {
        	builder.setSubText(emergencyDescription);
        }


        // Get an instance of the Notification manager
        NotificationManager mNotificationManager =
            (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

       	mNotificationManager.notify(idOfEmergency, builder.build());
    }

	
	

	@Override
	public ArrayList<Object> getRemoteData(String name) {
		ArrayList<Object> data=null;
		if(name.equalsIgnoreCase("emergency")) {
			try {
				String defaultValue=getResources().getString(R.string.urlemergencyjson);
				String uri=GlobalState.sharedPreferences.getString("urlemergencyjson", defaultValue);
				
				data = new JsonReaderFromRemotelyAcquiredJson(
					new ParsesJsonEmergency(), 
					uri).parse();
				return data;
			} catch (Exception e) {
				int bkhere=3;
				int bkthere=bkhere;
			} finally {
			}
		} else {
		}
		return data;
	}
	@Override
	public void decrementMCountItemsLeft() {
	}

	@Override
	public void anAsynchronousActionCompleted(String xname) {
	}

	@Override
	public void amGettingRemoteData(String name) {
	}


	@Override
	public void incrementMCountItemsLeft(String name) {
	}

	@Override
	public void gotMyDataFromDataLoader(String name, ArrayList<Object> data) {
	}

}
