package com.korea.hanintownSG;

import org.apache.http.util.EncodingUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.korea.common.Constants;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

public class MessageListActivity extends DYActivity {

	WebView webView;
	
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
    	try
    	{
    		super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_message_list);
            
    		initializeControls();
    		
    		initializeWebView();
    		
    		loadMessageList();
    	}
    	catch( Exception ex )
    	{
    		writeLog( ex.getMessage() );
    	}
    }

    private void initializeControls() throws Exception 
	{
		TextView txtTitle = (TextView) findViewById(R.id.txtTitle);
		txtTitle.setText("쪽지함");
	}

    @SuppressLint("SetJavaScriptEnabled")
	private void initializeWebView() {
		webView = (WebView) findViewById(R.id.webView1);
		webView.getSettings().setJavaScriptEnabled(true);
		
		webView.setWebViewClient(new WebViewClient() {
		    @Override
		    public void onPageFinished(WebView view, String url) {
		    	
		        if( pd != null && pd.isShowing() )
		        {
		            pd.dismiss();
		        }
		    }
		});

		webView.setWebChromeClient(new WebChromeClient() {
			@Override
			public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
				//Required functionality here
				return super.onJsAlert(view, url, message, result);
			} 
		});
		
		webView.addJavascriptInterface(new JavaScriptInterface(this), "Android");
	}
    
    ProgressDialog pd = null;
    
	private void loadMessageList() throws JSONException {
		
		String url = "web/mobile/messageList.php";
		String postData = "udid=" + getUniqueDeviceID() + "&userID=" + getMetaInfoString("USER_ID") + "&nickName=" + 
				getMetaInfoString("NICKNAME") + "&osType=" + getOSType();
		
		writeLog( "url:" + url );
		writeLog( "postData:" + postData );
		
		pd = ProgressDialog.show(this, "", "로딩중...",true);
		
		webView.postUrl( serverURL + url , EncodingUtils.getBytes(postData, "base64"));
	}
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_message_list, menu);
        return true;
    }
    
    public void goNewMessage( View v )
    {
    	Intent intent = new Intent( this, UserListActivity.class );
    	startActivityForResult( intent, Constants.REQUEST_CODE_COMMON );
    }
    
    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		try
		{
			if ( requestCode == Constants.REQUEST_CODE_COMMON &&
					resultCode == RESULT_OK )
			{
				loadMessageList();
			}
			
			super.onActivityResult(requestCode, resultCode, data);
		}
		catch( Exception ex )
		{
			writeLog( ex.getMessage());
		}
	}
}
