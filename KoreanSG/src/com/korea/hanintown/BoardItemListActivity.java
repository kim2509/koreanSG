package com.korea.hanintown;

import java.util.HashMap;

import org.apache.http.util.EncodingUtils;
import org.json.JSONObject;

import com.korea.common.Constants;

import android.net.Uri;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class BoardItemListActivity extends DYActivity implements OnClickListener{

	WebView webView;
	String boardName = "";
	ProgressDialog pd = null;
	
    @SuppressLint("SetJavaScriptEnabled")
	@Override
    public void onCreate(Bundle savedInstanceState) 
    {
    	try
    	{
    		super.onCreate(savedInstanceState);
    		
            setContentView(R.layout.activity_board_item_list);

            Button btnNewPost = (Button) findViewById(R.id.btnNewPost);
            btnNewPost.setOnClickListener( this );
            
            String jsonString = getIntent().getExtras().get("param").toString();
    		JSONObject jsonObject = new JSONObject( jsonString );
    		
    		boardName = jsonObject.getString("BOARD_NAME");
    		
    		TextView txtTitle = (TextView) findViewById(R.id.txtTitle);
    		txtTitle.setText( jsonObject.getString("MENU_NAME") );
    		
    		Button btnBoardCategory = (Button) findViewById(R.id.btnBoardCategory);
    		btnBoardCategory.setOnClickListener( this );
    		
    		if ( "".equals( getMetaInfoString( boardName + "_CATEGORY_NAME")))
    		{
    			btnBoardCategory.setText("전체");
    		}
    		else
    		{
    			btnBoardCategory.setText( getMetaInfoString( boardName + "_CATEGORY_NAME") );
    		}
            
            webView = (WebView) findViewById(R.id.webView1);
    		webView.getSettings().setJavaScriptEnabled(true);
    		
    		pd = ProgressDialog.show(this, "", "로딩중...",true);
    		
    		webView.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                	
                    if(pd.isShowing()&&pd!=null)
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
    		
    		String url = "web/mobile/board/BoardItemList.php";
    	    String postData = "categoryID=&boardName=" + boardName + "&osType=" + getOSType();
    	    
    	    webView.postUrl( serverURL + url , EncodingUtils.getBytes(postData, "base64"));
    	    
    	    
    	}
    	catch( Exception ex )
    	{
    		writeLog( ex.getMessage() );    		
    	}
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_board_item_list, menu);
        return true;
    }

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		try
		{
			if ( v.getId() == R.id.btnBack ) 
				finish();
			else if ( v.getId() == R.id.btnBoardCategory )
			{
				Intent intent = new Intent(this, CategorySelectActivity.class);
				intent.putExtra("param", getIntent().getExtras().getString("param"));
				intent.putExtra("showAll", true );
				startActivityForResult( intent, Constants.REQUEST_CODE_COMMON );
			}
			else if ( v.getId() == R.id.btnNewPost )
			{
				Intent intent = new Intent(this, NewPostActivity.class);
				intent.putExtra("mode", "new");
				intent.putExtra("param", getIntent().getExtras().getString("param"));
				startActivityForResult( intent, Constants.REQUEST_CODE_COMMON2 );
			}
		}
		catch( Exception ex )
		{
			writeLog( ex.getMessage() );
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		try
		{
			if ( requestCode == Constants.REQUEST_CODE_COMMON &&
					resultCode == RESULT_OK )
			{
				String url = "web/mobile/board/BoardItemList.php";
	    	    String postData = "categoryID=" + data.getExtras().getString("selectedCategoryID")
	    	    		+ "&boardName=" + boardName + "&osType=" + getOSType();
	    	    
	    	    Button btnBoardCategory = (Button) findViewById(R.id.btnBoardCategory);
	    	    btnBoardCategory.setText( data.getExtras().getString("selectedCategoryName") );
	    	    
	    	    pd = ProgressDialog.show(this, "", "로딩중...",true);
	    	    
	    	    webView.postUrl( serverURL + url , EncodingUtils.getBytes(postData, "base64"));
			}
			else if ( requestCode == Constants.REQUEST_CODE_COMMON2 &&
					resultCode == RESULT_OK )
			{
				String url = "web/mobile/board/BoardItemList.php";
	    	    String postData = "categoryID=" + getMetaInfoString( boardName + "_CATEGORY_ID")
	    	    		+ "&boardName=" + boardName + "&osType=" + getOSType();
	    	    
	    	    pd = ProgressDialog.show(this, "", "로딩중...",true);
	    	    webView.postUrl( serverURL + url , EncodingUtils.getBytes(postData, "base64"));
			}
		
			super.onActivityResult(requestCode, resultCode, data);
		}
		catch( Exception ex )
		{
			writeLog( ex.getMessage());
		}
	}
	
	public void goSearch( View v )
	{
		Intent intent = new Intent( this, SearchBoardActivity.class );
		intent.putExtra("param", getIntent().getExtras().get("param").toString());
		startActivity( intent );
	}
}