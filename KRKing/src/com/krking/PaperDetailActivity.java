package com.krking;

import java.util.ArrayList;

import org.json.JSONObject;

import com.common.Util;

import android.os.Bundle;
import android.app.Activity;
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

public class PaperDetailActivity extends BaseActivity implements OnItemClickListener{

	String day = "";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	try
    	{
    		super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_paper_detail);
            
            ListView listView = (ListView) findViewById(R.id.list);
    		listView.setDivider( null ); 
    		listView.setDividerHeight(0);
    		
    		String paramString = getIntent().getExtras().getString("param");
    		JSONObject jsonObj = new JSONObject( paramString );
    		
    		TextView txtSubTitle = (TextView) findViewById(R.id.txtSubTitle);
    		txtSubTitle.setText( jsonObj.getString("d") );
    		    		
    		day = jsonObj.getString("w");
    		
    		ImageView imgDay = (ImageView)findViewById(R.id.imgDay);

    		if ( "금".equals( day ) )
    			imgDay.setImageResource(R.drawable.btn_fri);
    		else if ( "토".equals( day ) )
    			imgDay.setImageResource(R.drawable.btn_sat);
    		else if ( "일".equals( day ) )
    			imgDay.setImageResource(R.drawable.btn_sun);
    		
    		execTransReturningString("KrPaper/paperDetail.aspx?ysDate=" + jsonObj.getString("k"), 1, null );
    	}
    	catch( Exception ex )
    	{
    		writeLog( ex.getMessage() );
    	}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_paper_detail, menu);
        return true;
    }
    
    @Override
    public void doPostTransaction(int requestCode, String result) {
    	// TODO Auto-generated method stub
    	try
    	{
    		super.doPostTransaction(requestCode, result);
    		
    		JSONObject resObj = new JSONObject( result );
    		ArrayList<JSONObject> tempData = Util.getArrayList( resObj.getJSONArray("LP") );
			
			ListView listView = (ListView) findViewById(R.id.list);
			listView.setAdapter( new PaperViewDetailItemAdapter( this, tempData ) );
			listView.setOnItemClickListener( this );
    	}
    	catch( Exception ex )
    	{
    		writeLog( ex.getMessage() );
    	}
    }
    
    public class PaperViewDetailItemAdapter extends BaseAdapter {
        
        private Activity activity;
        private ArrayList<JSONObject> data;
        private LayoutInflater inflater=null;
        
        public PaperViewDetailItemAdapter(Activity a, ArrayList<JSONObject> d) {
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
        
        public ArrayList<JSONObject> getData()
        {
        	return data;
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
        			vi = inflater.inflate(R.layout.list_paper_view_item, null);
        		
        		ImageView imgDay = (ImageView)vi.findViewById(R.id.imgDay);
        		imgDay.setVisibility(ViewGroup.GONE);
        		
        		TextView tv = (TextView) vi.findViewById(R.id.title);
        		tv.setText( "" + jsonObj.getString("p") + "P      " + jsonObj.getString("n") );
        		
        		vi.setTag( jsonObj );
        		
        		return vi;
        	}
        	catch( Exception ex )
        	{
        		writeLog( ex.getMessage() );
        		return null;
        	}
        }
    }

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		
		try
		{
			JSONObject jsonObj = (JSONObject) arg1.getTag();
			Intent intent = new Intent( this, PaperViewDetailActivity2.class );
			
			String paramString = getIntent().getExtras().getString("param");
    		JSONObject tempObj = new JSONObject( paramString );
    		
			intent.putExtra("param", jsonObj.toString());
			intent.putExtra("k", tempObj.getString("k"));
			startActivity( intent );
		}
		catch( Exception ex )
		{
			writeLog( ex.getMessage() );
		}
	}
}
