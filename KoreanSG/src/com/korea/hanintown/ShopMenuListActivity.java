package com.korea.hanintown;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.korea.common.Constants;
import com.korea.common.ShopDataSource;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class ShopMenuListActivity extends DYActivity implements OnItemClickListener{

	JSONObject paramObject = null;
	private ShopDataSource shopDC;
	
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
    	try
    	{
    		super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_shop_menu_list);
            
            shopDC = new ShopDataSource( this );
			shopDC.open();
			
            paramObject = new JSONObject( getIntent().getExtras().getString("param") );
            
            TextView txtTitle = (TextView) findViewById(R.id.txtTitle );
            txtTitle.setText( paramObject.getString("NAME") );
            
            ListView lv = (ListView) findViewById(R.id.list);
            ArrayList<JSONObject> data = new ArrayList<JSONObject>();
            lv.setAdapter( new MenuItemAdapter( this, data ) );
            lv.setOnItemClickListener( this );
            
            JSONObject request = getJSONDataWithDefaultSetting();
            request.put("keyword", paramObject.getString("NAME"));
            execTransReturningString("android/getMenuListByKeyword.php", request, Constants.REQUEST_CODE_COMMON, true );
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_shop_menu_list, menu);
        return true;
    }
    
    public class MenuItemAdapter extends BaseAdapter {
        
        private DYActivity activity;
        private ArrayList<JSONObject> data;
        private LayoutInflater inflater=null;
        
        public MenuItemAdapter(DYActivity a, ArrayList<JSONObject> d) {
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
        			vi = inflater.inflate(R.layout.list_menu_item2, null);
        		
                TextView title = (TextView)vi.findViewById(R.id.title); // title
                
                String price = "";

				if ( !"".equals( jsonObj.get("CURRENCY_NAME")) && !JSONObject.NULL.equals( jsonObj.get("CURRENCY_NAME")))
					price += jsonObj.get("CURRENCY_NAME");

				if ( !"".equals( jsonObj.get("PRICE")) && !JSONObject.NULL.equals( jsonObj.get("PRICE")))
					price += " " + String.format("%.1f", jsonObj.getDouble("PRICE") );

				title.setText( jsonObj.getString("MENU_NAME_KR") + "(" + price + ")");

				TextView txtShopName = (TextView) vi.findViewById(R.id.txtShopName);
				txtShopName.setText( jsonObj.getString("SHOP_NAME_KR") );
				
                ImageView imageView = (ImageView) vi.findViewById(R.id.list_image);
				imageView.setImageResource(R.drawable.preparing);

				// Get singletone instance of ImageLoader
				ImageLoader imageLoader = ImageLoader.getInstance();
				// Initialize ImageLoader with configuration. Do it once.
				imageLoader.init(ImageLoaderConfiguration.createDefault( activity ));
				// Load and display image asynchronously

				String imageURL = activity.serverURL + "iphone/images/menu/M" + jsonObj.getString("MENU_NO")+ ".png";
				imageLoader.displayImage( imageURL, imageView);

				TextView txtLikesCnt = (TextView) vi.findViewById(R.id.txtLikesCnt);
				
				if ( !"".equals( jsonObj.get("CNT_LIKE")) && !JSONObject.NULL.equals( jsonObj.get("CNT_LIKE")))
				{
					ImageView ivThumb = (ImageView) vi.findViewById(R.id.iv_thumb);
					ivThumb.setVisibility(ViewGroup.VISIBLE);
					txtLikesCnt.setText( jsonObj.getString("CNT_LIKE") );
				}
				else
				{
					ImageView ivThumb = (ImageView) vi.findViewById(R.id.iv_thumb);
					ivThumb.setVisibility(ViewGroup.GONE);
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
    public void doPostTransaction(int requestCode, Object result) {
    	// TODO Auto-generated method stub
    	try
    	{
    		super.doPostTransaction(requestCode, result);
    		
    		if ( result == null || "".equals( result ) ) return;
    		
    		JSONArray arResult = new JSONArray( result.toString() );
    		
    		ArrayList<JSONObject> data = new ArrayList<JSONObject>();
    		for ( int i = 0; i < arResult.length(); i++ )
    		{
    			data.add( arResult.getJSONObject(i) );
    		}
    		
    		ListView lv = (ListView) findViewById(R.id.list);
    		MenuItemAdapter adapter = (MenuItemAdapter) lv.getAdapter();
    		adapter.setData( data );
    		adapter.notifyDataSetChanged();
    	}
    	catch( Exception ex )
    	{
    		writeLog( ex.getMessage() );
    	}
    }

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		try
		{
			JSONObject obj = (JSONObject) arg1.getTag();
			
			obj = shopDC.getShopInfo( obj.getString("SHOP_NO") );
			
			Intent intent = new Intent( this, ShopDetailActivity.class );
			intent.putExtra("param", obj.toString());
			startActivity(intent);
		}
		catch( Exception ex )
		{
			writeLog( ex.getMessage() );
		}
		
	}
}
