package com.korea.hanintown;

import com.korea.common.Constants;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class LoginActivity extends DYActivity implements OnClickListener{

    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
    	try
    	{
    		super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_login);
            
            initializeControls();
    	}
    	catch( Exception ex )
    	{
    		writeLog(ex.getMessage());
    	}
    }

	private void initializeControls() {
		Button btnBack = (Button) findViewById(R.id.btnBack);
		btnBack.setOnClickListener( this );
		Button btnLogin = (Button) findViewById(R.id.btnLogin);
		btnLogin.setOnClickListener( this );
		Button btnFacebookLogin = (Button) findViewById(R.id.btnFacebookLogin);
		btnFacebookLogin.setOnClickListener( this );
		Button btnRegisterMember = (Button) findViewById(R.id.btnRegisterMember);
		btnRegisterMember.setOnClickListener( this );
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_login, menu);
        return true;
    }

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if ( v.getId() == R.id.btnLogin )
		{
			Intent intent = new Intent( this , LoginInputActivity.class);
			startActivityForResult(intent, Constants.REQUEST_CODE_COMMON );
		}
		else if ( v.getId() == R.id.btnFacebookLogin )
		{
			
		}
		else if ( v.getId() == R.id.btnRegisterMember )
		{
			Intent intent = new Intent( this , RegisterMemeberActivity.class);
			startActivityForResult(intent, Constants.REQUEST_CODE_COMMON );
		}
		else if ( v.getId() == R.id.btnBack )
			finish();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		try
		{
			if ( resultCode == RESULT_OK )
			{
				Toast.makeText(this, "어서오십시오. " + getMetaInfoString("NICKNAME") + "님.", Toast.LENGTH_LONG).show();
				finish();
			}
		
			super.onActivityResult(requestCode, resultCode, data);
		}
		catch( Exception ex )
		{
			writeLog( ex.getMessage());
		}
	}
}
