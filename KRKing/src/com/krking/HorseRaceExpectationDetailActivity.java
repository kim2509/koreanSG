package com.krking;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class HorseRaceExpectationDetailActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
    	try
    	{
    		super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_horse_race_expectation_detail);	
    	}
    	catch( Exception ex )
    	{
    		writeLog( ex.getMessage() );
    	}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_horse_race_expectation, menu);
        return true;
    }
}
