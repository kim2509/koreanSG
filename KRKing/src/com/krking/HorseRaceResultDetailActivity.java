package com.krking;

import java.util.ArrayList;

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
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class HorseRaceResultDetailActivity extends BaseActivity implements OnClickListener{

	String round = "";
	String totalRound = "";
	String city = "";
	String date = "";
	
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
    	try
    	{
    		super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_horse_race_result_detail);
            
            String param = getIntent().getExtras().getString("param");
            
            city = param.split("\\|")[0];
            
            if ( "서울".equals( city ) )
            	city = "KC";
            else if ( "부산".equals( city ) )
            	city = "BU";
            
            date = param.split("\\|")[1];
            
            date = date.replaceAll(" ", "");
            date = date.replaceAll("년", "");
            date = date.replaceAll("월", "");
            date = date.replaceAll("일", "");
            
            round = param.split("\\|")[2];
            
            totalRound = param.split("\\|")[3];
            
            ArrayList<JSONObject> data = new ArrayList<JSONObject>();

			ListView listView = (ListView) findViewById(R.id.list);
			listView.setDivider( null ); 
			listView.setDividerHeight(0);
			listView.setAdapter( new ResultItemAdapter( this, data ) );
            
            execTransReturningString( "krRaceInfo/krRaceResultDetail.aspx?pgb=" + city + "&pDate=" + date + "&pRound=" + 
            		round + "&totRound=" + totalRound , null );
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
        	
        	ArrayList<JSONObject> data = new ArrayList<JSONObject>();
        	
        	JSONObject jsonItem = new JSONObject();
        	jsonItem.put("TYPE", "ITEM1");
        	jsonItem.put("RaceINF1", headerInfo1);
        	jsonItem.put("RaceINF2", headerInfo2);
        	
        	data.add( jsonItem );
        	
        	ArrayList<JSONObject> tempAr = Util.getArrayList( jsonObj.getJSONArray("HorseINF") );
        	for ( int j = 0; j < tempAr.size(); j++ )
			{
				JSONObject firstObj = tempAr.get(j);
				firstObj.put("TYPE", "ITEM2");
			}
        	
        	data.addAll( tempAr );
        	
        	jsonItem = new JSONObject();
        	jsonItem.put("TYPE", "ITEM3");
        	data.add( jsonItem );
        	
        	tempAr = Util.getArrayList( jsonObj.getJSONArray("CORNERTIME") );
        	for ( int j = 0; j < tempAr.size(); j++ )
			{
				JSONObject firstObj = tempAr.get(j);
				firstObj.put("TYPE", "ITEM4");
			}
        	
        	data.addAll( tempAr );
        	
        	jsonItem = new JSONObject();
        	jsonItem.put("TYPE", "ITEM5");
        	data.add( jsonItem );
        	
        	jsonItem = new JSONObject();
        	jsonItem.put("TYPE", "ITEM6");
        	jsonItem.put("BAEDANG", jsonObj.getJSONObject("BAEDANG"));
        	data.add( jsonItem );
        	
        	jsonItem = new JSONObject();
        	jsonItem.put("TYPE", "ITEM7");
        	data.add( jsonItem );
        	
        	jsonItem = new JSONObject();
        	jsonItem.put("TYPE", "ITEM8");
        	jsonItem.put("JUDGMENT", jsonObj.getJSONObject("JUDGMENT"));
        	data.add( jsonItem );
        	
        	ListView listView = (ListView) findViewById(R.id.list);
        	ResultItemAdapter adapter = (ResultItemAdapter) listView.getAdapter();
			adapter.setData(data);
			adapter.notifyDataSetChanged();
        
			LinearLayout lLayout2 = (LinearLayout) findViewById(R.id.lLayout2 );
			lLayout2.removeAllViews();
			
			JSONArray jsonAr = jsonObj.getJSONArray( "Round" );
			
			for ( int i = 0; i < jsonAr.length(); i++ )
			{
				jsonItem = jsonAr.getJSONObject( i );
				
				LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				View vi = inflater.inflate(R.layout.template, null);
				LinearLayout lPLayout = (LinearLayout) vi.findViewById(R.id.lParent);

				Button btn = (Button) vi.findViewById(R.id.btn_template);
				lPLayout.removeView( btn );
				
				lLayout2.addView( btn );
				
				btn.setText( jsonItem.getString("r") + "R" );
				btn.setTextColor(Color.GRAY);
				
				btn.setOnClickListener( this );
				btn.setTag( jsonItem.getString("r") );
				
				if ( (i+1) == Integer.parseInt(round) )
				{
					btn.setBackgroundResource(R.drawable.btn_r_bg_on);
					btn.setTextColor(Color.WHITE);
				}
			}
    	}
    	catch( Exception ex )
    	{
    		writeLog( ex.getMessage() );    		
    	}
    }
    
    public class ResultItemAdapter extends BaseAdapter {

		private Activity activity;
		private ArrayList<JSONObject> data;
		private LayoutInflater inflater=null;

		public ResultItemAdapter(Activity a, ArrayList<JSONObject> d) {
			activity = a;
			data=d;
			inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		public void setData( ArrayList<JSONObject> data )
		{
			this.data = data;
		}

		public int getCount() {
			return data.size();
		}

		public JSONObject getItem(int position) {
			return data.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			try
			{
				View vi=convertView;

				JSONObject jsonObj = data.get(position);

				String tag = "";
				
				if ( "ITEM1".equals( jsonObj.getString("TYPE") ))
				{
					vi = inflater.inflate(R.layout.list_result_header1, null);
					
					ImageView iv = (ImageView) vi.findViewWithTag("city");
					
					JSONObject header1 = jsonObj.getJSONObject("RaceINF1");
					JSONObject header2 = jsonObj.getJSONObject("RaceINF2");

					if ( "서울".equals( header1.getString("g") ) )
						iv.setImageResource(R.drawable.btn_seoul);
					else if ( "부산".equals( header1.getString("g") ) )
						iv.setImageResource(R.drawable.btn_busan);
					else if ( "제주".equals( header1.getString("g") ) )
						iv.setImageResource(R.drawable.btn_jeju);

					TextView tv = (TextView) vi.findViewById(R.id.txtDate);
					tv.setText( header1.getString("d") );

					ImageView ivDay = (ImageView) vi.findViewWithTag("day");

					if ( "금".equals( header1.getString("w") ) )
						ivDay.setImageResource(R.drawable.btn_fri);
					else if ( "토".equals( header1.getString("w") ) )
						ivDay.setImageResource(R.drawable.btn_sat);
					else if ( "일".equals( header1.getString("w") ) )
						ivDay.setImageResource(R.drawable.btn_sun);

					TextView txtWeather = (TextView) vi.findViewById(R.id.txtWeather);
					txtWeather.setText( header1.getString("t") + " " + header1.getString("u") + " " + header1.getString("s") );

					TextView txtRound = (TextView) vi.findViewById(R.id.txtRound);
					txtRound.setText( round + "R");

					TextView txtEtc = (TextView) vi.findViewById(R.id.txtEtc);
					txtEtc.setText( header1.getString("k") + " " + header2.getString("li") + " " +
							header2.getString("ki") + " " + header2.getString("ji") + " " + header2.getString("ai") );
				}
				else if ( "ITEM2".equals( jsonObj.getString("TYPE") ))
				{
					vi = inflater.inflate(R.layout.list_result_header2, null);

					String[] dataHeaders = new String[]{"i0", "i1", "i2", "i3", "i4", "i6", "i7", "i8"};
					for ( int i = 0; i < dataHeaders.length; i++ )
					{
						TextView tv = (TextView) vi.findViewWithTag( dataHeaders[i] );
						tv.setText( jsonObj.getString( dataHeaders[i] ).trim());
					}
				}
				else if ( "ITEM3".equals( jsonObj.getString("TYPE") ))
				{
					vi = inflater.inflate(R.layout.list_result_header3, null);
				}
				else if ( "ITEM4".equals( jsonObj.getString("TYPE") ))
				{
					vi = inflater.inflate(R.layout.list_result_header4, null);

					String[] dataHeaders = new String[]{"o", "n", "S1F", "C1", "C2", "C3", "C4", "G3F", "G1F", "G"};
					for ( int i = 0; i < dataHeaders.length; i++ )
					{
						TextView tv = (TextView) vi.findViewWithTag( dataHeaders[i] );
						tv.setText( jsonObj.getString( dataHeaders[i] ).trim());
					}
				}
				else if ( "ITEM5".equals( jsonObj.getString("TYPE") ))
				{
					vi = inflater.inflate(R.layout.list_result_header5, null);
				}
				else if ( "ITEM6".equals( jsonObj.getString("TYPE") ))
				{
					vi = inflater.inflate(R.layout.list_result_header6, null);
					
					JSONObject baedangInfo = jsonObj.getJSONObject("BAEDANG");
					
					TextView tv = (TextView) vi.findViewWithTag("1");
					tv.setText( baedangInfo.getString("DS") + " " + baedangInfo.getString("YS") );
					tv = (TextView) vi.findViewWithTag("2");
					tv.setText( baedangInfo.getString("BS"));
					tv = (TextView) vi.findViewWithTag("3");
					tv.setText( baedangInfo.getString("BY"));
					tv = (TextView) vi.findViewWithTag("4");
					tv.setText( baedangInfo.getString("SS"));
					tv = (TextView) vi.findViewWithTag("5");
					tv.setText( baedangInfo.getString("SB"));
				}
				else if ( "ITEM7".equals( jsonObj.getString("TYPE") ))
				{
					vi = inflater.inflate(R.layout.list_result_header7, null);
				}
				else if ( "ITEM8".equals( jsonObj.getString("TYPE") ))
				{
					vi = inflater.inflate(R.layout.list_result_header8, null);
					
					JSONObject judgementInfo = jsonObj.getJSONObject("JUDGMENT");
					
					TextView tv = (TextView) vi.findViewWithTag("1");
					tv.setText( judgementInfo.getString("i") );
				}

				vi.setTag( tag );

				return vi;	
			}
			catch( Exception ex )
			{
				writeLog( ex.getMessage() );
			}

			return null;
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		try
		{
			Button btn = (Button) v;
			
			LinearLayout lLayout2 = (LinearLayout) findViewById(R.id.lLayout2 );
			for ( int i = 0; i < lLayout2.getChildCount(); i++ )
			{
				Button btnTemp = (Button) lLayout2.getChildAt(i);
				btnTemp.setTextColor(Color.GRAY);
				btnTemp.setBackgroundResource(R.drawable.btn_r_bg_off);
			}
			
			btn.setBackgroundResource(R.drawable.btn_r_bg_on);
			btn.setTextColor(Color.WHITE);
			
			round = btn.getTag().toString();
			execTransReturningString( "krRaceInfo/krRaceResultDetail.aspx?pgb=" + city + "&pDate=" + date + "&pRound=" + 
            		round + "&totRound=" + totalRound , null );
		}
		catch( Exception ex )
		{
			writeLog( ex.getMessage() );
		}
	}
}
