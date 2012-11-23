package com.krking;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
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
	
	public void login()
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
		
		execTransReturningString("krLogin/LoginProcess.aspx?uid=" + edtID.getText().toString() +
				"&upwd=" + edtPassword.getText().toString(), null );
	}
	
	@Override
	public void doPostTransaction(String result) {
		
		try
		{
			// TODO Auto-generated method stub
			super.doPostTransaction(result);
			
			
		}
		catch( Exception ex )
		{
			
		}
	}
}