package com.korea.hanintown;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.korea.common.Constants;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class ShopsByMenuActivity extends DYActivity implements OnItemClickListener{

	@Override
    public void onCreate(Bundle savedInstanceState) {
    	
    	try
    	{
    		super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_shops_by_category);
            
            ListView lv = (ListView) findViewById(R.id.list);
            ArrayList<JSONObject> data = new ArrayList<JSONObject>();
            lv.setAdapter( new MenuGroupItemAdapter( this, data ) );
            lv.setOnItemClickListener( this );
            
            JSONObject request = getJSONDataWithDefaultSetting();
            execTransReturningString("android/getMenuKeywordList.php", request, Constants.REQUEST_CODE_COMMON, true );
    	}
    	catch( Exception ex )
    	{
    		writeLog( ex.getMessage() );
    	}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_shops_by_category, menu);
        return true;
    }
    
    public class MenuGroupItemAdapter extends BaseAdapter {
        
        private DYActivity activity;
        private ArrayList<JSONObject> data;
        private LayoutInflater inflater=null;
        
        public MenuGroupItemAdapter(DYActivity a, ArrayList<JSONObject> d) {
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
                
                title.setText( jsonObj.getString("NAME") );

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
    			data.add( arResult.getJSONObject( i ) );
    		}
    		
    		ListView lv = (ListView) findViewById(R.id.list);
    		MenuGroupItemAdapter adapter = (MenuGroupItemAdapter) lv.getAdapter();
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
			Intent intent = new Intent( this, ShopMenuListActivity.class );
			
			JSONObject obj = (JSONObject) arg1.getTag();
			if ( "메뉴검색".equals( obj.getString("NAME") ) )
				intent = new Intent( this, ShopMenuSearchActivity.class );
			
			intent.putExtra("param", arg1.getTag().toString());
			startActivity(intent);
		}
		catch( Exception ex )
		{
			writeLog( ex.getMessage() );
		}
	}
}