package com.krking;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class TermsNContractActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_ncontract);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_terms_ncontract, menu);
        return true;
    }
}
