package com.lazypanda07.cloudstoragemobile.DataBases;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class ConnectionSettingsSingleton
{
	private static ConnectionSettingsSingleton instance = null;

	private ConnectionSettings connectionSettings;

	private ConnectionSettingsSingleton(Context context)
	{
		connectionSettings = new ConnectionSettings(context);
	}

	public static ConnectionSettingsSingleton getInstance()
	{
		return instance;
	}

	public static ConnectionSettingsSingleton getInstance(Context context)
	{
		if (instance == null)
		{
			instance = new ConnectionSettingsSingleton(context);
		}

		return instance;
	}

	public boolean addNewServerSettings(String ip, int port)
	{
		SQLiteDatabase db = connectionSettings.getWritableDatabase();
		ContentValues values = new ContentValues();

		values.put(ConnectionSettings.Constants.IP_ADDRESS, ip);
		values.put(ConnectionSettings.Constants.PORT, port);

		try
		{
			db.insertOrThrow(ConnectionSettings.Constants.TABLE_NAME, null, values);

			return true;
		}
		catch (SQLException e)
		{
			return false;
		}
	}

	public void deleteServerSettings(String ip, int port)
	{
		@SuppressLint("DefaultLocale") String condition = String.format("%s = '%s' AND %s = %d", ConnectionSettings.Constants.IP_ADDRESS, ip, ConnectionSettings.Constants.PORT, port);
		SQLiteDatabase db = connectionSettings.getWritableDatabase();

		db.delete(ConnectionSettings.Constants.TABLE_NAME, condition, null);
	}

	@SuppressLint("DefaultLocale")
	public void setLastUsed(String ip, int port)
	{
		String condition = ConnectionSettings.Constants.LAST_USED + " = 1";
		SQLiteDatabase db = connectionSettings.getWritableDatabase();
		ContentValues values = new ContentValues();

		values.put(ConnectionSettings.Constants.LAST_USED, 0);

		db.update(ConnectionSettings.Constants.TABLE_NAME, values, condition, null);

		values.clear();

		condition = String.format("%s = '%s' AND %s = %d", ConnectionSettings.Constants.IP_ADDRESS, ip, ConnectionSettings.Constants.PORT, port);

		values.put(ConnectionSettings.Constants.LAST_USED, 1);

		db.update(ConnectionSettings.Constants.TABLE_NAME, values, condition, null);
	}

	public ArrayList<Connection> getAllServerSettings()
	{
		ArrayList<Connection> result = new ArrayList<>();
		SQLiteDatabase db = connectionSettings.getReadableDatabase();

		Cursor cursor = db.query
				(
						ConnectionSettings.Constants.TABLE_NAME,
						new String[]{ConnectionSettings.Constants.IP_ADDRESS, ConnectionSettings.Constants.PORT, ConnectionSettings.Constants.LAST_USED},
						null,
						null,
						null,
						null,
						null
				);

		while (cursor.moveToNext())
		{
			result.add(new Connection
					(
							cursor.getString(cursor.getColumnIndex(ConnectionSettings.Constants.IP_ADDRESS)),
							cursor.getInt(cursor.getColumnIndex(ConnectionSettings.Constants.PORT)),
							cursor.getInt(cursor.getColumnIndex(ConnectionSettings.Constants.LAST_USED))
					));
		}

		cursor.close();

		return result;
	}

	public Connection getLastUsed()
	{
		String condition = ConnectionSettings.Constants.LAST_USED + " = 1";
		Connection result;
		SQLiteDatabase db = connectionSettings.getReadableDatabase();

		Cursor cursor = db.query
				(
						ConnectionSettings.Constants.TABLE_NAME,
						new String[]{ConnectionSettings.Constants.IP_ADDRESS, ConnectionSettings.Constants.PORT, ConnectionSettings.Constants.LAST_USED},
						null,
						null,
						null,
						null,
						null
				);

		if (!cursor.moveToFirst())
		{
			cursor.close();
			return null;
		}

		result = new Connection
				(
						cursor.getString(cursor.getColumnIndex(ConnectionSettings.Constants.IP_ADDRESS)),
						cursor.getInt(cursor.getColumnIndex(ConnectionSettings.Constants.PORT)),
						cursor.getInt(cursor.getColumnIndex(ConnectionSettings.Constants.LAST_USED))
				);

		cursor.close();

		return result;
	}
}
