package com.krking;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.common.Util;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class HorseRacePopularityFragment extends BaseFragment implements OnClickListener{

	MainActivity mainActivity;
	int requestCode = 0;

	public HorseRacePopularityFragment( MainActivity main, int requestCode )
	{
		this.mainActivity = main;
		this.requestCode = requestCode;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View v = inflater.inflate(R.layout.fragment_horserace_popularity, container, false);

		return v;
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
			listView.setAdapter( new PopularityItemAdapter( getActivity(), data ) );

			setDayButton();

			if ( requestCode == 1 )
				execTransReturningString("KrPopular/krPopular.aspx?dayGb=1", requestCode, null);
			else if ( requestCode == 2 )
				execTransReturningString("KrPopular/krPopular.aspx?dayGb=2", requestCode, null);
			else if ( requestCode == 3 )
				execTransReturningString("KrPopular/krPopular.aspx?dayGb=3", requestCode, null);

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

	String strDate = "";

	@Override
	public void doPostTransaction( int requestCode, String result) {

		try
		{
			// TODO Auto-generated method stub
			super.doPostTransaction(result);

			ArrayList<JSONObject> data = new ArrayList<JSONObject>();

			JSONArray jsonAr = new JSONArray( result );

			JSONObject jsonItem = new JSONObject();
			jsonItem.put("TYPE", "ITEM1");
			jsonItem.put("INFO", jsonAr.getJSONObject(0));
			data.add( jsonItem );

			ArrayList<JSONObject> tempAr = Util.getArrayList( jsonAr.getJSONArray(1) );
			for ( int j = 0; j < tempAr.size(); j++ )
			{
				JSONObject firstObj = tempAr.get(j);
				firstObj.put("TYPE", "ITEM2");
			}

			data.addAll( tempAr );

			jsonItem = new JSONObject();
			jsonItem.put("TYPE", "ITEM3");
			data.add( jsonItem );

			tempAr = Util.getArrayList( jsonAr.getJSONArray(2) );
			for ( int j = 0; j < tempAr.size(); j++ )
			{
				JSONObject firstObj = tempAr.get(j);
				firstObj.put("TYPE", "ITEM4");
			}

			data.addAll( tempAr );

			ListView listView = (ListView) getView().findViewById(R.id.list);
			PopularityItemAdapter adapter = (PopularityItemAdapter) listView.getAdapter();
			adapter.setData(data);
			adapter.notifyDataSetChanged();

			strDate = jsonAr.getJSONObject(0).getString("c");

			LinearLayout lLayout2 = (LinearLayout) getView().findViewById(R.id.lLayout2 );
			lLayout2.removeAllViews();

			JSONObject headerInfo = jsonAr.getJSONObject(0);
			
			jsonAr = jsonAr.getJSONArray( 3 );

			for ( int i = 0; i < jsonAr.length(); i++ )
			{
				jsonItem = jsonAr.getJSONObject( i );

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

				if ( headerInfo.getString("g").equals( jsonItem.getString("p")) &&
						headerInfo.getString("r").equals( jsonItem.getString("r")))
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

	public String[] getDataFromJSONObject( String[] dataHeaders,JSONObject data ) throws Exception
	{
		String[] d = new String[dataHeaders.length];
		for ( int i = 0; i < dataHeaders.length; i++ )
		{
			d[i] = data.getString( dataHeaders[i] );
		}

		return d;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
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

	@Override
	public void onClick(View arg0) {

		try
		{
			// TODO Auto-generated method stub
			if ( arg0.getId() == R.id.btnFri )
			{
				requestCode = 1;
				setDayButton();
				execTransReturningString("KrPopular/krPopular.aspx?dayGb=" + requestCode, requestCode, null);
			}
			else if ( arg0.getId() == R.id.btnSat )
			{
				requestCode = 2;
				setDayButton();
				execTransReturningString("KrPopular/krPopular.aspx?dayGb=" + requestCode, requestCode, null);
			}
			else if ( arg0.getId() == R.id.btnSun )
			{
				requestCode = 3;
				setDayButton();
				execTransReturningString("KrPopular/krPopular.aspx?dayGb=" + requestCode, requestCode, null);
			}
			else if ( arg0.getId() == R.id.btnGo )
			{
				mainActivity.goToHorseRaceSchedule( requestCode );
			}
			else
			{
				LinearLayout lLayout2 = (LinearLayout) getView().findViewById(R.id.lLayout2 );
				for ( int i = 0; i < lLayout2.getChildCount(); i++ )
				{
					Button btn = (Button) lLayout2.getChildAt(i);
					btn.setTextColor(Color.GRAY);
					btn.setBackgroundResource(R.drawable.btn_r_bg_off);
				}
				
				Button btn = (Button) arg0;
				btn.setBackgroundResource(R.drawable.btn_r_bg_on);
				btn.setTextColor(Color.WHITE);
				
				if ( arg0.getTag() != null )
				{
					JSONObject jsonItem = (JSONObject) arg0.getTag();
					execTransReturningString("KrPopular/krPopular.aspx?pDate=" + strDate + "&pGb=" + jsonItem.getString("g") + 
							"&pRound=" + jsonItem.getString("r") , requestCode, null);
				}
			}
		}
		catch( Exception ex )
		{
			writeLog( ex.getMessage() );
		}
	}

	public class PopularityItemAdapter extends BaseAdapter {

		private Activity activity;
		private ArrayList<JSONObject> data;
		private LayoutInflater inflater=null;

		public PopularityItemAdapter(Activity a, ArrayList<JSONObject> d) {
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

				if ( "ITEM1".equals( jsonObj.getString("TYPE") ))
				{
					vi = inflater.inflate(R.layout.list_popularity_header1, null);

					ImageView iv = (ImageView) vi.findViewWithTag("city");
					JSONObject jsonInfo = jsonObj.getJSONObject("INFO");

					if ( "서울".equals( jsonInfo.getString("g") ) )
						iv.setImageResource(R.drawable.btn_seoul);
					else if ( "부산".equals( jsonInfo.getString("g") ) )
						iv.setImageResource(R.drawable.btn_busan);
					else if ( "제주".equals( jsonInfo.getString("g") ) )
						iv.setImageResource(R.drawable.btn_jeju);

					TextView tv = (TextView) vi.findViewById(R.id.txtDate);
					tv.setText( jsonInfo.getString("d") );

					ImageView ivDay = (ImageView) vi.findViewWithTag("day");

					if ( "금".equals( jsonInfo.getString("w") ) )
						ivDay.setImageResource(R.drawable.btn_fri);
					else if ( "토".equals( jsonInfo.getString("w") ) )
						ivDay.setImageResource(R.drawable.btn_sat);
					else if ( "일".equals( jsonInfo.getString("w") ) )
						ivDay.setImageResource(R.drawable.btn_sun);

					TextView txtTime = (TextView) vi.findViewById(R.id.txtTime);
					txtTime.setText( "(발주:" + jsonInfo.getString("t") + ")");

					TextView txtRound = (TextView) vi.findViewById(R.id.txtRound);
					txtRound.setText( jsonInfo.getString("r") + "R");

					TextView txtEtc = (TextView) vi.findViewById(R.id.txtEtc);
					txtEtc.setText( jsonInfo.getString("l") + " " + jsonInfo.getString("u") +
							jsonInfo.getString("i") + " " + jsonInfo.getString("k") + " " + jsonInfo.getString("j"));
					
					Button btnGo = (Button) vi.findViewById(R.id.btnGo);
					btnGo.setOnClickListener( HorseRacePopularityFragment.this );
				}
				else if ( "ITEM2".equals( jsonObj.getString("TYPE") ))
				{
					vi = inflater.inflate(R.layout.list_popularity_header2, null);

					String[] dataHeaders = new String[]{"o", "n", "k", "p1", "p2", "p3", "p4", "p5", "t", "w"};
					for ( int i = 0; i < dataHeaders.length; i++ )
					{
						TextView tv = (TextView) vi.findViewWithTag( dataHeaders[i] );
						tv.setText( jsonObj.getString( dataHeaders[i] ));
					}

					String percent = jsonObj.getString("v");
					percent = percent.substring(0,  percent.length() - 1 );
					ImageView img = (ImageView) vi.findViewWithTag("percent");

					img.getLayoutParams().width = (int) ( 0.01 * Double.parseDouble( percent ) * (double) img.getLayoutParams().width );

					TextView tv = (TextView) vi.findViewWithTag("percentText");
					tv.setText( jsonObj.getString("v") );
				}
				else if ( "ITEM3".equals( jsonObj.getString("TYPE") ))
				{
					vi = inflater.inflate(R.layout.list_popularity_header3, null);
				}
				else if ( "ITEM4".equals( jsonObj.getString("TYPE") ))
				{
					vi = inflater.inflate(R.layout.list_popularity_header4, null);

					String[] dataHeaders = new String[]{"o", "n", "c", "m"};

					for ( int i = 0; i < dataHeaders.length; i++ )
					{
						TextView tv = (TextView) vi.findViewWithTag( dataHeaders[i] );
						tv.setText( jsonObj.getString( dataHeaders[i] ));
					}
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
}