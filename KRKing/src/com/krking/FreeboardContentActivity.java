package com.krking;

import org.json.JSONObject;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

public class FreeboardContentActivity extends BaseActivity implements OnClickListener{

	int gBn = 1;
	
	@SuppressLint("SetJavaScriptEnabled")
	@Override
	public void onCreate(Bundle savedInstanceState) {

		try
		{
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_freeboard_content);	

			gBn = getIntent().getExtras().getInt("bGb");
			
			TextView txtTopTitle = (TextView) findViewById(R.id.txtTopTitle);
			if ( gBn == 1 )
				txtTopTitle.setText("자유게시판");
			else
				txtTopTitle.setText("예상/복기");
			
			Drawable btn1 = getResources().getDrawable(R.drawable.btn_comment);
			Button btnComment = (Button) findViewById(R.id.btnComment);
			btnComment.setOnClickListener( this );
			btnComment.setBackgroundDrawable( btn1 );
			
			/*
			if ( getMetaInfoString("uid") != null &&
					!"".equals( getMetaInfoString("uid")))
			{
				btnComment.setEnabled(true);
				btnComment.setBackgroundResource(R.drawable.btn_comment);
			}
			else
			{
				btnComment.setEnabled(false);
				btnComment.setBackgroundResource(R.drawable.btn_comment_disable);
			}
			*/
			
			Button btnCorrect = (Button) findViewById(R.id.btnCorrect);
			btnCorrect.setOnClickListener( this );
			
			Button btnDelete = (Button) findViewById(R.id.btnDelete);
			btnDelete.setOnClickListener( this );
			
			WebView webView = (WebView) findViewById(R.id.webView1);
			webView.getSettings().setJavaScriptEnabled(true);
			WebSettings settings = webView.getSettings();
			settings.setBuiltInZoomControls(true);
			webView.setWebViewClient(new WebViewClient() {
				@Override
				public void onPageFinished(WebView view, String url) {
				}
			});
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
			
			String paramString = getIntent().getExtras().getString("param");
			JSONObject jsonObj = new JSONObject( paramString );
			
			execTransReturningString("krBoard/krBoardView.aspx?bGb=" + gBn + "&idx=" + jsonObj.getString("i") 
					+"&bUID=" + getMetaInfoString("uid") , 1, null );
		}
		catch( Exception ex )
		{
			writeLog( ex.getMessage() );
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_freeboard_content, menu);
		return true;
	}
	
	JSONObject resObj = null;

	@Override
	public void doPostTransaction(int requestCode, String result) {
		// TODO Auto-generated method stub
		try
		{
			super.doPostTransaction(requestCode, result);

			if ( requestCode == 1 )
			{
				// 조회
				resObj = new JSONObject( result );

				WebView webView = (WebView) findViewById(R.id.webView1);

				JSONObject viewObj = resObj.getJSONObject("VIEW"); 
				
				TextView txtSubTitle = (TextView) findViewById(R.id.txtSubTitle);
				txtSubTitle.setText( viewObj.getString("t") );
				
				TextView txtDate = (TextView) findViewById(R.id.txtDate);
				txtDate.setText( viewObj.getString("d") );
				
				TextView txtRefCount = (TextView) findViewById(R.id.txtRefCount);
				txtRefCount.setText( "조회 : " + viewObj.getString("v") );
				
				TextView txtLikesCount = (TextView) findViewById(R.id.txtLikesCount);
				txtLikesCount.setText( viewObj.getString("r") );
				
				TextView txtAuthor = (TextView) findViewById(R.id.txtAuthor);
				txtAuthor.setText( viewObj.getString("n") );
				
				String content = viewObj.getString("c");

				Button btnCorrect = (Button) findViewById(R.id.btnCorrect);
				if ( "N".equals( viewObj.getString("BM") ) )
				{
					btnCorrect.setEnabled(false);
					btnCorrect.setBackgroundResource(R.drawable.btn_correct_disable);
				}
				else
				{
					btnCorrect.setEnabled(true);
					btnCorrect.setBackgroundResource(R.drawable.btn_correct);
				}
				
				Button btnDelete = (Button) findViewById(R.id.btnDelete);
				if ( "N".equals( viewObj.getString("BD") ) )
				{
					btnDelete.setEnabled(false);
					btnDelete.setBackgroundResource(R.drawable.btn_delete_disable);
				}
				else
				{
					btnDelete.setEnabled(true);
					btnDelete.setBackgroundResource(R.drawable.btn_delet);
				}
				
				webView.loadDataWithBaseURL(null, content , "text/html", "utf-8", null);	
			}
			else if ( requestCode == 2 )
			{
				//삭제
				JSONObject jsonObj = new JSONObject( result );
				
				if ( "Y".equals( jsonObj.getString("C")))
				{
					showToastMessage("정상적으로 삭제되었습니다.");
					finish();
				}
				else
				{
					showOKDialog("삭제도중 오류가 발생했습니다.\r\n다시 시도해 주십시오.", null);
					return;
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
			if ( v.getId() == R.id.btnComment )
			{
				Intent intent = new Intent( this, FreeboardCommentsActivity.class );
				String paramString = getIntent().getExtras().getString("param");
				intent.putExtra("bGb", gBn );
				intent.putExtra("param", paramString);
				startActivity( intent );
			}
			else if ( v.getId() == R.id.btnCorrect )
			{
				Intent intent = new Intent( this, FreeboardWritePostActivity.class );
				intent.putExtra("mode", "UPD");
				String paramString = getIntent().getExtras().getString("param");
				intent.putExtra("bGb", gBn );
				intent.putExtra("param", paramString);
				intent.putExtra("param2", resObj.toString());
				startActivityForResult(intent, 1);
			}
			else if ( v.getId() == R.id.btnDelete )
			{
				showYesNoDialog("정말 삭제하시겠습니까?", null );
			}
		}
		catch( Exception ex )
		{
			
		}
	}
	
	@Override
	public void yesClicked(Object param) {
		// TODO Auto-generated method stub
		try
		{
			super.yesClicked(param);
			
			String paramString = getIntent().getExtras().getString("param");
			JSONObject jsonObj = new JSONObject( paramString );
			
			execTransReturningString("krBoard/krBoardDelete.aspx?bGb=" + gBn + "&bidx=" + jsonObj.getString("i") 
					+"&bUID=" + getMetaInfoString("uid") , 2, null );
			
		}
		catch( Exception ex )
		{
			writeLog( ex.getMessage() );
		}
	}
	
}
