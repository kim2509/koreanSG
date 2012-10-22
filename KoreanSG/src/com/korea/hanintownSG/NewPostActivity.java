package com.korea.hanintownSG;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;

import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;
import org.json.JSONArray;
import org.json.JSONObject;

import com.korea.common.*;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class NewPostActivity extends DYActivity {

	String boardName = "";
	private String selectedCategoryID;
	private String selectedCategoryName;
	ArrayList<JSONObject> arAttachments = null;
	ArrayList<File> arImages = null;
	
	JSONArray deletedArray = null;

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		try
		{
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_new_post);

			String jsonString = getIntent().getExtras().get("param").toString();
			JSONObject jsonObject = new JSONObject( jsonString );
			boardName = jsonObject.getString("BOARD_NAME");

			if ( "edit".equals( getIntent().getExtras().get("mode")))
			{
				TextView txtTitle = (TextView) findViewById(R.id.txtTitle);
				txtTitle.setText("수정");

				EditText edtTitle = (EditText) findViewById(R.id.edtTitle);
				edtTitle.setText( jsonObject.getString("SUBJECT"));

				String content = getIntent().getExtras().getString("content");
				EditText edtContent = (EditText) findViewById(R.id.edtContent);
				edtContent.setText( content );
				
				if ( getIntent().getExtras().get("attachments") != null )
				{
					String jsonAttachments = getIntent().getExtras().get("attachments").toString();
					JSONArray jsonAr = new JSONArray( jsonAttachments );
					String bodyTextID = getIntent().getExtras().getString("bodyTextID");
					String bodyTextOrder = getIntent().getExtras().getString("bodyTextOrder");
					
					Button btnBoardCategory = (Button) findViewById(R.id.btnBoardCategory);

					selectedCategoryID = getIntent().getExtras().getString("categoryID");
					selectedCategoryName = getIntent().getExtras().getString("categoryName");

					btnBoardCategory.setText( selectedCategoryName );
					
					arAttachments = new ArrayList<JSONObject>();

					for ( int i = 0; i < jsonAr.length(); i++ )
					{
						JSONObject jsonObj = jsonAr.getJSONObject(i);
						jsonObj.put("TYPE", "IMAGE");
						arAttachments.add( jsonObj );
					}

					JSONObject jsonObj = new JSONObject();
					jsonObj.put("content", "BODYTEXT");
					jsonObj.put("TYPE", "TEXT");
					jsonObj.put("ID", bodyTextID );
					jsonObj.put("VERTICAL_ORDER", bodyTextOrder );

					if ( jsonAr.length() > 0 && jsonAr.length() >= Integer.parseInt( bodyTextOrder ))
					{
						arAttachments.add(Integer.parseInt(bodyTextOrder), jsonObj);
					}
					else
					{
						arAttachments.add(0, jsonObj);
					}
					
					
					Button btnPhotoInfo = (Button) findViewById(R.id.btnPhotoInfo );
					btnPhotoInfo.setText( "그림 " + ( arAttachments.size() - 1 ));
				}
			}
		}
		catch( Exception ex )
		{
			writeLog( ex.getMessage() );
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		try
		{
			super.onResume();
		}
		catch( Exception ex )
		{
			writeLog( ex.getMessage() );
		}    	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_new_post, menu);
		return true;
	}

	public void attachPhoto( View v )
	{
		try
		{
			//    		Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
			Intent intent = new Intent( Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
			intent.setType("image/*");
			startActivityForResult(intent, Constants.REQUEST_CODE_COMMON2 );
		}
		catch( Exception ex )
		{
			writeLog( ex.getMessage() );
		}
	}

	public void showCategorySelectActivity( View v )
	{
		try
		{
			Intent intent = new Intent(this, CategorySelectActivity.class);
			intent.putExtra("param", getIntent().getExtras().getString("param"));
			startActivityForResult( intent, Constants.REQUEST_CODE_COMMON );
		}
		catch( Exception ex )
		{
			writeLog( ex.getMessage() );
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		try
		{
			if ( requestCode == Constants.REQUEST_CODE_COMMON && resultCode == RESULT_OK )
			{
				Button btnBoardCategory = (Button) findViewById(R.id.btnBoardCategory);

				selectedCategoryID = data.getExtras().getString("selectedCategoryID");
				selectedCategoryName = data.getExtras().getString("selectedCategoryName");

				btnBoardCategory.setText( data.getExtras().getString("selectedCategoryName") );
			}
			else if ( requestCode == Constants.REQUEST_CODE_COMMON2 && resultCode == RESULT_OK )
			{
				Uri imageUri = data.getData();
				if (imageUri == null) {
					// (code to show error message goes here)
					return;
				}

				String[] filePathColumn = { MediaStore.Images.Media.DATA };

				Cursor cursor = getContentResolver().query(imageUri,
						filePathColumn, null, null, null);
				cursor.moveToFirst();

				int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
				String picturePath = cursor.getString(columnIndex);
				cursor.close();

				File f = new File( picturePath );
				
				if ( arImages == null )
					arImages = new ArrayList<File>();
				
				arImages.add( f );
				
				JSONObject jsonObj = new JSONObject();
				jsonObj.put("TYPE", "IMAGE");
				jsonObj.put("ID", "");
				jsonObj.put("URL", f.getAbsoluteFile());
				arAttachments.add( jsonObj );
				
				Button btnPhotoInfo = (Button) findViewById(R.id.btnPhotoInfo );
				btnPhotoInfo.setText( "그림 " + ( arAttachments.size() - 1 ));
			}
			else if ( requestCode == Constants.REQUEST_CODE_COMMON3 && resultCode == RESULT_OK )
			{
				String jsonAttachments = data.getExtras().getString("attachments");
				
				JSONArray jsonAr = new JSONArray( jsonAttachments );
				
				arAttachments = Util.getArrayList(jsonAr);
				
				Button btnPhotoInfo = (Button) findViewById(R.id.btnPhotoInfo );
				btnPhotoInfo.setText( "그림 " + (arAttachments.size() - 1) );
				
				if ( data.getExtras().get("delete") != null )
				{
					deletedArray = new JSONArray( data.getExtras().getString("delete") );
				}
			}

			super.onActivityResult(requestCode, resultCode, data);
		}
		catch( Exception ex )
		{
			writeLog( ex.getMessage());
		}
	}

	@SuppressWarnings("unused")
	public void submitPost( View v )
	{
		try
		{
			EditText edtTitle = (EditText) findViewById(R.id.edtTitle);
			EditText edtContent = (EditText) findViewById(R.id.edtContent);

			String title = edtTitle.getText().toString();
			String content = edtContent.getText().toString();

			MultipartEntity reqEntity = getMultiFormRequestEntityWithDefaultSetting();
			reqEntity.addPart("subject", new StringBody(title) );
			reqEntity.addPart("content", new StringBody(content) );
			reqEntity.addPart("boardName", new StringBody( boardName ) );

			if ( "new".equals( getIntent().getExtras().get("mode")))
			{
				String categoryID = getMetaInfoString( boardName + "_CATEGORY_ID");

				reqEntity.addPart("bodyTextOrder", new StringBody("0") );
				reqEntity.addPart("categoryID", new StringBody( categoryID ) );

				for ( int i = 0; i < arImages.size(); i++ )
				{
					File f = arImages.get(i);
					String extension = MimeTypeMap.getFileExtensionFromUrl( f.getAbsolutePath() );
					String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);

					ByteArrayOutputStream bos = new ByteArrayOutputStream();
					Bitmap bm = Util.getImageFromPath( f.getAbsolutePath() );

					int width = (bm.getWidth() >= bm.getHeight())? 1024:768;
			        float ratio =  ((float) (bm.getHeight() * 1.0) / (float) (bm.getWidth() * 1.0));
			        
			        int height = (int) ( ratio * (width * 1.0));
			        
			        if ( width < bm.getWidth() )
			        {
			        	bm = Util.getResizedBitmap(bm, width, height);
			        }
					
			        bm.compress(CompressFormat.JPEG, 100, bos);
					
					InputStream in = new ByteArrayInputStream(bos.toByteArray());
					ContentBody mimePart = new InputStreamBody(in, mimeType, "image[]" );

					ContentBody cbFile = new FileBody( f, mimeType );
					
					reqEntity.addPart("image[]", mimePart );
//					reqEntity.addPart("image[]", cbFile );
				}
				
				execFormRequest("web/mobile/board/addBoardPost.php", reqEntity);
				
				
//				ArrayList<File> files = new ArrayList();
//				
//				for ( int i = 0; i < arImages.size(); i++ )
//				{
//					File f = arImages.get(i);
//					String extension = MimeTypeMap.getFileExtensionFromUrl( f.getAbsolutePath() );
//					String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
//					
//					files.add( f );
//				}
//				
//				Hashtable<String, String> ht = getHashtableWithDefaultSetting();
//				ht.put("subject", title );
//				ht.put("content", content );
//				ht.put("boardName", boardName );
//				ht.put("bodyTextOrder", "0");
//				ht.put("categoryID", categoryID );
//				ht.put("imageCount", String.valueOf( arImages.size() ));
//				
//				execFormRequest("web/mobile/board/addBoardPost.php", ht, files);
			}
			else
			{
				String jsonString = getIntent().getExtras().get("param").toString();
				JSONObject jsonObj = new JSONObject( jsonString );

				reqEntity.addPart("bID", new StringBody(jsonObj.getString("BID")));
				reqEntity.addPart("categoryID", new StringBody( selectedCategoryID ) );
				
				// has attachment
				if ( arAttachments != null )
				{
					JSONObject modifyInfo = new JSONObject();
					JSONArray newAr = new JSONArray();
					JSONArray modifyAr = new JSONArray();
					
					for ( int i = 0; i < arAttachments.size(); i++ )
					{
						JSONObject temp = arAttachments.get(i);
						
						if ( temp.get("ID") == null || "".equals( temp.get("ID")))
						{
							// NEW
							for ( int k = 0; k < arImages.size(); k++ )
							{
								File f = arImages.get(k);
								String extension = MimeTypeMap.getFileExtensionFromUrl( f.getAbsolutePath() );
								String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);

								ByteArrayOutputStream bos = new ByteArrayOutputStream();
								Bitmap bm = Util.getImageFromPath( f.getAbsolutePath() );

								int width = (bm.getWidth() >= bm.getHeight())? 1024:768;
						        float ratio =  ((float) (bm.getHeight() * 1.0) / (float) (bm.getWidth() * 1.0));
						        
						        int height = (int) ( ratio * (width * 1.0));
						        
						        if ( width < bm.getWidth() )
						        {
						        	bm = Util.getResizedBitmap(bm, width, height);
						        }
								
						        bm.compress(CompressFormat.JPEG, 100, bos);
								
								InputStream in = new ByteArrayInputStream(bos.toByteArray());
								ContentBody mimePart = new InputStreamBody(in, mimeType, "image[]" );

								ContentBody cbFile = new FileBody( f, mimeType );
								
								reqEntity.addPart("image[]", mimePart );
							}
							
							newAr.put( String.valueOf( i ) );
						}
						else
						{
							// MODIFY
							modifyAr.put( temp.getString("ID") + ":" + i );
						}
					}
					
					modifyInfo.put("NEW", newAr );
					modifyInfo.put("MODIFY", modifyAr );
					modifyInfo.put("DELETE", deletedArray );
					
					reqEntity.addPart("modifyInfo", new StringBody(modifyInfo.toString()));
				}

				execFormRequest("web/mobile/board/modifyBoardContent.php", reqEntity);
			}
		}
		catch( Exception ex )
		{
			writeLog( ex.getMessage() );    		
		}
	}

	@Override
	public void doFormPostTransaction(Object result) {
		// TODO Auto-generated method stub
		try
		{
			super.doFormPostTransaction(result);

			if ( checkJSONResult( result ) == false ) return;

			if ( "new".equals( getIntent().getExtras().get("mode")))
			{
				showToastMessage("정상적으로 등록되었습니다.");
			}
			else
			{
				showToastMessage("수정이 완료되었습니다.");
			}

			setResult( RESULT_OK );

			finish();
		}
		catch( Exception ex )
		{
			writeLog( ex.getMessage() );
		}
	}

	public void goModifyPostActivity( View v )
	{
		Intent intent = new Intent( this, ModifyPostActivity.class );
		startActivity( intent );
	}
	
	public void goManageAttachmentActivity( View v )
	{
		try
		{
			Intent intent = new Intent( this, ManageAttachmentActivity.class );
			JSONArray jsonAr = new JSONArray( arAttachments );
			intent.putExtra("attachments", jsonAr.toString());
			startActivityForResult(intent, Constants.REQUEST_CODE_COMMON3 );	
		}
		catch( Exception ex )
		{
			writeLog( ex.getMessage() );
		}
	}
	
	
}
