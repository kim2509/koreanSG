package com.krking;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.common.Util;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class FreeboardCommentsActivity extends BaseActivity implements OnClickListener, OnItemClickListener{

	JSONObject paramObject = null;
	int gBn = 1;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
    	try
    	{
    		super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_freeboard_comments);	
            
            gBn = getIntent().getExtras().getInt("bGb");
            
            paramObject = new JSONObject( getIntent().getExtras().getString("param") );
            
            TextView txtSubTitle = (TextView) findViewById(R.id.txtSubTitle);
            txtSubTitle.setText( paramObject.getString("t"));
            
            Button btnWriteComment = (Button) findViewById(R.id.btnWriteComment);
            btnWriteComment.setOnClickListener( this );
    	}
    	catch( Exception ex )
    	{
    		writeLog( ex.getMessage() );
    	}
    }
    
    @Override
	protected void onStart() {
		// TODO Auto-generated method stub
		try
		{
			super.onStart();
			
			reload();
		}
		catch( Exception ex )
		{
			writeLog( ex.getMessage() );
		}
	}

	private void reload() throws JSONException {
		String paramString = getIntent().getExtras().getString("param");
		JSONObject jsonObj = new JSONObject( paramString );
		
		execTransReturningString("krBoard/krBoardView.aspx?bGb=" + gBn + "&idx=" + jsonObj.getString("i") 
				+"&bUID=" + getMetaInfoString("uid") , 1, null );
	}
    
    @Override
    public void doPostTransaction(int requestCode, String result) {
    	// TODO Auto-generated method stub
    	try
    	{
    		super.doPostTransaction(requestCode, result);
    		
    		JSONObject resObj = new JSONObject( result );
    		
    		if ( requestCode == 1 )
    		{
    			ListView listView = (ListView) findViewById(R.id.list);
                ArrayList<JSONObject> tempData = Util.getArrayList( resObj.getJSONArray("COMMENT") );
                listView.setAdapter( new FreeboardCommentItemAdapter( this, tempData ) );
    			listView.setOnItemClickListener( this );	
    		}
    		else if ( requestCode == 2 )
    		{
    			reload();
    		}
    	}
    	catch( Exception ex )
    	{
    		writeLog( ex.getMessage() );
    	}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_freeboard_comments, menu);
        return true;
    }
    
    public class FreeboardCommentItemAdapter extends BaseAdapter {
        
        private Activity activity;
        private ArrayList<JSONObject> data;
        private LayoutInflater inflater=null;
        
        public FreeboardCommentItemAdapter(Activity a, ArrayList<JSONObject> d) {
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

        		if ( vi == null )
        			vi = inflater.inflate(R.layout.list_freeboard_comment_item, null);
        		
        		ImageView imgReply = (ImageView) vi.findViewById(R.id.imgReply);
        		if ( jsonObj.getInt("CommDepth") > 0 )
        			imgReply.setVisibility(ViewGroup.VISIBLE);
        		else
        			imgReply.setVisibility(ViewGroup.GONE);
        		
        		TextView txtAuthor = (TextView) vi.findViewById(R.id.txtAuthor);
        		txtAuthor.setText( jsonObj.getString("n") );
        		TextView txtDate = (TextView) vi.findViewById(R.id.txtDate);
        		txtDate.setText( jsonObj.getString("d") );
        		TextView txtComment = (TextView) vi.findViewById(R.id.txtComment);
        		txtComment.setText( jsonObj.getString("c") );
        		
        		Button btnReply = (Button) vi.findViewById(R.id.btnReply);
        		btnReply.setOnClickListener( FreeboardCommentsActivity.this );
        		
        		if ( "Y".equals( jsonObj.getString("BR") ) )
        		{
        			btnReply.setBackgroundResource(R.drawable.btn_reply);
        			btnReply.setEnabled(true);
        		}
        		else
        		{
        			btnReply.setBackgroundResource(R.drawable.btn_reply_disable);
            		btnReply.setEnabled(false);
        		}
        		
        		Button btnModify = (Button) vi.findViewById(R.id.btnModify);
        		btnModify.setOnClickListener( FreeboardCommentsActivity.this );
        		if ( "Y".equals( jsonObj.getString("BM") ) )
        		{
        			btnModify.setBackgroundResource(R.drawable.btn_modification);
        			btnModify.setEnabled(true);
        		}
        		else
        		{
        			btnModify.setBackgroundResource(R.drawable.btn_modification_disable);
        			btnModify.setEnabled(false);	
        		}
        		
        		Button btnDelete = (Button) vi.findViewById(R.id.btnDelete);
        		btnDelete.setOnClickListener( FreeboardCommentsActivity.this );
        		if ( "Y".equals( jsonObj.getString("BD") ) )
        		{
        			btnDelete.setBackgroundResource(R.drawable.btn_delete);
        			btnDelete.setEnabled(true);
        		}
        		else
        		{
        			btnDelete.setBackgroundResource(R.drawable.btn_delete2_disable);
        			btnDelete.setEnabled(false);	
        		}
        		
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
	public void onClick(View v) {
		// TODO Auto-generated method stub
		try
		{
			RelativeLayout r = (RelativeLayout) v.getParent().getParent();
			JSONObject jsonObj = (JSONObject) r.getTag();
			
			if ( v.getId() == R.id.btnReply )
			{
				Intent intent = new Intent( this, FreeboardWriteCommentActivity.class );
				intent.putExtra("mode", "REP" );
				intent.putExtra("bGb", gBn );
				intent.putExtra("CommRef", jsonObj.getString("CommRef") );
				intent.putExtra("comment", jsonObj.getString("c") );
				intent.putExtra("cindex", jsonObj.getString("k") );
				intent.putExtra("paramParent", getIntent().getExtras().getString("param"));
				startActivity( intent );
			}
			else if ( v.getId() == R.id.btnModify )
			{
				Intent intent = new Intent( this, FreeboardWriteCommentActivity.class );
				intent.putExtra("mode", "EDT" );
				intent.putExtra("bGb", gBn );
				intent.putExtra("CommRef", jsonObj.getString("CommRef") );
				intent.putExtra("comment", jsonObj.getString("c") );
				intent.putExtra("cindex", jsonObj.getString("k") );
				intent.putExtra("paramParent", getIntent().getExtras().getString("param"));
				startActivity( intent );
			}
			else if ( v.getId() == R.id.btnDelete )
			{
				showYesNoDialog("정말 삭제하시겠습니까?", jsonObj );
			}
			else
			{
				if ( getMetaInfoString("uid") == null ||
						"".equals( getMetaInfoString("uid") ) )
				{
					showOKDialog("로그인 후 작성이 가능합니다.", null );
					return;
				}
				
				Intent intent = new Intent( this, FreeboardWriteCommentActivity.class );
				intent.putExtra("mode", "INS" );
				intent.putExtra("CommRef", "0" );
				intent.putExtra("cindex", "0" );
				intent.putExtra("bGb", gBn );
				intent.putExtra("paramParent", getIntent().getExtras().getString("param"));
				startActivity( intent );
			}
		}
		catch( Exception ex )
		{
			writeLog( ex.getMessage() );
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void yesClicked(Object param) {
		// TODO Auto-generated method stub
		try
		{
			super.yesClicked(param);
			
			JSONObject jsonObj = (JSONObject) param;
			
			execTransReturningString("krBoard/krBoardCommentDelete.aspx?cGb=" + gBn + "&bidx=" + jsonObj.getString("k") 
					+"&bUID=" + getMetaInfoString("uid") , 2, null );
		}
		catch( Exception ex )
		{
			writeLog( ex.getMessage() );
		}
	}
}
