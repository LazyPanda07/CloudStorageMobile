package com.lazypanda07.cloudstoragemobile.DataBases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.lazypanda07.cloudstoragemobile.NetworkFunctions;

public class UserSettingsSingleton
{
	private static UserSettingsSingleton instance = null;

	private UserSettings userSettings;

	private UserSettingsSingleton(Context context)
	{
		userSettings = new UserSettings(context);
	}

	public static UserSettingsSingleton getInstance()
	{
		return instance;
	}

	public static UserSettingsSingleton getInstance(Context context)
	{
		if (instance == null)
		{
			instance = new UserSettingsSingleton(context);
		}

		return instance;
	}

	public void updateAutoLogin(String login, boolean autoLogin)
	{
		String condition = UserSettings.Constants.LOGIN + " = " + login;
		SQLiteDatabase db = userSettings.getWritableDatabase();
		ContentValues values = new ContentValues();

		values.put(UserSettings.Constants.AUTO_LOGIN, autoLogin);

		db.update(UserSettings.Constants.TABLE_NAME, values, condition, null);
	}

	public void updateStorageType(String login, NetworkFunctions.StorageType type)
	{
		String condition = UserSettings.Constants.LOGIN + " = " + login;
		SQLiteDatabase db = userSettings.getWritableDatabase();
		ContentValues values = new ContentValues();

		if (type.equals(NetworkFunctions.StorageType.INTERNAL))
		{
			values.put(UserSettings.Constants.STORAGE_TYPE, "INTERNAL");
		}
		else if (type.equals(NetworkFunctions.StorageType.SDCard))
		{
			values.put(UserSettings.Constants.STORAGE_TYPE, "SDCard");
		}

		db.update(UserSettings.Constants.TABLE_NAME, values, condition, null);
	}

	public void addNewUser(String login, String password)
	{
		ContentValues values = new ContentValues();
		SQLiteDatabase db = userSettings.getWritableDatabase();

		values.put(UserSettings.Constants.LOGIN, login);
		values.put(UserSettings.Constants.PASSWORD, password);

		db.insert(UserSettings.Constants.TABLE_NAME, null, values);
	}

	public User getAutoLogin()
	{
		User result;
		String condition = UserSettings.Constants.AUTO_LOGIN + " = 1";
		SQLiteDatabase db = userSettings.getReadableDatabase();

		Cursor cursor = db.query
				(
						UserSettings.Constants.TABLE_NAME,
						new String[]{UserSettings.Constants.LOGIN, UserSettings.Constants.PASSWORD},
						condition,
						null,
						null,
						null,
						UserSettings.Constants.AUTHORIZATION_DATE
				);

		if (!cursor.moveToFirst())
		{
			cursor.close();
			return null;
		}

		result = new User
				(
						cursor.getString(cursor.getColumnIndex(UserSettings.Constants.LOGIN)),
						cursor.getString(cursor.getColumnIndex(UserSettings.Constants.PASSWORD))
				);

		cursor.close();

		return result;
	}

	public NetworkFunctions.StorageType getStorageType(String login)
	{
		String type;
		String condition = UserSettings.Constants.LOGIN + " = '" + login + "'";
		SQLiteDatabase db = userSettings.getReadableDatabase();

		Cursor cursor = db.query
				(
						UserSettings.Constants.TABLE_NAME,
						new String[]{UserSettings.Constants.STORAGE_TYPE},
						condition,
						null,
						null,
						null,
						null
				);

		if (!cursor.moveToFirst())
		{
			cursor.close();
			return NetworkFunctions.StorageType.INTERNAL;
		}

		type = cursor.getString(cursor.getColumnIndex(UserSettings.Constants.STORAGE_TYPE));

		cursor.close();

		if (type.equals("INTERNAL"))
		{
			return NetworkFunctions.StorageType.INTERNAL;
		}
		else if (type.equals("SDCard"))
		{
			return NetworkFunctions.StorageType.SDCard;
		}
		else
		{
			return null;
		}
	}
}
