package com.krking;

import org.json.JSONObject;

import com.common.Util;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
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
            
            ImageView titleImage = (ImageView) findViewById(R.id.titleImage);
    		
    		if ( "JOK".equals( jsonObj.getString("g") ) )
    			titleImage.setImageResource(R.drawable.prediction_trainingbox);
    		else if ( "BOK".equals( jsonObj.getString("g") ) )
    			titleImage.setImageResource(R.drawable.prediction_reviewbox);
    		else if ( "ANA".equals( jsonObj.getString("g") ) )
    			titleImage.setImageResource(R.drawable.prediction_analysisbox);
    		else if ( "SUN".equals( jsonObj.getString("g") ) )
    			titleImage.setImageResource(R.drawable.prediction_sundaybox);
    		else if ( "SAT".equals( jsonObj.getString("g") ) )
    			titleImage.setImageResource(R.drawable.prediction_saturdaybox);
    		else if ( "FRI".equals( jsonObj.getString("g") ) )
    			titleImage.setImageResource(R.drawable.prediction_fridaybox);
    		
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
        	{
        		btnPhone.setText( "     " + jsonObj.getString("a") );
       
        		
        		btnPhone.setTag( jsonObj.getString("a") );
        	}
        	
        	Button btnSMS = (Button) findViewById(R.id.btnSMS);
        	
        	String sms = "    " + jsonObj.getString("s");
        	btnSMS.setTag( jsonObj.getString("s") );
        	
        	if ( !"".equals( jsonObj.getString("s").trim() ) && !"".equals( jsonObj.getString("e").trim() ) )
        	{
        		sms = sms + " (" + jsonObj.getString("e") + ")";
        	}
        	
        	btnSMS.setText( sms );
        	
        	TextView txtDate = (TextView) findViewById(R.id.txtDate);
        	txtDate.setText( "작성일: " + Util.getDateString(jsonObj.getString("d"), "yyyy-MM-dd a HH:mm:ss", "yy.MM.dd") +
        			" / 조회:" + jsonObj.getString("r") );
        	
        	WebView webView1 = (WebView) findViewById(R.id.webView1);
        	WebSettings settings = webView1.getSettings();
			settings.setBuiltInZoomControls(true);
        	webView1.loadDataWithBaseURL(null, jsonObj.getString("c") , "text/html", "utf-8", null);
    	}
    	catch( Exception ex )
    	{
    		writeLog( ex.getMessage() );
    	}
    }
    
    public void call( View v )
    {
    	try
    	{
    		Button btn = (Button) v;
    		
    		String phoneNumber = btn.getTag().toString().trim();
    		if ( phoneNumber == null || "".equals( phoneNumber ) || phoneNumber.indexOf("무통장") >= 0 )
    			return ;
    		
    		makeACall( phoneNumber );
    	}
    	catch( Exception ex )
    	{
    		writeLog( ex.getMessage() );
    	}
    }
}
