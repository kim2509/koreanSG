package com.korea.hanintown;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.korea.common.Constants;
import com.korea.common.Util;
import com.readystatesoftware.viewbadger.BadgeView;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends DYActivity implements OnItemClickListener, OnClickListener{

	private JSONArray notificationList = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {

		try
		{
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_main);

			GridView menuGrid = (GridView) findViewById(R.id.menuGrid);
			menuGrid.setOnItemClickListener( this );
		}
		catch( Exception ex )
		{
			writeLog( ex.getMessage() );
		}
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		TextView txtName = (TextView) findViewById(R.id.txtName);
		TextView txtLastLoginTime = (TextView) findViewById(R.id.txtLastLoginTime);
		
		if ( isAlreadyLogin() )
		{
			txtName.setText( getMetaInfoString("NICKNAME") + "(" + getMetaInfoString("USER_ID") + ")" );
			txtLastLoginTime.setText( "Last Login: " + Util.substring( getMetaInfoString("LAST_LOGIN_TIME"), 0, 10 ) );
		}
		else
		{
			txtName.setText("로그인 정보가 없습니다.");
			txtLastLoginTime.setText("");
		}
		
		new GetMainInfoTask( this ).execute( serverURL + "android/getMainInfo.php");
	}

	public void updateMenu( JSONObject jsonObj )
	{
		try {

			if ( jsonObj == null || jsonObj.get("serviceList") == null )
			{
				showOKDialog("응답이 올바르지 않습니다.\n다시 시도해 주십시오.", "finish");
				return;
			}

			JSONArray serviceList = (JSONArray) jsonObj.get("serviceList");

			if ( serviceList.length() != 2 )
			{
				showOKDialog("서버와의 데이터전송에 문제가 있습니다.\n다시 시도해 주십시오.", "finish");
				return;
			}

			JSONObject responseInfo = (JSONObject) ((JSONArray)serviceList.get(1)).get(0);

			if ( !"0000".equals( responseInfo.get("RES_CODE") ) )
			{
				showOKDialog("응답코드(" + responseInfo.get("RES_CODE") + ")가 올바르지 않습니다.\n관리자에게 문의바랍니다.", "finish");
				return;
			}
			
			if ( jsonObj.get("userInfo") == null )
			{
				showOKDialog("응답데이터가 올바르지 않습니다.\n관리자에게 문의 바랍니다.", "finish");
				return;
			}

			JSONArray userInfo = jsonObj.getJSONArray("userInfo");
			
			if ( userInfo.length() > 0 )
				setUserInfo( (JSONObject) userInfo.get(0) , false, false );
			
			GridView g = (GridView) findViewById(R.id.menuGrid);
			g.setAdapter(new ImageAdapter(this, ((JSONArray)serviceList.get(0))));
			
			notificationList = jsonObj.getJSONArray("notificationList");
			
			writeLog( notificationList.toString() );
			
			boardCategoryList = (JSONArray) ((JSONArray) jsonObj.get("boardCategoryList")).get(0);
			
			if ( bLoadRecentPosts )
			{
				LinearLayout layoutImageParent = (LinearLayout) findViewById(R.id.layoutImageParent);
				layoutImageParent.removeAllViews();
				JSONArray imageList = jsonObj.getJSONArray("imageList").getJSONArray(0);
				for ( int i = 0; i < imageList.length(); i++ )
				{
					LinearLayout layoutImage = new LinearLayout( this );
					layoutImage.setTag( imageList.getJSONObject( i ) );
					layoutImageParent.addView( layoutImage );
					ViewGroup.LayoutParams lParam = layoutImage.getLayoutParams();
					lParam.width = getPixelFromDP( 120 );
					lParam.height = getPixelFromDP( 120 );
					layoutImage.setLayoutParams( lParam );
					layoutImage.setOrientation(LinearLayout.VERTICAL);
					
					Button iv = new Button( this );
					layoutImage.addView( iv );
					iv.setTag("image");
					
					LinearLayout.LayoutParams ivParam = (LinearLayout.LayoutParams) iv.getLayoutParams();
					ivParam.width = getPixelFromDP( 16 );
					ivParam.height = getPixelFromDP( 16 );
					ivParam.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;
					ivParam.topMargin = getPixelFromDP(30);
					ivParam.bottomMargin = getPixelFromDP(10);
					iv.setBackgroundResource( R.drawable.loading16 );
					
					TextView tv = new TextView( this );
					layoutImage.addView( tv );
					tv.setTag("title");
					LinearLayout.LayoutParams tvLParam = (LinearLayout.LayoutParams) tv.getLayoutParams();
					tvLParam.width = getPixelFromDP( 120 );
					tvLParam.height = getPixelFromDP( 20 );
					tv.setGravity( Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL );
					tv.setTextColor(Color.WHITE);
					tv.setText("로딩중...");
				}
				
				new ImageFetchTask().execute((Void)null);
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			showOKDialog("응답 처리 도중 오류가 발생했습니다.\n관리자에게 문의바랍니다.", "finish");

			e.printStackTrace();

			return;
		}
	}
	
	private boolean bLoadRecentPosts = true;
	
	private class ImageFetchTask extends AsyncTask<Void, Integer, ArrayList<Bitmap>> {

		public ImageFetchTask()
		{
		}

		protected void onPreExecute() {
		}

		protected ArrayList<Bitmap> doInBackground( Void... data ) {

			try
        	{
        		LinearLayout layout = (LinearLayout) findViewById(R.id.layoutImageParent);
        		ArrayList<Bitmap> ar = new ArrayList<Bitmap>();
        		
            	for ( int i = 0; i < layout.getChildCount(); i++ )
            	{
            		LinearLayout layoutChild = (LinearLayout) layout.getChildAt(i);
            		JSONObject obj = (JSONObject) layoutChild.getTag();
            		URL url = new URL( "http", serverHost, serverPort, 
            				obj.getString("PATH") + "mobile/" + obj.getString("FILE_NAME") );
            		InputStream is = (InputStream) url.getContent();
		        	Bitmap bm = BitmapFactory.decodeStream( is );
		        	ar.add( bm );
            	}	
            	
            	return ar;
        	}
        	catch( Exception ex )
        	{
        		writeLog( ex.getMessage() );
        	}

			return null;
		}

		protected void onProgressUpdate(Integer... progress) {

		}

		protected void onPostExecute(ArrayList<Bitmap> result) {
			updateImage( result );
		}
	}
    
    public void updateImage( ArrayList<Bitmap> result )
    {
    	try
    	{
    		LinearLayout layout = (LinearLayout) findViewById(R.id.layoutImageParent);
    		
        	for ( int i = 0; i < layout.getChildCount(); i++ )
        	{
        		LinearLayout layoutChild = (LinearLayout) layout.getChildAt(i);
        		
        		LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) layoutChild.getLayoutParams();
        		lp.leftMargin = getPixelFromDP(5);
        		lp.rightMargin = getPixelFromDP(5);
        		
        		JSONObject obj = (JSONObject) layoutChild.getTag();
        		Bitmap bm = result.get(i);
        		
            	if ( bm != null )
            	{
            		Button imgBtn = (Button) layoutChild.findViewWithTag("image");
            		imgBtn.setOnClickListener( this );
            		
            		LinearLayout.LayoutParams ivParam = (LinearLayout.LayoutParams) imgBtn.getLayoutParams();
            		ivParam.topMargin = getPixelFromDP(5);
            		ivParam.bottomMargin = getPixelFromDP(5);
    				ivParam.width = getPixelFromDP( 120 );
    				ivParam.height = getPixelFromDP( 80 );
            		imgBtn.setBackgroundDrawable( new BitmapDrawable( bm ) );
            		TextView tv = (TextView) layoutChild.findViewWithTag("title");
            		tv.setText( obj.getString("B_SUBJECT") );
            	}
        	}
        	
        	bLoadRecentPosts = false;
    	}
    	catch( Exception ex )
    	{
    		writeLog( ex.getMessage() );
    	}
    }
	
	@Override
	public void okClicked(Object param) {
		// TODO Auto-generated method stub
		super.okClicked(param);
		
		if ( "finish".equals( param ))
			finish();
	}

	public class ImageAdapter extends BaseAdapter {

		private JSONArray serviceList;

		public ImageAdapter(Context c, JSONArray serviceList ) {
			mContext = c;
			this.serviceList = serviceList;
		}
		public int getCount() {
			return serviceList.length();
		}
		public Object getItem(int position) {
			return position;
		}
		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {

			LinearLayout linearLayout = null;

			try
			{
				ImageView imageView;

				if (convertView == null) {

					linearLayout = new LinearLayout( mContext );

					linearLayout.setOrientation(LinearLayout.VERTICAL);

					imageView = new ImageView(mContext);
					linearLayout.addView( imageView );
					
					imageView.setAdjustViewBounds(false);
					imageView.setScaleType(ImageView.ScaleType.FIT_XY);
					imageView.setPadding(8, 8, 8, 4);
					LinearLayout.LayoutParams ivp = (LinearLayout.LayoutParams) imageView.getLayoutParams();
					ivp.width = LinearLayout.LayoutParams.WRAP_CONTENT;
					ivp.height = getPixelFromDP( 80 );
					ivp.gravity = Gravity.CENTER_HORIZONTAL;

					TextView tv = new TextView( mContext );
					tv.setText("게시판");
					tv.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
					
					linearLayout.addView( tv );
					
					LinearLayout.LayoutParams tvp = (LinearLayout.LayoutParams) tv.getLayoutParams();
					tvp.width = LinearLayout.LayoutParams.FILL_PARENT;
					tv.setTextColor(Color.BLACK);

				} else {
					linearLayout = (LinearLayout) convertView;
				}

				JSONObject jsonObj = (JSONObject) serviceList.get(position);
				String menuName = jsonObj.getString("MENU_NAME");

				for ( int i = 0; i < linearLayout.getChildCount(); i++ )
				{
					View view = linearLayout.getChildAt(i);
					if ( view instanceof ImageView )
					{
						imageView = (ImageView) view;

						if ( "업체목록".equals( menuName ) )
							imageView.setImageResource( R.drawable.company );
						else if ( "게시판".equals( menuName ) )
							imageView.setImageResource( R.drawable.board128 );
						else if ( "룸렌탈".equals( menuName ) )
							imageView.setImageResource( R.drawable.room128 );
						else if ( "벼룩시장".equals( menuName ) )
							imageView.setImageResource( R.drawable.market128 );
						else if ( "취업".equals( menuName ) )
							imageView.setImageResource( R.drawable.job128 );
						else if ( "쪽지함".equals( menuName ) )
							imageView.setImageResource( R.drawable.message_128 );
						else if ( "알림센터".equals( menuName ) )
						{
							int count = 0;
							for ( int j = 0; j < notificationList.length(); j++ )
							{
								JSONObject obj = notificationList.getJSONObject(j);
								if ( "N".equals( obj.getString("IS_READ") ) )
									count++;
							}
							
							imageView.setImageResource( R.drawable.notification128 );
							
							if ( count > 0 )
							{
								BadgeView badge = new BadgeView( MainActivity.this, imageView);
								badge.setText( count + "" );
								badge.show();	
							}
						}
						else if ( "설정".equals( menuName ) )
							imageView.setImageResource( R.drawable.setting128 );
						else
							imageView.setImageResource( R.drawable.icon_etc128 );

					}
					else if ( view instanceof TextView )
					{
						TextView tv = (TextView) view;
						tv.setText( menuName );
						tv.setTag( jsonObj );
					}
				}

			}
			catch( Exception ex )
			{
				writeLog( ex.getMessage() );
			}

			return linearLayout;
		}

		private Context mContext;
		private Integer[] mThumbIds = {
				R.drawable.company, R.drawable.company,
				R.drawable.company, R.drawable.company,
				R.drawable.company, R.drawable.company,
				R.drawable.company, R.drawable.company,
				R.drawable.company, R.drawable.company,
				R.drawable.company, R.drawable.company,
				R.drawable.company, R.drawable.company,
		};

	}

	private class GetMainInfoTask extends AsyncTask<String, Integer, JSONObject> {

		private ProgressDialog dialog = null;

		public GetMainInfoTask( Context context )
		{
			dialog = new ProgressDialog(context);
		}

		protected void onPreExecute() {
			this.dialog.setMessage("로딩중...");
			this.dialog.show();
		}

		protected JSONObject doInBackground( String... data ) 
		{
			try{

				HttpClient client = new DefaultHttpClient();
				HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); //Timeout Limit
				HttpResponse response;
				JSONObject json = getJSONDataWithDefaultSetting();

				HttpPost post = new HttpPost( data[0] );
				StringEntity se = new StringEntity( json.toString(), "UTF-8");
				se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
				post.setEntity(se);
				response = client.execute(post);
				/*Checking response */
				String responseString = EntityUtils.toString(response.getEntity());

				Log.i("response", responseString);

				JSONObject jsonObj = new JSONObject( responseString );

				return jsonObj;

			}
			catch(Exception e){
				e.printStackTrace();
			}

			return null;
		}

		protected void onProgressUpdate(Integer... progress) {

		}

		protected void onPostExecute(JSONObject result) {
			if (dialog.isShowing())
				dialog.dismiss();

			MainActivity.this.updateMenu( result );
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) 
	{
		try
		{
			// TODO Auto-generated method stub
			if ( arg1 instanceof LinearLayout )
			{
				writeLog("LinearLayout");
				LinearLayout linearLayout = (LinearLayout) arg1;

				String selectedMenu = "";
				JSONObject jsonObj = null;

				for ( int i = 0; i < linearLayout.getChildCount(); i++ )
				{
					if ( linearLayout.getChildAt(i) instanceof TextView )
					{
						TextView tv = (TextView) linearLayout.getChildAt(i);
						jsonObj = (JSONObject) tv.getTag();
						selectedMenu = jsonObj.getString("MENU_NAME");

						break;
					}
				}

				if ( "업체목록".equals( selectedMenu ) )
				{
					Intent intent = new Intent( this, ShopsMainActivity.class );
					startActivity( intent );
				}
				else if ( "게시판".equals( selectedMenu ) )
				{
					Intent intent = new Intent( this, BoardHomeActivity.class );
					startActivity( intent );
				}
				else if ( "알림센터".equals( selectedMenu ) )
				{
					if ( isAlreadyLogin() == false )
					{
						showToastMessage("해당 기능을 이용하기 위해선\r\n로그인이 필요합니다.\r\n로그인 후 이용해 주시기 바랍니다.");
						loadLoginActivity();
						return;
					}
					
					Intent intent = new Intent( this, NotificationActivity.class);
					intent.putExtra("param", notificationList.toString());
					startActivity( intent );
				}
				else if ( "설정".equals( selectedMenu ) )
				{
					goSettingsActivity();
				}
				else if ( "쪽지함".equals( selectedMenu ) )
				{
					if ( isAlreadyLogin() == false )
					{
						showToastMessage("해당 기능을 이용하기 위해선\r\n로그인이 필요합니다.\r\n로그인 후 이용해 주시기 바랍니다.");
						loadLoginActivity();
						return;
					}
					
					Intent intent = new Intent( this , MessageListActivity.class);
					startActivity(intent);
				}
				else
				{
					Intent intent = new Intent( this , BoardItemListActivity.class);
					intent.putExtra("param", jsonObj.toString() );
					startActivity(intent);
				}
			}
			else
			{
				writeLog("not LinearLayout");
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
			LinearLayout layout = (LinearLayout) v.getParent();
			JSONObject obj = (JSONObject) layout.getTag();
			
			JSONObject jsonObj = new JSONObject();
        	jsonObj.put("BOARD_NAME", obj.getString("BOARD_NAME"));
        	jsonObj.put("BID", obj.getString("B_ID"));
        	jsonObj.put("SUBJECT", obj.getString("B_SUBJECT"));
        	jsonObj.put("USER_ID", obj.getString("USER_ID"));
        	
        	Intent intent = new Intent( this , BoardItemContentActivity.class);
			intent.putExtra("param", jsonObj.toString() );
		    startActivityForResult(intent, Constants.REQUEST_CODE_COMMON2 );
		}
		catch( Exception ex )
		{
			writeLog( ex.getMessage() );
		}
	}
}

