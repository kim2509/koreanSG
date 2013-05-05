package com.korea.hanintown;

import org.json.JSONObject;

import android.os.Bundle;
import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

public class ShopListMainActivity extends TabActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
    	try
    	{
    		super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_shops_main);
            
            TabHost tabHost = getTabHost();
            
            JSONObject paramObject = new JSONObject( getIntent().getExtras().getString("param") );
            
            TabSpec categorySpec = tabHost.newTabSpec("인기순");
            categorySpec.setIndicator("인기순");
            Intent intentByPopularity = new Intent(this, ShopListActivity.class);
            paramObject.put("mode", "popularity");
            intentByPopularity.putExtra("param", paramObject.toString());
            categorySpec.setContent(intentByPopularity);
            
            TabSpec menuSpec = tabHost.newTabSpec("이름순");
            menuSpec.setIndicator("이름순");
            Intent intentByName = new Intent(this, ShopListActivity.class);
            paramObject.put("mode", "alphabetically");
            intentByName.putExtra("param", paramObject.toString());
            menuSpec.setContent(intentByName);
            
            tabHost.addTab(categorySpec); 
            tabHost.addTab(menuSpec);
            
//            for (int i = 0; i < tabHost.getTabWidget().getTabCount(); i++) {
//                tabHost.getTabWidget().getChildAt(i).getLayoutParams().height = 33;
//            }  
    	}
    	catch( Exception ex )
    	{
    		System.out.println( ex.getMessage() );
    	}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_shops_main, menu);
        return true;
    }
    
    public void goBack( View v )
    {
    	finish();
    }
}