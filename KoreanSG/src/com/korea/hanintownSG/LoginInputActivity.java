package com.korea.hanintownSG;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.korea.common.Constants;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class LoginInputActivity extends DYActivity implements OnClickListener{

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		try
		{
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_login_input);

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
		Button btnLogin = (Button) findViewById(R.id.btnLogin);
		btnLogin.setOnClickListener( this );
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_login_input, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		try
		{
			if ( v.getId() == R.id.btnBack )
			{
				finish();
			}
			else if ( v.getId() == R.id.btnLogin )
			{
				EditText edtID = (EditText) findViewById(R.id.edtID );

				if ( "".equals( edtID.getText().toString()) )
				{
					showOKDialog("아이디를 입력해 주십시오.", null);
					return;
				}

				EditText edtPassword = (EditText) findViewById(R.id.edtPassword);

				if ( "".equals( edtPassword.getText().toString()) )
				{
					showOKDialog("비밀번호를 입력해 주십시오.", null);
					return;
				}

				new LoginTask( this ).execute( serverURL + "iphone/login.php");
			}
		}
		catch( Exception ex )
		{
			writeLog( ex.getMessage() );
		}
	}

	private class LoginTask extends AsyncTask<String, Integer, JSONArray> {

		private ProgressDialog dialog = null;

		public LoginTask( Context context )
		{
			dialog = new ProgressDialog(context);
		}

		protected void onPreExecute() {
			this.dialog.setMessage("로딩중...");
			this.dialog.show();
		}

		protected JSONArray doInBackground( String... data ) {

			try
			{
				HttpClient client = new DefaultHttpClient();
				HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); //Timeout Limit
				HttpResponse response;
				JSONObject json = getJSONDataWithDefaultSetting();

				EditText edtID = (EditText) findViewById(R.id.edtID);
				EditText edtPassword = (EditText) findViewById(R.id.edtPassword);

				json.put("userID" , edtID.getText().toString());
				json.put("password" , edtPassword.getText().toString());

				HttpPost post = new HttpPost( data[0] );
				StringEntity se = new StringEntity( json.toString(), "UTF-8");
				se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
				post.setEntity(se);
				response = client.execute(post);
				/*Checking response */
				String responseString = EntityUtils.toString(response.getEntity());

				Log.i("response", responseString);

				JSONArray jsonArray = new JSONArray( responseString );

				return jsonArray;

			}
			catch(Exception e){
				e.printStackTrace();
				
				writeLog( e.getMessage() );
				try
				{
					JSONArray jsonArr = new JSONArray();
					JSONObject jsonObj = new JSONObject();
					jsonObj.put("RES_CODE", Constants.FAIL);
					jsonObj.put("RES_MSG", "서버랑 통신이 원활하지 않습니다.\n잠시 후 다시 접속해 주십시오.");
					jsonArr.put( jsonObj );	
					
					// to adjust number of child items of the result as 2
					JSONObject jsonObj2 = new JSONObject();
					jsonArr.put( jsonObj2 );
					
					return jsonArr;
				}
				catch( Exception ex )
				{
					writeLog( ex.getMessage() );
				}
				
			}

			return null;
		}

		protected void onProgressUpdate(Integer... progress) {

		}

		protected void onPostExecute(JSONArray result) {
			if (dialog.isShowing())
				dialog.dismiss();

			LoginInputActivity.this.doAfterLogin( result );
		}
	}

	public void doAfterLogin( JSONArray result )
	{
		try
		{
			if ( result == null || result.length() < 1 )
			{
				showOKDialog("응답이 올바르지 않습니다.\n다시 시도해 주십시오.", null );
				return;
			}
			
			JSONObject responseInfo = result.getJSONObject(0);
			
			if ( !Constants.SUCCESS.equals( responseInfo.getString("RES_CODE")))
			{
				showOKDialog( responseInfo.getString("RES_MSG"), null );
				return;
			}
			
			if ( result.length() < 2 )
			{
				showOKDialog( "응답데이터가 올바르지 않습니다.\n관리자에게 문의바랍니다.", null );
				return;
			}
			
			JSONObject userInfo = result.getJSONObject(1);
			
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
