package com.korea.hanintown;

import org.apache.http.util.EncodingUtils;
import org.json.JSONException;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.view.Menu;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

public class NotificationActivity extends DYActivity {

	WebView webView;
	
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
    	try
    	{
    		super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_notification);
            
            initializeControls();
            
            initializeWebView();
            
            loadNotificationList();
    	}
    	catch( Exception ex )
    	{
    		writeLog( ex.getMessage() );
    	}
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_notification, menu);
        return true;
    }
    
    public void initializeControls()
    {
    	TextView tv = (TextView) findViewById(R.id.txtTitle);
    	tv.setText("알림센터");
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
    
	private void loadNotificationList() throws JSONException {
		
		String url = "web/mobile/notificationList.php";
		String postData = "udid=" + getUniqueDeviceID() + "&userID=" + getMetaInfoString("USER_ID") + "&nickName=" + 
				getMetaInfoString("NICKNAME") + "&osType=" + getOSType();
		
		pd = ProgressDialog.show(this, "", "로딩중...",true);
		
		webView.postUrl( serverURL + url , EncodingUtils.getBytes(postData, "base64"));
	}
}
