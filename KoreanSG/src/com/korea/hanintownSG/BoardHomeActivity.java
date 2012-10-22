package com.korea.hanintownSG;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.korea.common.Constants;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class BoardHomeActivity extends DYActivity implements OnItemClickListener, OnClickListener{

    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
    	try
    	{
    		super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_board_home);
            
            execTransReturningString("iphone/getBoardMainInfo.php", new JSONObject());
    	}
    	catch( Exception ex )
    	{
    		writeLog( ex.getMessage() );
    	}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_board_home, menu);
        return true;
    }
    
    @Override
    public void doPostTransaction(Object result) {
    	try
    	{
    		// TODO Auto-generated method stub
        	super.doPostTransaction(result);
        	
        	if ( result == null )
			{
				showOKDialog("응답이 올바르지 않습니다.\n다시 시도해 주십시오.", null);
				return;
			}
        	
        	JSONObject jsonObj = new JSONObject( result.toString() );
			
			if ( jsonObj.length() < 1 )
			{
				showOKDialog("응답이 올바르지 않습니다.\n다시 시도해 주십시오.", null);
				return;
			}
			
			JSONArray serviceList = jsonObj.getJSONArray("serviceList");
			serviceList = serviceList.getJSONArray(0);
			
			ListView lv = (ListView) findViewById(R.id.list);
			lv.setOnItemClickListener( this );
			lv.setAdapter( new BoardMenuAdapter( this , serviceList));
			
			LinearLayout layoutImageParent = (LinearLayout) findViewById(R.id.layoutImageParent);
			JSONArray imageList = jsonObj.getJSONArray("imageList").getJSONArray(0);
			for ( int i = 0; i < imageList.length(); i++ )
			{
				LinearLayout layoutImage = new LinearLayout( this );
				layoutImage.setTag( imageList.getJSONObject( i ) );
				layoutImageParent.addView( layoutImage );
				ViewGroup.LayoutParams lParam = layoutImage.getLayoutParams();
				lParam.width = getPixelFromDP( 120 );
				lParam.height = getPixelFromDP( 120 );
				layoutImage.setLayoutParams( lParam );
				layoutImage.setOrientation(LinearLayout.VERTICAL);
				
				Button iv = new Button( this );
				layoutImage.addView( iv );
				iv.setTag("image");
				
				LinearLayout.LayoutParams ivParam = (LinearLayout.LayoutParams) iv.getLayoutParams();
				ivParam.width = getPixelFromDP( 16 );
				ivParam.height = getPixelFromDP( 16 );
				ivParam.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;
				ivParam.topMargin = getPixelFromDP(30);
				ivParam.bottomMargin = getPixelFromDP(10);
				iv.setBackgroundResource( R.drawable.loading16 );
				
				TextView tv = new TextView( this );
				layoutImage.addView( tv );
				tv.setTag("title");
				LinearLayout.LayoutParams tvLParam = (LinearLayout.LayoutParams) tv.getLayoutParams();
				tvLParam.width = getPixelFromDP( 120 );
				tvLParam.height = getPixelFromDP( 20 );
				tv.setGravity( Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL );
				tv.setTextColor(Color.WHITE);
				tv.setText("로딩중...");
			}
			
			new ImageFetchTask().execute((Void)null);
    	}
    	catch( Exception ex )
    	{
    		writeLog( ex.getMessage() );
    	}
    }
    
    public class BoardMenuAdapter extends BaseAdapter {
        
        private Activity activity;
        private JSONArray data;
        private LayoutInflater inflater=null;
        
        public BoardMenuAdapter(Activity a, JSONArray d) {
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
                    vi = inflater.inflate(R.layout.list_board_menu, null);

                TextView title = (TextView)vi.findViewById(R.id.title); // title
                ImageView thumb_image=(ImageView)vi.findViewById(R.id.list_image); // thumb image
                
                JSONObject jsonObj = data.getJSONObject(position);
                
                vi.setTag( jsonObj );
                
                // Setting all values in listview
                title.setText( jsonObj.getString("MENU_NAME") + " (" + jsonObj.getString("ITEM_COUNT") + ")");
                
                if ( "자유게시판".equals( jsonObj.getString("MENU_NAME") ))
                    thumb_image.setImageResource(R.drawable.free_board);
                else if ( "질문/답변 게시판".equals( jsonObj.getString("MENU_NAME") ))
                	thumb_image.setImageResource(R.drawable.question);
                else if ( "로컬정보공유 게시판".equals( jsonObj.getString("MENU_NAME") ))
                	thumb_image.setImageResource(R.drawable.singapore);
                else if ( "번개게시판".equals( jsonObj.getString("MENU_NAME") ))
                	thumb_image.setImageResource(R.drawable.meet);
                else if ( "여행게시판".equals( jsonObj.getString("MENU_NAME") ))
                	thumb_image.setImageResource(R.drawable.travel);
                else if ( "사진갤러리".equals( jsonObj.getString("MENU_NAME") ))
                	thumb_image.setImageResource(R.drawable.gallery);
                else if ( "문의/건의 사항".equals( jsonObj.getString("MENU_NAME") ))
                	thumb_image.setImageResource(R.drawable.idea);
                else if ( "프로모션정보 게시판".equals( jsonObj.getString("MENU_NAME") ))
                	thumb_image.setImageResource(R.drawable.promotion);
                else if ( "취업정보공유 게시판".equals( jsonObj.getString("MENU_NAME") ))
                	thumb_image.setImageResource(R.drawable.job);
                else if ( "환전 게시판".equals( jsonObj.getString("MENU_NAME") ))
                	thumb_image.setImageResource(R.drawable.money_exchange);
                else {
                	thumb_image.setImageResource(R.drawable.icon_etc);
                }
                
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

			Intent intent = new Intent( this , BoardItemListActivity.class);
			intent.putExtra("param", jsonObj.toString() );
			startActivity(intent);
		}
		catch( Exception ex )
		{
			writeLog( ex.getMessage() );
		}
	}
    
    
    private class ImageFetchTask extends AsyncTask<Void, Integer, ArrayList<Bitmap>> {

		public ImageFetchTask()
		{
		}

		protected void onPreExecute() {
		}

		protected ArrayList<Bitmap> doInBackground( Void... data ) {

			try
        	{
        		LinearLayout layout = (LinearLayout) findViewById(R.id.layoutImageParent);
        		ArrayList<Bitmap> ar = new ArrayList<Bitmap>();
        		
            	for ( int i = 0; i < layout.getChildCount(); i++ )
            	{
            		LinearLayout layoutChild = (LinearLayout) layout.getChildAt(i);
            		JSONObject obj = (JSONObject) layoutChild.getTag();
            		URL url = new URL( "http", serverHost, serverPort, 
            				obj.getString("PATH") + "mobile/" + obj.getString("FILE_NAME") );
            		InputStream is = (InputStream) url.getContent();
		        	Bitmap bm = BitmapFactory.decodeStream( is );
		        	ar.add( bm );
            	}	
            	
            	return ar;
        	}
        	catch( Exception ex )
        	{
        		writeLog( ex.getMessage() );
        	}

			return null;
		}

		protected void onProgressUpdate(Integer... progress) {

		}

		protected void onPostExecute(ArrayList<Bitmap> result) {
			updateImage( result );
		}
	}
    
    public void updateImage( ArrayList<Bitmap> result )
    {
    	try
    	{
    		LinearLayout layout = (LinearLayout) findViewById(R.id.layoutImageParent);
    		
        	for ( int i = 0; i < layout.getChildCount(); i++ )
        	{
        		LinearLayout layoutChild = (LinearLayout) layout.getChildAt(i);
        		
        		LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) layoutChild.getLayoutParams();
        		lp.leftMargin = getPixelFromDP(5);
        		lp.rightMargin = getPixelFromDP(5);
        		
        		JSONObject obj = (JSONObject) layoutChild.getTag();
        		Bitmap bm = result.get(i);
        		
            	if ( bm != null )
            	{
            		Button imgBtn = (Button) layoutChild.findViewWithTag("image");
            		imgBtn.setTag( obj );
            		imgBtn.setOnClickListener( this );
            		LinearLayout.LayoutParams ivParam = (LinearLayout.LayoutParams) imgBtn.getLayoutParams();
            		ivParam.topMargin = getPixelFromDP(5);
            		ivParam.bottomMargin = getPixelFromDP(5);
    				ivParam.width = getPixelFromDP( 120 );
    				ivParam.height = getPixelFromDP( 80 );
            		imgBtn.setBackgroundDrawable( new BitmapDrawable( bm ) );
            		
            		TextView tv = (TextView) layoutChild.findViewWithTag("title");
            		tv.setText( obj.getString("B_SUBJECT") );
            	}
        	}	
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
			JSONObject obj = (JSONObject) v.getTag();
			
			JSONObject jsonObj = new JSONObject();
        	jsonObj.put("BOARD_NAME", obj.getString("BOARD_NAME"));
        	jsonObj.put("BID", obj.getString("B_ID"));
        	jsonObj.put("SUBJECT", obj.getString("B_SUBJECT"));
        	jsonObj.put("USER_ID", getMetaInfoString("USER_ID"));
        	
        	Intent intent = new Intent( this , BoardItemContentActivity.class);
			intent.putExtra("param", jsonObj.toString() );
		    startActivityForResult(intent, Constants.REQUEST_CODE_COMMON2 );
		}
		catch( Exception ex )
		{
			writeLog( ex.getMessage() );
		}
	}
}
