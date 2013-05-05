package com.krking;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.common.Util;
import com.krking.PredictionFragment.PredictItemAdapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class FreeBoardFragment extends BaseFragment implements OnItemClickListener, OnClickListener
{
	MainActivity mainActivity;
	int gBn = 1;
	int pageNo = 1;
	
	public FreeBoardFragment( MainActivity main, int gubun )
	{
		this.gBn = gubun;
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
		View v = inflater.inflate(R.layout.fragment_freeboard, container, false);
		
		Button btnWrite = (Button) v.findViewById(R.id.btnWrite);
		btnWrite.setOnClickListener( this );
		
		return v;
	}
	
	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		
		TextView txtTitle = (TextView) getView().findViewById(R.id.txtTitle);
		if ( gBn == 1 )
			txtTitle.setText("자유게시판");
		else
			txtTitle.setText("예상/복기");
		
		ListView listView = (ListView) getView().findViewById(R.id.list);
		listView.setDivider( null ); 
		listView.setDividerHeight(0);
		
		Button btnWrite = (Button) getActivity().findViewById(R.id.btnWrite);
		
		if ( mainActivity.getMetaInfoString("uid") != null &&
				!"".equals( mainActivity.getMetaInfoString("uid")))
			btnWrite.setVisibility( ViewGroup.VISIBLE );
		else
			btnWrite.setVisibility( ViewGroup.GONE );
		
		execTransReturningString("KrBoard/krBoardList.aspx?bGb=" + gBn + "&page=" + pageNo + "&refIdx=0", 1, null );
	}
	
	@Override
	public void doPostTransaction(int requestCode, String result) {
		
		try
		{
			// TODO Auto-generated method stub
			super.doPostTransaction(result);
			
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
				
				listView.setAdapter( new FreeboardItemAdapter( getActivity(), tempData ) );
				listView.setOnItemClickListener( this );
			}
			else if ( requestCode == 2 )
			{
				FreeboardItemAdapter adapter = (FreeboardItemAdapter) listView.getAdapter();
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
	
	public class FreeboardItemAdapter extends BaseAdapter {
        
        private Activity activity;
        private ArrayList<JSONObject> data;
        private LayoutInflater inflater=null;
        
        public FreeboardItemAdapter(Activity a, ArrayList<JSONObject> d) {
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
        
        public ArrayList<JSONObject> getData()
        {
        	return data;
        }
        
        public void setData( ArrayList<JSONObject> d )
        {
        	this.data = d;
        }
        
        public View getView(int position, View convertView, ViewGroup parent) {
        	try
        	{
        		View vi=convertView;
        		
        		JSONObject jsonObj = data.get(position);

        		if ( "ITEM1".equals( jsonObj.getString("TYPE") ) )
        		{
        			vi = inflater.inflate(R.layout.list_freeboard_item, null);
        			
        			TextView title = (TextView)vi.findViewById(R.id.title); // title
                    TextView txtDate = (TextView) vi.findViewById(R.id.txtDate);
                    TextView txtRefCount = (TextView) vi.findViewById(R.id.txtRefCount);
                    TextView txtAuthor = (TextView) vi.findViewById(R.id.txtAuthor);
                    TextView txtLikesCount = (TextView) vi.findViewById(R.id.txtLikesCount);
                    TextView txtComments = (TextView) vi.findViewById(R.id.txtComments);
                    
                    vi.setTag( jsonObj );
                    
                    title.setText( jsonObj.getString("t") );
                    txtDate.setText( jsonObj.getString("d").substring(0, 10) );
                    txtRefCount.setText( "조회: " + jsonObj.getString("r") );
                    txtAuthor.setText( jsonObj.getString("n") );
                    txtLikesCount.setText( jsonObj.getString("j") );
                    
                    ImageView imgReply = (ImageView) vi.findViewById(R.id.imgReply);
                    if ( jsonObj.getInt("re") < 1 )
                    	imgReply.setVisibility(ViewGroup.GONE);
                    else
                    	imgReply.setVisibility(ViewGroup.VISIBLE);
                    
                    FrameLayout f = (FrameLayout) vi.findViewById(R.id.fComments);
                    if ( jsonObj.getInt("c") < 1 )
                    {
                    	f.setVisibility(ViewGroup.GONE);
                    }
                    else
                    {
                    	f.setVisibility(ViewGroup.VISIBLE);
                    	txtComments.setText( jsonObj.getString("c") );
                    }
                    
        		}
        		else
        		{
        			vi = inflater.inflate(R.layout.list_item_more, null);
        			pageNo++;
        			execTransReturningString("KrBoard/krBoardList.aspx?bGb=" + gBn + "&page=" + pageNo + "&refIdx=0", 2, null );
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
			
			Intent intent = new Intent( getActivity(), FreeboardContentActivity.class );
			intent.putExtra("bGb", gBn );
			intent.putExtra("param", jsonObj.toString());
			startActivity( intent );
		}
		catch( Exception ex )
		{
			writeLog( ex.getMessage() );
		}
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		try
		{
			if ( mainActivity.getMetaInfoString("uid") == null ||
					"".equals( mainActivity.getMetaInfoString("uid") ) )
			{
				showOKDialog("로그인 후 작성이 가능합니다.", null );
				return;
			}
					
			Intent intent = new Intent( getActivity(), FreeboardWritePostActivity.class );
			intent.putExtra("bGb", gBn );
			intent.putExtra("mode", "NEW");
			startActivity( intent );
		}
		catch( Exception ex )
		{
			writeLog( ex.getMessage() );
		}
	}
}