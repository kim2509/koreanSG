package com.korea.hanintown;

import java.util.ArrayList;
import java.util.Iterator;

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

import com.korea.common.Constants;
import com.korea.common.MyLocation;
import com.korea.common.MyLocation.LocationResult;
import com.korea.common.ShopCommentDao;
import com.korea.common.ShopDao;
import com.korea.common.ShopDataSource;
import com.korea.common.ShopLikeDao;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ShopListActivity extends DYActivity implements OnItemClickListener{

	private ShopDataSource shopDC;
	private Location currentLocation = null;

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		try
		{
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_shop_list);

			shopDC = new ShopDataSource( this );
			shopDC.open();

			ListView lv = (ListView) findViewById(R.id.list);
			ArrayList<JSONObject> data = new ArrayList<JSONObject>();
			lv.setAdapter( new ShopItemAdapter( this, data ) );
			lv.setOnItemClickListener( this );

			EditText edtKeyword = (EditText)findViewById(R.id.edtKeyword);
			edtKeyword.addTextChangedListener(new TextWatcher() {

		        @Override
		        public void afterTextChanged(Editable s) {
		            // TODO Auto-generated method stub

		        }

		        @Override
		        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		            // TODO Auto-generated method stub

		        }

		        @Override
		        public void onTextChanged(CharSequence s, int start, int before, int count) {
		        	
		        	try
		        	{
		        		readData( s.toString() );
		        	}
		        	catch( Exception ex )
		        	{
		        		writeLog( ex.getMessage() );
		        	}
		        } 

		    });

			
			readData( "" );

			LocationResult locationResult = new LocationResult(){
			    @Override
			    public void gotLocation(Location location){
			        //Got the location!
			    	
			    	if ( location == null ) return;
			    	
					String text = "My current location is: " +
							"Latitud = " + location.getLatitude() +
							"Longitud = " + location.getLongitude();

					currentLocation = location;
					
					try
					{
						readData( "" );						
					}
					catch( Exception ex )
					{
						writeLog( ex.getMessage() );
					}
			    }
			};
			MyLocation myLocation = new MyLocation();
			myLocation.getLocation(this, locationResult);

			JSONObject request = getJSONDataWithDefaultSetting();
			request.put("shopLikeLastModifiedDate", getMetaInfoString("SHOP_LIKE_LAST_MODIFIED_DATE"));
			request.put("shopCommentLastModifiedDate", getMetaInfoString("SHOP_COMMENT_LAST_MODIFIED_DATE"));
			new FetchShopReviewAsyncTask( this, "android/getUserLikesNComments.php", false ).execute( request );

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
		shopDC.open();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		shopDC.close();
	}

	public void readData( String keyword ) throws Exception
	{
		ArrayList<JSONObject> data = new ArrayList<JSONObject>();

		writeLog( keyword );
		
		JSONObject jsonObj = new JSONObject( getIntent().getExtras().getString("param") );
		Cursor c = shopDC.getShopListByCategory( jsonObj.getString("CATEGORY") , keyword,  jsonObj.getString("mode") );
		if ( c != null )
		{
			if ( c.moveToFirst() )
			{
				do
				{
					int index = 0;
					JSONObject obj = new JSONObject();
					obj.put("SEQ", c.getString( index++ ));
					obj.put("CATEGORY", c.getString( index++ ) );
					obj.put("SHOPNAME", c.getString( index++ ) );
					obj.put("PHONE", c.getString( index++ ) );
					obj.put("MOBILE", c.getString( index++ ) );
					obj.put("PHONE1", c.getString( index++ ) );
					obj.put("PHONE2", c.getString( index++ ) );
					obj.put("ADDRESS", c.getString( index++ ) );
					obj.put("LONGITUDE", c.getString( index++ ) );
					obj.put("LATITUDE", c.getString( index++ ) );
					obj.put("EMAIL", c.getString( index++ ) );
					obj.put("HOMEPAGE", c.getString( index++ ) );
					obj.put("IS_SHOW_PRICE", c.getString( index++ ) );
					obj.put("CREATE_DATE", c.getString( index++ ) );
					obj.put("UPDATE_DATE", c.getString( index++ ) );
					obj.put("DELETE_DATE", c.getString( index++ ) );
					obj.put("SHOPNAME_EN", c.getString( index++ ) );
					obj.put("CATEGORY_EN", c.getString( index++ ) );
					obj.put("CNT_LIKES", c.getString( index++ ) );
					obj.put("CNT_COMMENTS", c.getString( index++ ) );
					data.add(obj);
				}
				while( c.moveToNext() );
			}
		}

		c.close();

		ListView lv = (ListView) findViewById(R.id.list);
		ShopItemAdapter adapter = (ShopItemAdapter) lv.getAdapter();
		adapter.setData( data );
		adapter.notifyDataSetChanged();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_shop_list, menu);
		return true;
	}

	public class ShopItemAdapter extends BaseAdapter {

		private DYActivity activity;
		private ArrayList<JSONObject> data;
		private LayoutInflater inflater=null;

		public ShopItemAdapter(DYActivity a, ArrayList<JSONObject> d) {
			activity = a;
			data=d;
			inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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

		public void setData( ArrayList<JSONObject> data )
		{
			this.data = data;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			try
			{
				View vi=convertView;

				JSONObject jsonObj = data.get(position);

				if ( vi == null )
					vi = inflater.inflate(R.layout.list_shop_item, null);

				TextView title = (TextView)vi.findViewById(R.id.title); // title

				ImageView imageView = (ImageView) vi.findViewById(R.id.list_image);
				imageView.setImageResource(R.drawable.preparing);

				// Get singletone instance of ImageLoader
				ImageLoader imageLoader = ImageLoader.getInstance();
				// Initialize ImageLoader with configuration. Do it once.
				imageLoader.init(ImageLoaderConfiguration.createDefault( activity ));
				// Load and display image asynchronously

				String imageURL = activity.serverURL + "iphone/images/shop/S" + jsonObj.getString("SEQ")+ ".png";
				imageLoader.displayImage( imageURL, imageView);
				title.setText( jsonObj.getString("SHOPNAME").replaceAll("&apos;", "'") );
				
				TextView txtCategory = (TextView) vi.findViewById( R.id.txtCategory );
				txtCategory.setText( jsonObj.getString("CATEGORY") );
				
				if ( currentLocation != null && !"-12345.0".equals( jsonObj.getString("LATITUDE") ) )
				{
					Location location = new Location("");
					location.setLatitude( Double.parseDouble( jsonObj.getString("LATITUDE") ) );
					location.setLongitude( Double.parseDouble( jsonObj.getString("LONGITUDE") ) );
					
					float meter = currentLocation.distanceTo( location ) / 1000;
					
					TextView txtDistance = (TextView) vi.findViewById( R.id.txtDistance );
					txtDistance.setText( String.format("%.1f", meter) + " km");
				}

				TextView txtLikesCnt = (TextView) vi.findViewById( R.id.txtLikesCnt );
				if ( jsonObj.has("CNT_LIKES") && jsonObj.get("CNT_LIKES") != null && !JSONObject.NULL.equals( jsonObj.get("CNT_LIKES" ) ) )
					txtLikesCnt.setText( jsonObj.getString("CNT_LIKES") );	
				else
					txtLikesCnt.setText("0");
				
				TextView txtCommentsCnt = (TextView) vi.findViewById( R.id.txtCommentsCnt );
				if ( jsonObj.has("CNT_COMMENTS") && jsonObj.get("CNT_COMMENTS") != null && !JSONObject.NULL.equals( jsonObj.get("CNT_COMMENTS" ) ) )
					txtCommentsCnt.setText( jsonObj.getString("CNT_COMMENTS") );
				else
					txtCommentsCnt.setText( "0" );
				
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
			Intent intent = new Intent( this, ShopDetailActivity.class );
			intent.putExtra("param", arg1.getTag().toString());
			startActivity(intent);
		}
		catch( Exception ex )
		{
			writeLog( ex.getMessage() );
		}

	}


	private class FetchShopReviewAsyncTask extends AsyncTask<JSONObject, Integer, Void> {

		private ProgressDialog dialog = null;
		private String url = "";
		private boolean bShowDlg = true;

		public FetchShopReviewAsyncTask( DYActivity activity, String url, boolean bModal )
		{
			dialog = new ProgressDialog( activity );
			this.url = url;
			this.bShowDlg = bModal;
		}

		protected void onPreExecute() {

			if ( bShowDlg )
			{
				this.dialog.setMessage("·ÎµùÁß...");
				this.dialog.show();	
			}
		}

		protected Void doInBackground( JSONObject... data ) {

			try
			{
				HttpClient client = new DefaultHttpClient();
				HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); //Timeout Limit
				HttpResponse response;
				JSONObject json = data[0];
				HttpPost post = new HttpPost( serverURL + url );
				StringEntity se = new StringEntity( json.toString(), "UTF-8");
				se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
				post.setEntity(se);
				response = client.execute(post);
				String responseString = EntityUtils.toString(response.getEntity());
				Log.d("RESPONSE", responseString );

				updateShopLikes( responseString );

				updateShopComments( responseString );
			}
			catch(Exception e){
				e.printStackTrace();
				writeLog( e.getMessage() );
			}

			return null;
		}

		private void updateShopLikes(String responseString)
				throws JSONException, Exception {

			JSONObject jsonObject = new JSONObject( responseString );

			if ( jsonObject.get("updatedShopLikeInfo") == null ||
					JSONObject.NULL.equals( jsonObject.get("updatedShopLikeInfo") )) return;

			JSONObject info = new JSONObject( responseString).getJSONObject("updatedShopLikeInfo");
			JSONArray list = new JSONObject( responseString).getJSONArray("updatedShopLikeList");

			if ( info == null || "".equals( info ) || JSONObject.NULL.equals( info ) )
				return;

			shopDC.deleteShopLikes("-1");

			@SuppressWarnings("rawtypes")
			Iterator keys = info.keys();
			String shopLikeLastUpdateDate = "";

			while(keys.hasNext()) 
			{
				String infoKey = (String)keys.next();
				String infoValue = info.getString( infoKey );

				if ( JSONObject.NULL.equals( infoValue ) )
					infoValue = "";

				if ( "shopLikeLastUpdateDate".equals( infoKey ) )
					shopLikeLastUpdateDate = infoValue;
				else
				{
					shopDC.deleteShopLikes( infoValue );

					if ( "deletedShopLikeList".equals( infoKey ) ) continue;

					JSONArray shopLikeList = null;

					if ( "newShopLikeList".equals( infoKey ) )
						shopLikeList = list.getJSONArray(0);
					else if ( "updatedShopLikeList".equals( infoKey ) )
						shopLikeList = list.getJSONArray(1);

					for ( int i = 0; i < shopLikeList.length(); i++ )
					{
						ShopLikeDao shopLike = new ShopLikeDao( shopLikeList.getJSONObject(i) );
						shopDC.insertShopLike(shopLike);
					}
				}

			}

			if ( shopLikeLastUpdateDate != null && !"".equals( shopLikeLastUpdateDate ) )
				setMetaInfo("SHOP_LIKE_LAST_MODIFIED_DATE", shopLikeLastUpdateDate );
		}

		private void updateShopComments( String responseString ) throws Exception
		{
			JSONObject jsonObject = new JSONObject( responseString );

			if ( jsonObject.get("updatedShopCommentInfo") == null ||
					JSONObject.NULL.equals( jsonObject.get("updatedShopCommentInfo") )) return;

			JSONObject info = new JSONObject( responseString).getJSONObject("updatedShopCommentInfo");
			JSONArray list = new JSONObject( responseString).getJSONArray("updatedShopCommentList");

			if ( info == null || "".equals( info ) || JSONObject.NULL.equals( info ) )
				return;

			shopDC.deleteShopComments("-1");

			@SuppressWarnings("rawtypes")
			Iterator keys = info.keys();
			String shopCommentLastUpdateDate = "";

			while(keys.hasNext()) 
			{
				String infoKey = (String)keys.next();
				String infoValue = info.getString( infoKey );

				if ( JSONObject.NULL.equals( infoValue ) )
					infoValue = "";

				if ( "shopCommentLastUpdateDate".equals( infoKey ) )
					shopCommentLastUpdateDate = infoValue;
				else
				{
					shopDC.deleteShopComments( infoValue );

					if ( "deletedShopCommentList".equals( infoKey ) ) continue;

					JSONArray shopCommentList = null;

					if ( "newShopCommentList".equals( infoKey ) )
						shopCommentList = list.getJSONArray(0);
					else if ( "updatedShopCommentList".equals( infoKey ) )
						shopCommentList = list.getJSONArray(1);

					for ( int i = 0; i < shopCommentList.length(); i++ )
					{
						ShopCommentDao shopComment = new ShopCommentDao( shopCommentList.getJSONObject(i) );
						shopDC.insertShopComment(shopComment);
					}
				}
			}

			if ( shopCommentLastUpdateDate != null && !"".equals( shopCommentLastUpdateDate ) )
				setMetaInfo("SHOP_COMMENT_LAST_MODIFIED_DATE", shopCommentLastUpdateDate );
		}

		protected void onProgressUpdate(Integer... progress) {

		}

		protected void onPostExecute(Void result) {

			if (dialog.isShowing())
				dialog.dismiss();

			try
			{
				readData("");	
			}
			catch( Exception ex )
			{
				writeLog( ex.getMessage() );
			}
		}
	}
}
