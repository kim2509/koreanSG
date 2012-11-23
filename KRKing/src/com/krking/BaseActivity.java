package com.krking;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
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
		new TransactionTaskReturningString( this, url.trim() ).execute( request );
	}
	
	public void doPostTransaction( String result )
	{
		
	}
	
	public void doPostTransaction( int requestCode, String result )
	{
		
	}
	
	public void showOKDialog( String message, final Object param )
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage( message )
		       .setCancelable(false)
		       .setPositiveButton("확인", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                okClicked( param );
		           }
		       });
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	public void okClicked( Object param )
	{
		
	}
	
	public void makeACall( String phoneNumber )
	{
		try {
			Intent callIntent = new Intent(Intent.ACTION_CALL);
			callIntent.setData(Uri.parse("tel:" + phoneNumber ));
			startActivity(callIntent);
		} catch (ActivityNotFoundException e) {
			showOKDialog("전화걸기에 실패했습니다.\r\n관리자에게 문의바랍니다.", null );
		}
	}
}
