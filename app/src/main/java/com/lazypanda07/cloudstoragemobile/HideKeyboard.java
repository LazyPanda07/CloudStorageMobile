package com.lazypanda07.cloudstoragemobile;

import android.app.Activity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AppCompatActivity;

public class HideKeyboard
{
	//method from https://stackoverflow.com/questions/1109022/how-do-you-close-hide-the-android-soft-keyboard-using-java
	public static void hideKeyboard(AppCompatActivity activity)
	{
		InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
		View view = activity.getCurrentFocus();

		if (view == null)
		{
			view = new View(activity);
		}

		inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}
}
