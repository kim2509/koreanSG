package com.korea.hanintown;

import org.json.JSONObject;

import com.korea.common.ShopDataSource;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class AppInfoActivity extends DYActivity {

	private ShopDataSource shopDC;
	
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
    	try
    	{
    		super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_app_info);
    
            shopDC = new ShopDataSource( this );
			shopDC.open();
			
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
   	protected void onResume() {
   		// TODO Auto-generated method stub
   		super.onResume();
   		shopDC.open();
   	}

   	@Override
   	protected void onPause() {
   		// TODO Auto-generated method stub
   		super.onPause();
   		shopDC.close();
   	}
   	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_app_info, menu);
        return true;
    }
    
    public void goShopInfo( View v )
    {
    	try
    	{
    		JSONObject obj = shopDC.getShopInfoByName( "한인타운SG" );
    		
    		Intent intent = new Intent( this, ShopDetailActivity.class );
    		intent.putExtra("param", obj.toString());
    		startActivity(intent);	
    	}
    	catch( Exception ex )
    	{
    		writeLog( ex.getMessage() );
    	}
    }
}
