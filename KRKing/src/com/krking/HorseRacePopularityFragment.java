package com.krking;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

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
		return inflater.inflate(R.layout.fragment_horserace_popularity, container, false);
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
			
			if ( requestCode == 1 )
			{
				execTransReturningString("KrPopular/krPopular.aspx?dayGb=1", requestCode, null);
			}
			else if ( requestCode == 2 )
			{
				execTransReturningString("KrPopular/krPopular.aspx?dayGb=2", requestCode, null);
			}
			else if ( requestCode == 3 )
			{
				execTransReturningString("KrPopular/krPopular.aspx?dayGb=3", requestCode, null);
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
	
	String strDate = "";
	
	@Override
	public void doPostTransaction( int requestCode, String result) {
	
		try
		{
			// TODO Auto-generated method stub
			super.doPostTransaction(result);
			
			JSONArray jsonAr = new JSONArray( result );
			
			LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			LinearLayout lLayout = (LinearLayout) getView().findViewById(R.id.lLayout );
			
			lLayout.removeAllViews();
			
			strDate = jsonAr.getJSONObject(0).getString("c");
			
			String[] dataHeaders = new String[]{"o", "n", "k", "p1", "p2", "p3", "p4", "p5", "t", "w"};
			int[] width = new int[]{30, 130, 90, 30, 30, 30, 30, 30, 70, 70 };
			
			showPopularity(jsonAr.getJSONArray(1), inflater, lLayout, "#D0FF31", dataHeaders, width );

			dataHeaders = new String[]{"o", "n", "c", "m"};
			width = new int[]{20, 80, 420, 20 };
			
			showPopularity(jsonAr.getJSONArray(2), inflater, lLayout, "#8B4B22", dataHeaders, width );
			
			LinearLayout lLayout2 = (LinearLayout) getView().findViewById(R.id.lLayout2 );
			lLayout2.removeAllViews();
			
			jsonAr = jsonAr.getJSONArray( 3 );
			
			for ( int i = 0; i < jsonAr.length(); i++ )
			{
				JSONObject jsonItem = jsonAr.getJSONObject( i );
				
				Button btn = new Button( getActivity() );
				lLayout2.addView( btn );
				LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) btn.getLayoutParams();
				lp.width = ViewGroup.LayoutParams.WRAP_CONTENT;
				lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
				btn.setText( jsonItem.getString("p") + " " + jsonItem.getString("r"));
				btn.setOnClickListener( this );
				btn.setTag( jsonItem );
			}
		}
		catch( Exception ex )
		{
			writeLog( ex.getMessage() );
		}
	}

	private void showPopularity(JSONArray jsonAr, LayoutInflater inflater,
			LinearLayout lLayout, String headerColor, String[] dataHeaders, int[] width ) throws JSONException, Exception {
		View vi = inflater.inflate(R.layout.horserace_popularity_inner_view, null);
		TableLayout tl = (TableLayout) vi.findViewWithTag("table");
		LinearLayout.LayoutParams tlp = (LinearLayout.LayoutParams) tl.getLayoutParams();
		tlp.setMargins(0, 0, 0, 1);
		lLayout.addView( vi );
		
		TextView tvHeader = (TextView) vi.findViewWithTag("header");
		tvHeader.setBackgroundColor(Color.parseColor( headerColor ) );
		
		for ( int i = 0; i < jsonAr.length();i++ )
		{
			JSONObject jsonItem = jsonAr.getJSONObject(i);
			TableRow trItem = getTableRow(tl, 0, 1, 0, 0 );
			String[] d = getDataFromJSONObject( dataHeaders, jsonItem );
			getTableCells(trItem, d, 0, 0, 0, 0, width, "#ffffff" );
		}
	}
	
	private TableRow getTableRow(TableLayout tl, int topMargin, int bottomMargin, int rightMargin, int leftMargin ) {
		TableRow tr = new TableRow( getActivity() );
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
			TextView tv = new TextView( getActivity() );
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
				execTransReturningString("KrPopular/krPopular.aspx?dayGb=" + requestCode, requestCode, null);
			}
			else if ( arg0.getId() == R.id.btnSat )
			{
				requestCode = 2;
				execTransReturningString("KrPopular/krPopular.aspx?dayGb=" + requestCode, requestCode, null);
			}
			else if ( arg0.getId() == R.id.btnSun )
			{
				requestCode = 3;
				execTransReturningString("KrPopular/krPopular.aspx?dayGb=" + requestCode, requestCode, null);
			}
			else
			{
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
}