package com.korea.common;

import java.util.ArrayList;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.util.TypedValue;

public class Util {

	public static String getString( Object obj )
	{
		try
		{
			if ( obj == null ) return "";
			
			if ( "null".equals( obj.toString().toLowerCase()) ) return "";
			
			return obj.toString();
		}
		catch( Exception ex )
		{
			return "";
		}
	}
	
	private static String emailPattern = "[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}";
	
	public static boolean validateEmail( String email )
	{
		Pattern pattern = Pattern.compile(emailPattern);
		return pattern.matcher( email ).matches();
	}
	
	public static Bitmap getImageFromPath( String imagePath ) throws Exception
	{
		Bitmap bm = BitmapFactory.decodeFile( imagePath );
		
		ExifInterface exif = new ExifInterface( imagePath );
		int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
		
		if ( orientation == 3 )
		{
			Matrix matrix = new Matrix();
			matrix.postRotate(180);
			bm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
		}
		else if ( orientation == 6 )
		{
			Matrix matrix = new Matrix();
			matrix.postRotate(90);
			bm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
		}
		else if ( orientation == 8 )
		{
			Matrix matrix = new Matrix();
			matrix.postRotate(-90);
			bm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
		}
		
		return bm;
	}
	
	public static Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);


        // RECREATE THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
        return resizedBitmap;
    }
	
	public static ArrayList<JSONObject> getArrayList( JSONArray jsonAr ) throws Exception
	{
		ArrayList<JSONObject> ar = new ArrayList<JSONObject>();
		
		for ( int i = 0; i < jsonAr.length(); i++ )
		{
			ar.add( jsonAr.getJSONObject(i) );
		}
		
		return ar;
	}
}
