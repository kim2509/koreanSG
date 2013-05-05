package com.korea.common;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MySQLiteHelper extends SQLiteOpenHelper 
{
	private static final String DATABASE_NAME = "korean.db";
	private static final int DATABASE_VERSION = 1;

	public MySQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		
		database.execSQL( ShopDataSource.SHOP_CREATE_STATEMENT );
		database.execSQL( ShopDataSource.SHOP_LIKE_CREATE_STATEMENT );
		database.execSQL( ShopDataSource.SHOP_COMMENT_CREATE_STATEMENT );
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		db.execSQL( ShopDataSource.SHOP_DROP_STATEMENT );
		db.execSQL( ShopDataSource.SHOP_LIKE_DROP_STATEMENT );
		db.execSQL( ShopDataSource.SHOP_COMMENT_DROP_STATEMENT );
		
		onCreate(db);
	}
}
