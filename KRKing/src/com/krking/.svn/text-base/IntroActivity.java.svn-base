package com.krking;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;

public class IntroActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_intro, menu);
        return true;
    }
}
