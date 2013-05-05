package com.krking;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.common.Util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebView.FindListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class PaperViewFragment extends BaseFragment implements OnItemClickListener, OnClickListener
{
	MainActivity mainActivity;
	
	public PaperViewFragment( MainActivity main)
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
		
		View view = inflater.inflate(R.layout.fragment_paper_view, container, false);
		
		return view;
	}
	
	ProgressDialog pd;
	
	
	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		
		ListView listView = (ListView) getView().findViewById(R.id.list);
		listView.setDivider( null ); 
		listView.setDividerHeight(0);
		
		execTransReturningString("KrPaper/paperList.aspx?cmd=1", 1, null );
	}
	
	@SuppressLint("SetJavaScriptEnabled")
	@Override
	public void doPostTransaction(int requestCode, String result) {
		// TODO Auto-generated method stub
		try
		{
			super.doPostTransaction(requestCode, result);
			
			JSONObject resObj = new JSONObject( result );
			
			JSONArray jsonAr = resObj.getJSONArray("T");
			
			LinearLayout lPapercase = (LinearLayout) getActivity().findViewById(R.id.lPapercase);
			
			for ( int i = 0; i < jsonAr.length(); i++ )
			{
				JSONObject jsonObj = jsonAr.getJSONObject(i);
				
				FrameLayout f = (FrameLayout) lPapercase.getChildAt(i);
				
				ImageView iv = (ImageView) f.findViewWithTag("image");
				
				if ("금".equals( jsonObj.getString("w") ) )
					iv.setImageResource(R.drawable.paper_04_img_01_fri);
				else if ("토".equals( jsonObj.getString("w") ) )
					iv.setImageResource(R.drawable.paper_04_img_02_sat);
				else if ("일".equals( jsonObj.getString("w") ) )
					iv.setImageResource(R.drawable.paper_04_img_02_sun);
				
				f.setTag( jsonObj );
				iv.setOnClickListener( this );
				
				TextView tv = (TextView) f.findViewWithTag("info");
				tv.setText( jsonObj.getString("d") + "\r\n" + "총 " + jsonObj.getString("p") + "페이지");
			}
			
			ArrayList<JSONObject> tempData = Util.getArrayList( resObj.getJSONArray("L") );
			
			ListView listView = (ListView) getView().findViewById(R.id.list);
			listView.setAdapter( new PaperViewItemAdapter( getActivity(), tempData ) );
			listView.setOnItemClickListener( this );
		}
		catch( Exception ex )
		{
			writeLog( ex.getMessage() );
		}
	}
	
	
	public class PaperViewItemAdapter extends BaseAdapter {
        
        private Activity activity;
        private ArrayList<JSONObject> data;
        private LayoutInflater inflater=null;
        
        public PaperViewItemAdapter(Activity a, ArrayList<JSONObject> d) {
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
        		
        		ImageView ivDay = (ImageView) vi.findViewById(R.id.imgDay);

				if ( "금".equals( jsonObj.getString("w") ) )
					ivDay.setImageResource(R.drawable.btn_fri);
				else if ( "토".equals( jsonObj.getString("w") ) )
					ivDay.setImageResource(R.drawable.btn_sat);
				else if ( "일".equals( jsonObj.getString("w") ) )
					ivDay.setImageResource(R.drawable.btn_sun);
        		
        		TextView tv = (TextView) vi.findViewById(R.id.title);
        		tv.setText( jsonObj.getString("d") + " - " + jsonObj.getString("p") );
        		
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
			goPaperDetail(jsonObj);
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
			FrameLayout fLayout = (FrameLayout) v.getParent();
			goPaperDetail( (JSONObject) fLayout.getTag() );
		}
		catch( Exception ex )
		{
			writeLog( ex.getMessage() );
		}
	}
	
	public void goPaperDetail( JSONObject jsonObj )
	{
		Intent intent = new Intent( getActivity(), PaperDetailActivity.class );
		intent.putExtra("param", jsonObj.toString());
		startActivity( intent );
	}
}