package com.korea.hanintown;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.UUID;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.korea.common.Constants;
import com.korea.common.HttpData;
import com.korea.common.HttpRequest;
import com.korea.common.Util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

public class DYActivity extends Activity{

	//protected String serverURL = "http://www.hanintownsg.com/";
	protected String serverURL = "http://192.168.10.100:8888/";
	protected String serverHost = "192.168.10.100";
	protected int serverPort = 8888;
	
	protected static JSONArray boardCategoryList = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 
	}
	
	public void goBack( View v )
	{
		finish();
	}
	
	public String getOSType()
	{
		return "Android";
	}
	
	public String getOSVersion()
	{
		return Build.VERSION.RELEASE;
	}
	
	public String getPackageVersion()
	{
		PackageInfo pInfo;
		try {
			
			pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			
			return pInfo.versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return "";
	}
	
	public String getUniqueDeviceID()
	{
		final TelephonyManager tm = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);

	    final String tmDevice, tmSerial, androidId;
	    tmDevice = "" + tm.getDeviceId();
	    tmSerial = "" + tm.getSimSerialNumber();
	    androidId = "" + android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

	    UUID deviceUuid = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());
	    return deviceUuid.toString();
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
	
	public void showYesNoDialog( String message, final Object param )
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage( message )
		       .setCancelable(false)
		       .setPositiveButton("예", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   yesClicked( param );
		           }
		       })
		       .setNegativeButton("아니요", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   noClicked( param );
		           }
		       });
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	public void yesClicked( Object param )
	{
		
	}
	
	public void noClicked( Object param )
	{
		
	}
	
	public void clearUserInfo()
	{
		setMetaInfo("USER_NO", "");
		setMetaInfo("USER_ID", "");
		setMetaInfo("NICKNAME", "");
		setMetaInfo("EMAIL", "");
		setMetaInfo("LAST_LOGIN_TIME", "");
	}
	
	public void setUserInfo( JSONObject jsonObj, boolean bForceSet, boolean bSetLogin ) throws Exception
	{
		if ( jsonObj == null ) return;
		
		if ( bForceSet )
		{
			setMetaInfo("USER_NO", Util.getString( jsonObj.getString("USER_NO")));
			setMetaInfo("USER_ID", Util.getString( jsonObj.getString("USER_ID")));
			setMetaInfo("NICKNAME", Util.getString( jsonObj.getString("NICKNAME")));
			setMetaInfo("EMAIL", Util.getString( jsonObj.getString("EMAIL")));
			setMetaInfo("LAST_LOGIN_TIME", Util.getString( jsonObj.getString("LAST_LOGIN_TIME")));			
		}
		else
		{
			if ( "".equals( getMetaInfoString("USER_NO")) )
				setMetaInfo("USER_NO", Util.getString( jsonObj.getString("USER_NO")));
			if ( "".equals( getMetaInfoString("USER_ID")) )
				setMetaInfo("USER_ID", Util.getString( jsonObj.getString("USER_NO")));
			
			if ( bSetLogin )
			{
				if ( "".equals( getMetaInfoString("NICKNAME")) )
					setMetaInfo("NICKNAME", Util.getString( jsonObj.getString("USER_NO")));
				if ( "".equals( getMetaInfoString("EMAIL")) )
					setMetaInfo("EMAIL", Util.getString( jsonObj.getString("USER_NO")));
				if ( "".equals( getMetaInfoString("LAST_LOGIN_TIME")) )
					setMetaInfo("LAST_LOGIN_TIME", Util.getString( jsonObj.getString("USER_NO")));				
			}
		}
	}
	
	public void setMetaInfo( String key, String value )
	{
		SharedPreferences settings = getSharedPreferences("USER_INFO", 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString( key, value );
		editor.commit();
	}
	
	public String getMetaInfoString( String key )
	{
		SharedPreferences settings = getSharedPreferences("USER_INFO",0);
		
		String value = settings.getString(key, "");
		if ("".equals( value ))
			return "";
		
	    return value;
	}
	
	public void hideSoftKeyboard()
	{
		InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE); 

		inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                   InputMethodManager.HIDE_NOT_ALWAYS);
		inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
	}
	
	public void showSoftKeyboard()
	{
		InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE); 

		inputManager.showSoftInput( getCurrentFocus(), InputMethodManager.SHOW_FORCED);
		inputManager.showSoftInputFromInputMethod(getCurrentFocus().getWindowToken(), 
				InputMethodManager.SHOW_FORCED);
	}
	
	public boolean isAlreadyLogin()
	{
		if ( getMetaInfoString("NICKNAME") == null ||
				"".equals( getMetaInfoString("NICKNAME")))
		{
			return false;
		}
		
		return true;
	}
	
	public void loadLoginActivity()
	{
		Intent intent = new Intent( this , LoginActivity.class);
		startActivity(intent);
	}
	
	public Hashtable<String,String> getHashtableWithDefaultSetting() throws Exception
	{
		Hashtable<String,String> hash = new Hashtable<String,String>();
		
		hash.put("osType", getOSType());
		hash.put("udid", getUniqueDeviceID());
		hash.put("userNo", getMetaInfoString("USER_NO"));
		hash.put("userID", getMetaInfoString("USER_ID"));
		hash.put("nickName", getMetaInfoString("NICKNAME"));
		hash.put("androidOSVersion", getOSVersion());
		hash.put("ClientVersion", getPackageVersion());
		
		return hash;
	}
	
	public JSONObject getJSONDataWithDefaultSetting() throws Exception
	{
		JSONObject jsonObj = new JSONObject();
		
		jsonObj.put("osType", getOSType());
		jsonObj.put("udid", getUniqueDeviceID());
		jsonObj.put("userNo", getMetaInfoString("USER_NO"));
		jsonObj.put("userID", getMetaInfoString("USER_ID"));
		jsonObj.put("nickName", getMetaInfoString("NICKNAME"));
		jsonObj.put("androidOSVersion", getOSVersion());
		jsonObj.put("ClientVersion", getPackageVersion());
		
		writeLog( jsonObj.toString() );
		
		return jsonObj;
	}
	
	public MultipartEntity getMultiFormRequestEntityWithDefaultSetting() throws Exception
	{
		MultipartEntity reqEntity = new MultipartEntity();
		
		reqEntity.addPart("osType", new StringBody( getOSType() ) );
		reqEntity.addPart("udid", new StringBody( getUniqueDeviceID() ) );
		reqEntity.addPart("userNo", new StringBody( getMetaInfoString("USER_NO") ) );
		reqEntity.addPart("userID", new StringBody( getMetaInfoString("USER_ID") ) );
		reqEntity.addPart("nickName", new StringBody( getMetaInfoString("NICKNAME") ) );
		reqEntity.addPart("androidOSVersion", new StringBody( getOSVersion() ) );
		reqEntity.addPart("ClientVersion", new StringBody( getPackageVersion() ) );
		
		return reqEntity;
	}
	
	public void goSettingsActivity()
	{
		Intent intent = new Intent( this, SettingsActivity.class );
		startActivity( intent );
	}
	
	public void showToastMessage( String message )
	{
		Toast.makeText(this, message, Toast.LENGTH_LONG).show();
	}
	
	public void writeLog( String log )
	{
		Log.i("KoreanSG", log );
	}
	
	public void execTrans( String url, JSONObject request, boolean bModal )
	{
		new TransactionTask( this, url, bModal ).execute( request );
	}
	
	public void execTransReturningString( String url, JSONObject request, boolean bModal )
	{
		new TransactionTaskReturningString( this, url, bModal ).execute( request );
	}
	
	public void execTransReturningString( String url, JSONObject request, int requestCode, boolean bModal )
	{
		new TransactionTaskReturningString( this, url, requestCode, bModal ).execute( request );
	}
	
	public void execFormRequest( String url, MultipartEntity reqEntity )
	{
		new FormTransactionTask( this, url ).execute( reqEntity );
	}
	
	@SuppressWarnings("unchecked")
	public void execFormRequest( String url, Hashtable<String,String> request, ArrayList<File> files )
	{
		new FormTransactionTask2( this, url, files ).execute( request );
	}
	
	public void doPostTransaction( Object result )
	{
		
	}
	
	public void doPostTransaction( int requestCode, Object result )
	{
		
	}
	
	public boolean checkJSONResult( Object result ) throws Exception
	{
		if ( result == null )
		{
			showOKDialog("응답이 올바르지 않습니다.\n다시 시도해 주십시오.", null );
			return false;
		}
		
		JSONArray jsonArr = (JSONArray) result;
		
		if ( jsonArr.length() < 1 )
		{
			showOKDialog("응답이 올바르지 않습니다.\n다시 시도해 주십시오.", null );
			return false;
		}
		
		JSONObject responseInfo = jsonArr.getJSONObject(0);
		
		if ( !Constants.SUCCESS.equals( responseInfo.getString("RES_CODE")))
		{
			showOKDialog( responseInfo.getString("RES_MSG"), null );
			return false;
		}
		
		return true;
	}
	
	public boolean checkJSONResult2( Object result ) throws Exception
	{
		if ( result == null )
		{
			showOKDialog("응답이 올바르지 않습니다.\n다시 시도해 주십시오.", null );
			return false;
		}
		
		JSONArray jsonArr = (JSONArray) result;
		
		if ( jsonArr.length() < 2 )
		{
			showOKDialog("응답이 올바르지 않습니다.\n다시 시도해 주십시오.", null );
			return false;
		}
		
		JSONObject responseInfo = jsonArr.getJSONArray(1).getJSONObject(0);
		
		if ( !Constants.SUCCESS.equals( responseInfo.getString("RES_CODE")))
		{
			showOKDialog( responseInfo.getString("RES_MSG"), null );
			return false;
		}
		
		return true;
	}
	
	private class TransactionTask extends AsyncTask<JSONObject, Integer, JSONArray> {

		private ProgressDialog dialog = null;
		private String url = "";
		private DYActivity activity;
		private boolean bModal = true;

		public TransactionTask( DYActivity activity, String url, boolean bModal )
		{
			this.activity = activity;
			dialog = new ProgressDialog( activity );
			this.url = url;
			this.bModal = bModal;
		}

		protected void onPreExecute() {
			
			if ( bModal )
			{
				this.dialog.setMessage("로딩중...");
				this.dialog.show();	
			}
		}

		protected JSONArray doInBackground( JSONObject... data ) {

			try
			{
				HttpClient client = new DefaultHttpClient();
				HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); //Timeout Limit
				HttpResponse response;
				JSONObject json = data[0];
				HttpPost post = new HttpPost( serverURL + url );
				StringEntity se = new StringEntity( json.toString(), "UTF-8");
				se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
				post.setEntity(se);
				response = client.execute(post);
				/*Checking response */
				String responseString = EntityUtils.toString(response.getEntity());

				Log.d("RESPONSE", responseString );
				
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
			
			activity.doPostTransaction( result );
		}
	}
	
	public void doFormPostTransaction( Object result )
	{
		
	}
	
	private class FormTransactionTask extends AsyncTask<MultipartEntity, Integer, JSONArray> {

		private ProgressDialog dialog = null;
		private String url = "";
		private DYActivity activity;

		public FormTransactionTask( DYActivity activity, String url )
		{
			this.activity = activity;
			dialog = new ProgressDialog( activity );
			this.url = url;
		}

		protected void onPreExecute() {
			this.dialog.setMessage("로딩중...");
			this.dialog.show();
		}

		protected JSONArray doInBackground( MultipartEntity... data ) {

			try
			{
				HttpClient client = new DefaultHttpClient();
				HttpPost post = new HttpPost( serverURL + url );
				
				MultipartEntity reqEntity = data[0];
				post.setEntity(reqEntity);
				HttpResponse response = client.execute(post);
				/*Checking response */
				String responseString = EntityUtils.toString(response.getEntity());

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
			
			activity.doFormPostTransaction( result );
		}
	}	
	
	private class FormTransactionTask2 extends AsyncTask<Hashtable<String,String>, Integer, JSONArray> {

		private ProgressDialog dialog = null;
		private String url = "";
		private DYActivity activity;
		private ArrayList<File> files = null;

		public FormTransactionTask2( DYActivity activity, String url, ArrayList<File> files )
		{
			this.activity = activity;
			dialog = new ProgressDialog( activity );
			this.url = url;
			this.files = files;
		}

		protected void onPreExecute() {
			this.dialog.setMessage("로딩중...");
			this.dialog.show();
		}

		protected JSONArray doInBackground( Hashtable<String,String>... data ) {

			try
			{
				Hashtable<String,String> request = data[0];

				HttpData resData = HttpRequest.post( serverURL + url, request, files);				

				JSONArray jsonArray = new JSONArray( resData.content );

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
			
			activity.doFormPostTransaction( result );
		}
	}
	
	private class TransactionTaskReturningString extends AsyncTask<JSONObject, Integer, String> {

		private ProgressDialog dialog = null;
		private String url = "";
		private DYActivity activity;
		private boolean bShowDlg = true;
		int requestCode = 0;

		public TransactionTaskReturningString( DYActivity activity, String url, boolean bModal )
		{
			this.activity = activity;
			dialog = new ProgressDialog( activity );
			this.url = url;
			this.bShowDlg = bModal;
		}

		public TransactionTaskReturningString( DYActivity activity, String url, int requestCode, boolean bModal )
		{
			this.activity = activity;
			dialog = new ProgressDialog( activity );
			this.url = url;
			this.bShowDlg = bModal;
			this.requestCode = requestCode;
		}
		
		protected void onPreExecute() {
			
			if ( bShowDlg )
			{
				this.dialog.setMessage("로딩중...");
				this.dialog.show();	
			}
		}

		protected String doInBackground( JSONObject... data ) {

			try
			{
				HttpClient client = new DefaultHttpClient();
				HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); //Timeout Limit
				HttpResponse response;
				JSONObject json = data[0];
				HttpPost post = new HttpPost( serverURL + url );
				StringEntity se = new StringEntity( json.toString(), "UTF-8");
				se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
				post.setEntity(se);
				response = client.execute(post);
				
				String responseString = EntityUtils.toString(response.getEntity());
				
				Log.d("RESPONSE", responseString );
				
				return responseString;

			}
			catch(Exception e){
				e.printStackTrace();
				
				writeLog( e.getMessage() );
				
				return Constants.FAIL;
			}
		}

		protected void onProgressUpdate(Integer... progress) {

		}

		protected void onPostExecute(String result) {
			
			if (dialog.isShowing())
				dialog.dismiss();
			
			if ( requestCode == 0 )
				activity.doPostTransaction( result );
			else
				activity.doPostTransaction( requestCode, result );
		}
	}
	
	public String getAppVersion() throws Exception
	{
		return getPackageManager().getPackageInfo( getPackageName(), 0 ).versionName;
	}
	
	public JSONArray getBoardCategoryList( String boardName, boolean bShowOptional ) throws Exception
	{
		JSONArray ar = new JSONArray();
		
		for ( int i = 0; i < boardCategoryList.length(); i++ )
		{
			if ( boardName.equals( boardCategoryList.getJSONObject(i).getString("BOARD_NAME")) )
			{
				if ( bShowOptional )
				{
					ar.put( boardCategoryList.getJSONObject(i));
				}
				else if	( "N".equals( boardCategoryList.getJSONObject(i).getString("IS_OPTIONAL")) )
				{
					ar.put( boardCategoryList.getJSONObject(i));
				}
			}
		}
		
		return ar;
	}
	
	public class JavaScriptInterface {
        Context mContext;

        /** Instantiate the interface and set the context */
        JavaScriptInterface(Context c) {
            mContext = c;
        }
        
        public void displayContent( String boardName, String bID, String subject, String userID )
        {
        	try
        	{
        		writeLog("boardName:" + boardName + " bID:" + bID + " subject:" + subject + " userID:" + userID );
        		
            	JSONObject jsonObj = new JSONObject();
            	jsonObj.put("BOARD_NAME", boardName);
            	jsonObj.put("BID", bID);
            	jsonObj.put("SUBJECT", subject);
            	jsonObj.put("USER_ID", userID);
            	
            	Intent intent = new Intent( mContext , BoardItemContentActivity.class);
    			intent.putExtra("param", jsonObj.toString() );
    		    startActivityForResult(intent, Constants.REQUEST_CODE_COMMON2 );
        	}
        	catch( Exception ex )
        	{
        		writeLog( ex.getMessage() );
        	}
        }
        
        public void displayMessage( String fromUserID, String toUserID )
        {
        	try
        	{
        		Intent intent = new Intent( mContext , MessageContentActivity.class);
        		intent.putExtra("fromUserID", fromUserID);
        		intent.putExtra("toUserID", toUserID);
				startActivity(intent);
        	}
        	catch( Exception ex )
        	{
        		writeLog( ex.getMessage() );
        	}
        }
        
        public void selectNotificationMessage( String type, String isRead, String id, 
        		String param1, String param2, String param3, String param4, String param5 )
        {
        	try
        	{
        		if ( "MESSAGE".equals( type ) )
        		{
        			Intent intent = new Intent( mContext , MessageListActivity.class);
					startActivity(intent);
        		}
        		else if ( "COMMENT".equals( type ) )
        		{
        			displayContent( param1, param2, param3, getMetaInfoString("USER_ID"));
        		}
        	}
        	catch( Exception ex )
        	{
        		writeLog( ex.getMessage() );
        	}
        }
        
        public void sendMessage( String userID, String nickName )
        {
        	try
        	{
        		JSONObject jsonObj = new JSONObject();
        		jsonObj.put("USER_ID", userID);
        		jsonObj.put("NICKNAME", nickName );
        		Intent intent = new Intent( mContext, SendMessageActivity.class );
    			intent.putExtra("param", jsonObj.toString() );
    			startActivityForResult( intent , Constants.REQUEST_CODE_COMMON);
        	}
        	catch( Exception ex )
        	{
        		writeLog( ex.getMessage() );
        	}
        }
        
        // for BoardItemContentActivity
        public void getBoardContent( String content, String bodyTextID, String bodyTextOrder, 
        		String categoryID, String categoryName, String jsonAttachments )
        {
        	passBoardContent(content, bodyTextID, bodyTextOrder, categoryID, categoryName, jsonAttachments);
        }
    }
	
	// for BoardItemContentActivity
	public void passBoardContent( String content, String bodyTextID, String bodyTextOrder, 
			String categoryID, String categoryName, String jsonAttachments )
	{
		
	}
	
	public int getPixelFromDP( int dp )
	{
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
	}
}
