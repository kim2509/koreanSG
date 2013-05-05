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

public class FreeboardWriteCommentActivity extends BaseActivity implements OnClickListener{

	String mode = "";
	String commRef = "";
	String cindex = "";
	JSONObject paramParent = null;
	int gBn = 1;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
    	try
    	{
    		super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_freeboard_write_comment);
            
            Button btnWrite = (Button) findViewById(R.id.btnWrite);
            btnWrite.setOnClickListener( this );
            
            TextView txtTopTitle = (TextView) findViewById(R.id.txtTopTitle);
			if ( "EDT".equals( mode ) )
				txtTopTitle.setText("덧글 수정");
			else
				txtTopTitle.setText("덧글 작성");
			
            gBn = getIntent().getExtras().getInt("bGb");
            paramParent = new JSONObject( getIntent().getExtras().getString("paramParent") );
            mode = getIntent().getExtras().getString("mode");
            commRef = getIntent().getExtras().getString("CommRef");
            cindex = getIntent().getExtras().getString("cindex");
            
            if ( "EDT".equals( mode ) )
            {
            	EditText edtComment = (EditText) findViewById(R.id.edtComment);
            	edtComment.setText( getIntent().getExtras().getString("comment") );
            }
    	}
    	catch( Exception ex )
    	{
    		writeLog( ex.getMessage() );
    	}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_freeboard_write_comment, menu);
        return true;
    }

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		try
		{
			EditText edtComment = (EditText) findViewById(R.id.edtComment);
			
			JSONObject request = new JSONObject();
			request.put("cmd", mode );
			request.put("cGb", gBn );
			request.put("bidx", paramParent.getString("i") );
			request.put("cidx", cindex );
			request.put("tbContent", edtComment.getText().toString() );
			request.put("bCommentRef", commRef );
			request.put("bDepth", "0");
			request.put("bStep", "0");
			request.put("bUID",  getMetaInfoString("uid"));
			execTransReturningString("krBoard/krBoardCommentWriteOk.aspx", 1, request );
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
				if ( "INS".equals( mode ) )
					showToastMessage("덧글작성이 완료되었습니다.");
				else if ( "EDT".equals( mode ) )
				{
					showToastMessage("수정이 완료되었습니다.");
				}
				
				finish();
			}
			else
			{
				showOKDialog("전송중 오류가 발생했습니다.\r\n다시 시도해 주십시오.", null );
				return;
			}
		}
		catch( Exception ex )
		{
			writeLog( ex.getMessage() );
		}
	}
}
