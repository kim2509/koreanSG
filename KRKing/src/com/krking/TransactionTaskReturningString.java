package com.krking;

import java.util.Enumeration;
import java.util.Iterator;

import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import com.common.Constants;

public class TransactionTaskReturningString extends AsyncTask<JSONObject, Integer, String> {

	private ProgressDialog dialog = null;
	private String url = "";
	private BaseFragment fragment;
	String serverURL = "http://app.krking.net/";
	int requestCode = 0;

	BaseActivity activity;
	
	String callFrom = "";

	public TransactionTaskReturningString( BaseFragment fragment, String url )
	{
		this.fragment = fragment;
		dialog = new ProgressDialog( fragment.getActivity() );
		this.url = url;
		callFrom = "fragment";
	}
	
	public TransactionTaskReturningString( BaseFragment fragment, String url, int requestCode )
	{
		this.fragment = fragment;
		dialog = new ProgressDialog( fragment.getActivity() );
		this.url = url;
		callFrom = "fragment";
		this.requestCode = requestCode;
	}
	
	public TransactionTaskReturningString( BaseActivity activity, String url )
	{
		this.activity = activity;
		dialog = new ProgressDialog( activity );
		this.url = url;
		callFrom = "activity";
	}
	
	public TransactionTaskReturningString( BaseActivity activity, String url, int requestCode )
	{
		this.activity = activity;
		dialog = new ProgressDialog( activity );
		this.url = url;
		callFrom = "activity";
		this.requestCode = requestCode;
	}

	protected void onPreExecute() {
		this.dialog.setMessage("로딩중...");
		this.dialog.show();
	}

	@SuppressWarnings("rawtypes")
	protected String doInBackground( JSONObject... data ) {

		try
		{
			HttpClient client = new DefaultHttpClient();
			HttpContext localContext = new BasicHttpContext();
			CookieStore cookieStore = new BasicCookieStore();
			localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
			HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); //Timeout Limit
			HttpResponse response;
			
			Log.d("URL", serverURL + url );
			
			HttpPost post = new HttpPost( serverURL + url );
			
			JSONObject json = null;
			if ( data == null || data[0] == null )
			{
				json = new JSONObject();
				StringEntity se = new StringEntity( json.toString(), "UTF-8");
				se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
				post.setEntity(se);
			}
			else
			{
				
				json = data[0];
				
				Iterator keys = json.keys();

				String key = "";
				String value = "";
				
				MultipartEntity reqEntity = new MultipartEntity();
				while (keys.hasNext()) {
					key = keys.next().toString();
					value = json.getString( key );
					
					reqEntity.addPart( key , new StringBody( value ));
				}
				
				post.setEntity(reqEntity);
			}
			
			response = client.execute(post, localContext);
			
			for ( int i = 0; i < cookieStore.getCookies().size(); i++ )
			{
				Cookie cookie = cookieStore.getCookies().get(i);
				System.out.println("cookie name:" + cookie.getName() + " value:" + cookie.getValue() );
			}
			
			String responseString = EntityUtils.toString(response.getEntity()); 
			
			Log.d("RESPONSE", responseString );
			
			return responseString;

		}
		catch(Exception e){
			e.printStackTrace();
			
			return Constants.FAIL;
		}
	}

	protected void onProgressUpdate(Integer... progress) {

	}

	protected void onPostExecute(String result) {
		if (dialog.isShowing())
			dialog.dismiss();
		
		if ( "activity".equals( callFrom ))
		{
			if ( requestCode == 0 )
				activity.doPostTransaction( result );
			else
				activity.doPostTransaction( requestCode, result );
		}
		else
		{
			if ( requestCode == 0 )
				fragment.doPostTransaction( result );
			else
				fragment.doPostTransaction( requestCode, result );
		}
	}
}