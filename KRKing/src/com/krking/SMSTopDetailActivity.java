package com.krking;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import com.common.Util;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

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
import android.widget.ImageView;
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

			String phoneNo = "             " + headerInfo.getString("tARS");

			if ( !"".equals( headerInfo.getString("cARS").trim() ) )
				phoneNo += " (" + headerInfo.getString("cARS") + ")";

			btnPhone.setText( phoneNo );

			// Get singletone instance of ImageLoader
			ImageLoader imageLoader = ImageLoader.getInstance();
			// Initialize ImageLoader with configuration. Do it once.
			imageLoader.init(ImageLoaderConfiguration.createDefault( this ));
			// Load and display image asynchronously

			ImageView list_image = (ImageView) findViewById(R.id.list_image);

			String imageURL = headerInfo.getString("i");
			imageLoader.displayImage( imageURL, list_image);

			Button btnSMS = (Button) findViewById(R.id.btnSMS);

			String smsNo = "             " + headerInfo.getString("tSMS");

			if ( !"".equals( headerInfo.getString("cSMS").trim() ) )
				smsNo += " (" + headerInfo.getString("cSMS") + ")";

			btnSMS.setText( smsNo );

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

			String[] headers = new String[]{"R", "구분", "복숭", "쌍숭", "삼복", "축", "적중%", "환수%", "SMS발송내역", "착순"};
			String[] dataHeaders = new String[]{"r", "a", "bs", "ss", "sb", "f", "h", "p", "c", "t"};
			int[] width = new int[]{100, 90, 60, 60, 60, 40, 80, 80, 350, 70 };
			int[] alignments = new int[]{ Gravity.CENTER_HORIZONTAL , Gravity.CENTER_HORIZONTAL, Gravity.CENTER_HORIZONTAL, 
					Gravity.CENTER_HORIZONTAL, Gravity.CENTER_HORIZONTAL, Gravity.CENTER_HORIZONTAL, Gravity.CENTER_HORIZONTAL, 
					Gravity.CENTER_HORIZONTAL, Gravity.CENTER_HORIZONTAL, Gravity.CENTER_HORIZONTAL };

			for ( int j = 0; j < keys.length; j++ )
			{
				View vi = inflater.inflate(R.layout.list_sms_hit_item, null);
				TableLayout tl = (TableLayout) vi.findViewWithTag("table");
				LinearLayout.LayoutParams tlp = (LinearLayout.LayoutParams) tl.getLayoutParams();
				tlp.setMargins(0, 0, 0, 1);
				TableRow tr = getTableRow(tl, 0, 1, 0, 0 );
				
				// SMS 발송내역 헤더 중앙정렬
				alignments[8] = Gravity.CENTER_HORIZONTAL;

				getTableCells(tr, headers, 0, 1, 1, 0, width, alignments, 14, null , R.drawable.sms_title_bg, 40, null  );
				lLayout.addView( vi );

				// SMS 발송내역 좌측정렬
				alignments[8] = Gravity.LEFT;

				ArrayList<JSONObject> arItems = map.get( keys[j]);
				for ( int i = 0; i < arItems.size(); i++ )
				{
					JSONObject jsonItem = arItems.get(i);

					if ( i == 0 )
					{
						TextView tvDate = (TextView) vi.findViewWithTag("date");
						tvDate.setText( Util.getDateString( jsonItem.getString("d"), "yyyyMMdd", "yyyy-MM-dd" )  + 
								" (" + jsonItem.getString("w") + ")" );
						list_image = (ImageView) vi.findViewById(R.id.list_image);
						if ("금".equals( jsonItem.getString("w") ) )
							list_image.setBackgroundResource(R.drawable.prediction_fridaybox);
						else if ("토".equals( jsonItem.getString("w") ) )
							list_image.setBackgroundResource(R.drawable.prediction_saturdaybox);
						else if ("일".equals( jsonItem.getString("w") ) )
							list_image.setBackgroundResource(R.drawable.prediction_sundaybox);
					}

					TableRow trItem = getTableRow(tl, 0, 1, 0, 0 );
					String[] d = getDataFromJSONObject( dataHeaders, jsonItem );
					getTableCells(trItem, d, 0, 0, 1, 0, width, alignments, 13, jsonItem.getString("rowColor"), 0 , 40, jsonItem );
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
		tr.setBackgroundColor( Color.parseColor("#D2D2D2"));
		return tr;
	}

	public String[] getDataFromJSONObject( String[] dataHeaders, JSONObject data ) throws Exception
	{
		String[] d = new String[dataHeaders.length];
		for ( int i = 0; i < dataHeaders.length; i++ )
		{
			d[i] = data.getString( dataHeaders[i] );
		}

		return d;
	}

	private void getTableCells(TableRow tr, String[] columnData, int topMargin, int bottomMargin, int rightMargin, int leftMargin,
			int[] width, int[] alignments , float fontSize, String backgroundColor, int backgroundDrawable, int height, JSONObject jsonItem )
	throws Exception
	{

		for ( int i = 0; i < columnData.length; i++ )
		{
			TextView tv = new TextView( this );
			
			if ( i == 0 && jsonItem != null && jsonItem.has("g"))
			{
				LinearLayout l = new LinearLayout( this );
				l.setOrientation(LinearLayout.HORIZONTAL);
				tr.addView(l);
				
				l.getLayoutParams().height = height;
				l.getLayoutParams().width = width[i];
				LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams ) l.getLayoutParams();
				lp.setMargins( topMargin, bottomMargin, rightMargin, leftMargin);
				//lp.gravity = Gravity.CENTER_HORIZONTAL;
				
				ImageView iv = new ImageView( this );
				l.addView( iv );
				l.addView( tv );
				
				
				iv.getLayoutParams().height = 25;
				iv.getLayoutParams().width = 28;
				
				LinearLayout.LayoutParams ivp = (LinearLayout.LayoutParams) iv.getLayoutParams();
				ivp.gravity = Gravity.CENTER_VERTICAL;
				
				ivp.leftMargin = 20;
				
				if ( "서울".equals( jsonItem.getString("g") ) )
					iv.setImageResource(R.drawable.table_icon_seoul);
				else if ( "부산".equals( jsonItem.getString("g") ) )
					iv.setImageResource(R.drawable.table_icon_busan);
				else if ( "제주".equals( jsonItem.getString("g") ) )
					iv.setImageResource(R.drawable.table_icon_jeju);
				
				if ( backgroundColor != null && !"".equals( backgroundColor ))
					l.setBackgroundColor( Color.parseColor( backgroundColor ));
				
				LinearLayout.LayoutParams tvp = (LinearLayout.LayoutParams) tv.getLayoutParams();
				tvp.gravity = Gravity.CENTER_VERTICAL;
				
				tv.setText( columnData[i] + "R");
			}
			else
			{
				tr.addView( tv );
				
				LinearLayout.LayoutParams tvp = (LinearLayout.LayoutParams) tv.getLayoutParams();
				tvp.height = height;
				tvp.width = width[i];
				tvp.setMargins( topMargin, bottomMargin, rightMargin, leftMargin );
				
				if ( backgroundColor != null && !"".equals( backgroundColor ))
					tv.setBackgroundColor( Color.parseColor( backgroundColor ));
				else
					tv.setBackgroundResource( backgroundDrawable );
				
				tv.setText( columnData[i] );
			}

			tv.setSingleLine(true);
			
			tv.setPadding(3, 2, 3, 2);
			tv.setTextSize( fontSize );
			tv.setTextColor( Color.parseColor("#353535") );

			if ( jsonItem != null )
			{
				switch ( i )
				{
				case 2:
					tv.setTextColor( Color.parseColor( jsonItem.getString("bsColor") ) );
					break;
				case 3:
					tv.setTextColor( Color.parseColor( jsonItem.getString("ssColor") ) );
					break;
				case 4:
					tv.setTextColor( Color.parseColor( jsonItem.getString("sbColor") ) );
					break;
				case 5:
					tv.setTextColor( Color.parseColor( jsonItem.getString("fColor") ) );
					break;
				case 6:
					tv.setTextColor( Color.parseColor( jsonItem.getString("hColor") ) );
					break;
				case 7:
					tv.setTextColor( Color.parseColor( jsonItem.getString("pColor") ) );
					break;
				default:
					tv.setTextColor( Color.parseColor("#353535") );
				}
			}

			tv.setGravity( alignments[i] | Gravity.CENTER_VERTICAL );
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
