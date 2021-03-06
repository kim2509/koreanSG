package com.krking;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.common.Util;

import android.annotation.SuppressLint;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

@SuppressLint("ValidFragment")
public class PredictionFragment extends BaseFragment implements OnItemClickListener
{
	MainActivity mainActivity;
	String mode = "";
	int pageNo = 1;
	
	public PredictionFragment( MainActivity main, String m )
	{
		this.mainActivity = main;
		this.mode = m;
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
		View v = inflater.inflate(R.layout.fragment_prediction, container, false);
		
		TextView tv = (TextView) v.findViewById(R.id.txtTitle);
		
		if ( "K".equals( mode ) )
			tv.setText("경마왕 예상");
		else if ( "F".equals( mode ) )
			tv.setText("프리 예상");
		return v;
	}
	
	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		
		ListView listView = (ListView) getView().findViewById(R.id.list);
		listView.setDivider( null ); 
		listView.setDividerHeight(0);
		
		execTransReturningString("KrPro/krProList.aspx?proGb=" + mode + "&Page=" + pageNo + "&refIdx=0", 1, null);
	}
	
	@Override
	public void doPostTransaction(int requestCode, String result) {
		
		try
		{
			// TODO Auto-generated method stub
			super.doPostTransaction(requestCode, result);
			
			ListView listView = (ListView) getView().findViewById(R.id.list);
			
			JSONArray jsonAr = new JSONArray( result );
			ArrayList<JSONObject> tempData = Util.getArrayList( jsonAr );
			
			for ( int i = 0; i < tempData.size(); i++ )
			{
				JSONObject jsonObj = tempData.get(i);
				if ( jsonObj.has("TYPE") == false )
				{
					jsonObj.put("TYPE", "ITEM1");
				}
			}
			
			if ( requestCode == 1 )
			{
				JSONObject tempObj = new JSONObject();
				tempObj.put("TYPE", "ITEM2");
				tempData.add( tempObj );
				
				listView.setAdapter( new PredictItemAdapter( getActivity(), tempData ) );
				listView.setOnItemClickListener( this );
			}
			else if ( requestCode == 2 )
			{
				PredictItemAdapter adapter = (PredictItemAdapter) listView.getAdapter();
				ArrayList<JSONObject> data = adapter.getData();
				JSONObject tempObj = data.get( data.size() - 1 );
				data.remove( data.size() - 1 );
				data.addAll( tempData );
				data.add( tempObj );
				adapter.notifyDataSetChanged();
			}
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

        		if ( "ITEM1".equals( jsonObj.getString("TYPE") ) )
        		{
            		vi = inflater.inflate(R.layout.list_predict_item, null);
            		
            		ImageView list_image = (ImageView)vi.findViewById(R.id.list_image);
            		
            		if ( "JOK".equals( jsonObj.getString("g") ) )
            			list_image.setImageResource(R.drawable.prediction_trainingbox);
            		else if ( "BOK".equals( jsonObj.getString("g") ) )
            			list_image.setImageResource(R.drawable.prediction_reviewbox);
            		else if ( "ANA".equals( jsonObj.getString("g") ) )
            			list_image.setImageResource(R.drawable.prediction_analysisbox);
            		else if ( "SUN".equals( jsonObj.getString("g") ) )
            			list_image.setImageResource(R.drawable.prediction_sundaybox);
            		else if ( "SAT".equals( jsonObj.getString("g") ) )
            			list_image.setImageResource(R.drawable.prediction_saturdaybox);
            		else if ( "FRI".equals( jsonObj.getString("g") ) )
            			list_image.setImageResource(R.drawable.prediction_fridaybox);
            		
                    TextView title = (TextView)vi.findViewById(R.id.title); // title
                    TextView txtDate = (TextView) vi.findViewById(R.id.txtDate);
                    TextView txtRefCount = (TextView) vi.findViewById(R.id.txtRefCount);
                    TextView txtAuthor = (TextView) vi.findViewById(R.id.txtAuthor);
                    
                    vi.setTag( jsonObj );
                    
                    title.setText( jsonObj.getString("t") );
                    txtDate.setText( "작성 " + jsonObj.getString("d").substring(0, 10 ) );
                    txtRefCount.setText( "조회 " + jsonObj.getString("r") );
                    txtAuthor.setText( jsonObj.getString("n") );	
        		}
        		else
        		{
        			vi = inflater.inflate(R.layout.list_item_more, null);
        			pageNo++;
        			execTransReturningString("KrPro/krProList.aspx?proGb=" + mode + "&Page=" + pageNo + "&refIdx=0", 2, null);
        		}
                
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
			
			Intent intent = new Intent( getActivity(), PredictionContentActivity.class );
			intent.putExtra("param", jsonObj.toString());
			startActivity( intent );
		}
		catch( Exception ex )
		{
			writeLog( ex.getMessage() );
		}
		
	}
}