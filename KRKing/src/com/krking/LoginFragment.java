package com.krking;

import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.Button;
import android.widget.EditText;

public class LoginFragment extends BaseFragment implements OnClickListener
{
	MainActivity mainActivity;
	
	public LoginFragment( MainActivity main)
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
		
		View view = inflater.inflate(R.layout.fragment_login, container, false);
		
		Button btnLogin = (Button) view.findViewById(R.id.btnLogin);
		btnLogin.setOnClickListener( this );
		
		return view;
	}
	
	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		try
		{
			if ( v.getId() == R.id.btnLogin )
				login();
		}
		catch( Exception ex )
		{
			writeLog( ex.getMessage() );
		}
	}
	
	public void login() throws Exception
	{
		EditText edtID = (EditText) getView().findViewById(R.id.edtID );
		
		if ( "".equals( edtID.getText().toString() ) )
		{
			showOKDialog("아이디를 입력해 주십시오.", null );
			return;
		}
		
		EditText edtPassword = (EditText) getView().findViewById(R.id.edtPassword );
		
		if ( "".equals( edtPassword.getText().toString() ) )
		{
			showOKDialog("비밀번호를 입력해 주십시오.", null );
			return;
		}
		
		hideSoftKeyboard();

		id = edtID.getText().toString();
		password = edtPassword.getText().toString();
		
		execTransReturningString("krLogin/LoginProcess.aspx?uid=" + id + "&upwd=" + password, 1, null );
	}
	
	String id = "";
	String password = "";
	
	public void hideSoftKeyboard()
	{
		InputMethodManager inputManager = (InputMethodManager)
                getActivity().getSystemService(Context.INPUT_METHOD_SERVICE); 

		if ( getActivity().getCurrentFocus() != null )
		{
			inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
	                   InputMethodManager.HIDE_NOT_ALWAYS);
			inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);	
		}
	}
	
	@Override
	public void doPostTransaction(int requestCode, String result) {
		// TODO Auto-generated method stub
		try
		{
			super.doPostTransaction(requestCode, result);
			
			JSONObject resObj = new JSONObject( result );
			if ( "Y".equals( resObj.getString("C") ) )
			{
				showToastMessage("성공적으로 로그인되었습니다.\r\n자유게시판으로 이동합니다.");
				mainActivity.setMetaInfo("uid", id );
				mainActivity.setMetaInfo("upwd", password );
				mainActivity.goToFreeBoard();
				return;
			}
			else if ( "D".equals( resObj.getString("C") ))
			{
				showOKDialog("입력하신 ID 가 존재하지 않습니다.", null );
				return;
			}
			else if ( "U".equals( resObj.getString("C") ))
			{
				showOKDialog("입력하신 비밀번호가 올바르지 않습니다.", null );
				return;
			}
		}
		catch( Exception ex )
		{
			writeLog( ex.getMessage() );
		}
	}
}