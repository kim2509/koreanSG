package com.krking;

import org.json.JSONObject;

import com.common.Util;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

public class PredictionContentActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
    	try
    	{
    		super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_prediction_content);
            String paramString = getIntent().getExtras().getString("param");
            JSONObject jsonObj = new JSONObject( paramString );
            execTransReturningString( "KrPro/krProView.aspx?idx=" + jsonObj.getString("i") , null );
    	}
    	catch( Exception ex )
    	{
    		writeLog( ex.getMessage() );
    	}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_prediction_content, menu);
        return true;
    }
    
    @Override
    public void doPostTransaction(String result) {
    	
    	try
    	{
    		// TODO Auto-generated method stub
        	super.doPostTransaction(result);
        	JSONObject jsonObj = new JSONObject( result );
        	
        	TextView txtTitle = (TextView) findViewById(R.id.txtTitle);
        	txtTitle.setText( jsonObj.getString("t") );
        	TextView txtAuthor = (TextView) findViewById(R.id.txtAuthor);
        	txtAuthor.setText( jsonObj.getString("n") );
        	
        	Button btnPhone = (Button) findViewById(R.id.btnPhone);
        	
        	if ( !"".equals( jsonObj.getString("a") ) )
        		btnPhone.setText( "전화연결 " + jsonObj.getString("a") );
        	
        	String sms = "SMS " + jsonObj.getString("s");
        	if ( !"".equals( jsonObj.getString("s").trim() ) && !"".equals( jsonObj.getString("e").trim() ) )
        	{
        		sms = sms + " (" + jsonObj.getString("e") + ")";
        	}
        	
        	Button btnSMS = (Button) findViewById(R.id.btnSMS);
        	btnSMS.setText( sms );
        	
        	TextView txtDate = (TextView) findViewById(R.id.txtDate);
        	txtDate.setText( Util.getDateString(jsonObj.getString("d"), "yyyy-MM-dd a HH:mm:ss", "yy.MM.dd") );
        	
        	TextView txtHitCount = (TextView) findViewById(R.id.txtHitCount);
        	txtHitCount.setText( jsonObj.getString("r"));
        	
        	WebView webView1 = (WebView) findViewById(R.id.webView1);
        	webView1.loadDataWithBaseURL(null, jsonObj.getString("c") , "text/html", "utf-8", null);
    	}
    	catch( Exception ex )
    	{
    		writeLog( ex.getMessage() );
    	}
    }
}
