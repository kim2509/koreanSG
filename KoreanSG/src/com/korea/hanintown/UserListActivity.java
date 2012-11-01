package com.korea.hanintown;

import org.apache.http.util.EncodingUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.korea.common.Constants;

import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class UserListActivity extends DYActivity implements OnItemClickListener{

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		try
		{
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_user_list);

			initializeControls();

			execTrans("iphone/userList.php", getJSONDataWithDefaultSetting() , true );
		}
		catch( Exception ex )
		{
			writeLog( ex.getMessage() );
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_user_list, menu);
		return true;
	}

	public void initializeControls()
	{
		TextView txtTitle = (TextView) findViewById(R.id.txtTitle);
		txtTitle.setText("받을사람 선택");
	}

	@Override
	public void doPostTransaction(Object result) {
		try
		{
			// TODO Auto-generated method stub
			super.doPostTransaction(result);	

			if ( checkJSONResult2( result ) == false ) return;

			JSONArray jsonArr = (JSONArray) result;
			jsonArr = jsonArr.getJSONArray(0);

			final ListView lv = (ListView) findViewById(R.id.ListView01);
			lv.setAdapter( new UserListAdapter( this , jsonArr));
			lv.setOnItemClickListener( this );
		}
		catch( Exception ex )
		{
			writeLog( ex.getMessage() );
		}
	}
	
	public class UserListAdapter extends BaseAdapter {
        
        private Activity activity;
        private JSONArray data;
        private LayoutInflater inflater=null;
        
        public UserListAdapter(Activity a, JSONArray d) {
            activity = a;
            data=d;
            inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public int getCount() {
            return data.length();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }
        
        public View getView(int position, View convertView, ViewGroup parent) {
        	try
        	{
        		View vi=convertView;
                if(convertView==null)
                    vi = inflater.inflate(R.layout.list_black_text, null);

                TextView title = (TextView)vi.findViewById(R.id.list_content); // title
                
                JSONObject jsonObj = data.getJSONObject(position);
                
                vi.setTag( jsonObj );
                
                title.setText( jsonObj.getString("NICKNAME") + "(" + jsonObj.getString("USER_ID") + ")");
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
		// TODO Auto-generated method stub
		try
		{
			JSONObject jsonObj = (JSONObject) arg1.getTag();
			Intent intent = new Intent( this, SendMessageActivity.class );
			intent.putExtra("param", jsonObj.toString() );
			startActivityForResult( intent , Constants.REQUEST_CODE_COMMON);
		}
		catch( Exception ex )
		{
			writeLog( ex.getMessage() );
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		try
		{
			if ( requestCode == Constants.REQUEST_CODE_COMMON &&
					resultCode == RESULT_OK )
			{
				setResult(RESULT_OK);
				finish();
			}
			
			super.onActivityResult(requestCode, resultCode, data);
		}
		catch( Exception ex )
		{
			writeLog( ex.getMessage());
		}
	}
}
