package com.korea.hanintownSG;

import org.json.JSONObject;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class SendMessageActivity extends DYActivity {

	
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
    	try
    	{
    		super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_send_message);	
            
            initializeControls();
    	}
    	catch( Exception ex )
    	{
    		writeLog( ex.getMessage() );
    	}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_send_message, menu);
        return true;
    }
    
    public void initializeControls() throws Exception
    {
    	TextView txtTo = (TextView) findViewById(R.id.txtTo );
    	String jsonString = getIntent().getExtras().getString("param");
    	JSONObject jsonObj = new JSONObject( jsonString );
    	txtTo.setText( jsonObj.getString("NICKNAME") + "(" + jsonObj.getString("USER_ID")+ ")");
    }
    
    public void sendMessage( View v )
    {
    	try
    	{
    		EditText edtMessage = (EditText) findViewById(R.id.edtMessage);
    		
    		if ( "".equals( edtMessage.getText()))
    		{
    			showOKDialog("메세지를 입력해 주십시오.", null );
    			return;
    		}
    		
    		String jsonString = getIntent().getExtras().getString("param");
        	JSONObject jsonObj = new JSONObject( jsonString );
        	
        	JSONObject request = getJSONDataWithDefaultSetting();
        	request.put("fromUserID", getMetaInfoString("USER_ID"));
        	request.put("toUserID", jsonObj.getString("USER_ID"));
        	request.put("message", edtMessage.getText().toString() );
     
        	execTrans("iphone/sendMessage.php", request );
    	}
    	catch( Exception ex )
    	{
    		writeLog( ex.getMessage() );
    	}
    }
    
    @Override
    public void doPostTransaction(Object result) {
    	
    	try
    	{
    		// TODO Auto-generated method stub
        	super.doPostTransaction(result);
        	
        	if ( checkJSONResult(result) == false ) return;
        	
        	setResult(RESULT_OK);
        	finish();
    	}
    	catch( Exception ex )
    	{
    		writeLog( ex.getMessage() );
    	}
    }
}
