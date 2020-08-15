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

public class AuthorizationActivity extends AppCompatActivity
{
	private final AppCompatActivity ref = this;
	private String eventType;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_authorization);

		UserSettingsSingleton instance = UserSettingsSingleton.getInstance(getApplicationContext());
		Intent intent = getIntent();

		if (intent != null)
		{
			eventType = intent.getStringExtra("Event");

			//TODO: class for events
			if (eventType != null && eventType.equals("Exit from account"))
			{
				return;
			}
			else if (eventType != null && eventType.equals("Authorization for upload files"))
			{
				return;
			}
		}

		User user = instance.getLastAutoLoginUser();

		if (user != null)
		{
			((TextView) findViewById(R.id.authorization_login)).setText(user.login);
			((TextView) findViewById(R.id.authorization_password)).setText(user.password);

			NetworkFunctions.authorization(ref, findViewById(R.id.authorization), CloudStorageActivity.class);
		}
	}

	public void authorization(View view)
	{
		HideKeyboard.hideKeyboard(ref);

		if (eventType.equals("Authorization for upload files"))
		{
			NetworkFunctions.authorization(ref, findViewById(R.id.authorization), UploadFilesActivity.class);
		}
		else
		{
			NetworkFunctions.authorization(ref, findViewById(R.id.authorization), CloudStorageActivity.class);
		}
	}

	public void toRegistrationActivity(View view)
	{
		Intent intent = new Intent(ref, RegistrationActivity.class);

		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

		startActivity(intent);

		finish();
	}
}