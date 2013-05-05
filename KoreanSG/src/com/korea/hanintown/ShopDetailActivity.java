package com.korea.hanintown;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.korea.common.Constants;
import com.korea.common.ShopCommentDao;
import com.korea.common.ShopDataSource;
import com.korea.common.ShopLikeDao;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ShopDetailActivity extends DYActivity implements OnClickListener{

	private ShopDataSource shopDC;
	private JSONObject paramObject = null;

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		try
		{
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_shop_detail);

			shopDC = new ShopDataSource( this );
			shopDC.open();

			paramObject = new JSONObject( getIntent().getExtras().getString("param") );

			TextView txtTitle = (TextView) findViewById(R.id.txtTitle);
			txtTitle.setText( paramObject.getString("SHOPNAME") );

			ArrayList<JSONObject> data = new ArrayList<JSONObject>();
			ListView lv = (ListView) findViewById(R.id.list);
			lv.setDescendantFocusability(ListView.FOCUS_AFTER_DESCENDANTS);
			lv.setDivider( null );
			lv.setAdapter( new ShopInfoItemAdapter( this, data ) );

			reloadListData();
		}
		catch( Exception ex )
		{
			writeLog( ex.getMessage() );
		}
	}

	private void reloadListData() throws JSONException, Exception {
		ArrayList<JSONObject> data = new ArrayList<JSONObject>();

		JSONObject obj = new JSONObject();
		obj.put("CELL_TYPE", "BASIC_HEADER");
		obj.put("TITLE", "기본정보");
		data.add( obj );
		paramObject.put("CELL_TYPE", "BASIC_INFO");
		data.add( paramObject );
		obj = new JSONObject();
		obj.put("CELL_TYPE", "BASIC_HEADER");
		obj.put("TITLE", "Likes and 댓글");
		data.add( obj );

		boolean bDoesUserLike = shopDC.doesUserLikeShop( getMetaInfoString("USER_NO"), paramObject.getString("SEQ"));
		int shopLikesCount = shopDC.getShopLikesCount( paramObject.getString("SEQ") );

		obj = new JSONObject();
		obj.put("CELL_TYPE", "SHOP_LIKE_CELL");
		obj.put("doesUserLike", bDoesUserLike);
		obj.put("shopLikesCount", shopLikesCount);
		data.add( obj );

		ArrayList<JSONObject> ar = shopDC.getShopComments( paramObject.getString("SEQ") );
		for ( int i = 0; i < ar.size(); i++ )
		{
			obj = ar.get(i);
			obj.put("CELL_TYPE", "SHOP_COMMENT_CELL");
			data.add( obj );
		}

		obj = new JSONObject();
		obj.put("CELL_TYPE", "LIKE_BUTTON_CELL");
		obj.put("doesUserLike", bDoesUserLike);
		data.add( obj );
		obj = new JSONObject();
		obj.put("CELL_TYPE", "COMMENT_INPUT_CELL");
		data.add( obj );

		ListView lv = (ListView) findViewById(R.id.list);
		ShopInfoItemAdapter adapter = (ShopInfoItemAdapter) lv.getAdapter();
		adapter.setData( data );
		adapter.notifyDataSetChanged();

		JSONObject request = getJSONDataWithDefaultSetting();
		request.put("shopNo", paramObject.getString("SEQ"));
		execTransReturningString("android/getAllMenuByShop.php", request , Constants.REQUEST_CODE_COMMON, false );
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		shopDC.open();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		shopDC.close();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_shop_detail, menu);
		return true;
	}

	public class ShopInfoItemAdapter extends BaseAdapter {

		private DYActivity activity;
		private ArrayList<JSONObject> data;
		private LayoutInflater inflater=null;

		public ShopInfoItemAdapter(DYActivity a, ArrayList<JSONObject> d) {
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

		public void setData( ArrayList<JSONObject> data )
		{
			this.data = data;
		}

		public ArrayList<JSONObject> getData()
		{
			return this.data;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			try
			{
				View vi=convertView;

				JSONObject jsonObj = null;

				if ( vi == null )
					jsonObj = data.get(position);
				else
					jsonObj = (JSONObject) vi.getTag();

				boolean bReInflate = true;

				if ( vi != null )
				{
					if ( jsonObj.getString("CELL_TYPE").equals( data.get(position).getString("CELL_TYPE")))
						bReInflate = false;
					jsonObj = data.get(position);
				}

				if ( "BASIC_HEADER".equals( jsonObj.getString("CELL_TYPE")))
				{
					if ( bReInflate )
						vi = inflater.inflate(R.layout.list_shop_info_header, null);	

					TextView txtTitle = (TextView)vi.findViewById(R.id.txtTitle); // title
					txtTitle.setText( jsonObj.getString("TITLE") );
				}
				else if ( "BASIC_INFO".equals( jsonObj.getString("CELL_TYPE")))
				{
					if ( bReInflate )
						vi = inflater.inflate(R.layout.list_shop_basic_info_item, null);	

					TextView txtCategory = (TextView)vi.findViewById(R.id.txtCategory);
					txtCategory.setText( jsonObj.getString("CATEGORY") );
					Button btnPhone = (Button) vi.findViewById(R.id.btnPhone);
					btnPhone.setText( jsonObj.getString("PHONE"));
					if ( "".equals( btnPhone.getText()) ) btnPhone.setVisibility(ViewGroup.GONE);
					else btnPhone.setTag( jsonObj.getString("PHONE"));
					Button btnAddress = (Button) vi.findViewById(R.id.btnAddress);
					btnAddress.setText( jsonObj.getString("ADDRESS"));
					if ( "".equals( btnAddress.getText()) ) btnAddress.setVisibility(ViewGroup.GONE);
					else btnAddress.setTag( jsonObj.getString("ADDRESS"));
					Button btnEmail = (Button) vi.findViewById(R.id.btnEmail);
					btnEmail.setText( jsonObj.getString("EMAIL"));
					if ( "".equals( btnEmail.getText()) ) btnEmail.setVisibility(ViewGroup.GONE);
					else btnEmail.setTag( jsonObj.getString("EMAIL"));
					Button btnHomepage = (Button) vi.findViewById(R.id.btnHomepage);
					btnHomepage.setText( jsonObj.getString("HOMEPAGE"));
					if ( "".equals( btnHomepage.getText()) ) btnHomepage.setVisibility(ViewGroup.GONE);
					else btnHomepage.setTag( jsonObj.getString("HOMEPAGE"));
				}
				else if ( "SHOP_LIKE_CELL".equals( jsonObj.getString("CELL_TYPE")))
				{
					if ( bReInflate )
						vi = inflater.inflate(R.layout.list_shop_like_info_item, null);	

					boolean bDoesUserLike = jsonObj.getBoolean("doesUserLike");
					int shopLikesCount = jsonObj.getInt("shopLikesCount");

					TextView txtLikes = (TextView) vi.findViewById(R.id.txtLikes);

					if ( bDoesUserLike )
					{
						if ( shopLikesCount == 1 )
							txtLikes.setText( "당신이 좋아합니다." );
						else
							txtLikes.setText( "당신, " + (shopLikesCount - 1) + " 명의 고객이 좋아합니다." );	
					}
					else
						txtLikes.setText( shopLikesCount + " 명의 고객이 좋아합니다." );
				}
				else if ( "SHOP_COMMENT_CELL".equals( jsonObj.getString("CELL_TYPE")))
				{
					if ( bReInflate )
						vi = inflater.inflate(R.layout.list_shop_comment_item, null);	

					TextView txtComment = (TextView) vi.findViewById(R.id.txtComment);
					txtComment.setText( jsonObj.getString("COMMENT") );

					Button btnDeleteComment = (Button) vi.findViewById(R.id.btnDeleteComment);

					if ( getMetaInfoString("USER_NO").equals( jsonObj.getString("USER_NO")))
					{
						btnDeleteComment.setVisibility(ViewGroup.VISIBLE);
						btnDeleteComment.setTag("deleteComment");
						btnDeleteComment.setOnClickListener( ShopDetailActivity.this );
					}
					else
						btnDeleteComment.setVisibility(ViewGroup.GONE);
				}
				else if ( "LIKE_BUTTON_CELL".equals( jsonObj.getString("CELL_TYPE")))
				{
					if ( bReInflate )
						vi = inflater.inflate(R.layout.list_like_button_item, null);

					Button btnLike = (Button) vi.findViewById(R.id.btnLike);
					btnLike.setTag("shopLike");
					btnLike.setOnClickListener( ShopDetailActivity.this );

					TextView txtLike = (TextView) vi.findViewById(R.id.txtLike);

					if ( jsonObj.getBoolean("doesUserLike") )
					{
						txtLike.setText("더이상 좋아하지 않습니까?");
						btnLike.setText("Unlike");
					}
					else
					{
						txtLike.setText("이 업체를 좋아합니까?");
						btnLike.setText("Like");
					}
				}
				else if ( "COMMENT_INPUT_CELL".equals( jsonObj.getString("CELL_TYPE")))
				{
					if ( bReInflate )
					{
						vi = inflater.inflate(R.layout.list_comment_input_item, null);
						Button btnSend = (Button) vi.findViewById(R.id.btnSend);
						btnSend.setTag("sendComment");
						btnSend.setOnClickListener( ShopDetailActivity.this );
					}
				}
				else if ( "MENU_CELL".equals( jsonObj.getString("CELL_TYPE")))
				{
					if ( bReInflate )
						vi = inflater.inflate(R.layout.list_menu_item, null);

					TextView title = (TextView) vi.findViewById(R.id.title);

					String price = "";

					if ( !"".equals( jsonObj.get("CURRENCY_NAME")) && !JSONObject.NULL.equals( jsonObj.get("CURRENCY_NAME")))
						price += jsonObj.get("CURRENCY_NAME");

					if ( !"".equals( jsonObj.get("PRICE")) && !JSONObject.NULL.equals( jsonObj.get("PRICE")))
						price += " " + String.format("%.1f", jsonObj.getDouble("PRICE") );

					title.setText( jsonObj.getString("MENU_NAME_KR") + "(" + price + ")");

					TextView txtMenuType = (TextView) vi.findViewById(R.id.txtMenuType);
					txtMenuType.setText( jsonObj.getString("MENU_TYPE") );

					ImageView imageView = (ImageView) vi.findViewById(R.id.list_image);
					imageView.setImageResource(R.drawable.preparing);

					// Get singletone instance of ImageLoader
					ImageLoader imageLoader = ImageLoader.getInstance();
					// Initialize ImageLoader with configuration. Do it once.
					imageLoader.init(ImageLoaderConfiguration.createDefault( activity ));
					// Load and display image asynchronously

					String imageURL = activity.serverURL + "iphone/images/menu/M" + jsonObj.getString("MENU_NO")+ ".png";
					imageLoader.displayImage( imageURL, imageView);

					TextView txtLikesCnt = (TextView) vi.findViewById(R.id.txtLikesCnt);

					if ( !"".equals( jsonObj.get("CNT_LIKE")) && !JSONObject.NULL.equals( jsonObj.get("CNT_LIKE")))
					{
						ImageView ivThumb = (ImageView) vi.findViewById(R.id.iv_thumb);
						ivThumb.setVisibility(ViewGroup.VISIBLE);
						txtLikesCnt.setText( jsonObj.getString("CNT_LIKE") + " 명이 좋아합니다.");
					}
					else
					{
						ImageView ivThumb = (ImageView) vi.findViewById(R.id.iv_thumb);
						ivThumb.setVisibility(ViewGroup.GONE);
					}

					Button btnLike = (Button) vi.findViewById(R.id.btnLike);

					if ( "Y".equals( jsonObj.getString("USER_LIKE")) )
						btnLike.setText("Unlike");
					else
						btnLike.setText("Like");

					btnLike.setTag("menuLike");
					btnLike.setOnClickListener( ShopDetailActivity.this );	
				}

				vi.setTag( jsonObj );

				return vi;
			}
			catch( Exception ex )
			{
				writeLog( ex.getMessage() );
			}

			return null;
		}
	}

	public void makeACall( View v )
	{
		try {
			Intent callIntent = new Intent(Intent.ACTION_CALL);
			callIntent.setData(Uri.parse("tel:" + v.getTag().toString() ));
			startActivity(callIntent);
		} catch (ActivityNotFoundException e) {
			showOKDialog("전화걸기에 실패했습니다.\r\n관리자에게 문의바랍니다.", null );
		}
	}

	public void openMap( View v )
	{
		try {
			String uri = "geo:" + paramObject.getString("LATITUDE") + "," + paramObject.getString("LONGITUDE");
			
			if ( "-12345.0".equals( paramObject.getString("LATITUDE") ))
				uri = "geo:0,0?q=" + v.getTag().toString();
			
			startActivity(new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri)));

		} catch (Exception e) {
			showOKDialog("주소가 올바르지 않습니다.\r\n관리자에게 문의바랍니다.", null );
		}
	}

	public void openBrowser( View v )
	{
		try {
			String url = v.getTag().toString();
			if (!url.startsWith("http://") && !url.startsWith("https://"))
				url = "http://" + url;

			Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse( url ));
			startActivity(browserIntent);
		} catch (ActivityNotFoundException e) {
			showOKDialog("주소가 올바르지 않습니다.\r\n관리자에게 문의바랍니다.", null );
		}
	}

	public void sendMail( View v )
	{
		Intent i = new Intent(Intent.ACTION_SEND);
		i.setType("message/rfc822");
		i.putExtra(Intent.EXTRA_EMAIL  , new String[]{ v.getTag().toString()});
		i.putExtra(Intent.EXTRA_SUBJECT, "");
		i.putExtra(Intent.EXTRA_TEXT   , "");
		try {
			startActivity(Intent.createChooser(i, "Send mail..."));
		} catch (android.content.ActivityNotFoundException ex) {
			showOKDialog("메일주소가 올바르지 않습니다.\r\n관리자에게 문의바랍니다.", null);
		}
	}

	@Override
	public void doPostTransaction( int requestCode, Object result) {
		// TODO Auto-generated method stub
		try
		{
			if ( requestCode == Constants.REQUEST_CODE_COMMON )
			{
				// load menu list
				JSONArray arResult = new JSONArray( result.toString() );

				if ( arResult == null || arResult.length() == 0 ) return;

				ListView lv = (ListView) findViewById(R.id.list);
				ShopInfoItemAdapter adapter = (ShopInfoItemAdapter) lv.getAdapter();
				ArrayList<JSONObject> data = adapter.getData();

				JSONObject obj = new JSONObject();
				obj.put("CELL_TYPE", "BASIC_HEADER");
				obj.put("TITLE", "메뉴정보");
				data.add( obj );

				for ( int i = 0; i < arResult.length(); i++ )
				{
					obj = arResult.getJSONObject(i);
					obj.put("CELL_TYPE", "MENU_CELL");
					data.add( obj );
				}

				adapter.notifyDataSetChanged();
			}
			else if ( requestCode == Constants.REQUEST_CODE_COMMON2 )
			{
				// like shop
				JSONObject returnObj = new JSONObject( result.toString() );

				JSONArray ar = returnObj.getJSONArray("shopInfo");

				ShopLikeDao shopLike = new ShopLikeDao();
				shopLike.setShopLikeNo( ar.getInt(0) );
				shopLike.setUserNo( ar.getInt(1) );
				shopLike.setShopNo( ar.getInt(2) );
				shopLike.setIsUse( ar.getString(3) );
				shopLike.setCreateDate( ar.getString( 4 ) );
				shopLike.setUpdateDate( ar.getString( 5 ) );
				shopLike.setDeleteDate( ar.getString( 6 ) );

				shopDC.insertShopLike(shopLike);

				reloadListData();
			}
			else if ( requestCode == Constants.REQUEST_CODE_COMMON3 )
			{
				// add shop comment 
				JSONArray ar = new JSONArray( result.toString() );
				ShopCommentDao shopComment = new ShopCommentDao();
				shopComment.setShopCommentNo( ar.getInt( 0 ) );
				shopComment.setUserNo( ar.getInt( 1 ) );
				shopComment.setShopNo( ar.getInt( 2 ) );
				shopComment.setComment( ar.getString( 3 ) );
				shopComment.setIsUse( ar.getString( 4 ) );
				shopComment.setCreateDate( ar.getString( 5 ) );
				shopComment.setUpdateDate( ar.getString( 6 ) );
				shopComment.setDeleteDate( ar.getString( 7 ) );

				shopDC.insertShopComment(shopComment);

				reloadListData();
			}
			else if ( requestCode == Constants.REQUEST_CODE_COMMON4 )
			{
				// unlike shop click
				ShopLikeDao shopLike = shopDC.getShopLike( paramObject.getString("SEQ"), getMetaInfoString("USER_NO") );
				shopDC.deleteShopLike( shopLike.getShopLikeNo() );

				reloadListData();
			}
			else if ( requestCode == Constants.REQUEST_CODE_COMMON5 )
			{
				// menuLike click
				reloadListData();
			}
			else if ( requestCode == Constants.REQUEST_CODE_COMMON6 )
			{
				JSONObject resultObj = new JSONObject( result.toString() );

				if ( !"0000".equals( resultObj.getString("resCode") ) )
				{
					showOKDialog( resultObj.getString("resMsg"), null );
					return;
				}

				shopDC.deleteShopComments( resultObj.getString("shopCommentNo") );

				reloadListData();
			}
		}
		catch( Exception ex )
		{
			writeLog( ex.getMessage() );
		}
		super.doPostTransaction(result);


	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		try
		{
			if ( arg0.getTag() != null && "shopLike".equals( arg0.getTag()))
			{
				if ( isAlreadyLogin() == false )
				{
					showToastMessage("오메!! 아직 로그인을 안햅써요?\r\nLike 할라믄 로그인 해주야제~\r\n얼른 로그인 해주삼~~ ^^");
					loadLoginActivity();
					return;
				}

				JSONObject request = getJSONDataWithDefaultSetting();
				request.put("shopNo", paramObject.getString("SEQ"));
				request.put("USER_NO", getMetaInfoString("USER_NO"));

				if ( "Like".equals( ((Button) arg0 ).getText() ) )
					execTransReturningString("iphone/AddShopLike.php", request, Constants.REQUEST_CODE_COMMON2, true );
				else
				{
					ShopLikeDao shopLike = shopDC.getShopLike( paramObject.getString("SEQ"), getMetaInfoString("USER_NO"));
					request.put("shopLikeNo", shopLike.getShopLikeNo());
					execTransReturningString("iphone/UnlikeShop.php", request, Constants.REQUEST_CODE_COMMON4, true );
				}
			}
			else if ( arg0.getTag() != null && "sendComment".equals( arg0.getTag()))
			{
				if ( isAlreadyLogin() == false )
				{
					showToastMessage("오메!! 아직 로그인을 안햅써요?\r\n한마디 할라믄 로그인 해주야제~\r\n얼른 로그인 해주삼~~ ^^");
					loadLoginActivity();
					return;
				}

				LinearLayout l = (LinearLayout) arg0.getParent();
				EditText edtComment = (EditText) l.getChildAt(0);

				String strComment = edtComment.getText().toString();

				edtComment.setText("");

				JSONObject request = getJSONDataWithDefaultSetting();
				request.put("shopNo", paramObject.getString("SEQ"));
				request.put("comment", strComment);
				execTransReturningString("iphone/AddShopComment.php", request, Constants.REQUEST_CODE_COMMON3, true );
			}
			else if ( arg0.getTag() != null && "menuLike".equals( arg0.getTag()))
			{
				if ( isAlreadyLogin() == false )
				{
					showToastMessage("오메!! 아직 로그인을 안햅써요?\r\nLike 할라믄 로그인 해주야제~\r\n얼른 로그인 해주삼~~ ^^");
					loadLoginActivity();
					return;
				}

				RelativeLayout rl = (RelativeLayout) arg0.getParent();
				JSONObject jsonObj = (JSONObject) rl.getTag();

				JSONObject request = getJSONDataWithDefaultSetting();
				request.put("menuLikeNo", jsonObj.getString("MENU_LIKE_NO"));
				request.put("menuNo", jsonObj.getString("MENU_NO"));

				if ( "Like".equals( ((Button) arg0 ).getText() ) )
					execTransReturningString("iphone/AddMenuLike.php", request, Constants.REQUEST_CODE_COMMON5, true );
				else
				{
					execTransReturningString("iphone/UnlikeMenu.php", request, Constants.REQUEST_CODE_COMMON5, true );
				}
			}
			else if ( arg0.getTag() != null && "deleteComment".equals( arg0.getTag()))
			{
				RelativeLayout rl = (RelativeLayout) arg0.getParent();
				JSONObject jsonObj = (JSONObject) rl.getTag();

				JSONObject request = getJSONDataWithDefaultSetting();
				request.put("shopCommentNo", jsonObj.getString("SHOP_COMMENT_NO"));
				execTransReturningString("iphone/DeleteShopComment.php", request, Constants.REQUEST_CODE_COMMON6, true );
			}
		}
		catch( Exception ex )
		{
			writeLog( ex.getMessage() );
		}
	}
}
