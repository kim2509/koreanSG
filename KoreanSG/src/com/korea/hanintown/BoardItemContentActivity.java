package com.korea.hanintown;

import org.apache.http.util.EncodingUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.korea.common.Constants;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class BoardItemContentActivity extends DYActivity implements OnClickListener{

	WebView webView;

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		try
		{
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_board_item_content);

			initializeControls();

			initializeWebView();

			loadBoardContent();
		}
		catch( Exception ex )
		{
			writeLog( ex.getMessage() );
		}
	}

	private void initializeControls() throws Exception 
	{

		String jsonString = getIntent().getExtras().get("param").toString();
		JSONObject jsonObj = new JSONObject( jsonString );

		TextView txtTitle = (TextView) findViewById(R.id.txtTitle);
		txtTitle.setText( jsonObj.getString("SUBJECT"));

		Button btnSend = (Button) findViewById(R.id.btnSend);
		btnSend.setOnClickListener( this );

		EditText edtComment = (EditText) findViewById(R.id.edtComment);

		edtComment.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) 
			{
				try
				{
					// TODO Auto-generated method stub
					if (actionId == EditorInfo.IME_ACTION_SEND) {
						return addReply();
					}

					return true;
				}
				catch( Exception ex )
				{
					writeLog( ex.getMessage() );
					return false;
				}
			}
		});

		Button btnModifyPost = (Button) findViewById(R.id.btnModifyPost);  
		registerForContextMenu(btnModifyPost);
		btnModifyPost.setLongClickable(false);
		
		if ( !"".equals( getMetaInfoString("USER_ID") ) && getMetaInfoString("USER_ID").equals( jsonObj.getString("USER_ID")))
			btnModifyPost.setVisibility(View.VISIBLE);
		else
			btnModifyPost.setVisibility(View.INVISIBLE);
	}

	private void loadBoardContent() throws JSONException {
		String jsonString = getIntent().getExtras().get("param").toString();
		JSONObject jsonObj = new JSONObject( jsonString );

		String url = "web/mobile/board/boardContent.php";
		String postData = "postID=" + jsonObj.getString("BID")+ "&udid=" + getUniqueDeviceID() + 
				"&userID=" + getMetaInfoString("USER_ID") + "&nickName=" + getMetaInfoString("NICKNAME") + 
				"&boardName=" + jsonObj.getString("BOARD_NAME") + "&osType=" + getOSType();

		writeLog( "url:" + url );
		writeLog( "postData:" + postData );

		pd = ProgressDialog.show(this, "", "로딩중...",true);

		webView.postUrl( serverURL + url , EncodingUtils.getBytes(postData, "base64"));
	}

	ProgressDialog pd = null;

	@SuppressLint("SetJavaScriptEnabled")
	private void initializeWebView() {
		webView = (WebView) findViewById(R.id.webView1);
		webView.getSettings().setJavaScriptEnabled(true);

		webView.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageFinished(WebView view, String url) {

				if( pd != null && pd.isShowing() )
				{
					pd.dismiss();
				}
			}
		});

		webView.setWebChromeClient(new WebChromeClient() {
			@Override
			public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
				//Required functionality here
				return super.onJsAlert(view, url, message, result);
			} 
		});

		webView.addJavascriptInterface(new JavaScriptInterface(this), "Android");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_board_item_content, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		try
		{
			if ( v.getId() == R.id.btnSend )
			{
				addReply();
			}	
		}
		catch( Exception ex )
		{
			writeLog( ex.getMessage() );
		}
	}

	private boolean addReply() throws Exception 
	{
		if ( isAlreadyLogin() == false )
		{
			showToastMessage("해당 기능을 이용하기 위해선\r\n로그인이 필요합니다.\r\n로그인 후 이용해 주시기 바랍니다.");
			loadLoginActivity();
			return false;
		}

		EditText edtComment = (EditText) findViewById(R.id.edtComment);

		String strComment = edtComment.getText().toString();

		if ( "".equals( strComment ) )
		{
			showOKDialog("댓글을 입력해 주십시오.", null);
			return false;
		}

		String jsonString = getIntent().getExtras().get("param").toString();
		JSONObject jsonObj = new JSONObject( jsonString );

		strComment = strComment.replaceAll("\'", "&apos;");

		webView.loadUrl("javascript:addComment('" + jsonObj.getString("BID") + "','" +
				strComment + "','" + getMetaInfoString("USER_ID") + "','" + getMetaInfoString("NICKNAME") + "')");

		edtComment.setText("");

		hideSoftKeyboard();

		return true;
	}

	public void modifyPost( View v )
	{
		openContextMenu(v);
	}

	@Override  
	public void onCreateContextMenu(ContextMenu menu, View v,ContextMenuInfo menuInfo) {  
		super.onCreateContextMenu(menu, v, menuInfo);  
		menu.setHeaderTitle("메뉴선택");  
		menu.add(0, v.getId(), 0, "수정");  
		menu.add(0, v.getId(), 0, "삭제");
		menu.add(0, v.getId(), 0, "취소");
	} 

	@Override  
	public boolean onContextItemSelected(MenuItem item) {  
		if(item.getTitle()=="수정")
		{
			webView.loadUrl("javascript:getBoardContent();");
		}  
		else if(item.getTitle()=="삭제")
		{
			showYesNoDialog("정말 삭제하시겠습니까?", null );
		}  
		else {return false;}  
		return true;  
	}  
	
	@Override
	public void passBoardContent(String content, String bodyTextID, String bodyTextOrder, 
			String categoryID, String categoryName, String jsonAttachments ) {
		// TODO Auto-generated method stub
		
		Intent intent = new Intent(this, NewPostActivity.class);
		intent.putExtra("param", getIntent().getExtras().getString("param"));
		intent.putExtra("mode", "edit");
		intent.putExtra("content", content );
		intent.putExtra("attachments", jsonAttachments );
		intent.putExtra("bodyTextID", bodyTextID );
		intent.putExtra("bodyTextOrder", bodyTextOrder );
		intent.putExtra("categoryID", categoryID );
		intent.putExtra("categoryName", categoryName );
		startActivityForResult( intent, Constants.REQUEST_CODE_COMMON );
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		try
		{
			if ( requestCode == Constants.REQUEST_CODE_COMMON &&
					resultCode == RESULT_OK )
			{
				loadBoardContent();
			}

			super.onActivityResult(requestCode, resultCode, data);
		}
		catch( Exception ex )
		{
			writeLog( ex.getMessage());
		}
	}
	
	@Override
	public void yesClicked(Object param) {
		// TODO Auto-generated method stub
		try
		{
			JSONObject request = getJSONDataWithDefaultSetting();
			
			String jsonString = getIntent().getExtras().get("param").toString();
			JSONObject jsonObj = new JSONObject( jsonString );
			
			request.put("boardName", jsonObj.getString("BOARD_NAME"));
			request.put("bID", jsonObj.getString("BID"));
			
			execTrans("web/mobile/board/deleteBoardContent.php", request, true );
		}
		catch( Exception ex )
		{
			writeLog( ex.getMessage() );
		}
	}
	
	@Override
	public void doPostTransaction(Object result) {
		// TODO Auto-generated method stub
		try
		{
			if ( checkJSONResult(result) == false ) return;
			
			setResult(RESULT_OK);
			
			finish();
		}
		catch( Exception ex )
		{
			writeLog( ex.getMessage() );
		}
	}
}
