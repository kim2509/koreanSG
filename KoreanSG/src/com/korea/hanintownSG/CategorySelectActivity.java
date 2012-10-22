package com.korea.hanintownSG;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class CategorySelectActivity extends DYActivity implements OnClickListener, OnItemClickListener{

	private String selectedCategoryID;
	private String selectedCategoryName;
	private boolean bShowAll = false;
	private String boardName = "";
	JSONArray categoryList = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		try
		{
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_category_select);	

			initializeControls();
		}
		catch( Exception ex )
		{
			writeLog( ex.getMessage() );
		}
	}

	private void initializeControls() throws Exception  
	{

		Button btnBack = (Button) findViewById(R.id.btnBack);
		btnBack.setOnClickListener(this);

		ListView listView = (ListView) findViewById(R.id.list);
		listView.setOnItemClickListener( this );

		bShowAll = getIntent().getExtras().getBoolean("showAll");
		
		String jsonString = getIntent().getExtras().get("param").toString();
		JSONObject jsonObject = new JSONObject( jsonString );
		boardName = jsonObject.getString("BOARD_NAME");

		categoryList = getBoardCategoryList(boardName, bShowAll );

		listView.setAdapter( new MyPerformanceArrayAdapter(this, getCategoryNameArray( categoryList )));
	}

	public ArrayList<String> getCategoryNameArray( JSONArray categoryList ) throws Exception 
	{
		ArrayList<String> ar = new ArrayList<String>();

		for ( int i = 0; i < categoryList.length(); i++ )
		{
			JSONObject jsonObj = categoryList.getJSONObject(i);
			ar.add( jsonObj.getString("CATEGORY_NAME"));
		}

		return ar;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_category_select, menu);
		return true;
	}

	public class MyPerformanceArrayAdapter extends ArrayAdapter<String> {
		private final Activity context;
		private final ArrayList<String> names;

		class ViewHolder {
			public CheckedTextView text;
		}

		public MyPerformanceArrayAdapter(Activity context, ArrayList<String> names) {
			super(context, R.layout.list_row, names);
			this.context = context;
			this.names = names;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) 
		{
			try
			{
				View rowView = convertView;
				if (rowView == null) {
					LayoutInflater inflater = context.getLayoutInflater();
					rowView = inflater.inflate(R.layout.list_row, null);
					ViewHolder viewHolder = new ViewHolder();
					viewHolder.text = (CheckedTextView) rowView.findViewById(R.id.ctCategoryItem);
					rowView.setTag(viewHolder);
				}

				ViewHolder holder = (ViewHolder) rowView.getTag();
				String s = names.get(position).toString();
				holder.text.setText(s);
				
				if ( bShowAll && "전체".equals( s ) && getMetaInfoString( boardName + "_CATEGORY_ID").equals("") )
				{
					holder.text.setChecked(true);
					holder.text.setCheckMarkDrawable(android.R.drawable.checkbox_on_background);
				}
				else
				{
					JSONObject jsonObj = categoryList.getJSONObject(position);
					if ( getMetaInfoString( boardName + "_CATEGORY_ID").equals( jsonObj.getString("ID")))
					{
						holder.text.setChecked(true);
						holder.text.setCheckMarkDrawable(android.R.drawable.checkbox_on_background);
					}
					else
					{
						holder.text.setChecked(false);
						holder.text.setCheckMarkDrawable(android.R.drawable.checkbox_off_background);	
					}
				}

				return rowView;	
			}
			catch( Exception ex )
			{
				writeLog( ex.getMessage() );
				
				return null;
			}
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		try
		{
			if ( v.getId() == R.id.btnBack )
				finish();
		}
		catch( Exception ex )
		{
			writeLog( ex.getMessage() );
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		try
		{
			if ( arg1.getTag() instanceof MyPerformanceArrayAdapter.ViewHolder)
			{
				ListView lv = (ListView) arg0;

				for ( int i = 0; i < lv.getChildCount(); i++ )
				{
					if ( arg2 == i ) continue;
					
					View v = lv.getChildAt(i);
					CheckedTextView ctv = (CheckedTextView) v.findViewById(R.id.ctCategoryItem );
					ctv.setChecked(false);
					ctv.setCheckMarkDrawable(android.R.drawable.checkbox_off_background);
				}

				MyPerformanceArrayAdapter.ViewHolder holder = (MyPerformanceArrayAdapter.ViewHolder) arg1.getTag();
				holder.text.setChecked(true);
				holder.text.setCheckMarkDrawable(android.R.drawable.checkbox_on_background);
				
				String categoryID = ((JSONObject)categoryList.getJSONObject(arg2)).getString("ID");
				String categoryName = ((JSONObject)categoryList.getJSONObject(arg2)).getString("CATEGORY_NAME");
				
				if ( bShowAll )
				{
					// regard true as being called from BoardItemListActivity, not from BoardItemContent
					setMetaInfo( boardName + "_CATEGORY_ID", categoryID);
					setMetaInfo( boardName + "_CATEGORY_NAME", categoryName);	
				}
				
				Intent intent = new Intent();
				intent.putExtra("selectedCategoryID", categoryID );
				intent.putExtra("selectedCategoryName", categoryName );
				setResult(RESULT_OK, intent );
				
				finish();
			}
		}
		catch( Exception ex )
		{
			writeLog( ex.getMessage() );
		}
	}
}
