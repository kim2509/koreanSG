package com.korea.hanintown;

import org.json.JSONArray;
import org.json.JSONObject;

import com.korea.common.Constants;
import com.korea.common.Util;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterMemeberActivity extends DYActivity implements OnClickListener{

    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
    	try
    	{
    		super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_register_memeber);
            
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
		Button btnRegisterMember = (Button) findViewById(R.id.btnRegisterMember);
		btnRegisterMember.setOnClickListener( this );
	}
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_register_memeber, menu);
        return true;
    }

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		try
		{
			if ( arg0.getId() == R.id.btnBack )
			{
				finish();
			}
			else if ( arg0.getId() == R.id.btnRegisterMember )
			{
				EditText edtID = (EditText) findViewById(R.id.edtID );
				EditText edtEmail = (EditText) findViewById(R.id.edtEmail );
				EditText edtNickname = (EditText) findViewById(R.id.edtNickname );
				EditText edtPassword = (EditText) findViewById(R.id.edtPassword );
				EditText edtPasswordConfirm = (EditText) findViewById(R.id.edtPasswordConfirm );
				
				if ( "".equals( edtID.getText().toString() ) )
				{
					showOKDialog("아이디를 입력해 주십시오.", null );
					return;
				}
				
				if ( "".equals( edtEmail.getText().toString() ) )
				{
					showOKDialog("email을 입력해 주십시오.", null );
					return;
				}
				
				if ( Util.validateEmail( edtEmail.getText().toString() ) == false )
				{
					showOKDialog("email형식이 올바르지 않습니다.", null );
					return;
				}
				
				if ( "".equals( edtNickname.getText().toString() ) )
				{
					showOKDialog("닉네임을 입력해 주십시오.", null );
					return;
				}
				
				String password = edtPassword.getText().toString();
				
				if ( "".equals( password ) )
				{
					showOKDialog("비밀번호를 입력해 주십시오.", null );
					return;
				}
				
				String passwordConfirm = edtPasswordConfirm.getText().toString();
				if ( "".equals( passwordConfirm ) )
				{
					showOKDialog("비밀번호 확인란을 입력해 주십시오.", null );
					return;
				}
				
				if ( !password.equals( passwordConfirm ) )
				{
					showOKDialog("비밀번호와 비밀번호 확인값이 일치하지 않습니다.", null );
					return;
				}
				
				JSONObject jsonObj = getJSONDataWithDefaultSetting();
				
				jsonObj.put("userID", edtID.getText().toString() );
				jsonObj.put("email", edtEmail.getText().toString() );
				jsonObj.put("nickName", edtNickname.getText().toString() );
				jsonObj.put("password", edtPassword.getText().toString() );
				jsonObj.put("confirmPassword", edtPasswordConfirm.getText().toString() );
				
				execTrans("iphone/registerMember.php",  jsonObj , true );
			}
		}
		catch( Exception ex )
		{
			writeLog( ex.getMessage() );
		}
	}
	
	@Override
	public void doPostTransaction(Object result) {
		// TODO Auto-generated method stub
		try
		{
			super.doPostTransaction(result);
			
			if ( result == null )
			{
				showOKDialog("응답이 올바르지 않습니다.\n다시 시도해 주십시오.", null );
				return;
			}
			
			JSONArray jsonArr = (JSONArray) result;
			
			if ( jsonArr.length() < 1 )
			{
				showOKDialog("응답이 올바르지 않습니다.\n다시 시도해 주십시오.", null );
				return;
			}
			
			JSONObject responseInfo = jsonArr.getJSONObject(0);
			
			if ( !Constants.SUCCESS.equals( responseInfo.getString("RES_CODE")))
			{
				showOKDialog( responseInfo.getString("RES_MSG"), null );
				return;
			}
			
			if ( jsonArr.length() < 2 )
			{
				showOKDialog( "응답데이터가 올바르지 않습니다.\n관리자에게 문의바랍니다.", null );
				return;
			}
			
			JSONObject userInfo = jsonArr.getJSONObject(1);
			
			setUserInfo( userInfo, true, true );
			
			setResult( RESULT_OK );
			
			finish();
		}
		catch( Exception ex )
		{
			writeLog( ex.getMessage() );
		}
	}
}
