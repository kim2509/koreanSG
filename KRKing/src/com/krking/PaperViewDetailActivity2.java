package com.krking;

import org.json.JSONArray;
import org.json.JSONObject;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;

public class PaperViewDetailActivity2 extends BaseActivity implements OnClickListener{

	ProgressDialog pd = null;
	String page = "";
	
	@SuppressLint("SetJavaScriptEnabled")
	@Override
    public void onCreate(Bundle savedInstanceState) 
    {
    	try
    	{
    		super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_paper_view_detail_activity2);
            
            WebView webView = (WebView) findViewById(R.id.webView1);
			webView.getSettings().setJavaScriptEnabled(true);
			webView.setWebViewClient(new WebViewClient() {
	            @Override
	            public void onPageFinished(WebView view, String url) {
	            	
	                if(pd.isShowing()&&pd!=null)
	                {
	                    pd.dismiss();
	                }
	            }
	        });
			
            String paramString = getIntent().getExtras().getString("param");
            JSONObject jsonObj = new JSONObject( paramString );
            
            page = jsonObj.getString("p");
            
            execTransReturningString( "KrPaper/paperDetail.aspx?ysDate=" + 
					getIntent().getExtras().getString("k") + "&ysPage=" + page, 1, null );
    	}
    	catch( Exception ex )
    	{
    		writeLog(ex.getMessage());
    	}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_paper_view_detail_activity2, menu);
        return true;
    }
    
    @Override
    public void doPostTransaction(int requestCode, String result) {
    	// TODO Auto-generated method stub
    	try
    	{
    		super.doPostTransaction(requestCode, result);
    		
    		JSONObject jsonObj = new JSONObject( result );
    		JSONArray jsonAr = jsonObj.getJSONArray("i");
    		
    		String urlString = jsonAr.getJSONObject(0).getString("n");
    		
    		WebView webView = (WebView) findViewById(R.id.webView1);
    		WebSettings settings = webView.getSettings();
    		settings.setBuiltInZoomControls(true);
    		settings.setUseWideViewPort(true);
    		settings.setLoadWithOverviewMode(true);
			webView.loadUrl( urlString );

			pd = ProgressDialog.show( this , "", "로딩중...",true);
			
			LinearLayout lLayout2 = (LinearLayout) findViewById(R.id.lLayout2 );
			lLayout2.removeAllViews();

			jsonAr = jsonObj.getJSONArray("LP");

			for ( int i = 0; i < jsonAr.length(); i++ )
			{
				JSONObject jsonItem = jsonAr.getJSONObject( i );

				LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				View vi = inflater.inflate(R.layout.template, null);
				LinearLayout lPLayout = (LinearLayout) vi.findViewById(R.id.lParent);

				Button btn = (Button) vi.findViewById(R.id.btn_template);
				lPLayout.removeView( btn );
				lLayout2.addView( btn );
				btn.setText( jsonItem.getString("p") + "P" );
				btn.setTextColor(Color.GRAY);
				btn.setOnClickListener( this );
				btn.setTag( jsonItem );

				if ( page.equals( jsonItem.getString("p")))
				{
					btn.setBackgroundResource(R.drawable.btn_r_bg_on);
					btn.setTextColor(Color.WHITE);
				}
			}
			
    	}
    	catch( Exception ex )
    	{
    		writeLog( ex.getMessage() );
    	}
    }

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		try
		{
			LinearLayout lLayout2 = (LinearLayout) findViewById(R.id.lLayout2 );
			for ( int i = 0; i < lLayout2.getChildCount(); i++ )
			{
				Button btn = (Button) lLayout2.getChildAt(i);
				btn.setTextColor(Color.GRAY);
				btn.setBackgroundResource(R.drawable.btn_r_bg_off);
			}
			
			Button btn = (Button) v;
			btn.setBackgroundResource(R.drawable.btn_r_bg_on);
			btn.setTextColor(Color.WHITE);
			
			JSONObject jsonObj = (JSONObject) v.getTag();
			page = jsonObj.getString("p");
            
            execTransReturningString( "KrPaper/paperDetail.aspx?ysDate=" + 
					getIntent().getExtras().getString("k") + "&ysPage=" + page, 1, null );
		}
		catch( Exception ex )
		{
			writeLog( ex.getMessage() );
		}
	}
}
