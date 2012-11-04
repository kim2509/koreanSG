package com.korea.hanintown;

import android.os.Bundle;
import android.os.Handler;
import android.app.PendingIntent;
import android.content.Intent;

public class IntroActivity extends DYActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		try
		{
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_intro);

			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					final Intent mainIntent = new Intent(IntroActivity.this, MainActivity.class);
					IntroActivity.this.startActivity(mainIntent);
					IntroActivity.this.finish();

				}
			}, 2000);

			Intent registrationIntent = new Intent("com.google.android.c2dm.intent.REGISTER");
			// sets the app name in the intent
			registrationIntent.putExtra("app", PendingIntent.getBroadcast(this, 0, new Intent(), 0));
			registrationIntent.putExtra("sender", "761361569216");
			startService(registrationIntent);
		}
		catch( Exception ex )
		{
			writeLog( ex.getMessage() );
		}
	}
}
