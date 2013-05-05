package com.krking;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebView.FindListener;
import android.widget.Button;
import android.widget.EditText;

public class ShowKingHomeFragment extends BaseFragment
{
	MainActivity mainActivity;
	
	public ShowKingHomeFragment( MainActivity main)
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
		
		View view = inflater.inflate(R.layout.fragment_showking_home, container, false);
		
		return view;
	}
	
	ProgressDialog pd;
	
	
	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		
		execTransReturningString("krSetting/setMenu.aspx", 1, null );
	}
	
	@SuppressLint("SetJavaScriptEnabled")
	@Override
	public void doPostTransaction(int requestCode, String result) {
		// TODO Auto-generated method stub
		try
		{
			super.doPostTransaction(requestCode, result);
			
			JSONObject jsonObj = new JSONObject( result );
			
			jsonObj = jsonObj.getJSONObject("TopBanner");
			
			WebView webView = (WebView) getActivity().findViewById(R.id.webView1);
			
			webView.getSettings().setJavaScriptEnabled(true);
			
			webView.loadUrl( jsonObj.getString("K") );
			
			pd = ProgressDialog.show( getActivity(), "", "로딩중...",true);
			
			webView.setWebViewClient(new WebViewClient() {
	            @Override
	            public void onPageFinished(WebView view, String url) {
	            	
	                if(pd.isShowing()&&pd!=null)
	                {
	                    pd.dismiss();
	                }
	            }
	        });
		}
		catch( Exception ex )
		{
			writeLog( ex.getMessage() );
		}
	}
	
}