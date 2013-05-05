package com.korea.common;

import com.korea.hanintown.*;
import java.util.List;

import org.json.JSONObject;

import com.korea.hanintown.IntroActivity;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.PowerManager;
import android.util.Log;

public class MyIntentService extends IntentService {

	public MyIntentService() {
		super("MyIntentService");
	}

	private static PowerManager.WakeLock sWakeLock;
	private static final Object LOCK = MyIntentService.class;

	static void runIntentInService(Context context, Intent intent) {
		synchronized(LOCK) {
			if (sWakeLock == null) {
				PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
				sWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "my_wakelock");
			}
		}
		sWakeLock.acquire();

		intent.setClassName(context, MyIntentService.class.getName());
		context.startService(intent);
	}

	@Override
	public final void onHandleIntent(Intent intent) {
		try {
			String action = intent.getAction();
			if (action.equals("com.google.android.c2dm.intent.REGISTRATION")) {
				handleRegistration(intent);
			} else if (action.equals("com.google.android.c2dm.intent.RECEIVE")) {
				handleMessage(intent);
			}
		} finally {
			synchronized(LOCK) {
				sWakeLock.release();
			}
		}
	}

	private void handleRegistration(Intent intent) {
		String registrationId = intent.getStringExtra("registration_id");
		String error = intent.getStringExtra("error");
		String unregistered = intent.getStringExtra("unregistered");       
		// registration succeeded
		if (registrationId != null) {
			// store registration ID on shared preferences
			// notify 3rd-party server about the registered ID
			Log.d("MyIntentService", "registration succeeded: " + registrationId );

			SharedPreferences settings = getSharedPreferences("USER_INFO", 0);
			SharedPreferences.Editor editor = settings.edit();
			editor.putString( "registration_id", registrationId );
			editor.commit();
		}

		// unregistration succeeded
		if (unregistered != null) {
			// get old registration ID from shared preferences
			// notify 3rd-party server about the unregistered ID
			Log.d("MyIntentService", "unregistration succeeded: " + unregistered );
		} 

		// last operation (registration or unregistration) returned an error;
		if (error != null) {
			if ("SERVICE_NOT_AVAILABLE".equals(error)) {
				// optionally retry using exponential back-off
				// (see Advanced Topics)
				Log.d("MyIntentService", "Received error: " + error);

			} else {
				// Unrecoverable error, log it
				Log.d("MyIntentService", "Received error: " + error);
			}
		}
	}

	private void handleMessage(Intent intent) {

		try
		{
			String data = intent.getExtras().getString("message");

			Log.d("MyIntentService", "handleMessage is called.");
			Log.d("MyIntentService", data );

			boolean bShow = true;

			ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
			List<RunningTaskInfo> tasks = activityManager.getRunningTasks(Integer.MAX_VALUE);

			ComponentName componentInfo = tasks.get(0).topActivity;
			if(componentInfo.getPackageName().equals( getPackageName())) bShow = false;

			PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
			
			if ( pm.isScreenOn() == false ) 
			{
				bShow = true;
			}

			if ( bShow )
			{
				NotificationManager notificationManager =
						(NotificationManager) getSystemService(NOTIFICATION_SERVICE);
				Notification notification = new Notification( R.drawable.ic_main, "알림메세지가 도착했습니다!", System.currentTimeMillis());
				Intent myIntent = new Intent( this, MainActivity.class);
				myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
		                Intent.FLAG_ACTIVITY_SINGLE_TOP);
				PendingIntent pendingIntent = PendingIntent.getActivity(getBaseContext(), 0, myIntent, Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
				notification.defaults |= Notification.DEFAULT_SOUND;
				notification.flags |= Notification.FLAG_AUTO_CANCEL;
				notification.setLatestEventInfo( this, "한인타운", data , pendingIntent);
				notificationManager.notify(0, notification);	
			}
		}
		catch( Exception ex )
		{
			Log.e("MyIntentService", ex.getMessage() );
		}
	}
}
