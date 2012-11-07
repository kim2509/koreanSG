package com.korea.hanintown;

import org.json.JSONArray;
import org.json.JSONObject;

import com.korea.common.Util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;

public class ManageAttachmentActivity extends DYActivity
{
	JSONArray deletedArray = null;
	ArrayList<JSONObject> arAttachments = null;
	ArrayList<Bitmap> arBitmaps = null;
	ProgressDialog dialog = null;

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		try
		{
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_manage_attachment);

			String jsonAttachments = getIntent().getExtras().getString("attachments");

			JSONArray jsonAr = new JSONArray( jsonAttachments );

			arAttachments = new ArrayList<JSONObject>();

			for ( int i = 0; i < jsonAr.length(); i++ )
			{
				arAttachments.add( jsonAr.getJSONObject(i) );
			}

			arBitmaps = new ArrayList<Bitmap>();

			dialog = new ProgressDialog( this );
			dialog.setMessage("로딩중...");
			dialog.show();
			new FetchImageTask( "http", serverHost, serverPort ).execute();
		}
		catch( Exception ex )
		{
			writeLog( ex.getMessage() );
		}
	}

	public void loadListView()
	{
		ListView lv = (ListView) findViewById(R.id.list);
		lv.setAdapter( new AttachmentAdapter( this , arAttachments ));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_manage_attachment, menu);
		return true;
	}

	public class AttachmentAdapter extends BaseAdapter {

		private Activity activity;
		private ArrayList<JSONObject> data;
		private LayoutInflater inflater=null;

		public AttachmentAdapter(Activity a, ArrayList<JSONObject> d) {
			activity = a;
			data=d;
			inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		public ArrayList<JSONObject> getData()
		{
			return data;
		}

		public int getCount() {
			return data.size();
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
					vi = inflater.inflate(R.layout.list_attachment, null);

				TextView title = (TextView)vi.findViewById(R.id.title); // title
				ImageView thumb_image=(ImageView)vi.findViewById(R.id.list_image); // thumb image

				LinearLayout thumbnail = (LinearLayout) vi.findViewById(R.id.thumbnail); 

				JSONObject jsonObj = data.get(position);

				if ( "TEXT".equals( jsonObj.getString("TYPE")))
				{
					title.setText("본문");
					thumb_image.setImageBitmap(null);
					thumbnail.setVisibility(View.GONE);
					thumb_image.setVisibility(View.GONE);
				}
				else
				{
					title.setText("");
					thumbnail.setVisibility(View.VISIBLE);
					thumb_image.setVisibility(View.VISIBLE);

					thumb_image.setImageBitmap( arBitmaps.get(position) );
				}

				ImageButton imgBtnDelete = (ImageButton) vi.findViewById(R.id.imgBtnDelete);
				imgBtnDelete.setTag( String.valueOf( position ) );

				ImageButton imgBtnMoveUp = (ImageButton) vi.findViewById(R.id.imgBtnMoveUp);
				imgBtnMoveUp.setTag( String.valueOf( position ) );

				ImageButton imgBtnMoveDown = (ImageButton) vi.findViewById(R.id.imgBtnMoveDown);
				imgBtnMoveDown.setTag( String.valueOf( position ) );

				return vi;
			}
			catch( Exception ex )
			{
				writeLog( ex.getMessage() );
				return null;
			}
		}
	}

	class FetchImageTask extends AsyncTask<Void, Void, Void> {

		ImageView imgView = null;
		URL url = null;
		String protocol = "";
		String server = "";
		int port = 0;

		public FetchImageTask( String protocol, String server, int port )
		{
			try
			{
				this.protocol = protocol;
				this.server = server;
				this.port = port;
			}
			catch( Exception ex )
			{
				writeLog( ex.getMessage() );
			}
		}

		private InputStream OpenHttpConnection(String urlString)
				throws IOException
				{
			InputStream in = null;
			int response = -1;

			URL url = new URL(urlString);
			URLConnection conn = url.openConnection();

			if (!(conn instanceof HttpURLConnection))                    
				throw new IOException("Not an HTTP connection");

			try{
				HttpURLConnection httpConn = (HttpURLConnection) conn;
				httpConn.setAllowUserInteraction(false);
				httpConn.setInstanceFollowRedirects(true);
				httpConn.setRequestMethod("GET");
				httpConn.connect();

				response = httpConn.getResponseCode();                
				if (response == HttpURLConnection.HTTP_OK) {
					in = httpConn.getInputStream();                                
				}                    
			}
			catch (Exception ex)
			{
				throw new IOException("Error connecting");           
			}
			return in;    
				}
		private Bitmap downloadImage(String URL)
		{       
			Bitmap bitmap = null;
			InputStream in = null;       
			try {
				in = OpenHttpConnection(URL);
				bitmap = BitmapFactory.decodeStream(in);
				in.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			return bitmap;               
		}

		protected Void doInBackground(Void...voids) {
			try {

				writeLog("doInBackground start!");

				for ( int i = 0; i < arAttachments.size(); i++ )
				{
					JSONObject jsonObj = arAttachments.get(i);

					writeLog( i + " 0");

					writeLog( jsonObj.toString() );

					writeLog( i + " 1");

					if ( "TEXT".equals( jsonObj.get("TYPE"))) 
					{
						writeLog( i + " 2");
						arBitmaps.add( null );
						writeLog( i + " 3");
						continue;
					}

					Bitmap bm = null;
					if ( jsonObj.get("ID") != null && !"".equals( jsonObj.get("ID")))
					{
						String urlString = jsonObj.getString("URL");
						bm = downloadImage( urlString );
					}
					else
					{
						bm = Util.getResizedBitmap(jsonObj.getString("URL"), 48, 48);
					}

					arBitmaps.add( bm );
				}

				writeLog("doInBackground ended!");

			} catch (Exception e) {
				writeLog( e.getMessage() );
			}

			return null;
		}

		protected void onPostExecute( Void result ) {
			// TODO: check this.exception 
			// TODO: do something with the feed
			if (dialog.isShowing())
				dialog.dismiss();

			loadListView();
		}
	}


	public void deleteItem( View v )
	{
		try
		{
			showYesNoDialog("정말 삭제하시겠습니까?", v.getTag() );
		}
		catch( Exception ex )
		{
			writeLog( ex.getMessage() );
		}
	}

	@Override
	public void yesClicked(Object param) {
		// TODO Auto-generated method stub
		try
		{
			super.yesClicked(param);

			int position = Integer.parseInt( param.toString() );

			ListView lv = (ListView) findViewById(R.id.list);
			AttachmentAdapter adapter = (AttachmentAdapter) lv.getAdapter();

			ArrayList<JSONObject> data = adapter.getData();

			JSONObject jsonObj = data.get(position);

			if ( "TEXT".equals( jsonObj.getString("TYPE") ) )
			{
				showOKDialog("본문은 삭제하실수 없습니다.", null );
				return;
			}

			if ( deletedArray == null )
				deletedArray = new JSONArray();

			deletedArray.put( jsonObj.getString("ID") );

			data.remove( position );
			arBitmaps.remove(position);

			adapter.notifyDataSetChanged();
		}
		catch( Exception ex )
		{
			writeLog( ex.getMessage() );
		}
	}

	public void moveItemUp( View v )
	{
		try
		{
			ListView lv = (ListView) findViewById(R.id.list);
			AttachmentAdapter adapter = (AttachmentAdapter) lv.getAdapter();

			ArrayList<JSONObject> data = adapter.getData();

			int position = Integer.parseInt( v.getTag().toString() );

			if ( position == 0 )
				return;

			Collections.swap( data, position, position - 1 );
			Collections.swap( arBitmaps, position, position - 1 );

			adapter.notifyDataSetChanged();
		}
		catch( Exception ex )
		{
			writeLog( ex.getMessage() );
		}
	}

	public void moveItemDown( View v )
	{
		try
		{
			ListView lv = (ListView) findViewById(R.id.list);
			AttachmentAdapter adapter = (AttachmentAdapter) lv.getAdapter();

			ArrayList<JSONObject> data = adapter.getData();

			int position = Integer.parseInt( v.getTag().toString() );

			if ( position >= data.size() - 1 )
				return;

			Collections.swap( data, position, position + 1 );
			Collections.swap( arBitmaps, position, position + 1 );

			adapter.notifyDataSetChanged();
		}
		catch( Exception ex )
		{
			writeLog( ex.getMessage() );
		}
	}

	public void saveAttachments( View v )
	{
		ListView lv = (ListView) findViewById(R.id.list);
		AttachmentAdapter adapter = (AttachmentAdapter) lv.getAdapter();
		ArrayList<JSONObject> data = adapter.getData();
		JSONArray jsonAr = new JSONArray( data );
		Intent intent = new Intent();
		intent.putExtra("attachments", jsonAr.toString());

		if ( deletedArray != null )
			intent.putExtra("delete", deletedArray.toString());

		setResult(RESULT_OK, intent);

		finish();
	}
}
