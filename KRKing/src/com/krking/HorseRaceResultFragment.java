package com.krking;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.common.Util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class HorseRaceResultFragment extends BaseFragment implements OnClickListener {

	MainActivity mainActivity;
	int requestCode = 0;
	String strPeriod = "D";

	public HorseRaceResultFragment( MainActivity main, int requestCode )
	{
		this.mainActivity = main;
		this.requestCode = requestCode;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_horserace_result, container, false);
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
			
			Button btnSeoul = (Button) getView().findViewById(R.id.btnSeoul);
			btnSeoul.setBackgroundResource(R.drawable.tab_menu_01_seoul_on);
			Button btnBusan = (Button) getView().findViewById(R.id.btnBusan);
			btnBusan.setBackgroundResource(R.drawable.tab_menu_02_busan_off);

			execTransReturningString("krRaceInfo/krRaceResultList.aspx?pGb=KC", requestCode, null);

			btnSeoul.setOnClickListener( this );
			btnBusan.setOnClickListener( this );

			ArrayList<JSONObject> data = new ArrayList<JSONObject>();

			ListView listView = (ListView) getView().findViewById(R.id.list);
			listView.setDivider( null ); 
			listView.setDividerHeight(0);
			listView.setAdapter( new HorseRaceResultItemAdapter( getActivity(), data ) );
		}
		catch( Exception ex )
		{
			writeLog( ex.getMessage() );
		}
	}

	String strDate = "";
	String strDay = "금";

	@Override
	public void doPostTransaction( int requestCode, String result) {

		try
		{
			// TODO Auto-generated method stub
			super.doPostTransaction(result);

			JSONArray jsonAr = new JSONArray( result );

			ArrayList<JSONObject> data = Util.getArrayList(jsonAr);

			ListView listView = (ListView) getView().findViewById(R.id.list);
			HorseRaceResultItemAdapter adapter = (HorseRaceResultItemAdapter) listView.getAdapter();
			adapter.setData(data);
			adapter.notifyDataSetChanged();
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

	public class HorseRaceResultItemAdapter extends BaseAdapter {

		private Activity activity;
		private ArrayList<JSONObject> data;
		private LayoutInflater inflater=null;

		public HorseRaceResultItemAdapter(Activity a, ArrayList<JSONObject> d) {
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

		public Object getItem(int position) {
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

				vi = inflater.inflate(R.layout.list_horserace_result, null);
				LinearLayout lLayout = (LinearLayout) vi.findViewById(R.id.lLayout);
				
				ImageView imgCity = (ImageView) vi.findViewById(R.id.imgCity);
				if ( "서울".equals( jsonObj.getString("r") ) )
					imgCity.setImageResource(R.drawable.btn_seoul);
				else if ( "부산".equals( jsonObj.getString("r") ) )
					imgCity.setImageResource(R.drawable.btn_busan);
				else if ( "제주".equals( jsonObj.getString("r") ) )
					imgCity.setImageResource(R.drawable.btn_jeju);

				TextView tvHeader = (TextView) vi.findViewWithTag("header");
				
				tvHeader.setText( jsonObj.getString("d") );
				
				int roundCount = Integer.parseInt( jsonObj.getString("t") );
				
				ImageView imgDay = (ImageView) vi.findViewById(R.id.imgDay);
				
				if ( "금".equals( jsonObj.getString("w") ) )
					imgDay.setImageResource(R.drawable.icon_fri_s);
				else if ( "토".equals( jsonObj.getString("w") ) )
					imgDay.setImageResource(R.drawable.icon_sat_s);
				else if ( "일".equals( jsonObj.getString("w") ) )
					imgDay.setImageResource(R.drawable.icon_sun_s);
				
				for ( int i = 0; i < roundCount; i++ )
				{
					LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					View v = inflater.inflate(R.layout.template, null);
					LinearLayout lPLayout = (LinearLayout) v.findViewById(R.id.lParent);

					Button btn = (Button) v.findViewById(R.id.btn_template);
					lPLayout.removeView( btn );
					lLayout.addView( btn );
					
					btn.setText( jsonObj.getString( "r" + (i+1) ) + "R");
					btn.setOnClickListener( HorseRaceResultFragment.this );
					btn.setTag( jsonObj.getString("r") + "|" + jsonObj.getString("d") + "|" + 
							jsonObj.getString("r" + (i+1) ) + "|" + jsonObj.getString("t") );
				}
				
				vi.setTag( jsonObj );

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
	public void onClick(View arg0) {

		try
		{
			Button btn = (Button) arg0;
			
			// TODO Auto-generated method stub
			if ( arg0.getId() == R.id.btnSeoul )
			{
				btn.setBackgroundResource(R.drawable.tab_menu_01_seoul_on);

				Button btnBusan = (Button) getView().findViewById(R.id.btnBusan);
				btnBusan.setBackgroundResource(R.drawable.tab_menu_02_busan_off);
				
				execTransReturningString("krRaceInfo/krRaceResultList.aspx?pGb=KC", requestCode, null);
			}
			else if ( arg0.getId() == R.id.btnBusan )
			{
				Button btnSeoul = (Button) getView().findViewById(R.id.btnSeoul);
				btnSeoul.setBackgroundResource(R.drawable.tab_menu_01_seoul_off);

				btn.setBackgroundResource(R.drawable.tab_menu_02_busan_on);
				
				execTransReturningString("krRaceInfo/krRaceResultList.aspx?pGb=BU", requestCode, null);
			}
			else
			{
				if ( arg0.getTag() != null )
				{
					Intent intent = new Intent( getActivity(), HorseRaceResultDetailActivity.class );
					intent.putExtra("param", btn.getTag().toString());
					startActivity( intent );
				}
			}
		}
		catch( Exception ex )
		{
			writeLog( ex.getMessage() );
		}
	}
}