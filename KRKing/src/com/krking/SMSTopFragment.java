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

public class SMSTopFragment extends BaseFragment implements OnClickListener,OnItemClickListener{

	MainActivity mainActivity;
	int requestCode = 0;
	String strPeriod = "D";

	public SMSTopFragment( MainActivity main, int requestCode )
	{
		this.mainActivity = main;
		this.requestCode = requestCode;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_smstop, container, false);
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
			
			RelativeLayout rLayout2 = (RelativeLayout) getView().findViewById(R.id.rLayout2);
			LinearLayout lLayout2 = (LinearLayout) getView().findViewById(R.id.lLayout2);
			
			if ( requestCode == 1 )
			{
				lLayout2.setVisibility(View.INVISIBLE);
				rLayout2.setVisibility(View.VISIBLE);
				strPeriod = "D";
				execTransReturningString("KrSMS/krSMSBest.aspx?gbWK=" + strPeriod + "&gbDAY=1", requestCode, null);
			}
			else if ( requestCode == 2 )
			{
				lLayout2.setVisibility(View.VISIBLE);
				rLayout2.setVisibility(View.INVISIBLE);
				strPeriod = "W";
				execTransReturningString("KrSMS/krSMSBest.aspx?gbWK=" + strPeriod + "&gbCNT=1", requestCode, null);
			}
			else if ( requestCode == 3 )
			{
				lLayout2.setVisibility(View.VISIBLE);
				rLayout2.setVisibility(View.INVISIBLE);
				strPeriod = "M";
				execTransReturningString("KrSMS/krSMSBest.aspx?gbWK=" + strPeriod + "&gbCNT=1", requestCode, null);
			}
			
			Button btnDaysBest = (Button) getView().findViewById(R.id.btnDaysBest);
			btnDaysBest.setOnClickListener( this );
			Button btnWeeksBest = (Button) getView().findViewById(R.id.btnWeeksBest);
			btnWeeksBest.setOnClickListener( this );
			Button btnMonthsBest = (Button) getView().findViewById(R.id.btnMonthsBest);
			btnMonthsBest.setOnClickListener( this );
			
			setHeaderButton();
			
			Button btnFri = (Button) getView().findViewById(R.id.btnFri);
			btnFri.setOnClickListener( this );
			Button btnSat = (Button) getView().findViewById(R.id.btnSat);
			btnSat.setOnClickListener( this );
			Button btnSun = (Button) getView().findViewById(R.id.btnSun);
			btnSun.setOnClickListener( this );
			
			Button btnRefund = (Button) getView().findViewById(R.id.btnRefund);
			btnRefund.setOnClickListener( this );
			Button btnHit = (Button) getView().findViewById(R.id.btnHit);
			btnHit.setOnClickListener( this );
			Button btnDividends = (Button) getView().findViewById(R.id.btnDividends);
			btnDividends.setOnClickListener( this );
			Button btnCHorse = (Button) getView().findViewById(R.id.btnCHorse);
			btnCHorse.setOnClickListener( this );
			
			ArrayList<JSONObject> data = new ArrayList<JSONObject>();
			
