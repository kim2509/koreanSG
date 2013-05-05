package com.krking;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.common.Util;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ProgramListActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_program_list);
        
        Display display = getWindowManager().getDefaultDisplay(); 
		int initialVideoWith = display.getWidth();  // deprecated
		int initialVideoHeight = display.getHeight();  // deprecated
		
		RelativeLayout rProgramList = (RelativeLayout) findViewById(R.id.rProgramList);
		rProgramList.getLayoutParams().width = (int) (initialVideoWith * 0.95);
		rProgramList.getLayoutParams().height = (int) (initialVideoHeight * 0.6);
        
		ArrayList<JSONObject> data = new ArrayList<JSONObject>();

		ListView listView = (ListView) findViewById(R.id.list);
		listView.setDivider( null ); 
		listView.setDividerHeight(0);
		listView.setAdapter( new ShockingTVItemAdapter( this, data ) );
		
		execTransReturningString("krCast/krCastList.aspx?cGb=0", 1, null );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_program_list, menu);
        return true;
    }
    
    @Override
	public void doPostTransaction(int requestCode, String result) {

		try
		{
			// TODO Auto-generated method stub
			super.doPostTransaction(requestCode, result);
			
			JSONArray jsonAr = new JSONArray( result );

			ArrayList<JSONObject> data = Util.getArrayList(jsonAr);

			ListView listView = (ListView) findViewById(R.id.list);
			ShockingTVItemAdapter adapter = (ShockingTVItemAdapter) listView.getAdapter();
			adapter.setData(data);
			adapter.notifyDataSetChanged();

		}
		catch( Exception ex )
		{
			writeLog( ex.getMessage() );
		}
	}
    
    public class ShockingTVItemAdapter extends BaseAdapter {

		private Activity activity;
		private ArrayList<JSONObject> data;
		private LayoutInflater inflater=null;

		public ShockingTVItemAdapter(Activity a, ArrayList<JSONObject> d) {
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

				vi = inflater.inflate(R.layout.list_showkingtv_item, null);

				String[] items = {"num", "t", "d", "u"};
				
				for ( int i = 0; i < items.length; i++ )
				{
					TextView tv = (TextView) vi.findViewWithTag( items[i] );
					
					if ( "num".equals(items[i]) )
						tv.setText( (position+1) + "" );
					else
						tv.setText( jsonObj.getString( items[i] ) );
				}
				 
				return vi;	
			}
			catch( Exception ex )
			{
				writeLog( ex.getMessage() );
			}

			return null;
		}
	}
}
