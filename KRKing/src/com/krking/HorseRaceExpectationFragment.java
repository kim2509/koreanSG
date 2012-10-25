package com.krking;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.common.Util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class HorseRaceExpectationFragment extends BaseFragment implements OnItemClickListener
{
	MainActivity mainActivity;
	
	public HorseRaceExpectationFragment( MainActivity main)
	{
		this.mainActivity = main;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.fragment_prediction, container, false);
	}
	
	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		
		execTransReturningString("krBoard/krBoardList.aspx?bGb=2&page=1", null );
	}
	
	@Override
	public void doPostTransaction(String result) {
		
		try
		{
			// TODO Auto-generated method stub
			super.doPostTransaction(result);
			
			ArrayList<JSONObject> data = Util.getArrayList( new JSONArray( result ) );
			ListView listView = (ListView) getView().findViewById(R.id.list);
			listView.setAdapter( new PredictItemAdapter( getActivity(), data ) );
			listView.setOnItemClickListener( this );
		}
		catch( Exception ex )
		{
			
		}
	}
	
	public class PredictItemAdapter extends BaseAdapter {
        
        private Activity activity;
        private ArrayList<JSONObject> data;
        private LayoutInflater inflater=null;
        
        public PredictItemAdapter(Activity a, ArrayList<JSONObject> d) {
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
        			vi = inflater.inflate(R.layout.list_expectation_item, null);
        		
                TextView title = (TextView)vi.findViewById(R.id.title); // title
                TextView txtDate = (TextView) vi.findViewById(R.id.txtDate);
                TextView txtRefCount = (TextView) vi.findViewById(R.id.txtRefCount);
                
                vi.setTag( jsonObj );
                
                title.setText( jsonObj.getString("t") );
                txtDate.setText( jsonObj.getString("d") );
                txtRefCount.setText( jsonObj.getString("r") );
                
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
		
		try
		{
			JSONObject jsonObj = (JSONObject) arg1.getTag();
			
			Intent intent = new Intent( getActivity(), HorseRaceExpectationDetailActivity.class );
			intent.putExtra("param", jsonObj.toString());
			startActivity( intent );
		}
		catch( Exception ex )
		{
			writeLog( ex.getMessage() );
		}
		
	}
}