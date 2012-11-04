package com.korea.hanintown;

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
import org.json.JSONException;
import org.json.JSONObject;

import com.readystatesoftware.viewbadger.BadgeView;

import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.Preference;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends DYActivity implements OnItemClickListener{

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

		} catch (Exception e) {
			// TODO Auto-generated catch block
			showOKDialog("응답 처리 도중 오류가 발생했습니다.\n관리자에게 문의바랍니다.", "finish");

			e.printStackTrace();

			return;
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

					LinearLayout.LayoutParams lp = 
							new LinearLayout.LayoutParams( LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT );
					//linearLayout.setLayoutParams( lp );

					linearLayout.setOrientation(LinearLayout.VERTICAL);

					imageView = new ImageView(mContext);
					imageView.setLayoutParams(new GridView.LayoutParams( GridLayout.LayoutParams.FILL_PARENT, GridLayout.LayoutParams.WRAP_CONTENT));
					imageView.setAdjustViewBounds(false);
					imageView.setScaleType(ImageView.ScaleType.FIT_XY);
					imageView.setPadding(8, 8, 8, 8);

					TextView tv = new TextView( mContext );
					tv.setText("게시판");
					tv.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);

					linearLayout.addView( imageView );
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

		}
	}
}

