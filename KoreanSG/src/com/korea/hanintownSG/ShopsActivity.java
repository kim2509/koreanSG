package com.korea.hanintownSG;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class ShopsActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shops);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_shops, menu);
        return true;
    }
}
