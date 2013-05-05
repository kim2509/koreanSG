package com.krking;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.common.Util;
import com.common.VideoViewCustom;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class DawnActivity extends BaseActivity implements OnItemClickListener, OnCompletionListener, OnClickListener{

	int initialVideoWith = 0;
	int initialVideoHeight = 0;
	MediaController mediaController = null;
	VideoViewCustom videoView = null;
	
	int repeatCount = 1;
	int playedCount = 0;
	JSONObject selectedObject = null;
	
	boolean bSelectionPlayMode = false;
	int selectedPlayIndex = 0;
	
	String city = "KC";
	String reqDate = "";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dawn);

		Display display = getWindowManager().getDefaultDisplay(); 
		initialVideoWith = display.getWidth();  // deprecated
		initialVideoHeight = display.getHeight();  // deprecated
		
		videoView = (VideoViewCustom) findViewById(R.id.video);
		mediaController = new MediaController(this);
		mediaController.setAnchorView(videoView);
		videoView.setMediaController(mediaController);
		videoView.setDimensions( initialVideoWith, (int)( initialVideoWith *0.6));
		videoView.setOnCompletionListener( this );
		
		ImageView imgSelectAll = (ImageView) findViewById(R.id.imgSelectAll);
		imgSelectAll.setOnClickListener( DawnActivity.this );
		imgSelectAll.setTag("true");
		
		Button btnSelectPlay = (Button) findViewById(R.id.btnSelectPlay);
		btnSelectPlay.setOnClickListener( this );
		Button btnStopSelectPlay = (Button) findViewById(R.id.btnStopSelectPlay);
		btnStopSelectPlay.setOnClickListener( this );
		
		Button btnFirst = (Button) findViewById(R.id.btnFirst);
		btnFirst.setOnClickListener( this );
		Button btnSecond = (Button) findViewById(R.id.btnSecond);
		btnSecond.setOnClickListener( this );
		Button btnThird = (Button) findViewById(R.id.btnThird);
		btnThird.setOnClickListener( this );
		
		TextView txtPlayPrev = (TextView) findViewById(R.id.txtPlayPrev);
		txtPlayPrev.setOnClickListener( this );
		TextView txtPlayNext = (TextView) findViewById(R.id.txtPlayNext);
		txtPlayNext.setOnClickListener( this );
		
		ArrayList<JSONObject> data = new ArrayList<JSONObject>();

		ListView listView = (ListView) findViewById(R.id.list);
		listView.setDivider( null ); 
		listView.setDividerHeight(0);
		listView.setAdapter( new DawnItemAdapter( this, data ) );
		listView.setOnItemClickListener( this );
		
		execTransReturningString("krCast/krCastDawnList.aspx?cGb=" + city + "&cDate=" + reqDate + "&cPhoneGb=1", 1, null );
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_dawn, menu);
		return true;
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

		RelativeLayout rLayout = (RelativeLayout) findViewById(R.id.rLayout);
		LinearLayout lPanel = (LinearLayout) findViewById(R.id.lPanel);
		RelativeLayout rDates = (RelativeLayout) findViewById(R.id.rDates);
		
		VideoViewCustom videoView = (VideoViewCustom) findViewById(R.id.video);

		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {

			Display display = getWindowManager().getDefaultDisplay(); 
			int width = display.getWidth();  // deprecated
			int height = display.getHeight();  // deprecated
			
			rLayout.setVisibility(ViewGroup.GONE);
			lPanel.setVisibility(ViewGroup.GONE);
			rDates.setVisibility(ViewGroup.GONE);
			
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
			videoView.setDimensions(width, height);
			videoView.getHolder().setFixedSize(width, height);
			mediaController.setPadding(0, 0, 0, 0);

		} else {

			Display display = getWindowManager().getDefaultDisplay(); 
			int width = display.getWidth();  // deprecated
			int height = display.getHeight();  // deprecated

			rLayout.setVisibility(ViewGroup.VISIBLE);
			lPanel.setVisibility(ViewGroup.VISIBLE);
			rDates.setVisibility(ViewGroup.VISIBLE);

			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

			videoView.setDimensions( width, (int)(width*0.6) );
			videoView.getHolder().setFixedSize( width, height );

			mediaController.setPadding(0, 0, 0, rDates.getHeight() + lPanel.getHeight() );

		}
	}

	public void inquiryByDate( View v )
	{
		try
		{
			reqDate = v.getTag().toString();
			execTransReturningString("krCast/krCastDawnList.aspx?cGb=" + city + "&cDate=" + reqDate + "&cPhoneGb=1", 1, null );	
		}
		catch( Exception ex )
		{
			writeLog( ex.getMessage() );
		}
	}
	
	public void seoulClicked( View v )
	{
		try
		{
			reqDate = "";
			Button btnSeoul = (Button) v;
			btnSeoul.setBackgroundResource(R.drawable.menubtn_soul_on);
			Button btnBusan = (Button) findViewById(R.id.btnBusan);
			btnBusan.setBackgroundResource(R.drawable.menubtn_busan_off);
			
			city = "KC";
			
			execTransReturningString("krCast/krCastDawnList.aspx?cGb=" + city + "&cDate=" + reqDate + "&cPhoneGb=1", 1, null );	
		}
		catch( Exception ex )
		{
			writeLog( ex.getMessage() );
		}
	}
	
	public void busanClicked( View v )
	{
		try
		{
			Button btnBusan = (Button) v;
			btnBusan.setBackgroundResource(R.drawable.menubtn_busan_on);
			Button btnSeoul = (Button) findViewById(R.id.btnSeoul);
			btnSeoul.setBackgroundResource(R.drawable.menubtn_soul_off);
			
			reqDate = "";
			city = "BU";
			
			execTransReturningString("krCast/krCastDawnList.aspx?cGb=" + city + "&cDate=" + reqDate + "&cPhoneGb=1", 1, null );	
		}
		catch( Exception ex )
		{
			writeLog( ex.getMessage() );
		}
	}
	
	@Override
	public void doPostTransaction(int requestCode, String result) {

		try
		{
			// TODO Auto-generated method stub
			super.doPostTransaction(requestCode, result);

			RelativeLayout rDates = (RelativeLayout) findViewById(R.id.rDates);
			LinearLayout lPanel = (LinearLayout) findViewById(R.id.lPanel);
			mediaController.setPadding(0, 0, 0, rDates.getHeight() + lPanel.getHeight() );
			
			ArrayList<JSONObject> data = new ArrayList<JSONObject>();
			
			JSONObject jsonObj = new JSONObject( result );

			ArrayList<JSONObject> tempAr = Util.getArrayList( jsonObj.getJSONArray("MP4List"));
			for ( int i = 0; i < tempAr.size(); i++ )
			{
				JSONObject jsonItem = (JSONObject) tempAr.get(i);
				jsonItem.put("SELECTED", true );
				jsonItem.put("PLAYING", false );
				
				if ( "".equals( reqDate ) )
					reqDate = jsonItem.getString("d");
			}
			
			data.addAll( tempAr );
			
			ListView listView = (ListView) findViewById(R.id.list);
			DawnItemAdapter adapter = (DawnItemAdapter) listView.getAdapter();
			adapter.setData(data);
			adapter.notifyDataSetChanged();

			LinearLayout lLayout2 = (LinearLayout) findViewById(R.id.lLayout2 );
			lLayout2.removeAllViews();
			
			JSONArray jsonAr = jsonObj.getJSONArray( "DateList" );
			
			for ( int i = 0; i < jsonAr.length(); i++ )
			{
				JSONObject jsonItem = jsonAr.getJSONObject( i );
				
				LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				View vi = inflater.inflate(R.layout.template, null);
				LinearLayout lPLayout = (LinearLayout) vi.findViewById(R.id.lParent);

				Button btn = (Button) vi.findViewById(R.id.dawn_btn_template);
				lPLayout.removeView( btn );
				
				lLayout2.addView( btn );
				
				btn.setText( jsonItem.getString("d") );
				btn.setTextColor(Color.WHITE);
				
				btn.setOnClickListener( this );
				btn.setTag( jsonItem.getString("k") );
				
				if ( reqDate.equals( jsonItem.getString("k")) )
					btn.setBackgroundResource(R.drawable.bottom_btn_on);
			}
		}
		catch( Exception ex )
		{
			writeLog( ex.getMessage() );
		}
	}
	
	public class DawnItemAdapter extends BaseAdapter {

		private Activity activity;
		private ArrayList<JSONObject> data;
		private LayoutInflater inflater=null;

		public DawnItemAdapter(Activity a, ArrayList<JSONObject> d) {
			activity = a;
			data=d;
			inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		public void setData( ArrayList<JSONObject> data )
		{
			this.data = data;
		}

		public ArrayList<JSONObject> getData()
		{
			return data;
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

				JSONObject jsonObj = getItem(position);

				vi = inflater.inflate(R.layout.list_dawn_item, null);

				String[] items = {"j", "h", "g", "t", "e"};

				ImageView iv = (ImageView) vi.findViewById(R.id.imgSelect);
				iv.setOnClickListener( DawnActivity.this );
				
				if ( jsonObj.getBoolean("SELECTED") )
					iv.setImageResource(R.drawable.icon_check_on);
				else
					iv.setImageResource(R.drawable.icon_check_off);
				
				LinearLayout l = (LinearLayout) vi.findViewById(R.id.lParent);
				
				if ( jsonObj.getBoolean("PLAYING") )
					l.setBackgroundResource(R.drawable.table_line_on );
				else
					l.setBackgroundResource(R.drawable.table_line_off );
				
				for ( int i = 0; i < items.length; i++ )
				{
					TextView tv2 = (TextView) vi.findViewWithTag( items[i] );
					tv2.setText( jsonObj.getString( items[i] ) );	
				}

				ImageView imgMerge = (ImageView) vi.findViewById(R.id.imgMerge);
				
				if ( "병합".equals( jsonObj.getString("b") ) )
					imgMerge.setVisibility(ViewGroup.VISIBLE);
				else
					imgMerge.setVisibility(ViewGroup.GONE);
				
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
		// TODO Auto-generated method stub
		try
		{
			LinearLayout l = (LinearLayout) arg1.findViewById(R.id.lParent);
			l.setBackgroundResource(R.drawable.table_line_on );
			
			ListView listView = (ListView) findViewById(R.id.list);
			DawnItemAdapter adapter = (DawnItemAdapter) listView.getAdapter();

			ArrayList<JSONObject> data = adapter.getData();
			for ( int i = 0; i < data.size(); i++ )
			{
				data.get(i).put("PLAYING", false);
			}
			
			JSONObject jsonObj = (JSONObject) arg1.getTag();
			jsonObj.put("PLAYING", true );
			adapter.notifyDataSetChanged();
			
			writeLog( jsonObj.toString() );
			
			TextView txtTitle = (TextView) findViewById(R.id.txtTitle);
			txtTitle.setText( jsonObj.getString("i") );
			
			if ( Util.isSimulator() )
			{
				showToastMessage("시뮬레이터에선 동영상 재생 불가.");
				return;
			}
			
			selectedObject = jsonObj;
			
			videoView.stopPlayback();
			videoView.setVideoURI(Uri.parse( jsonObj.getString("p") ));
			videoView.start();
			
			playedCount = 0;
		}
		catch( Exception ex )
		{
			writeLog(ex.getMessage());
		}
	}

	@Override
	public void onCompletion(MediaPlayer arg0) {
		
		try
		{
			// TODO Auto-generated method stub
			playedCount++;
			
			if ( repeatCount > playedCount )
			{
				videoView.stopPlayback();
				videoView.setVideoURI(Uri.parse( selectedObject.getString("p") ));
				videoView.start();
				TextView txtTitle = (TextView) findViewById(R.id.txtTitle);
				txtTitle.setText( selectedObject.getString("h") );
				
				showToastMessage( (playedCount + 1 ) + "회 째 반복합니다.");
				return;
			}
			
			if ( bSelectionPlayMode )
			{
				ListView listView = (ListView) findViewById(R.id.list);
				DawnItemAdapter adapter = (DawnItemAdapter) listView.getAdapter();
				ArrayList<JSONObject> data = adapter.getData();
				
				for ( int i = selectedPlayIndex + 1 ; i < data.size(); i++ )
				{
					if ( data.get(i).getBoolean("SELECTED") )
					{
						showToastMessage( data.get(i).getString("h") + "를 재생합니다.");
						
						selectedPlayIndex = i;
						selectedObject = data.get(i);
						videoView.stopPlayback();
						videoView.setVideoURI(Uri.parse( data.get(i).getString("p") ));
						videoView.start();
						playedCount = 0;
						
						TextView txtTitle = (TextView) findViewById(R.id.txtTitle);
						txtTitle.setText( selectedObject.getString("h") );
						
						break;
					}
				}
			}
		}
		catch( Exception ex )
		{
			writeLog( ex.getMessage() );
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		try
		{
			if ( v.getId() == R.id.btnSelectPlay )
			{
				ListView listView = (ListView) findViewById(R.id.list);
				DawnItemAdapter adapter = (DawnItemAdapter) listView.getAdapter();

				int selectedCount = 0;
				
				ArrayList<JSONObject> data = adapter.getData();
				for ( int i = 0; i < data.size(); i++ )
				{
					if ( data.get(i).getBoolean("SELECTED") )
					{
						if ( Util.isSimulator() == false )
						{
							selectedPlayIndex = i;
							selectedObject = data.get(i);
							videoView.stopPlayback();
							videoView.setVideoURI(Uri.parse( data.get(i).getString("p") ));
							videoView.start();
							TextView txtTitle = (TextView) findViewById(R.id.txtTitle);
							txtTitle.setText( selectedObject.getString("h") );
							playedCount = 0;
						}
						
						selectedCount++;
						break;
					}
				}
				
				if ( selectedCount == 0 )
				{
					showOKDialog("재생할 항목을 선택해 주십시오.", null );
					return;
				}
				
				RelativeLayout r1 = (RelativeLayout) findViewById(R.id.rPanel1 );
				r1.setVisibility(ViewGroup.VISIBLE);
				RelativeLayout r2 = (RelativeLayout) findViewById(R.id.rPanel2 );
				r2.setVisibility(ViewGroup.GONE);
				
				LinearLayout lListHeader = (LinearLayout) findViewById(R.id.lListHeader);
				lListHeader.setVisibility(ViewGroup.INVISIBLE);
				ListView list = (ListView) findViewById(R.id.list);
				list.setVisibility(ViewGroup.INVISIBLE);
				RelativeLayout rDates = (RelativeLayout) findViewById(R.id.rDates);
				rDates.setVisibility(ViewGroup.INVISIBLE);
				
				bSelectionPlayMode = true;
			}
			else if ( v.getId() == R.id.txtPlayPrev )
			{
				ListView listView = (ListView) findViewById(R.id.list);
				DawnItemAdapter adapter = (DawnItemAdapter) listView.getAdapter();

				int selectedCount = 0;
				
				ArrayList<JSONObject> data = adapter.getData();
				for ( int i = selectedPlayIndex - 1; i >= 0; i-- )
				{
					if ( data.get(i).getBoolean("SELECTED") )
					{
						selectedPlayIndex = i;
						selectedObject = data.get(i);
						showToastMessage( data.get(i).getString("h") + "를 재생합니다.");
						
						if ( Util.isSimulator() == false )
						{
							videoView.stopPlayback();
							videoView.setVideoURI(Uri.parse( data.get(i).getString("p") ));
							videoView.start();
							TextView txtTitle = (TextView) findViewById(R.id.txtTitle);
							txtTitle.setText( selectedObject.getString("h") );
							playedCount = 0;
						}
						
						selectedCount++;
						break;
					}
				}
				
				if ( selectedCount == 0 )
				{
					showOKDialog("이전 항목이 존재하지 않습니다.", null );
					return;
				}
			}
			else if ( v.getId() == R.id.txtPlayNext )
			{
				ListView listView = (ListView) findViewById(R.id.list);
				DawnItemAdapter adapter = (DawnItemAdapter) listView.getAdapter();

				int selectedCount = 0;
				
				ArrayList<JSONObject> data = adapter.getData();
				for ( int i = selectedPlayIndex + 1 ; i < data.size(); i++ )
				{
					if ( data.get(i).getBoolean("SELECTED") )
					{
						selectedPlayIndex = i;
						selectedObject = data.get(i);
						showToastMessage( data.get(i).getString("h") + "를 재생합니다.");
						
						if ( Util.isSimulator() == false )
						{
							videoView.stopPlayback();
							videoView.setVideoURI(Uri.parse( data.get(i).getString("p") ));
							videoView.start();
							TextView txtTitle = (TextView) findViewById(R.id.txtTitle);
							txtTitle.setText( selectedObject.getString("h") );
							playedCount = 0;
						}
						
						selectedCount++;
						break;
					}
				}
				
				if ( selectedCount == 0 )
				{
					showOKDialog("다음 항목이 존재하지 않습니다.", null );
					return;
				}
			}
			else if ( v.getId() == R.id.btnStopSelectPlay )
			{
				if ( videoView != null && videoView.isPlaying() )
					videoView.stopPlayback();
				
				RelativeLayout r1 = (RelativeLayout) findViewById(R.id.rPanel1 );
				r1.setVisibility(ViewGroup.GONE);
				RelativeLayout r2 = (RelativeLayout) findViewById(R.id.rPanel2 );
				r2.setVisibility(ViewGroup.VISIBLE);
				
				LinearLayout lListHeader = (LinearLayout) findViewById(R.id.lListHeader);
				lListHeader.setVisibility(ViewGroup.VISIBLE);
				ListView list = (ListView) findViewById(R.id.list);
				list.setVisibility(ViewGroup.VISIBLE);
				RelativeLayout rDates = (RelativeLayout) findViewById(R.id.rDates);
				rDates.setVisibility(ViewGroup.VISIBLE);
				
				bSelectionPlayMode = false;
			}
			else if ( v.getId() == R.id.btnFirst )
			{
				Button btnFirst = (Button) findViewById(R.id.btnFirst);
				btnFirst.setBackgroundResource(R.drawable.btn_once_on);
				Button btnSecond = (Button) findViewById(R.id.btnSecond);
				btnSecond.setBackgroundResource(R.drawable.btn_twice_off);
				Button btnThird = (Button) findViewById(R.id.btnThird);
				btnThird.setBackgroundResource(R.drawable.btn_thrice_off);
				
				repeatCount = 1;
			}
			else if ( v.getId() == R.id.btnSecond )
			{
				Button btnFirst = (Button) findViewById(R.id.btnFirst);
				btnFirst.setBackgroundResource(R.drawable.btn_once_off);
				Button btnSecond = (Button) findViewById(R.id.btnSecond);
				btnSecond.setBackgroundResource(R.drawable.btn_twice_on);
				Button btnThird = (Button) findViewById(R.id.btnThird);
				btnThird.setBackgroundResource(R.drawable.btn_thrice_off);
				
				repeatCount = 2;
			}
			else if ( v.getId() == R.id.btnThird )
			{
				Button btnFirst = (Button) findViewById(R.id.btnFirst);
				btnFirst.setBackgroundResource(R.drawable.btn_once_off);
				Button btnSecond = (Button) findViewById(R.id.btnSecond);
				btnSecond.setBackgroundResource(R.drawable.btn_twice_off);
				Button btnThird = (Button) findViewById(R.id.btnThird);
				btnThird.setBackgroundResource(R.drawable.btn_thrice_on);
				
				repeatCount = 3;
			}
			else if ( v.getId() == R.id.imgSelectAll )
			{
				ImageView iv = (ImageView) v;
				
				boolean bSelected = false;
				
				if ( "false".equals( iv.getTag() ) )
				{
					bSelected = true;
					iv.setTag("true");
					iv.setImageResource(R.drawable.icon_check_on);
				}
				else
				{
					iv.setTag("false");
					bSelected = false;
					iv.setImageResource(R.drawable.icon_check_off);
				}
				
				ListView listView = (ListView) findViewById(R.id.list);
				DawnItemAdapter adapter = (DawnItemAdapter) listView.getAdapter();

				ArrayList<JSONObject> data = adapter.getData();
				for ( int i = 0; i < data.size(); i++ )
				{
					data.get(i).put("SELECTED", bSelected );
				}
				
				adapter.notifyDataSetChanged();
			}
			else if ( v.getId() == R.id.imgSelect )
			{
				JSONObject jsonObj = (JSONObject) ((LinearLayout)v.getParent().getParent().getParent()).getTag();
				
				ImageView iv = (ImageView) v;
				
				if ( jsonObj.getBoolean("SELECTED"))
				{
					jsonObj.put("SELECTED", false );
					iv.setImageResource(R.drawable.icon_check_off);
				}
				else
				{
					jsonObj.put("SELECTED", true );
					iv.setImageResource(R.drawable.icon_check_on);
				}	
			}
			else if ( v.getId() == R.id.dawn_btn_template )
			{
				LinearLayout lLayout2 = (LinearLayout) findViewById(R.id.lLayout2 );
				for ( int i = 0; i < lLayout2.getChildCount(); i++ )
				{
					Button btnTemp = (Button) lLayout2.getChildAt(i);
					btnTemp.setBackgroundResource(R.drawable.bottom_btn_off);
				}
				
				Button btn = (Button) v;
				btn.setBackgroundResource(R.drawable.bottom_btn_on);
				inquiryByDate( v );
			}
		}
		catch( Exception ex )
		{
			writeLog( ex.getMessage() );
		}
	}
}
