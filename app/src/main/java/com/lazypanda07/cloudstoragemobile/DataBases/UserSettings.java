package com.lazypanda07.cloudstoragemobile.DataBases;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import static com.lazypanda07.cloudstoragemobile.DataBases.UserSettings.Constants.AUTHORIZATION_DATE;
import static com.lazypanda07.cloudstoragemobile.DataBases.UserSettings.Constants.AUTO_LOGIN;
import static com.lazypanda07.cloudstoragemobile.DataBases.UserSettings.Constants.ID;
import static com.lazypanda07.cloudstoragemobile.DataBases.UserSettings.Constants.LOGIN;
import static com.lazypanda07.cloudstoragemobile.DataBases.UserSettings.Constants.PASSWORD;
import static com.lazypanda07.cloudstoragemobile.DataBases.UserSettings.Constants.STORAGE_TYPE;
import static com.lazypanda07.cloudstoragemobile.DataBases.UserSettings.Constants.TABLE_NAME;

class UserSettings extends SQLiteOpenHelper
{
	public static final String DATABASE_NAME = "UserSettings.sqlite3";
	public static final int DATABASE_VERSION = 1;

	public UserSettings(Context context)
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase sqLiteDatabase)
	{
		sqLiteDatabase.execSQL
				(
						"CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "( " +
								ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
								LOGIN + " TEXT UNIQUE NOT NULL, " +
								PASSWORD + " TEXT NOT NULL, " +
								AUTO_LOGIN + " INTEGER NOT NULL DEFAULT 0, " +
								STORAGE_TYPE + " TEXT NOT NULL DEFAULT \"INTERNAL\", " +
								AUTHORIZATION_DATE + " DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP);"
				);
	}

	@Override
	public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1)
	{
		sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		onCreate(sqLiteDatabase);
	}

	public static class Constants implements BaseColumns
	{
		public static final String TABLE_NAME = "UserSettings";
		public static final String ID = "id";
		public static final String LOGIN = "login";
		public static final String PASSWORD = "password";
		public static final String AUTO_LOGIN = "auto_login";
		public static final String STORAGE_TYPE = "storage_type";
		public static final String AUTHORIZATION_DATE = "added_date";
	}
}
