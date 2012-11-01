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
import org.json.JSONObject;

import com.korea.common.Constants;
import com.korea.common.ShopDao;
import com.korea.common.ShopDataSource;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class ShopsByCategoryActivity extends DYActivity implements OnItemClickListener {

    private ShopDataSource shopDC;

	@Override
    public void onCreate(Bundle savedInstanceState) {
    	
    	try
    	{
    		super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_shops_by_category);
            
            shopDC = new ShopDataSource( this );
            shopDC.open();
            
            ListView lv = (ListView) findViewById(R.id.list);
            ArrayList<JSONObject> data = new ArrayList<JSONObject>();
            lv.setAdapter( new CategoryItemAdapter( this, data ) );
            lv.setOnItemClickListener( this );

            readData();
            
            JSONObject request = getJSONDataWithDefaultSetting();
            request.put("shopInfoLastModifiedDate", getMetaInfoString("SHOP_INFO_LAST_MODIFIED_DATE"));
            
            if ( lv.getAdapter().getCount() == 1 )
            	execTransReturningString("android/metaInfoJson.php", request, true );
            else
            	execTransReturningString("android/metaInfoJson.php", request, false );
    	}
    	catch( Exception ex )
    	{
    		writeLog( ex.getMessage() );
    	}
    }
	
	public void readData() throws Exception
	{
		ArrayList<JSONObject> data = new ArrayList<JSONObject>();
		
		Cursor c = shopDC.getShopCountByCategory();
        if ( c != null )
        {
        	if ( c.moveToFirst() )
        	{
        		do
        		{
        			JSONObject obj = new JSONObject();
        			obj.put("CATEGORY", c.getString(0));
        			obj.put("CNT", c.getString(1) );
        			data.add(obj);
        		}
        		while( c.moveToNext() );
        	}
        }
        c.close();
        
        ListView lv = (ListView) findViewById(R.id.list);
        CategoryItemAdapter adapter = (CategoryItemAdapter) lv.getAdapter();
        adapter.setData( data );
        adapter.notifyDataSetChanged();
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
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_shops_by_category, menu);
        return true;
    }
    
    public class CategoryItemAdapter extends BaseAdapter {
        
        private DYActivity activity;
        private ArrayList<JSONObject> data;
        private LayoutInflater inflater=null;
        
        public CategoryItemAdapter(DYActivity a, ArrayList<JSONObject> d) {
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
        			vi = inflater.inflate(R.layout.list_category_item, null);
        		
                TextView title = (TextView)vi.findViewById(R.id.title); // title
                title.setText( jsonObj.getString("CATEGORY") + "(" + jsonObj.getString("CNT") + ")" );
                vi.setTag( jsonObj );
                
                return vi;
        	}
        	catch( Exception ex )
        	{
        	}
        	
        	return null;
        }
    }

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		
		// TODO Auto-generated method stub
		
		try
		{
			Intent intent = new Intent( this, ShopListMainActivity.class );
			intent.putExtra("param", arg1.getTag().toString());
			startActivity(intent);
		}
		catch( Exception ex )
		{
			writeLog( ex.getMessage() );
		}
	}
	
	@Override
	public void doPostTransaction(Object result) {
		// TODO Auto-generated method stub
		
		try
		{
			super.doPostTransaction(result);
			
			JSONObject jsonObject = new JSONObject( result.toString() );
			
			if ( jsonObject.get("updatedShopInfo") == null ||
					JSONObject.NULL.equals( jsonObject.get("updatedShopInfo") )) return;
			
			new UpdateShopTask( this ).execute( jsonObject );
			
		}
		catch( Exception ex )
		{
			writeLog( ex.getMessage() );
		}
	}
	
	private class UpdateShopTask extends AsyncTask<JSONObject, Integer, Void> {

		private ProgressDialog dialog = null;

		public UpdateShopTask( DYActivity activity  )
		{
			dialog = new ProgressDialog( activity );
		}

		protected void onPreExecute() {
			this.dialog.setMessage("업데이트 중입니다.\r\n잠시만 기다려 주십시오.");
			this.dialog.show();
		}

		protected Void doInBackground( JSONObject... data ) {

			try
			{
				JSONObject jsonObject = data[0];
				
				JSONObject updateShopInfo = jsonObject.getJSONObject("updatedShopInfo");
				
				JSONArray list = jsonObject.getJSONArray("updatedShopList");
				
				if ( "".equals( getMetaInfoString("SHOP_INFO_LAST_MODIFIED_DATE") ) )
				{
					// delete all shop list
					shopDC.deleteAllShop();
				}

				@SuppressWarnings("rawtypes")
				Iterator keys = updateShopInfo.keys();
				String shopInfoLastModifiedDate = "";
				
				while(keys.hasNext()) 
				{
					String infoKey = (String)keys.next();
					String infoValue = updateShopInfo.getString( infoKey );
					
					if ( infoValue == null )
						infoValue = "";
					
					if ( "shopInfoLastModifiedDate".equals( infoKey ) )
					{
						shopInfoLastModifiedDate = infoValue;
					}
					else
					{
						shopDC.deleteShops( infoValue );
						if ( "deletedShopList".equals( infoKey ) ) continue;

						JSONArray shopList = null;
						
						if ( "newShopList".equals( infoKey ) )
							shopList = list.getJSONArray(0);
						else if ( "updatedShopList".equals( infoKey ) )
							shopList = list.getJSONArray(1);	
						
						for ( int i = 0; i < shopList.length(); i++ )
						{
							ShopDao shop = new ShopDao( shopList.getJSONObject(i) );
							shopDC.insertShop( shop );
						}
					}
				}
				
				if ( shopInfoLastModifiedDate != null && !"".equals( shopInfoLastModifiedDate ) )
					setMetaInfo("SHOP_INFO_LAST_MODIFIED_DATE", shopInfoLastModifiedDate );
				
			}
			catch(Exception e){
				
				writeLog( e.getMessage() );
			}
			
			return null;
		}

		protected void onProgressUpdate(Integer... progress) {

		}

		protected void onPostExecute( Void v ) {
			if (dialog.isShowing())
				dialog.dismiss();
			
			try
			{
				readData();	
			}
			catch( Exception ex )
			{
				writeLog( ex.getMessage() );
			}
		}
	}
}
