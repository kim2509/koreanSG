package com.krking;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.common.Util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class HorseRaceScheduleFragment extends BaseFragment implements OnClickListener{

	MainActivity mainActivity;
	int requestCode = 0;

	public HorseRaceScheduleFragment( MainActivity main, int requestCode )
	{
		this.mainActivity = main;
		this.requestCode = requestCode;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_horserace_schedule, container, false);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public void onStart() {
		
		try
		{
			// TODO Auto-generated method stub
			super.onStart();
			
			ArrayList<JSONObject> data = new ArrayList<JSONObject>();

			ListView listView = (ListView) getView().findViewById(R.id.list);
			listView.setDivider( null ); 
			listView.setDividerHeight(0);
			listView.setAdapter( new ScheduleItemAdapter( getActivity(), data ) );
			
			setDayButton();
			
			if ( requestCode == 1 )
			{
				execTransReturningString("krRaceInfo/krRaceList.aspx?dGb=1", requestCode, null);
			}
			else if ( requestCode == 2 )
			{
				execTransReturningString("krRaceInfo/krRaceList.aspx?dGb=2", requestCode, null);
			}
			else if ( requestCode == 3 )
			{
				execTransReturningString("krRaceInfo/krRaceList.aspx?dGb=3", requestCode, null);
			}
			
			Button btnFri = (Button) getView().findViewById(R.id.btnFri);
			btnFri.setOnClickListener( this );
			Button btnSat = (Button) getView().findViewById(R.id.btnSat);
			btnSat.setOnClickListener( this );
			Button btnSun = (Button) getView().findViewById(R.id.btnSun);
			btnSun.setOnClickListener( this );
			
		}
		catch( Exception ex )
		{
			writeLog( ex.getMessage() );
		}
	}
	
	public void setDayButton()
	{
		Button btnFri = (Button) getActivity().findViewById(R.id.btnFri);
		Button btnSat = (Button) getActivity().findViewById(R.id.btnSat);
		Button btnSun = (Button) getActivity().findViewById(R.id.btnSun);

		btnFri.setBackgroundResource( R.drawable.tab_menu01_fri_off);
		btnSat.setBackgroundResource( R.drawable.tab_menu02_sat_off);
		btnSun.setBackgroundResource( R.drawable.tab_menu03_sun_off);

		if ( requestCode == 1 )
			btnFri.setBackgroundResource( R.drawable.tab_menu01_fri_on);
		else if ( requestCode == 2 )
			btnSat.setBackgroundResource( R.drawable.tab_menu02_sat_on);
		else if ( requestCode == 3 )
			btnSun.setBackgroundResource( R.drawable.tab_menu03_sun_on);
	}
	
	String strDate = "";
	
	@Override
	public void doPostTransaction( int requestCode, String result) {
	
		try
		{
			// TODO Auto-generated method stub
			super.doPostTransaction(result);
			
			JSONArray jsonAr = new JSONArray( result );
			
			ArrayList<JSONObject> data = new ArrayList<JSONObject>();

			for ( int i = 0; i < jsonAr.length() - 1; i++ )
			{
				ArrayList<JSONObject> tempAr = Util.getArrayList( jsonAr.getJSONArray(i) );
				for ( int j = 0; j < tempAr.size(); j++ )
				{
					JSONObject firstObj = tempAr.get(j);
					if ( j == 0 )
						firstObj.put("TYPE", "ITEM1");
					else
						firstObj.put("TYPE", "ITEM2");
				}

				data.addAll( tempAr );
			}
			
			ListView listView = (ListView) getView().findViewById(R.id.list);
			ScheduleItemAdapter adapter = (ScheduleItemAdapter) listView.getAdapter();
			adapter.setData(data);
			adapter.notifyDataSetChanged();

			LinearLayout lLayout2 = (LinearLayout) getView().findViewById(R.id.lLayout2 );
			lLayout2.removeAllViews();
			
			jsonAr = jsonAr.getJSONArray( jsonAr.length() - 1 );
			
			for ( int i = 0; i < jsonAr.length(); i++ )
			{
				JSONObject jsonItem = jsonAr.getJSONObject( i );
				
				LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				View vi = inflater.inflate(R.layout.template, null);
				LinearLayout lPLayout = (LinearLayout) vi.findViewById(R.id.lParent);

				Button btn = (Button) vi.findViewById(R.id.btn_template);
				lPLayout.removeView( btn );
				
				lLayout2.addView( btn );
				
				btn.setText( jsonItem.getString("p").substring(0, 1) + jsonItem.getString("r") + "R" );
				btn.setTextColor(Color.GRAY);
				
				btn.setOnClickListener( this );
				btn.setTag( jsonItem );
				
				if ( i == 0 )
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
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onClick(View arg0) {
		
		try
		{
			// TODO Auto-generated method stub
			if ( arg0.getId() == R.id.btnFri )
			{
				requestCode = 1;
				setDayButton();
				execTransReturningString("krRaceInfo/krRaceList.aspx?dGb=1", requestCode, null);
			}
			else if ( arg0.getId() == R.id.btnSat )
			{
				requestCode = 2;
				setDayButton();
				execTransReturningString("krRaceInfo/krRaceList.aspx?dGb=2", requestCode, null);
			}
			else if ( arg0.getId() == R.id.btnSun )
			{
				requestCode = 3;
				setDayButton();
				execTransReturningString("krRaceInfo/krRaceList.aspx?dGb=3", requestCode, null);
			}
			else if ( arg0.getId() == R.id.btnGo )
			{
				mainActivity.goToHorseRacePopularity( requestCode );
			}
			else
			{
				if ( arg0.getTag() != null )
				{
					LinearLayout lLayout2 = (LinearLayout) getView().findViewById(R.id.lLayout2 );
					for ( int i = 0; i < lLayout2.getChildCount(); i++ )
					{
						Button btn = (Button) lLayout2.getChildAt(i);
						btn.setTextColor(Color.GRAY);
						btn.setBackgroundResource(R.drawable.btn_r_bg_off);
					}
					
					JSONObject jsonItem = (JSONObject) arg0.getTag();
					Button btn = (Button) arg0;
					
					ListView listView = (ListView) getView().findViewById(R.id.list);
					btn.setBackgroundResource(R.drawable.btn_r_bg_on);
					btn.setTextColor(Color.WHITE);
					
					ScheduleItemAdapter adapter = (ScheduleItemAdapter) listView.getAdapter();
					for ( int i = 0; i < adapter.getCount(); i++ )
					{
						JSONObject jsonObj = adapter.getItem(i);
						if ( jsonObj.has("TYPE") && "ITEM1".equals( jsonObj.getString("TYPE") ) )
						{
							if (( jsonObj.getString("g") + jsonObj.getString("r") ).equals( 
									jsonItem.getString("p") + jsonItem.getString("r")  ) )
							{
								listView.smoothScrollToPosition(i);
								break;
							}
						}
					}
				}
			}
		}
		catch( Exception ex )
		{
			writeLog( ex.getMessage() );
		}
	}
	
	public class ScheduleItemAdapter extends BaseAdapter {

		private Activity activity;
		private ArrayList<JSONObject> data;
		private LayoutInflater inflater=null;

		public ScheduleItemAdapter(Activity a, ArrayList<JSONObject> d) {
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
					vi = inflater.inflate(R.layout.list_schedule_header1, null);

					ImageView iv = (ImageView) vi.findViewWithTag("city");

					if ( "서울".equals( jsonObj.getString("g") ) )
						iv.setImageResource(R.drawable.btn_seoul);
					else if ( "부산".equals( jsonObj.getString("g") ) )
						iv.setImageResource(R.drawable.btn_busan);
					else if ( "제주".equals( jsonObj.getString("g") ) )
						iv.setImageResource(R.drawable.btn_jeju);

					TextView tv = (TextView) vi.findViewById(R.id.txtDate);
					tv.setText( jsonObj.getString("d") );

					ImageView ivDay = (ImageView) vi.findViewWithTag("day");

					if ( "금".equals( jsonObj.getString("w") ) )
						ivDay.setImageResource(R.drawable.btn_fri);
					else if ( "토".equals( jsonObj.getString("w") ) )
						ivDay.setImageResource(R.drawable.btn_sat);
					else if ( "일".equals( jsonObj.getString("w") ) )
						ivDay.setImageResource(R.drawable.btn_sun);

					TextView txtTime = (TextView) vi.findViewById(R.id.txtTime);
					txtTime.setText( "(발주:" + jsonObj.getString("t") + ")");

					TextView txtRound = (TextView) vi.findViewById(R.id.txtRound);
					txtRound.setText( jsonObj.getString("r") + "R");

					TextView txtEtc = (TextView) vi.findViewById(R.id.txtEtc);
					txtEtc.setText( jsonObj.getString("u") + jsonObj.getString("l") + " " +
							jsonObj.getString("i") + " " + jsonObj.getString("k") );
					
					tag = jsonObj.getString("g") + jsonObj.getString("r");
					
					Button btnGo = (Button) vi.findViewById(R.id.btnGo);
					btnGo.setOnClickListener( HorseRaceScheduleFragment.this );
				}
				else if ( "ITEM2".equals( jsonObj.getString("TYPE") ))
				{
					vi = inflater.inflate(R.layout.list_schedule_header2, null);

					String[] dataHeaders = new String[]{"o", "n", "c", "x", "w", "k", "j", "m"};
					for ( int i = 0; i < dataHeaders.length; i++ )
					{
						TextView tv = (TextView) vi.findViewWithTag( dataHeaders[i] );
						tv.setText( jsonObj.getString( dataHeaders[i] ));
					}					
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
	
}