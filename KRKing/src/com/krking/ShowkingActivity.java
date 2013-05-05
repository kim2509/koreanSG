package com.krking;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.common.Util;
import com.common.VideoViewCustom;
import com.krking.HorseRaceResultFragment.HorseRaceResultItemAdapter;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Point;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

public class ShowkingActivity extends BaseActivity implements OnItemClickListener, OnCompletionListener{

	boolean bPlaying = false;
	int initialVideoWith = 0;
	int initialVideoHeight = 0;
	MediaController mediaController = null;
	VideoViewCustom videoView = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_showking);

		Display display = getWindowManager().getDefaultDisplay(); 
		initialVideoWith = display.getWidth();  // deprecated
		initialVideoHeight = display.getHeight();  // deprecated
		
		videoView = (VideoViewCustom) findViewById(R.id.video);
		mediaController = new MediaController(this);
		mediaController.setAnchorView(videoView);
		videoView.setMediaController(mediaController);
		videoView.setDimensions( initialVideoWith, (int)( initialVideoWith *0.6));
		videoView.setOnCompletionListener( this );
		
		writeLog("height:" + initialVideoHeight + " 0.6:" + (int)( initialVideoWith *0.6) + " con height:" + mediaController.getHeight() );
		
		ArrayList<JSONObject> data = new ArrayList<JSONObject>();

		ListView listView = (ListView) findViewById(R.id.list);
		listView.setDivider( null ); 
		listView.setDividerHeight(0);
		listView.setAdapter( new ShockingItemAdapter( this, data ) );
		listView.setOnItemClickListener( this );

		int requestCode = Integer.parseInt( getIntent().getExtras().get("requestCode").toString() );

		Button btnThisWeek = (Button) findViewById(R.id.btnThisWeek);
		Button btnLastWeek = (Button) findViewById(R.id.btnLastWeek);
		
		if ( requestCode == 1 )
		{
			btnThisWeek.setBackgroundResource(R.drawable.btn_this_week_on);
		}
		else if ( requestCode == 2 )
		{
			btnLastWeek.setBackgroundResource(R.drawable.btn_last_week_on);
		}
		
		execTransReturningString("krCast/krCastList.aspx?cGb=" + requestCode + "&cPGB=1", requestCode, null );
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		LinearLayout lToolbar = (LinearLayout) findViewById(R.id.lToolbar);
		mediaController.setPadding(0, 0, 0, lToolbar.getHeight() );
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//        getMenuInflater().inflate(R.menu.activity_terms_ncontract, menu);
		return true;
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

		RelativeLayout rLayout = (RelativeLayout) findViewById(R.id.rLayout);
		LinearLayout lPanel = (LinearLayout) findViewById(R.id.lPanel);
		LinearLayout lToolbar = (LinearLayout) findViewById(R.id.lToolbar);
		
		VideoViewCustom videoView = (VideoViewCustom) findViewById(R.id.video);

		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {

			Display display = getWindowManager().getDefaultDisplay(); 
			int width = display.getWidth();  // deprecated
			int height = display.getHeight();  // deprecated
			
			rLayout.setVisibility(ViewGroup.GONE);
			lPanel.setVisibility(ViewGroup.GONE);
			lToolbar.setVisibility(ViewGroup.GONE);
			
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
			lToolbar.setVisibility(ViewGroup.VISIBLE);

			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

			videoView.setDimensions( width, (int)(width*0.6) );
			videoView.getHolder().setFixedSize( width, height );

			mediaController.setPadding(0, 0, 0, lToolbar.getHeight() + lPanel.getHeight() );

		}
	}

	@Override
	public void doPostTransaction(int requestCode, String result) {

		try
		{
			// TODO Auto-generated method stub
			super.doPostTransaction(requestCode, result);
			
			LinearLayout lToolbar = (LinearLayout) findViewById(R.id.lToolbar);
			LinearLayout lPanel = (LinearLayout) findViewById(R.id.lPanel);
			mediaController.setPadding(0, 0, 0, lToolbar.getHeight() + lPanel.getHeight() );
			
			JSONArray jsonAr = new JSONArray( result );

			ArrayList<JSONObject> data = Util.getArrayList(jsonAr);

			ListView listView = (ListView) findViewById(R.id.list);
			ShockingItemAdapter adapter = (ShockingItemAdapter) listView.getAdapter();
			adapter.setData(data);
			adapter.notifyDataSetChanged();

		}
		catch( Exception ex )
		{
			writeLog( ex.getMessage() );
		}
	}

	public class ShockingItemAdapter extends BaseAdapter {

		private Activity activity;
		private ArrayList<JSONObject> data;
		private LayoutInflater inflater=null;

		public ShockingItemAdapter(Activity a, ArrayList<JSONObject> d) {
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

				JSONObject jsonObj = getItem(position);

				vi = inflater.inflate(R.layout.list_showking_item1, null);

				ImageView list_image = (ImageView) vi.findViewById(R.id.list_image);

				if ( "조교".equals( jsonObj.getString("g") ) )
					list_image.setImageResource(R.drawable.icon_jo);
				else if ( "예상".equals( jsonObj.getString("g")) )
					list_image.setImageResource(R.drawable.icon_expectation);
				else if ( "복기".equals( jsonObj.getString("g")) )
					list_image.setImageResource(R.drawable.icon_bok);
				else if ( "기타".equals( jsonObj.getString("g")) )
					list_image.setImageResource(R.drawable.icon_etc);
				else if ( "특집".equals( jsonObj.getString("g")) )
					list_image.setImageResource(R.drawable.icon_feature);

				TextView txtName = (TextView) vi.findViewById(R.id.txtName);
				txtName.setText( jsonObj.getString("t") );

				TextView txtDesc = (TextView) vi.findViewById(R.id.txtDesc);
				txtDesc.setText( jsonObj.getString("s") );

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
			
			TextView txtTitle = (TextView) findViewById(R.id.txtTitle);
			txtTitle.setText( jsonObj.getString("t"));
			TextView txtSubtitle = (TextView) findViewById(R.id.txtSubtitle);
			txtSubtitle.setText( jsonObj.getString("s"));
			
			videoView.stopPlayback();
			videoView.setVideoURI(Uri.parse( jsonObj.getString("k") ));
			videoView.start();
		}
		catch( Exception ex )
		{
			writeLog( ex.getMessage() );
		}

	}
	
	public void loadList( View v )
	{
		try
		{
			
			Button btnWeeklyMonthly = (Button) findViewById(R.id.btnWeeklyMonthly);
			btnWeeklyMonthly.setBackgroundResource(R.drawable.menubtn_01_off);
			Button btnFriday = (Button) findViewById(R.id.btnFriday);
			btnFriday.setBackgroundResource(R.drawable.menubtn_02_off);
			Button btnSaturday = (Button) findViewById(R.id.btnSaturday);
			btnSaturday.setBackgroundResource(R.drawable.menubtn_03_off);
			Button btnSunday = (Button) findViewById(R.id.btnSunday);
			btnSunday.setBackgroundResource(R.drawable.menubtn_04_off);
			
			Button btnThisWeek = (Button) findViewById(R.id.btnThisWeek);
			btnThisWeek.setBackgroundResource(R.drawable.btn_this_week_off);
			Button btnLastWeek = (Button) findViewById(R.id.btnLastWeek);
			btnLastWeek.setBackgroundResource(R.drawable.btn_last_week_off);
			Button btnPlan = (Button) findViewById(R.id.btnPlan);
			btnPlan.setBackgroundResource(R.drawable.btn_plan_off);
			Button btnETC = (Button) findViewById(R.id.btnETC);
			btnETC.setBackgroundResource(R.drawable.btn_etc_off);
			Button btnProgramList = (Button) findViewById(R.id.btnProgramList);
			btnProgramList.setBackgroundResource(R.drawable.btn_program_list_off);
			
			if ( v.getId() == R.id.btnWeeklyMonthly )
				v.setBackgroundResource(R.drawable.menubtn_01_on);
			else if ( v.getId() == R.id.btnFriday )
				v.setBackgroundResource(R.drawable.menubtn_02_on);
			else if ( v.getId() == R.id.btnSaturday )
				v.setBackgroundResource(R.drawable.menubtn_03_on);
			else if ( v.getId() == R.id.btnSunday )
				v.setBackgroundResource(R.drawable.menubtn_04_on);
			else if ( v.getId() == R.id.btnThisWeek )
				v.setBackgroundResource(R.drawable.btn_this_week_on);
			else if ( v.getId() == R.id.btnLastWeek )
				v.setBackgroundResource(R.drawable.btn_last_week_on);
			else if ( v.getId() == R.id.btnPlan )
				v.setBackgroundResource(R.drawable.btn_plan_on);
			else if ( v.getId() == R.id.btnETC )
				v.setBackgroundResource(R.drawable.btn_etc_on);
			else if ( v.getId() == R.id.btnProgramList )
				v.setBackgroundResource(R.drawable.btn_program_list_on);
			
			if ( v.getId() == R.id.btnProgramList )
			{
				Intent intent = new Intent( this, ProgramListActivity.class);
				startActivity(intent);
				return;
			}
			
			String tag = v.getTag().toString();
			execTransReturningString("krCast/krCastList.aspx?cGb=" + tag + "&cPGB=1", 1, null );
		}
		catch( Exception ex )
		{
			writeLog( ex.getMessage() );
		}
	}

	@Override
	public void onCompletion(MediaPlayer arg0) {
		// TODO Auto-generated method stub
		
	}
}
