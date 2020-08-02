package com.lazypanda07.cloudstoragemobile;

import android.widget.Toast;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

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
}
