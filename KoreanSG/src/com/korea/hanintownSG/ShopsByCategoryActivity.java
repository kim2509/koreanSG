package com.korea.hanintownSG;

import java.util.ArrayList;

import org.json.JSONObject;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
    	try
    	{
    		super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_shops_by_category);
            
            ListView lv = (ListView) findViewById(R.id.list);
            ArrayList<JSONObject> data = new ArrayList<JSONObject>();
            lv.setAdapter( new CategoryItemAdapter( this, data ) );
            lv.setOnItemClickListener( this );
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
        
        public View getView(int position, View convertView, ViewGroup parent) {
        	try
        	{
        		View vi=convertView;
        		
        		JSONObject jsonObj = data.get(position);

        		if ( vi == null )
        			vi = inflater.inflate(R.layout.list_category_item, null);
        		
                TextView title = (TextView)vi.findViewById(R.id.title); // title
                
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
			
		}
		catch( Exception ex )
		{
			writeLog( ex.getMessage() );
		}
	}
}
