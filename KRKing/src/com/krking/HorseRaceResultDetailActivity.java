package com.krking;

import org.json.JSONArray;
import org.json.JSONObject;

import com.common.Util;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class HorseRaceResultDetailActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
    	try
    	{
    		super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_horse_race_result_detail);
            
            String param = getIntent().getExtras().getString("param");
            
            String city = param.split("\\|")[0];
            
            if ( "서울".equals( city ) )
            	city = "KC";
            else if ( "부산".equals( city ) )
            	city = "BU";
            
            String date = param.split("\\|")[1];
            
            date = date.replaceAll(" ", "");
            date = date.replaceAll("년", "");
            date = date.replaceAll("월", "");
            date = date.replaceAll("일", "");
            
            String round = param.split("\\|")[2];
            
            execTransReturningString( "krRaceInfo/krRaceResultDetail.aspx?pgb=" + city + "&pDate=" + date + "&pRound=" + 
            		round + "&totRound=10", null );
    	}
    	catch( Exception ex )
    	{
    		writeLog( ex.getMessage() );
    	}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_horse_race_result_detail, menu);
        return true;
    }
    
    @Override
    public void doPostTransaction(String result) {
    	
    	try
    	{
    		// TODO Auto-generated method stub
        	super.doPostTransaction(result);
        	
        	JSONObject jsonObj = new JSONObject( result );
        	
        	JSONObject headerInfo1 = jsonObj.getJSONObject("RaceINF1");
        	JSONObject headerInfo2 = jsonObj.getJSONObject("RaceINF2");
        	
        	TextView tv = (TextView) findViewById(R.id.txtHeaderInfo);
        	
        	tv.setText( Util.getDateString( headerInfo1.getString("c"), "yyyyMMdd", "yyyy년 MM월 dd일") + 
        			" (" + headerInfo1.getString("w") + ") " + headerInfo1.getString("g") + "  " + 
        			headerInfo1.getString("r") + "  " + headerInfo1.getString("t") + "  " + headerInfo1.getString("u") +
        			"  " + headerInfo1.getString("s") );
        	
        	TextView tv2 = (TextView) findViewById(R.id.txtHeaderInfo2);
        	
        	tv2.setText( headerInfo1.getString("k") + "  " + headerInfo2.getString("li") + " " + headerInfo2.getString("ki") +
        			headerInfo2.getString("ji") + " " + headerInfo2.getString("ai") );
        	
        	LinearLayout l = (LinearLayout) findViewById(R.id.lLayout );
        	
        	JSONArray jsonAr = jsonObj.getJSONArray("HorseINF");
        	
        	String[] dataHeaders = new String[]{"i0", "i1", "i2", "i3", "i4", "i5", "i6", "i7", "i8", "i9"};
        	int[] width = new int[]{20, 20, 80, 30, 50, 70, 40, 60, 40, 40};
        	
        	for ( int i = 0; i < jsonAr.length(); i++ )
        	{
        		JSONObject jsonItem = jsonAr.getJSONObject(i);
            	TableLayout tl = (TableLayout) l.findViewWithTag("table1");
            	TableRow tr = getTableRow(tl, 0, 1, 0, 0);
            	String[] d = getDataFromJSONObject( dataHeaders, jsonItem );
            	getTableCells(tr, d, 0, 0, 0, 0, width, "#ffffff");	
        	}
        	
        	jsonAr = jsonObj.getJSONArray("CORNERTIME");
        	
        	dataHeaders = new String[]{"o", "n", "S1F", "C1", "C2", "C3", "C4", "G3F", "G1F", "G"};
        	width = new int[]{20, 20, 60, 60, 60, 60, 60, 50, 50, 50};
        	
        	for ( int i = 0; i < jsonAr.length(); i++ )
        	{
        		JSONObject jsonItem = jsonAr.getJSONObject(i);
            	TableLayout tl = (TableLayout) l.findViewWithTag("table2");
            	TableRow tr = getTableRow(tl, 0, 1, 0, 0);
            	String[] d = getDataFromJSONObject( dataHeaders, jsonItem );
            	getTableCells(tr, d, 0, 0, 0, 0, width, "#ffffff");
        	}
        	
        	JSONObject baedang = jsonObj.getJSONObject("BAEDANG");
        	
        	TextView txt1 = (TextView) findViewById(R.id.txt1 );
        	txt1.setText( baedang.getString("DS") + "  " + baedang.getString("YS") );
        	TextView txt2 = (TextView) findViewById(R.id.txt2 );
        	txt2.setText( baedang.getString("BS") + "  " + baedang.getString("BY") );
        	TextView txt3 = (TextView) findViewById(R.id.txt3 );
        	txt3.setText( baedang.getString("SS") );
        	TextView txt4 = (TextView) findViewById(R.id.txt4 );
        	txt4.setText( baedang.getString("SB") );
        	
        	JSONObject judgement = jsonObj.getJSONObject("JUDGMENT");
        	
        	TextView txt5 = (TextView) findViewById(R.id.txt5 );
        	txt5.setText( judgement.getString("i") );
    	}
    	catch( Exception ex )
    	{
    		writeLog( ex.getMessage() );    		
    	}
    }
    
    private TableRow getTableRow(TableLayout tl, int topMargin, int bottomMargin, int rightMargin, int leftMargin ) {
		TableRow tr = new TableRow( this );
		tl.addView( tr );
		TableLayout.LayoutParams trp = (TableLayout.LayoutParams) tr.getLayoutParams();
		trp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
		trp.width = ViewGroup.LayoutParams.WRAP_CONTENT;
		trp.setMargins( topMargin, bottomMargin, rightMargin, leftMargin );
		tr.setBackgroundColor(Color.BLACK);
		return tr;
	}
    
    public String[] getDataFromJSONObject( String[] dataHeaders,JSONObject data ) throws Exception
	{
		String[] d = new String[dataHeaders.length];
		for ( int i = 0; i < dataHeaders.length; i++ )
		{
			d[i] = data.getString( dataHeaders[i] );
		}

		return d;
	}
    
    public void getTableCells(TableRow tr, String[] columnData, int topMargin, int bottomMargin, int rightMargin, int leftMargin,
			int[] width, String backgroundColor ) {

		for ( int i = 0; i < columnData.length; i++ )
		{
			TextView tv = new TextView( this );
			tr.addView( tv );
			LinearLayout.LayoutParams tvp = (LinearLayout.LayoutParams) tv.getLayoutParams();
			tvp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
			tvp.width = ViewGroup.LayoutParams.WRAP_CONTENT;
			tvp.setMargins( topMargin, bottomMargin, rightMargin, leftMargin );
			tv.setPadding(3, 2, 3, 2);
			tv.setWidth( width[i] );
			tv.setText( columnData[i] );
			tv.setBackgroundColor( Color.parseColor( backgroundColor ));
			tv.setTextColor(Color.BLACK);
			tv.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL );
		}
	}
}
