package com.korea.hanintownSG;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class LoginInfoActivity extends DYActivity implements OnClickListener{

    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
    	try
    	{
    		super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_login_info);
            
            Button btnBack = (Button) findViewById(R.id.btnBack);
            btnBack.setOnClickListener( this );
            
            TextView txtUserID = (TextView) findViewById(R.id.txtUserID );
            txtUserID.setText( getMetaInfoString("USER_ID") );
            
            TextView txtNickname = (TextView) findViewById(R.id.txtNickname );
            txtNickname.setText( getMetaInfoString("NICKNAME") );
    	}
    	catch( Exception ex )
    	{
    		writeLog( ex.getMessage() );
    	}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_login_info, menu);
        return true;
    }

	@Override
	public void onClick(View v) {
		try
		{
			// TODO Auto-generated method stub
			
			if ( v.getId() == R.id.btnBack )
				finish();
		}
		catch( Exception ex )
		{
			writeLog( ex.getMessage() );
		}
	}
}
