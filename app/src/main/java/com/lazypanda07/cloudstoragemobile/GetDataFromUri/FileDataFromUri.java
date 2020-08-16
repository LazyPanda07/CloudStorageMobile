package com.lazypanda07.cloudstoragemobile.GetDataFromUri;

import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;

import androidx.appcompat.app.AppCompatActivity;

public class FileDataFromUri
{
	public static int getFileSize(AppCompatActivity activity, Uri fileUri)
	{
		Cursor cursor = activity.getContentResolver().query(fileUri, null, null, null, null);
		int result;

		cursor.moveToFirst();

		result = cursor.getInt(cursor.getColumnIndex(OpenableColumns.SIZE));

		cursor.close();

		return result;
	}

	public static String getFileName(AppCompatActivity activity, Uri fileUri)
	{
		String result = null;
		Cursor cursor = activity.getContentResolver().query(fileUri, null, null, null, null);

		cursor.moveToFirst();

		result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));

		cursor.close();

		if (result == null)
		{
			result = fileUri.getPath();
			int cut = result.lastIndexOf('/');
			if (cut != -1)
			{
				result = result.substring(cut + 1);
			}
		}

		return result;
	}
}
