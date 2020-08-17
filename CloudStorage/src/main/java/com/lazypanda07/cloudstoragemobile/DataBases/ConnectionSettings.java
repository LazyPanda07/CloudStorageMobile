package com.lazypanda07.cloudstoragemobile.DataBases;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import static com.lazypanda07.cloudstoragemobile.DataBases.ConnectionSettings.Constants.ID;
import static com.lazypanda07.cloudstoragemobile.DataBases.ConnectionSettings.Constants.IP_ADDRESS;
import static com.lazypanda07.cloudstoragemobile.DataBases.ConnectionSettings.Constants.LAST_USED;
import static com.lazypanda07.cloudstoragemobile.DataBases.ConnectionSettings.Constants.PORT;
import static com.lazypanda07.cloudstoragemobile.DataBases.ConnectionSettings.Constants.TABLE_NAME;


class ConnectionSettings extends SQLiteOpenHelper
{
	public static final String DATABASE_NAME = "Connection.sqlite3";
	public static final int DATABASE_VERSION = 1;

	public ConnectionSettings(Context context)
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
								IP_ADDRESS + " TEXT NOT NULL, " +
								PORT + " INTEGER NOT NULL, " +
								LAST_USED + " INTEGER NOT NULL DEFAULT 0);"
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
		public static final String TABLE_NAME = "Connection";
		public static final String ID = "id";
		public static final String IP_ADDRESS = "ip_address";
		public static final String PORT = "port";
		public static final String LAST_USED = "last_user";
	}
}
