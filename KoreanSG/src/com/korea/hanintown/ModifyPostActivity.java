package com.korea.hanintown;

import java.util.ArrayList;

import com.ericharlow.DragNDrop.DragListener;
import com.ericharlow.DragNDrop.DragNDropAdapter;
import com.ericharlow.DragNDrop.DragNDropListView;
import com.ericharlow.DragNDrop.DropListener;
import com.ericharlow.DragNDrop.RemoveListener;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ModifyPostActivity extends DYActivity implements OnItemClickListener
{

	private static String[] mListContent={"Item 1", "Item 2", "Item 3", "Item 4", "Item 5", "Item 6", "Item 7", 
		"Item 8", "Item 9", "Item 10", "Item 11", "Item 12", "Item 13", "Item 14", "Item 15"};

	com.ericharlow.DragNDrop.DragNDropListView dList;

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		try
		{
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_modify_post);

			ArrayList<String> content = new ArrayList<String>(mListContent.length);
			for (int i=0; i < mListContent.length; i++) {
				content.add(mListContent[i]);
			}

			dList = (com.ericharlow.DragNDrop.DragNDropListView) findViewById(R.id.dList);

			dList.setAdapter(new DragNDropAdapter(this, new int[]{R.layout.dragitem}, new int[]{R.id.TextView01}, content));//new DragNDropAdapter(this,content)
			ListView listView = getListView();

			if (listView instanceof DragNDropListView) {
				((DragNDropListView) listView).setDropListener(mDropListener);
				((DragNDropListView) listView).setRemoveListener(mRemoveListener);
				((DragNDropListView) listView).setDragListener(mDragListener);
			}

			listView.setOnItemClickListener( this );

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
			Toast.makeText(this, "dsafdsaf", Toast.LENGTH_LONG).show();
		}
		catch( Exception ex )
		{

		}
	}

	public ListAdapter getListAdapter()
	{
		return dList.getAdapter();
	}

	public ListView getListView()
	{
		return dList;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_modify_post, menu);
		return true;
	}


	private DropListener mDropListener = 
			new DropListener() {
		public void onDrop(int from, int to) {
			ListAdapter adapter = getListAdapter();
			if (adapter instanceof DragNDropAdapter) {
				((DragNDropAdapter)adapter).onDrop(from, to);
				getListView().invalidateViews();
			}
		}
	};

	private RemoveListener mRemoveListener =
			new RemoveListener() {
		public void onRemove(int which) {
			ListAdapter adapter = getListAdapter();
			if (adapter instanceof DragNDropAdapter) {
				((DragNDropAdapter)adapter).onRemove(which);
				getListView().invalidateViews();
			}
		}
	};

	private DragListener mDragListener =
			new DragListener() {

		int backgroundColor = 0xe0103010;
		int defaultBackgroundColor;

		public void onDrag(int x, int y, ListView listView) {
			// TODO Auto-generated method stub
		}

		public void onStartDrag(View itemView) {
			itemView.setVisibility(View.INVISIBLE);
			defaultBackgroundColor = itemView.getDrawingCacheBackgroundColor();
			itemView.setBackgroundColor(backgroundColor);
			ImageView iv = (ImageView)itemView.findViewById(R.id.ImageView01);
			if (iv != null) iv.setVisibility(View.INVISIBLE);
		}

		public void onStopDrag(View itemView) {
			itemView.setVisibility(View.VISIBLE);
			itemView.setBackgroundColor(defaultBackgroundColor);
			ImageView iv = (ImageView)itemView.findViewById(R.id.ImageView01);
			if (iv != null) iv.setVisibility(View.VISIBLE);
		}

	};

}