			ListView listView = (ListView) getView().findViewById(R.id.list);
			listView.setDivider( null ); 
			listView.setDividerHeight(0);
			listView.setAdapter( new SMSItemAdapter( getActivity(), data ) );
			listView.setOnItemClickListener( this );
		}
		catch( Exception ex )
		{
			writeLog( ex.getMessage() );
		}
	}
	
	String strDate = "";
	String strDay = "금";
	
	public void setHeaderButton()
	{
		Button btnDaysBest = (Button) getView().findViewById(R.id.btnDaysBest);
		Button btnWeeksBest = (Button) getView().findViewById(R.id.btnWeeksBest);
		Button btnMonthsBest = (Button) getView().findViewById(R.id.btnMonthsBest);
		
		if ( requestCode == 1 )
		{
			btnDaysBest.setBackgroundResource(R.drawable.tab_menu_bg_01_on);
			btnWeeksBest.setBackgroundResource(R.drawable.tab_menu_bg_02_off);
			btnMonthsBest.setBackgroundResource(R.drawable.tab_menu_bg_03_off);
		}
		else if ( requestCode == 2 )
		{
			btnDaysBest.setBackgroundResource(R.drawable.tab_menu_bg_01_off);
			btnWeeksBest.setBackgroundResource(R.drawable.tab_menu_bg_02_on);
			btnMonthsBest.setBackgroundResource(R.drawable.tab_menu_bg_03_off);
		}
		else if ( requestCode == 3 )
		{
			btnDaysBest.setBackgroundResource(R.drawable.tab_menu_bg_01_off);
			btnWeeksBest.setBackgroundResource(R.drawable.tab_menu_bg_02_off);
			btnMonthsBest.setBackgroundResource(R.drawable.tab_menu_bg_03_on);
		}
	}
	
	@Override
	public void doPostTransaction( int requestCode, String result) {
	
		try
		{
			// TODO Auto-generated method stub
			super.doPostTransaction(result);
			
			JSONArray jsonAr = new JSONArray( result );
			
			ArrayList<JSONObject> data = new ArrayList<JSONObject>();			
			
			if ( requestCode == 1 )
				setDaysData(jsonAr, data);
			else
			{
				ArrayList<JSONObject> tempAr = Util.getArrayList( jsonAr );
				JSONObject titleObject = new JSONObject();
				titleObject.put("TYPE", "ITEM1");
				
				if ( requestCode == 2 || requestCode == 3 )
					titleObject.put("TITLE", "환수 Top");
				else if ( requestCode == 4 )
					titleObject.put("TITLE", "적중 Top");
				else if ( requestCode == 5 )
					titleObject.put("TITLE", "배당 Top");
				else if (requestCode == 6 )
					titleObject.put("TITLE", "축마 Top");
				
				tempAr.add(0, titleObject);
				
				for ( int j = 1; j < tempAr.size(); j++ )
				{
					JSONObject firstObj = tempAr.get(j);
					
					strDate = firstObj.getString("d");
					
					if ( j == 1 )
						firstObj.put("TYPE", "ITEM2");
					else
						firstObj.put("TYPE", "ITEM3");
				}
				
				data.addAll( tempAr );
			}
			
			ListView listView = (ListView) getView().findViewById(R.id.list);
			SMSItemAdapter adapter = (SMSItemAdapter) listView.getAdapter();
			adapter.setData(data);
			adapter.notifyDataSetChanged();
		}
		catch( Exception ex )
		{
			writeLog( ex.getMessage() );
		}
	}

	private void setDaysData(JSONArray jsonAr, ArrayList<JSONObject> data)
			throws Exception, JSONException {
		for ( int i = 0; i < jsonAr.length(); i++ )
		{
			
			ArrayList<JSONObject> tempAr = Util.getArrayList( jsonAr.getJSONArray(i) );
			
			JSONObject titleObject = new JSONObject();
			titleObject.put("TYPE", "ITEM1");
			
			if ( i == 0 )
				titleObject.put("TITLE", "환수 Top");
			else if ( i == 1 )
				titleObject.put("TITLE", "적중 Top");
			else if ( i == 2 )
				titleObject.put("TITLE", "배당 Top");
			else if ( i == 3 )
				titleObject.put("TITLE", "축마 Top");
			
			tempAr.add(0, titleObject);
			
			if ( i > 0 )
			{
				JSONObject seperatorObject = new JSONObject();
				seperatorObject.put("TYPE", "ITEM4");
				tempAr.add(0, seperatorObject);
			}
			
			//첫째 cell 만 사진 나오는 타이틀로.
			boolean bBig = true;
			
			for ( int j = 0; j < tempAr.size(); j++ )
			{
				JSONObject firstObj = tempAr.get(j);
				
				if ( firstObj.has("TYPE") && 
						("ITEM1".equals( firstObj.get("TYPE")) || "ITEM4".equals( firstObj.get("TYPE"))) )
					continue;
				
				strDate = firstObj.getString("d");
				
				if ( firstObj.getInt("o") == 1 && bBig )
				{
					firstObj.put("TYPE", "ITEM2");
					bBig = false;
				}
				else
					firstObj.put("TYPE", "ITEM3");
			}
			
			data.addAll( tempAr );
		}
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
	}

	public class SMSItemAdapter extends BaseAdapter {

		private Activity activity;
		private ArrayList<JSONObject> data;
		private LayoutInflater inflater=null;

		public SMSItemAdapter(Activity a, ArrayList<JSONObject> d) {
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
        			vi = inflater.inflate(R.layout.list_smstop_item1, null);
        			TextView txtHeaderName = (TextView) vi.findViewById(R.id.txtHeaderName );
        			txtHeaderName.setText( jsonObj.getString("TITLE") );
        			
        			TextView txtDate = (TextView) vi.findViewById(R.id.txtDate);
        			
        			if ( requestCode == 1 )
        			{
        				LinearLayout thumbnail = (LinearLayout) vi.findViewById(R.id.thumbnail);
        				thumbnail.setVisibility(ViewGroup.VISIBLE);
        				ImageView list_image = ( ImageView ) vi.findViewById(R.id.list_image);
        				
        				((RelativeLayout.LayoutParams)txtHeaderName.getLayoutParams() ).leftMargin = 0;
        				
        				if ( "금".equals( strDay ) )
        					list_image.setImageResource(R.drawable.prediction_fridaybox);
        				else if ( "토".equals( strDay ) )
        					list_image.setImageResource(R.drawable.prediction_saturdaybox);
        				else if ( "일".equals( strDay ) )
        					list_image.setImageResource(R.drawable.prediction_sundaybox);
        				
        				txtDate.setText( strDate + " (" + strDay + ")" );
        			}
        			else
        				txtDate.setText( strDate );
        		}
        		else if ( "ITEM2".equals( jsonObj.getString("TYPE") ))
        		{
        			vi = inflater.inflate(R.layout.list_smstop_item2, null);
        			
        			// Get singletone instance of ImageLoader
    				ImageLoader imageLoader = ImageLoader.getInstance();
    				// Initialize ImageLoader with configuration. Do it once.
    				imageLoader.init(ImageLoaderConfiguration.createDefault( activity ));
    				// Load and display image asynchronously

    				ImageView list_image = (ImageView) vi.findViewById(R.id.list_image);
    				
    				String imageURL = jsonObj.getString("i");
    				imageLoader.displayImage( imageURL, list_image);
        			
        			TextView txtName = (TextView) vi.findViewById(R.id.txtName);
        			txtName.setText( jsonObj.getString("n") );
        			TextView txtHitRate = (TextView) vi.findViewById(R.id.txtHitRate );
        			txtHitRate.setText( jsonObj.getString("p") );
        			TextView txtCity1 = (TextView) vi.findViewById(R.id.txtCity1);
        			txtCity1.setText( Util.getStringFromJSONObject( jsonObj,"r1n" ) );
        			TextView txtCity1Rate = (TextView) vi.findViewById(R.id.txtCity1Rate);
        			txtCity1Rate.setText( Util.getStringFromJSONObject( jsonObj,"r1v" ) );
        			TextView txtCity2 = (TextView) vi.findViewById(R.id.txtCity2);
        			txtCity2.setText( Util.getStringFromJSONObject( jsonObj,"r2n" ));
        			TextView txtCity2Rate = (TextView) vi.findViewById(R.id.txtCity2Rate);
        			txtCity2Rate.setText( Util.getStringFromJSONObject( jsonObj,"r2v") );
        			TextView txtCity3 = (TextView) vi.findViewById(R.id.txtCity3);
        			txtCity3.setText( Util.getStringFromJSONObject( jsonObj,"r3n" ) );
        			TextView txtCity3Rate = (TextView) vi.findViewById(R.id.txtCity3Rate);
        			txtCity3Rate.setText( Util.getStringFromJSONObject( jsonObj,"r3v" ) );
        		}
        		else if ( "ITEM3".equals( jsonObj.getString("TYPE") ))
        		{
        			vi = inflater.inflate(R.layout.list_smstop_item3, null);
        			
        			ImageView list_image = (ImageView) vi.findViewById(R.id.list_image);
        			
        			if ( jsonObj.getInt("o") < 4 )
        				list_image.setImageResource(R.drawable.sms_othercell_numberbox);
        			else
        				list_image.setImageResource(R.drawable.sms_othercell_numberbox2);
        			
        			TextView txtRanking = (TextView) vi.findViewById(R.id.txtRanking);
        			txtRanking.setText( jsonObj.getString("o") );
        			
        			TextView txtName = (TextView) vi.findViewById(R.id.txtName);
        			txtName.setText( jsonObj.getString("n") );
        			TextView txtHitRate = (TextView) vi.findViewById(R.id.txtHitRate );
        			txtHitRate.setText( jsonObj.getString("p") );
        		}
        		else if ( "ITEM4".equals( jsonObj.getString("TYPE") ))
        		{
        			vi = inflater.inflate(R.layout.list_smstop_item4, null);
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
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

		try
		{
			JSONObject jsonObj = (JSONObject) arg1.getTag();

			Intent intent = new Intent( getActivity(), SMSTopDetailActivity.class );
			intent.putExtra("param", jsonObj.toString());
			startActivity( intent );
		}
		catch( Exception ex )
		{
			writeLog( ex.getMessage() );
		}

	}

	@Override
	public void onClick(View arg0) {
		
		try
		{
			RelativeLayout rLayout2 = (RelativeLayout) getView().findViewById(R.id.rLayout2);
			LinearLayout lLayout2 = (LinearLayout) getView().findViewById(R.id.lLayout2);
			
			// TODO Auto-generated method stub
			if ( arg0.getId() == R.id.btnDaysBest )
			{
				lLayout2.setVisibility(View.INVISIBLE);
				rLayout2.setVisibility(View.VISIBLE);
				
				requestCode = 1;
				strDay = "금";
				strPeriod = "D";
				
				setHeaderButton();
				setWeekDay();
				
				execTransReturningString("KrSMS/krSMSBest.aspx?gbWK=" + strPeriod + "&gbDAY=1", requestCode, null);
			}
			else if ( arg0.getId() == R.id.btnWeeksBest )
			{
				lLayout2.setVisibility(View.VISIBLE);
				rLayout2.setVisibility(View.INVISIBLE);
				
				requestCode = 2;
				strPeriod = "W";
				
				setHeaderButton();
				
				execTransReturningString("KrSMS/krSMSBest.aspx?gbWK=" + strPeriod + "&gbCNT=1", requestCode, null);
			}
			else if ( arg0.getId() == R.id.btnMonthsBest )
			{
				lLayout2.setVisibility(View.VISIBLE);
				rLayout2.setVisibility(View.INVISIBLE);
				
				requestCode = 3;
				strPeriod = "M";
				
				setHeaderButton();
				
				execTransReturningString("KrSMS/krSMSBest.aspx?gbWK=" + strPeriod + "&gbCNT=1", requestCode, null);
			}
			else if ( arg0.getId() == R.id.btnFri )
			{
				requestCode = 1;
				strDay = "금";
				strPeriod = "D";
				
				setWeekDay();
				
				execTransReturningString("KrSMS/krSMSBest.aspx?gbWK=" + strPeriod + "&gbDAY=1", requestCode, null);
			}
			else if ( arg0.getId() == R.id.btnSat )
			{
				requestCode = 1;
				strDay = "토";
				strPeriod = "D";
				
				setWeekDay();
				
				execTransReturningString("KrSMS/krSMSBest.aspx?gbWK=" + strPeriod + "&gbDAY=2", requestCode, null);
			}
			else if ( arg0.getId() == R.id.btnSun )
			{
				requestCode = 1;
				strDay = "일";
				strPeriod = "D";
				
				setWeekDay();
				
				execTransReturningString("KrSMS/krSMSBest.aspx?gbWK=" + strPeriod + "&gbDAY=3", requestCode, null);
			}
			else if ( arg0.getId() == R.id.btnRefund )
			{
				Button btnRefund = (Button) getView().findViewById(R.id.btnRefund);
				btnRefund.setBackgroundResource(R.drawable.btn_bottm_01_on);
				Button btnHit = (Button) getView().findViewById(R.id.btnHit);
				btnHit.setBackgroundResource(R.drawable.btn_bottm_02_off);
				Button btnDividends = (Button) getView().findViewById(R.id.btnDividends);
				btnDividends.setBackgroundResource(R.drawable.btn_bottm_03_off);
				Button btnCHorse = (Button) getView().findViewById(R.id.btnCHorse);
				btnCHorse.setBackgroundResource(R.drawable.btn_bottm_04_off);
				
				execTransReturningString("KrSMS/krSMSBest.aspx?gbWK=" + strPeriod + "&gbCNT=1", requestCode, null);
			}
			else if ( arg0.getId() == R.id.btnHit )
			{
				Button btnRefund = (Button) getView().findViewById(R.id.btnRefund);
				btnRefund.setBackgroundResource(R.drawable.btn_bottm_01_off);
				Button btnHit = (Button) getView().findViewById(R.id.btnHit);
				btnHit.setBackgroundResource(R.drawable.btn_bottm_02_on);
				Button btnDividends = (Button) getView().findViewById(R.id.btnDividends);
				btnDividends.setBackgroundResource(R.drawable.btn_bottm_03_off);
				Button btnCHorse = (Button) getView().findViewById(R.id.btnCHorse);
				btnCHorse.setBackgroundResource(R.drawable.btn_bottm_04_off);
				
				requestCode = 4;
				execTransReturningString("KrSMS/krSMSBest.aspx?gbWK=" + strPeriod + "&gbCNT=2", requestCode, null);
			}
			else if ( arg0.getId() == R.id.btnDividends )
			{
				Button btnRefund = (Button) getView().findViewById(R.id.btnRefund);
				btnRefund.setBackgroundResource(R.drawable.btn_bottm_01_off);
				Button btnHit = (Button) getView().findViewById(R.id.btnHit);
				btnHit.setBackgroundResource(R.drawable.btn_bottm_02_off);
				Button btnDividends = (Button) getView().findViewById(R.id.btnDividends);
				btnDividends.setBackgroundResource(R.drawable.btn_bottm_03_on);
				Button btnCHorse = (Button) getView().findViewById(R.id.btnCHorse);
				btnCHorse.setBackgroundResource(R.drawable.btn_bottm_04_off);
				
				requestCode = 5;
				execTransReturningString("KrSMS/krSMSBest.aspx?gbWK=" + strPeriod + "&gbCNT=3", requestCode, null);
			}
			else if ( arg0.getId() == R.id.btnCHorse )
			{
				Button btnRefund = (Button) getView().findViewById(R.id.btnRefund);
				btnRefund.setBackgroundResource(R.drawable.btn_bottm_01_off);
				Button btnHit = (Button) getView().findViewById(R.id.btnHit);
				btnHit.setBackgroundResource(R.drawable.btn_bottm_02_off);
				Button btnDividends = (Button) getView().findViewById(R.id.btnDividends);
				btnDividends.setBackgroundResource(R.drawable.btn_bottm_03_off);
				Button btnCHorse = (Button) getView().findViewById(R.id.btnCHorse);
				btnCHorse.setBackgroundResource(R.drawable.btn_bottm_04_on);
				
				requestCode = 6;
				execTransReturningString("KrSMS/krSMSBest.aspx?gbWK=" + strPeriod + "&gbCNT=4", requestCode, null);
			}
			
		}
		catch( Exception ex )
		{
			writeLog( ex.getMessage() );
		}
	}
	
	public void setWeekDay()
	{
		Button btnFriday = (Button) getView().findViewById(R.id.btnFri);
		Button btnSat = (Button) getView().findViewById(R.id.btnSat);
		Button btnSun = (Button) getView().findViewById(R.id.btnSun);
		
		if ( "금".equals( strDay ) )
		{
			btnFriday.setBackgroundResource(R.drawable.btn_fri_on);
			btnSat.setBackgroundResource(R.drawable.btn_sat_off);
			btnSun.setBackgroundResource(R.drawable.btn_sun_off);
		}
		else if ( "토".equals( strDay ) )
		{
			btnFriday.setBackgroundResource(R.drawable.btn_fri_off);
			btnSat.setBackgroundResource(R.drawable.btn_sat_on);
			btnSun.setBackgroundResource(R.drawable.btn_sun_off);
		}
		else if ( "일".equals( strDay ) )
		{
			btnFriday.setBackgroundResource(R.drawable.btn_fri_off);
			btnSat.setBackgroundResource(R.drawable.btn_sat_off);
			btnSun.setBackgroundResource(R.drawable.btn_sun_on);
		}
		
		
	}
}