package com.krking;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class SMSTopDetailActivity extends BaseActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {

		try
		{
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_smstop_detail);

			JSONObject param = new JSONObject( getIntent().getExtras().getString("param") );
			
			execTransReturningString( "KrSMS/krSMSHitList.aspx?proID=" + param.getString("x") + "&smsNum=" +
					param.getString("t") + "&smsCode=" + param.getString("c"), null );
		}
		catch( Exception ex )
		{
			writeLog( ex.getMessage() );
		}
	}

	@Override
	public void doPostTransaction(String result) {

		try
		{
			// TODO Auto-generated method stub
			super.doPostTransaction(result);
			
			JSONArray resultData = new JSONArray( result );
			
			JSONObject headerInfo = resultData.getJSONObject(0);
			Button btnPhone = (Button) findViewById(R.id.btnPhone);
			btnPhone.setText( "ARS " + headerInfo.getString("tARS") + " (" + headerInfo.getString("cARS") + ")" );
			
			Button btnSMS = (Button) findViewById(R.id.btnSMS);
			btnSMS.setText( "SMS " + headerInfo.getString("tSMS") + " (" + headerInfo.getString("cSMS") + ")");
			
			HashMap<String, ArrayList<JSONObject>> map = new HashMap<String, ArrayList<JSONObject>>();
			ArrayList<JSONObject> tempAr = null;
			
			for ( int i = 1; i < resultData.length(); i++ )
			{
				JSONObject item = resultData.getJSONObject(i);
				if ( map.containsKey( item.getString("d") ) )
				{
					tempAr = map.get( item.getString("d") );
				}
				else
				{
					tempAr = new ArrayList<JSONObject>();
					map.put( item.getString("d"), tempAr);
				}
				
				tempAr.add( item );
			}

			String[] keys = (String[])( map.keySet().toArray( new String[map.size()] ) );
			java.util.Arrays.sort(keys, Collections.reverseOrder());

			LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			LinearLayout lLayout = (LinearLayout) findViewById(R.id.lLayout );

			String[] headers = new String[]{"R", "±¸ºÐ", "º¹¼þ", "½Ö¼þ", "»ïº¹", "Ãà", "ÀûÁß", "È¯¼ö", "SMS¹ß¼Û³»¿ª", "Âø¼ø"};
			String[] dataHeaders = new String[]{"r", "a", "bs", "ss", "sb", "f", "h", "p", "c", "t"};
			int[] width = new int[]{30, 90, 40, 40, 40, 30, 40, 40, 160, 70 };

			for ( int j = 0; j < keys.length; j++ )
			{
				View vi = inflater.inflate(R.layout.list_sms_hit_item, null);
				TableLayout tl = (TableLayout) vi.findViewWithTag("table");
				LinearLayout.LayoutParams tlp = (LinearLayout.LayoutParams) tl.getLayoutParams();
				tlp.setMargins(0, 0, 0, 2);
				TableRow tr = getTableRow(tl, 0, 1, 0, 0 );
				getTableCells(tr, headers, 0, 2, 1, 0, width, "#7E9995" );
				lLayout.addView( vi );
				
				ArrayList<JSONObject> arItems = map.get( keys[j]);
				for ( int i = 0; i < arItems.size(); i++ )
				{
					JSONObject jsonItem = arItems.get(i);
					
					if ( i == 0 )
					{
						TextView tvDate = (TextView) vi.findViewWithTag("date");
						tvDate.setText( Util.getDateString( jsonItem.getString("d"), "yyyyMMdd", "yyyy-MM-dd" )  + 
								" (" + jsonItem.getString("w") + ")" );
					}

					TableRow trItem = getTableRow(tl, 0, 1, 0, 0 );
					String[] d = getDataFromJSONObject( dataHeaders, jsonItem );
					getTableCells(trItem, d, 0, 0, 1, 0, width, jsonItem.getString("rowColor") );
				}
			}


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

	private void getTableCells(TableRow tr, String[] columnData, int topMargin, int bottomMargin, int rightMargin, int leftMargin,
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		try
		{
			getMenuInflater().inflate(R.menu.activity_smstop_detail, menu);
		}
		catch( Exception ex )
		{

		}

		return true;
	}
}
