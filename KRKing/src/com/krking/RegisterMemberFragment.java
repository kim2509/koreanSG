package com.krking;

import org.json.JSONObject;

import com.common.Util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

public class RegisterMemberFragment extends BaseFragment implements OnClickListener
{
	MainActivity mainActivity;
	
	public RegisterMemberFragment( MainActivity main)
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
		View v = inflater.inflate(R.layout.fragment_register_member, container, false);
		
		Button btnTerms1 = (Button) v.findViewById(R.id.btnTerms1);
		btnTerms1.setOnClickListener( this );
		Button btnTerms2 = (Button) v.findViewById(R.id.btnTerms2);
		btnTerms2.setOnClickListener( this );
		Button btnChkDuplicate1 = (Button) v.findViewById(R.id.btnChkDuplicate1);
		btnChkDuplicate1.setOnClickListener( this );
		Button btnChkDuplicate2 = (Button) v.findViewById(R.id.btnChkDuplicate2);
		btnChkDuplicate2.setOnClickListener( this );
		Button btnRegister = (Button) v.findViewById(R.id.btnRegister);
		btnRegister.setOnClickListener( this );
		
		return v;
	}
	
	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	boolean bIDDuplicate = true;
	boolean bNickDuplicate = true;
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		try
		{
			if ( v.getId() == R.id.btnTerms1 )
			{
				Intent httpIntent = new Intent(Intent.ACTION_VIEW);
				httpIntent.setData(Uri.parse("http://app.krking.net/krlaw1.html"));
				startActivity(httpIntent); 
			}
			else if ( v.getId() == R.id.btnTerms2 )
			{
				Intent httpIntent = new Intent(Intent.ACTION_VIEW);
				httpIntent.setData(Uri.parse("http://app.krking.net/krlaw2.html"));
				startActivity(httpIntent);
			}
			else if ( v.getId() == R.id.btnChkDuplicate1 )
			{
				EditText edtID = (EditText) getActivity().findViewById(R.id.edtID);
				String id = edtID.getText().toString();
				if ( "".equals( id ) )
				{
					showOKDialog("ID를 입력해 주십시오.", null );
					return;
				}
				
				execTransReturningString("krRegister/regCheck.aspx?cmd=1&param=" + id, 1, null );
			}
			else if ( v.getId() == R.id.btnChkDuplicate2 )
			{
				EditText edtNickname = (EditText) getActivity().findViewById(R.id.edtNickname);
				String nickName = edtNickname.getText().toString();
				if ( "".equals( nickName ) )
				{
					showOKDialog("닉네임을 입력해 주십시오.", null );
					return;
				}
				
				execTransReturningString("krRegister/regCheck.aspx?cmd=2&param=" + nickName, 2, null );
			}
			else if ( v.getId() == R.id.btnRegister )
			{
				CheckBox chkAgree1 = (CheckBox) getActivity().findViewById(R.id.chkAgree1);
				if ( chkAgree1.isChecked() == false )
				{
					showOKDialog("회원가입약관을 동의해 주시기 바랍니다.", null );
					return;
				}
				
				CheckBox chkAgree2 = (CheckBox) getActivity().findViewById(R.id.chkAgree2);
				if ( chkAgree2.isChecked() == false )
				{
					showOKDialog("서비스 이용 약관을 동의해 주시기 바랍니다.", null );
					return;
				}
				
				EditText edtID = (EditText) getActivity().findViewById(R.id.edtID);
				id = edtID.getText().toString();
				
				if ( "".equals( id ) )
				{
					showOKDialog("ID를 입력해 주십시오.", null );
					return;
				}
				
				EditText edtPassword = (EditText) getActivity().findViewById(R.id.edtPassword);
				password = edtPassword.getText().toString();
				
				if ( "".equals( password ) )
				{
					showOKDialog("비밀번호를 입력해 주십시오.", null );
					return;
				}
				
				EditText edtPasswordConfirm = (EditText) getActivity().findViewById(R.id.edtPasswordConfirm);
				
				if ( "".equals( edtPasswordConfirm.getText().toString() ) )
				{
					showOKDialog("비밀번호 확인란을 입력해 주십시오.", null );
					return;
				}
				
				if ( !edtPassword.getText().toString().equals( edtPasswordConfirm.getText().toString() ) )
				{
					showOKDialog("비밀번호와 비밀번호 확인값이 일치하지 않습니다.", null );
					return;
				}
				
				if ( bIDDuplicate )
				{
					showOKDialog("ID 중복확인을 해 주시기 바랍니다.", null );
					return;
				}
				
				if ( Util.validateID( id ) == false )
				{
					showOKDialog("ID는 숫자와 영문자만 사용할 수 있습니다.", null );
					return;
				}
				
				EditText edtName = (EditText) getActivity().findViewById(R.id.edtName);
				
				if ( "".equals( edtName.getText().toString() ) )
				{
					showOKDialog("이름을 입력해 주십시오.", null );
					return;
				}
				
				EditText edtNickname = (EditText) getActivity().findViewById(R.id.edtNickname);
				
				if ( "".equals( edtNickname.getText().toString() ) )
				{
					showOKDialog("닉네임을 입력해 주십시오.", null );
					return;
				}
				
				if (edtNickname.getText().toString().length() < 2 )
				{
					showOKDialog("닉네임을 2자 이상 입력해 주십시요.", null );
					return;
				}
				
				if ( bNickDuplicate )
				{
					showOKDialog("닉네임 중복확인을 해 주시기 바랍니다.", null );
					return;
				}
				
				hideSoftKeyboard();
				
				JSONObject reqObj = new JSONObject();
				reqObj.put("uid", id);
				reqObj.put("uPwd", password );
				reqObj.put("uName", edtName.getText().toString());
				reqObj.put("uNick", edtNickname.getText().toString());
				execTransReturningString("krUser/regUserOk.aspx", 3, reqObj );
			}
		}
		catch( Exception ex )
		{
			writeLog( ex.getMessage() );
		}
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
			
			if ( requestCode == 1 )
			{
				// ID check
				JSONObject resObj = new JSONObject( result );
				if ( "Y".equals( resObj.getString("C") ) )
				{
					showOKDialog("사용하실수 있는 아이디입니다.", null );
					bIDDuplicate = false;
				}
				else
				{
					showOKDialog("이미 사용중인 아이디입니다.", null );
				}
			}
			else if ( requestCode == 2 )
			{
				// ID check
				JSONObject resObj = new JSONObject( result );
				if ( "Y".equals( resObj.getString("C") ) )
				{
					showOKDialog("사용하실수 있는 닉네임입니다.", null );
					bNickDuplicate = false;
				}
				else
				{
					showOKDialog("이미 사용중인 닉네임입니다.", null );
				}
			}
			else if ( requestCode == 3 )
			{
				JSONObject resObj = new JSONObject( result );
				if ( "Y".equals( resObj.getString("C") ) )
				{
					showToastMessage("회원가입이 성공적으로 처리되었습니다.\r\n자유게시판으로 이동합니다.");
					mainActivity.setMetaInfo("uid", id );
					mainActivity.setMetaInfo("upwd", password );
					mainActivity.goToFreeBoard();
					return;
				}
				else if ( "SID".equals( resObj.getString("C") ))
				{
					showOKDialog("ID는 4자 이상이어야 합니다.", null );
					return;
				}
				else if ( "EID".equals( resObj.getString("C") ))
				{
					showOKDialog("이미 사용중인 ID 입니다.", null );
					return;
				}
				else if ( "SNICK".equals( resObj.getString("C") ))
				{
					showOKDialog("닉네임은 2자 이상이어야 합니다.", null );
					return;
				}
				else if ( "ENICK".equals( resObj.getString("C") ))
				{
					showOKDialog("이미 사용중인 닉네임입니다.", null );
					return;
				}
				else
				{
					showOKDialog("회원가입에 실해하였습니다.\r\n다시 시도해 주십시오.", null );
					return;
				}
			}
		}
		catch( Exception ex )
		{
			writeLog( ex.getMessage() );
		}
	}
}