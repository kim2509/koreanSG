package com.korea.hanintownSG;

import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class AppInfoActivity extends DYActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
    	try
    	{
    		super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_app_info);
            
            Button btnBack = (Button) findViewById(R.id.btnBack);
            btnBack.setOnClickListener( new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					finish();
				}
			});
            
            TextView txtVersion = (TextView) findViewById(R.id.txtVersion);
            txtVersion.setText( getAppVersion() );
    	}
    	catch( Exception ex )
    	{
    		writeLog( ex.getMessage() );
    	}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_app_info, menu);
        return true;
    }
}
