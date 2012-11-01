package com.korea.hanintown;

import org.apache.http.util.EncodingUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.korea.hanintown.DYActivity.JavaScriptInterface;

import android.os.Bundle;
import android.os.Handler;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class SearchBoardActivity extends DYActivity {

    private String boardName;
    WebView webView;

	@Override
    public void onCreate(Bundle savedInstanceState) 
    {
    	try
    	{
    		super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_search_board);	
    		
    		String jsonString = getIntent().getExtras().get("param").toString();
    		JSONObject jsonObject = new JSONObject( jsonString );
    		
    		boardName = jsonObject.getString("BOARD_NAME");
    		
    		initializeControls(jsonObject);
    		
    		initializeWebView();
    	}
    	catch( Exception ex )
    	{
    		writeLog( ex.getMessage() );
    	}
    }
	
	@Override
	public void goBack(View v) {
		// TODO Auto-generated method stub
		hideSoftKeyboard();
		super.goBack(v);
	}

	private void initializeControls(JSONObject jsonObject) throws JSONException {
		TextView txtTitle = (TextView) findViewById(R.id.txtTitle);
		txtTitle.setText( jsonObject.getString("MENU_NAME") );
		
		EditText edtSearchBox = (EditText) findViewById(R.id.edtSearchBox);
		
		edtSearchBox.setOnEditorActionListener(new OnEditorActionListener() {
			
			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				try
				{
					// TODO Auto-generated method stub
					if (actionId == EditorInfo.IME_ACTION_SEARCH) {
						searchBoard();
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
		
		new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
            	showSoftKeyboard();
            }
        }, 500);
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_search_board, menu);
        return true;
    }
    
    ProgressDialog pd = null;
    
    @SuppressLint("SetJavaScriptEnabled")
	private void initializeWebView() {
		webView = (WebView) findViewById(R.id.webView);
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
    
    public void searchBoard()
    {
    	RadioGroup rdoGrpSearch = (RadioGroup) findViewById(R.id.rdoGrpSearch);
    	
    	int selectedId = rdoGrpSearch.getCheckedRadioButtonId();
    	 
    	String keyWordType = "";
    	
    	if ( selectedId == R.id.rdoByContent)
    		keyWordType = "CONTENT";
    	else
    		keyWordType = "AUTHOR";
    	
    	EditText edtSearchBox = (EditText) findViewById(R.id.edtSearchBox);
    	
    	rdoGrpSearch.setVisibility(View.GONE);
    	webView.setVisibility(View.VISIBLE);
    	String url = "web/mobile/board/searchBoard.php";
	    String postData = "searchKeywordType=" + keyWordType + "&searchKeyword=" + edtSearchBox.getText().toString() +
	    		"&udid=" + getUniqueDeviceID() + "&userID=" + getMetaInfoString("USER_ID") + 
	    		"&boardName=" + boardName + "&osType=" + getOSType();
	    
	    pd = ProgressDialog.show(this, "", "로딩중...",true);
	    
	    webView.postUrl( serverURL + url , EncodingUtils.getBytes(postData, "base64"));
	    
	    hideSoftKeyboard();
    }
}
