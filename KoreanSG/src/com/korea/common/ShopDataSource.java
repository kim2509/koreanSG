package com.korea.common;

import java.util.ArrayList;

import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class ShopDataSource {

	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;

	public final static String SHOP_CREATE_STATEMENT = 
			"create table if not exists SHOP" + 
			"(" +
			"SHOPSEQ integer," +
			"CATEGORY text," +
			"SHOPNAME text," +
			"PHONE text," +
			"MOBILE text," +
			"PHONE1 text," +
			"PHONE2 text," +
			"ADDRESS text," +
			"LONGITUDE text," +
			"LATITUDE text," +
			"EMAIL text," +
			"HOMEPAGE text," +
			"IS_SHOW_PRICE text," +
			"CREATE_DATE text," +
			"UPDATE_DATE text," +
			"DELETE_DATE text," +
			"SHOPNAME_EN text," +
			"CATEGORY_EN text" +
			");";
	
	public final static String SHOP_DROP_STATEMENT = 
			"DROP TABLE IF EXISTS SHOP";
	
	public final static String SHOP_LIKE_CREATE_STATEMENT = 
			"CREATE TABLE IF NOT EXISTS SHOP_LIKE " +
			"( " +
			"SHOP_LIKE_NO integer, " +
			"USER_NO INTEGER, " +
			"SHOP_NO INTEGER, " +
			"IS_USE text, " +
			"CREATE_DATE TEXT, " +
			"UPDATE_DATE TEXT, " +
			"DELETE_DATE TEXT )";
	
	public final static String SHOP_LIKE_DROP_STATEMENT = 
			"DROP TABLE IF EXISTS SHOP_LIKE";
	
	public final static String SHOP_COMMENT_CREATE_STATEMENT =
			"CREATE TABLE IF NOT EXISTS SHOP_COMMENT " +
			"( " +
			"SHOP_COMMENT_NO	INTEGER," +
			"USER_NO	INTEGER," +
			"SHOP_NO	INTEGER," +
			"COMMENT	TEXT," +
			"IS_USE	TEXT," +
			"CREATE_DATE	TEXT," +
			"UPDATE_DATE	TEXT," +
			"DELETE_DATE	TEXT" +
			");";
	
	public final static String SHOP_COMMENT_DROP_STATEMENT =
			"DROP TABLE IF EXISTS SHOP_COMMENT";

	public ShopDataSource( Context context )
	{
		dbHelper = new MySQLiteHelper(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public long insertShop( ShopDao shop ) {

		ContentValues values = new ContentValues();

		values.put("SHOPSEQ", shop.getSeq());
		values.put("CATEGORY", shop.getCategory().replaceAll("\'", "&apos;"));
		values.put("SHOPNAME", shop.getShopName().replaceAll("\'", "&apos;"));
		values.put("PHONE", shop.getPhone().replaceAll("\'", "&apos;"));
		values.put("MOBILE", shop.getMobile().replaceAll("\'", "&apos;"));
		values.put("PHONE1", shop.getPhone1().replaceAll("\'", "&apos;"));
		values.put("PHONE2", shop.getPhone2().replaceAll("\'", "&apos;"));
		values.put("ADDRESS", shop.getAddress().replaceAll("\'", "&apos;"));
		values.put("LONGITUDE", shop.getLongitude());
		values.put("LATITUDE", shop.getLatitude());
		values.put("EMAIL", shop.getEmail().replaceAll("\'", "&apos;"));
		values.put("HOMEPAGE", shop.getHomepage().replaceAll("\'", "&apos;"));
		values.put("CREATE_DATE", shop.getCreateDate().replaceAll("\'", "&apos;"));
		values.put("UPDATE_DATE", shop.getUpdateDate().replaceAll("\'", "&apos;"));
		values.put("DELETE_DATE", shop.getDeleteDate().replaceAll("\'", "&apos;"));
		values.put("SHOPNAME_EN", shop.getShopNameEn().replaceAll("\'", "&apos;"));
		values.put("CATEGORY_EN", shop.getCategoryEn().replaceAll("\'", "&apos;"));

		long insertId = database.insert( "SHOP" , null,
				values);

		return insertId;
	}
	
	public void deleteAllShop()
	{
		database.delete( "SHOP", null, null );
	}
	
	public void deleteShops( String shopList )
	{
		if ( shopList == null || "".equals( shopList ) ) return;
		
		database.delete( "SHOP" , "SHOPSEQ in (" + shopList + ")", null );
	}
	
	public Cursor getShopCountByCategory()
	{
		return database.rawQuery("SELECT '전체' CATEGORY, COUNT(SHOPSEQ) AS CNT FROM SHOP " +
				"UNION ALL " +
				"SELECT CATEGORY, COUNT(SHOPSEQ) AS CNT FROM SHOP " +
				"GROUP BY CATEGORY " +
				"ORDER BY CNT DESC", null );
	}
	
	public JSONObject getShopInfo( String shopNo ) throws Exception
	{
		String sql = "SELECT S.*, CNT_LIKE, CNT_COMMENT " +
				"FROM SHOP S " +
				"LEFT OUTER JOIN ( SELECT SHOP_NO, COUNT(SHOP_NO) AS CNT_LIKE " +
				"FROM SHOP_LIKE " +
				"GROUP BY SHOP_NO ) SL ON S.SHOPSEQ=SL.SHOP_NO " +
				"LEFT OUTER JOIN ( SELECT SHOP_NO, COUNT(SHOP_NO) AS CNT_COMMENT " +
				"FROM SHOP_COMMENT " +
				"GROUP BY SHOP_NO) SC ON S.SHOPSEQ=SC.SHOP_NO " +
				"WHERE S.SHOPSEQ=" + shopNo + "";
		
		Cursor c = database.rawQuery( sql , null ); 
		
		JSONObject obj = new JSONObject();
		
		if ( c != null )
		{
			if ( c.moveToFirst() )
			{
				int index = 0;
				
				obj.put("SEQ", c.getString( index++ ));
				obj.put("CATEGORY", c.getString( index++ ) );
				obj.put("SHOPNAME", c.getString( index++ ) );
				obj.put("PHONE", c.getString( index++ ) );
				obj.put("MOBILE", c.getString( index++ ) );
				obj.put("PHONE1", c.getString( index++ ) );
				obj.put("PHONE2", c.getString( index++ ) );
				obj.put("ADDRESS", c.getString( index++ ) );
				obj.put("LONGITUDE", c.getString( index++ ) );
				obj.put("LATITUDE", c.getString( index++ ) );
				obj.put("EMAIL", c.getString( index++ ) );
				obj.put("HOMEPAGE", c.getString( index++ ) );
				obj.put("IS_SHOW_PRICE", c.getString( index++ ) );
				obj.put("CREATE_DATE", c.getString( index++ ) );
				obj.put("UPDATE_DATE", c.getString( index++ ) );
				obj.put("DELETE_DATE", c.getString( index++ ) );
				obj.put("SHOPNAME_EN", c.getString( index++ ) );
				obj.put("CATEGORY_EN", c.getString( index++ ) );
				obj.put("CNT_LIKES", c.getString( index++ ) );
				obj.put("CNT_COMMENTS", c.getString( index++ ) );
			}
		}
		c.close();
		
		return obj;
	}
	
	public JSONObject getShopInfoByName( String shopName ) throws Exception
	{
		String sql = "SELECT S.*, CNT_LIKE, CNT_COMMENT " +
				"FROM SHOP S " +
				"LEFT OUTER JOIN ( SELECT SHOP_NO, COUNT(SHOP_NO) AS CNT_LIKE " +
				"FROM SHOP_LIKE " +
				"GROUP BY SHOP_NO ) SL ON S.SHOPSEQ=SL.SHOP_NO " +
				"LEFT OUTER JOIN ( SELECT SHOP_NO, COUNT(SHOP_NO) AS CNT_COMMENT " +
				"FROM SHOP_COMMENT " +
				"GROUP BY SHOP_NO) SC ON S.SHOPSEQ=SC.SHOP_NO " +
				"WHERE S.SHOPNAME='" + shopName + "'";
		
		Cursor c = database.rawQuery( sql , null ); 
		
		JSONObject obj = new JSONObject();
		
		if ( c != null )
		{
			if ( c.moveToFirst() )
			{
				int index = 0;
				
				obj.put("SEQ", c.getString( index++ ));
				obj.put("CATEGORY", c.getString( index++ ) );
				obj.put("SHOPNAME", c.getString( index++ ) );
				obj.put("PHONE", c.getString( index++ ) );
				obj.put("MOBILE", c.getString( index++ ) );
				obj.put("PHONE1", c.getString( index++ ) );
				obj.put("PHONE2", c.getString( index++ ) );
				obj.put("ADDRESS", c.getString( index++ ) );
				obj.put("LONGITUDE", c.getString( index++ ) );
				obj.put("LATITUDE", c.getString( index++ ) );
				obj.put("EMAIL", c.getString( index++ ) );
				obj.put("HOMEPAGE", c.getString( index++ ) );
				obj.put("IS_SHOW_PRICE", c.getString( index++ ) );
				obj.put("CREATE_DATE", c.getString( index++ ) );
				obj.put("UPDATE_DATE", c.getString( index++ ) );
				obj.put("DELETE_DATE", c.getString( index++ ) );
				obj.put("SHOPNAME_EN", c.getString( index++ ) );
				obj.put("CATEGORY_EN", c.getString( index++ ) );
				obj.put("CNT_LIKES", c.getString( index++ ) );
				obj.put("CNT_COMMENTS", c.getString( index++ ) );
			}
		}
		c.close();
		
		return obj;
	}
	
	public Cursor getShopListByCategory( String category, String shopName, String orderBy )
	{
		String sql = "";
		
		String orderByClause = " ORDER BY CNT_LIKE DESC, CNT_COMMENT DESC,S.SHOPNAME";
		
		if ( "alphabetically".equals( orderBy ) )
			orderByClause = "ORDER BY S.SHOPNAME, CNT_LIKE DESC, CNT_COMMENT DESC";
		
		sql = "SELECT S.*, CNT_LIKE, CNT_COMMENT " +
				"FROM SHOP S " +
				"LEFT OUTER JOIN ( SELECT SHOP_NO, COUNT(SHOP_NO) AS CNT_LIKE " +
				"FROM SHOP_LIKE " +
				"GROUP BY SHOP_NO ) SL ON S.SHOPSEQ=SL.SHOP_NO " +
				"LEFT OUTER JOIN ( SELECT SHOP_NO, COUNT(SHOP_NO) AS CNT_COMMENT " +
				"FROM SHOP_COMMENT " +
				"GROUP BY SHOP_NO) SC ON S.SHOPSEQ=SC.SHOP_NO " +
				"WHERE S.CATEGORY='" + category + "'";
		
		if (!"".equals( shopName ) )
			sql += " AND S.SHOPNAME LIKE '%" + shopName + "%'";
		
		if ( category == null || "".equals( category ) || "전체".equals( category ) )
		{
			sql = "SELECT S.*, CNT_LIKE, CNT_COMMENT " +
					"FROM SHOP S " +
					"LEFT OUTER JOIN ( SELECT SHOP_NO, COUNT(SHOP_NO) AS CNT_LIKE " +
					"FROM SHOP_LIKE " +
					"GROUP BY SHOP_NO ) SL ON S.SHOPSEQ=SL.SHOP_NO " +
					"LEFT OUTER JOIN ( SELECT SHOP_NO, COUNT(SHOP_NO) AS CNT_COMMENT " +
					"FROM SHOP_COMMENT " +
					"GROUP BY SHOP_NO) SC ON S.SHOPSEQ=SC.SHOP_NO ";
			
			if (!"".equals( shopName ) )
				sql += " WHERE SHOP_NAME_KR LIKE %" + shopName + "%";
		}
		
		sql += orderByClause;
		
		return database.rawQuery( sql , null );
	}
	
	public boolean doesUserLikeShop( String userNo, String shopNo )
	{
		boolean bUserLikes = false;
		
		if ( userNo == null || "".equals( userNo ) ) return bUserLikes;
		if ( shopNo == null || "".equals( shopNo ) ) return bUserLikes;
		
		Cursor c = database.rawQuery("SELECT COUNT(SHOP_LIKE_NO) FROM SHOP_LIKE " +
				"WHERE SHOP_NO=" + shopNo + " AND USER_NO=" + userNo, null );
		if ( c != null )
		{
			if ( c.moveToFirst() )
			{
				int count = c.getInt(0);
				if ( count > 0 )
					bUserLikes = true;
				else
					bUserLikes = false;
			}
		}
		c.close();
		
		return bUserLikes;
	}
	
	public int getShopLikesCount( String shopNo )
	{
		int count = 0;
		Cursor c = database.rawQuery("SELECT COUNT(USER_NO) FROM SHOP_LIKE WHERE SHOP_NO=" + shopNo, null );
		if ( c != null )
		{
			if ( c.moveToFirst() )
			{
				count = c.getInt(0);
			}
		}
		c.close();
		
		return count;
	}
	
	public ShopLikeDao getShopLike( String shopNo, String userNo )
	{
		if ( shopNo == null || "".equals( shopNo ) ) return null;
		if ( userNo == null || "".equals( userNo ) ) return null;
		
		ShopLikeDao shopLike = new ShopLikeDao();
		
		Cursor c = database.rawQuery("SELECT * FROM SHOP_LIKE WHERE SHOP_NO=" + 
									shopNo + " AND USER_NO=" + userNo , null );
		if ( c != null )
		{
			if ( c.moveToFirst() )
			{
				int index = 0;
				
				shopLike.setShopLikeNo( c.getInt( index++ ) );
				shopLike.setUserNo( c.getInt( index++ ) );
				shopLike.setShopNo( c.getInt( index++ ) );
				shopLike.setIsUse( c.getString( index++ ) );
				shopLike.setCreateDate( c.getString( index++ ) );
				shopLike.setUpdateDate( c.getString( index++ ) );
				shopLike.setDeleteDate( c.getString( index++ ) );
				
			}
		}
		c.close();
		
		return shopLike;
	}
	
	public long insertShopLike( ShopLikeDao shopLike ) {

		ContentValues values = new ContentValues();

		values.put("SHOP_LIKE_NO", shopLike.getShopLikeNo());
		values.put("USER_NO", shopLike.getUserNo());
		values.put("SHOP_NO", shopLike.getShopNo());
		values.put("IS_USE", shopLike.getIsUse());
		values.put("CREATE_DATE", shopLike.getCreateDate());
		values.put("UPDATE_DATE", shopLike.getUpdateDate());
		values.put("DELETE_DATE", shopLike.getDeleteDate());

		long insertId = database.insert( "SHOP_LIKE" , null,
				values);

		return insertId;
	}
	
	public void deleteShopLikes( String shopLikes )
	{
		if ( shopLikes == null || "".equals( shopLikes ) ) return;
		
		database.delete( "SHOP_LIKE" , "SHOP_LIKE_NO in (" + shopLikes + ")", null );
	}
	
	public void deleteShopLike( int shopLikeNo )
	{
		if ( shopLikeNo == 0 ) return;
		
		database.delete( "SHOP_LIKE" , "SHOP_LIKE_NO=" + shopLikeNo , null );
	}
	
	public void deleteShopLike( String shopNo, String userNo )
	{
		if ( shopNo == null || "".equals( shopNo ) ) return;
		if ( userNo == null || "".equals( userNo ) ) return;
		
		database.delete( "SHOP_LIKE" , "SHOP_NO=" + shopNo + " AND USER_NO=" + userNo , null );
	}
	
	public int getShopCommentsCount( int shopNo )
	{
		int count = 0;
		Cursor c = database.rawQuery("SELECT COUNT(USER_NO) FROM SHOP_LIKE WHERE SHOP_NO=" + shopNo, null );
		if ( c != null )
		{
			if ( c.moveToFirst() )
			{
				count = c.getInt(0);
			}
		}
		c.close();
		
		return count;
	}
	
	public ArrayList<JSONObject> getShopComments( String shopNo ) throws Exception
	{
		ArrayList<JSONObject> ar = new ArrayList<JSONObject>();
		
		Cursor c = database.rawQuery("select * from SHOP_COMMENT where SHOP_NO=" + shopNo, null );
		if ( c != null )
		{
			if ( c.moveToFirst() )
			{
				do
				{
					int index = 0;
					
					JSONObject obj = new JSONObject();
					obj.put("SHOP_COMMENT_NO", c.getInt( index++ ) );
					obj.put("USER_NO", c.getInt( index++ ));
					obj.put("SHOP_NO", c.getInt( index++ ));
					obj.put("COMMENT", c.getString( index++ ).replaceAll("&apos;", "'") );
					obj.put("IS_USE", c.getString( index++ ));
					obj.put("CREATE_DATE", c.getString( index++ ));
					obj.put("UPDATE_DATE", c.getString( index++ ));
					obj.put("DELETE_DATE", c.getString( index++ ));
					ar.add( obj );
				}
				while( c.moveToNext() );
			}
		}
		c.close();
		
		return ar;
	}
	
	public long insertShopComment( ShopCommentDao shopComment ) {

		ContentValues values = new ContentValues();

		values.put("SHOP_COMMENT_NO", shopComment.getShopCommentNo());
		values.put("USER_NO", shopComment.getUserNo());
		values.put("SHOP_NO", shopComment.getShopNo());
		values.put("COMMENT", shopComment.getComment());
		values.put("IS_USE", shopComment.getIsUse());
		values.put("CREATE_DATE", shopComment.getCreateDate());
		values.put("UPDATE_DATE", shopComment.getUpdateDate());
		values.put("DELETE_DATE", shopComment.getDeleteDate());

		long insertId = database.insert( "SHOP_COMMENT" , null, values);

		return insertId;
	}
	
	public void deleteShopComments( String shopComments )
	{
		if ( shopComments == null || "".equals( shopComments ) ) return;
		
		database.delete( "SHOP_COMMENT" , "SHOP_COMMENT_NO in (" + shopComments + ")", null );
	}
}
