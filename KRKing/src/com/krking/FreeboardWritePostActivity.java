package com.krking;

import org.json.JSONObject;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class FreeboardWritePostActivity extends BaseActivity implements OnClickListener{

	String mode = "";
	String idx = "0";
	String ref = "0";
	JSONObject paramObject = null;
	JSONObject paramObject2 = null;
	int gBn = 1;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        
    	try
    	{
    		super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_freeboard_write_post);
            
            mode = getIntent().getExtras().getString("mode");
            
            TextView txtTopTitle = (TextView) findViewById(R.id.txtTopTitle);
			if ( "NEW".equals( mode ) )
				txtTopTitle.setText("글 작성");
			else
				txtTopTitle.setText("글 수정");
			
            gBn = getIntent().getExtras().getInt("bGb");
            
            if ( "UPD".equals( mode ) )
            {
            	paramObject = new JSONObject( getIntent().getExtras().getString("param") );
            	paramObject2 = new JSONObject( getIntent().getExtras().getString("param2") );
            	idx = paramObject.getString("i");
            	ref = paramObject2.getJSONObject("VIEW").getString("ref");
            	
            	EditText edtTitle = (EditText) findViewById(R.id.edtTitle);
            	edtTitle.setText( paramObject2.getJSONObject("VIEW").getString("t") );
            	EditText edtContent = (EditText) findViewById(R.id.edtContent);
            	
            	String content = paramObject2.getJSONObject("VIEW").getString("c");
            	content = content.replaceAll("<BR>", "\n");
            	content = content.replaceAll("<BR/>", "\n");
            	content = content.replaceAll("<br>", "\n");
            	content = content.replaceAll("<br/>", "\n");
            	edtContent.setText( content );
            }
            	
            Button btnWrite = (Button) findViewById(R.id.btnWrite);
            btnWrite.setOnClickListener( this );
    	}
    	catch( Exception ex )
    	{
    		writeLog( ex.getMessage() );
    	}
    	
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_freeboard_write_post, menu);
        return true;
    }

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		try
		{
			EditText edtTitle = (EditText) findViewById(R.id.edtTitle);
			EditText edtContent = (EditText) findViewById(R.id.edtContent );
			
			String title = edtTitle.getText().toString();
//			title = title.replaceAll("\r", "");
//			title = title.replaceAll("\n", "<br/>");
			String content = edtContent.getText().toString();
//			content = content.replaceAll("\r", "");
//			content = content.replaceAll("\n", "<br/>");
			
			JSONObject request = new JSONObject();
			request.put("cmd", mode );
			request.put("bGb", String.valueOf( gBn ) );
			request.put("idx", idx );
			request.put("html", "9");
			request.put("tbTitle", title );
			request.put("tbContent", content );
			request.put("bRef", ref );
			request.put("bDepth", "0");
			request.put("bStep", "0");
			request.put("bUID",  getMetaInfoString("uid"));
			execTransReturningString("krBoard/krBoardWriteOk.aspx", 1, request);
		}
		catch( Exception ex )
		{
			writeLog( ex.getMessage() );
		}
	}
	
	@Override
	public void doPostTransaction(int requestCode, String result) {
		// TODO Auto-generated method stub
		try
		{
			super.doPostTransaction(requestCode, result);
			
			JSONObject resObj = new JSONObject( result );
			
			if ( "Y".equals( resObj.getString("C") ) )
			{
				if ( "NEW".equals( mode ) )
					showToastMessage("글작성이 완료되었습니다.");
				else if ( "UPD".equals( mode ) )
				{
					showToastMessage("수정이 완료되었습니다.");
					setResult( com.common.Constants.RELOAD );
				}
				
				finish();
			}
			else 
				showOKDialog("처리중 오류가 발생했습니다. \r\n관리자에게 문의 바랍니다.", null );
		}
		catch( Exception ex )
		{
			writeLog( ex.getMessage() );
		}
	}
}
