package com.lazypanda07.cloudstoragemobile;

import android.widget.Toast;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import java.io.UnsupportedEncodingException;

class ErrorHandling
{
	public static void showError(final AppCompatActivity activity, final String errorMessage)
	{
		activity.runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				Toast.makeText(activity.getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
			}
		});
	}

	public static void showError(final AppCompatActivity activity, final @StringRes int errorMessage)
	{
		activity.runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				Toast.makeText(activity.getApplicationContext(), activity.getResources().getString(errorMessage), Toast.LENGTH_LONG).show();
			}
		});
	}

	public static void showError(final AppCompatActivity activity, final byte[] errorMessage)
	{
		activity.runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					Toast.makeText(activity.getApplicationContext(), new String(errorMessage, "CP1251"), Toast.LENGTH_LONG).show();
				}
				catch (UnsupportedEncodingException e)
				{
					showError(activity, R.string.cast_error);
					e.printStackTrace();
				}
			}
		});
	}
}
