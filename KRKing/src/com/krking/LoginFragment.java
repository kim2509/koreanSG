package com.krking;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class LoginFragment extends BaseFragment
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
		return inflater.inflate(R.layout.fragment_login, container, false);
	}
	
	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
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