package com.korea.hanintown;

import org.apache.http.util.EncodingUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.korea.common.Constants;
import com.korea.hanintown.DYActivity.JavaScriptInterface;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class MessageContentActivity extends DYActivity {

	WebView webView;
	
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
    	try
    	{
    		super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_message_content);
            
            initializeControls();
            
            initializeWebView();
            
            loadMessageContent();
    	}
    	catch( Exception ex )
    	{
    		writeLog( ex.getMessage() );
    	}
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_message_content, menu);
        return true;
    }
    
    public void initializeControls()
    {
    	TextView txtTitle = (TextView) findViewById(R.id.txtTitle);
    	txtTitle.setText( "쪽지함" );
    	
    	EditText edtMessage = (EditText) findViewById(R.id.edtMessage);
    	edtMessage.setOnEditorActionListener(new OnEditorActionListener() {
			
			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				try
				{
					// TODO Auto-generated method stub
					if (actionId == EditorInfo.IME_ACTION_SEND) {
						sendMessage();
			        }
					
					return true;
				}
				catch( Exception ex )
				{
					writeLog( ex.getMessage() );
					return false;
				}
				
			}

		});
    }
    
    ProgressDialog pd = null;
    
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
    
    
    private void loadMessageContent() throws JSONException {

    	String url = "web/mobile/messageContent.php";
		String postData = "fromUserID=" + getIntent().getExtras().get("fromUserID") +
				"&toUserID=" + getIntent().getExtras().get("toUserID") + 
				"&userID=" + getMetaInfoString("USER_ID") + "&osType=" + getOSType();
		
		writeLog( "url:" + url );
		writeLog( "postData:" + postData );
		
		pd = ProgressDialog.show(this, "", "로딩중...",true);
		
		webView.postUrl( serverURL + url , EncodingUtils.getBytes(postData, "base64"));
	}

    public void sendMessage( View v )
    {
    	try
    	{
    		sendMessage();	
    	}
    	catch( Exception ex )
    	{
    		writeLog( ex.getMessage() );
    	}
    }
    
    public void sendMessage() throws Exception
    {
    	EditText edtMessage = (EditText) findViewById(R.id.edtMessage);
    	
    	if ( "".equals( edtMessage.getText().toString().trim()))
    	{
    		showOKDialog("메세지를 입력해 주십시오.", null );
			return;
    	}
    	
    	JSONObject request = getJSONDataWithDefaultSetting();
    	
    	request.put("fromUserID", getMetaInfoString("USER_ID"));
    	
    	if ( getMetaInfoString("USER_ID").equals(
    			getIntent().getExtras().get("fromUserID")))
    		request.put("toUserID", getIntent().getExtras().get("toUserID"));
    	else
    		request.put("toUserID", getIntent().getExtras().get("fromUserID"));
    	
    	request.put("message", edtMessage.getText().toString());
    	
    	edtMessage.setText("");
		hideSoftKeyboard();
    	
    	execTrans("iphone/sendMessage.php", request, true );
    }
    
    @Override
    public void doPostTransaction(Object result) {
    	try
    	{
    		// TODO Auto-generated method stub
        	super.doPostTransaction(result);
        	
        	if ( result == null )
			{
				showOKDialog("응답이 올바르지 않습니다.\n다시 시도해 주십시오.", null);
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
				showOKDialog( responseInfo.getString("RES_MSG") , null );
				return;
			}
			
			loadMessageContent();
    	}
    	catch( Exception ex )
    	{
    		writeLog( ex.getMessage() );
    	}
    }
}
