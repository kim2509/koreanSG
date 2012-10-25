package com.krking;

import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

public class BaseActivity extends FragmentActivity{

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_intro, menu);
        return true;
    }
    
    public void showToastMessage( String message )
	{
		Toast.makeText(this, message, Toast.LENGTH_LONG).show();
	}
    
    public void writeLog( String log )
	{
		Log.i("KRKing", log );
	}
    
    public void goBack( View v )
    {
    	finish();
    }
    
    public void execTransReturningString( String url, JSONObject request )
	{
		new TransactionTaskReturningString( this, url ).execute( request );
	}
	
	public void doPostTransaction( String result )
	{
		
	}
	
	public void doPostTransaction( int requestCode, String result )
	{
		
	}
}
