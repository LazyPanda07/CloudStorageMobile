package com.lazypanda07.cloudstoragemobile.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.lazypanda07.cloudstoragemobile.DataBases.User;
import com.lazypanda07.cloudstoragemobile.DataBases.UserSettingsSingleton;
import com.lazypanda07.cloudstoragemobile.HideKeyboard;
import com.lazypanda07.cloudstoragemobile.NetworkFunctions;
import com.lazypanda07.cloudstoragemobile.R;

//TODO: add database with automated authorization
public class AuthorizationActivity extends AppCompatActivity
{
	private final AppCompatActivity ref = this;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_authorization);

		UserSettingsSingleton instance = UserSettingsSingleton.getInstance(getApplicationContext());

		User user = instance.getLastAutoLoginUser();

		if (user != null)
		{
			((TextView) findViewById(R.id.authorization_login)).setText(user.login);
			((TextView) findViewById(R.id.authorization_password)).setText(user.password);

			NetworkFunctions.authorization(ref, findViewById(R.id.authorization));
		}
	}

	public void authorization(View view)
	{
		HideKeyboard.hideKeyboard(ref);

		NetworkFunctions.authorization(ref, findViewById(R.id.authorization));
	}

	public void toRegistrationActivity(View view)
	{
		Intent intent = new Intent(ref, RegistrationActivity.class);

		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

		startActivity(intent);

		finish();
	}
}