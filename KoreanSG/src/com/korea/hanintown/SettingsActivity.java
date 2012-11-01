package com.korea.hanintown;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class SettingsActivity extends DYActivity implements OnClickListener{

	Button btnLogin = null;
	Button btnLogout = null;
	
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
    	try
    	{
    		super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_settings);
            
            initializeControls();
    	}
    	catch( Exception ex )
    	{
    		writeLog( ex.getMessage() );
    	}
    }

	private void initializeControls() {
		Button btnBack = (Button) findViewById(R.id.btnBack);
		btnBack.setOnClickListener( this );
		btnLogin = (Button) findViewById(R.id.btnLogin);
		btnLogin.setOnClickListener( this );
		btnLogout = (Button) findViewById(R.id.btnLogout);
		btnLogout.setOnClickListener( this );
		
		Button btnAppInfo = (Button) findViewById(R.id.btnAppInfo);
		btnAppInfo.setOnClickListener( this );
		Button btnLoginInfo = (Button) findViewById(R.id.btnLoginInfo);
		btnLoginInfo.setOnClickListener( this );
	}
    
    @Override
    protected void onResume() {
    	try
    	{
    		configureButtonsVisibility();
    		// TODO Auto-generated method stub
    		super.onResume();
    	}
    	catch( Exception ex )
    	{
    		writeLog( ex.getMessage() );
    	}
    }

	private void configureButtonsVisibility() {
		if ( isAlreadyLogin() )
		{
			btnLogin.setVisibility(View.GONE);
			btnLogout.setVisibility(View.VISIBLE);
		}
		else
		{
			btnLogin.setVisibility(View.VISIBLE);
			btnLogout.setVisibility(View.GONE);
		}
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_settings, menu);
        return true;
    }

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		try
		{
			if ( v.getId() == R.id.btnLogin )
			{
				loadLoginActivity();
			}
			else if ( v.getId() == R.id.btnLogout )
			{
				clearUserInfo();
				configureButtonsVisibility();
			}
			else if ( v.getId() == R.id.btnLoginInfo )
			{
				startActivity( new Intent( this, LoginInfoActivity.class ) );
			}
			else if ( v.getId() == R.id.btnAppInfo )
			{
				startActivity( new Intent( this, AppInfoActivity.class ) );				
			}
			else if ( v.getId() == R.id.btnBack )
				finish();
		}
		catch( Exception ex )
		{
			writeLog( ex.getMessage() );
		}
	}
}
