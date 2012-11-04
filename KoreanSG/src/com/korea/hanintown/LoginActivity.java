package com.korea.hanintown;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import org.json.JSONArray;
import org.json.JSONObject;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;
import com.korea.common.Constants;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import com.facebook.android.AsyncFacebookRunner.RequestListener;

public class LoginActivity extends DYActivity implements OnClickListener, RequestListener{

	Facebook facebook = new Facebook("307448279353631");
	AsyncFacebookRunner mAsyncRunner = new AsyncFacebookRunner(facebook);
	
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

    private ProgressDialog dialog = null;
    
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
			facebook.authorize(this, new String[] { "email" }, new DialogListener() {
	            @Override
	            public void onComplete(Bundle values) {
	            	
	            	dialog = new ProgressDialog( LoginActivity.this );
	            	dialog.setMessage("로딩중...");
					dialog.show();	
					
	            	mAsyncRunner.request("me", LoginActivity.this );
	            }

	            @Override
	            public void onFacebookError(FacebookError error) {
	            	showToastMessage("로그인도중 오류가 발생했습니다.\r\n다시 시도해 주십시오.");
	            }

	            @Override
	            public void onError(DialogError e) {
	            	showToastMessage("로그인도중 오류가 발생했습니다.\r\n다시 시도해 주십시오.");
	            }

	            @Override
	            public void onCancel() {
	            	showToastMessage("로그인을 취소했습니다.");
	            }
	        });
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
			if ( requestCode == Constants.REQUEST_CODE_COMMON && resultCode == RESULT_OK )
			{
				Toast.makeText(this, "어서오십시오. " + getMetaInfoString("NICKNAME") + "님.", Toast.LENGTH_LONG).show();
				finish();
			}
			else if ( resultCode == RESULT_OK )
			{
				// facebook login
				facebook.authorizeCallback(requestCode, resultCode, data);
		
			}
		
			super.onActivityResult(requestCode, resultCode, data);
		}
		catch( Exception ex )
		{
			writeLog( ex.getMessage());
		}
	}
	
	@Override
    public void onComplete(final String response, final Object state) {
        
        try {
        	
        	Message msg = new Message();
        	msg.obj = response;
        	msg.what = 0;
        	mainHandler.sendMessage( msg );

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

	@Override
	public void onIOException(IOException e, Object state) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onFileNotFoundException(FileNotFoundException e, Object state) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onMalformedURLException(MalformedURLException e, Object state) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onFacebookError(FacebookError e, Object state) {
		// TODO Auto-generated method stub
	}

	public Handler mainHandler = new Handler() {
        public void handleMessage(android.os.Message msg) 
        {
        	try
        	{
        		if (msg.what == 0) {
                    // updates the TextView with the received text
                	JSONObject responseObj = new JSONObject( msg.obj.toString() );
                	
                	JSONObject request = getJSONDataWithDefaultSetting();
                	request.put("facebookID", responseObj.getString("id"));
                	request.put("facebookURL", responseObj.getString("link"));
                	request.put("nickName", responseObj.getString("first_name"));
                	request.put("email", responseObj.getString("email"));
                	execTransReturningString("iphone/login.php", request, Constants.REQUEST_CODE_COMMON, false );
                }	
        	}
        	catch( Exception ex )
        	{
        		writeLog( ex.getMessage() );
        	}
        };
    };
    
	public void doPostTransaction(int requestCode, Object result) 
    {
    	try
    	{
    		if ( dialog != null && dialog.isShowing() )
    			dialog.dismiss();

    		if ( result == null )
			{
				showOKDialog("응답이 올바르지 않습니다.\n다시 시도해 주십시오.", null );
				return;
			}
			
    		JSONArray resultAr = new JSONArray( result.toString() ); 
			JSONObject responseInfo = resultAr.getJSONObject(0);
			
			if ( !Constants.SUCCESS.equals( responseInfo.getString("RES_CODE")))
			{
				showOKDialog( responseInfo.getString("RES_MSG"), null );
				return;
			}
			
			if ( resultAr.length() < 2 )
			{
				showOKDialog( "응답데이터가 올바르지 않습니다.\n관리자에게 문의바랍니다.", null );
				return;
			}
			
			JSONObject userInfo = resultAr.getJSONObject(1);
			
			setUserInfo( userInfo, true, true );
			
			showToastMessage("어서오십시오. " + getMetaInfoString("NICKNAME") + " 님.");
			finish();
    	}
    	catch( Exception ex )
    	{
    		writeLog( ex.getMessage() );
    	}
    }
}
