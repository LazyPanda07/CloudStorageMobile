package com.lazypanda07.cloudstoragemobile.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.lazypanda07.cloudstoragemobile.DataBases.Connection;
import com.lazypanda07.cloudstoragemobile.DataBases.ConnectionSettingsSingleton;
import com.lazypanda07.cloudstoragemobile.DataBases.User;
import com.lazypanda07.cloudstoragemobile.DataBases.UserSettingsSingleton;
import com.lazypanda07.cloudstoragemobile.HideKeyboard;
import com.lazypanda07.cloudstoragemobile.IntentEvents;
import com.lazypanda07.cloudstoragemobile.NetworkFunctions;
import com.lazypanda07.cloudstoragemobile.R;
import com.lazypanda07.networklib.Constants;

public class AuthorizationActivity extends AppCompatActivity
{
	private final AppCompatActivity ref = this;
	private String eventType;
	private Connection connection;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_authorization);

		Toolbar toolbar = findViewById(R.id.toolbar);
		UserSettingsSingleton userInstance = UserSettingsSingleton.getInstance(getApplicationContext());
		ConnectionSettingsSingleton connectionInstance = ConnectionSettingsSingleton.getInstance(getApplicationContext());
		Intent intent = getIntent();
		connection = connectionInstance.getLastUsed();

		toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener()
		{
			@Override
			public boolean onMenuItemClick(MenuItem item)
			{
				if (item.getItemId() == R.id.settings)
				{
					Intent toSettingsActivity = new Intent(getBaseContext(), SettingsActivity.class);

					toSettingsActivity.putExtra(IntentEvents.string(), IntentEvents.AUTHORIZATION_SETTINGS);

					startActivity(toSettingsActivity);

					return true;
				}

				return false;
			}
		});

		if (connection != null)
		{
			Constants.APIServerIp = connection.ip;
			Constants.APIServerPort = connection.port;
		}

		if (intent != null)
		{
			eventType = intent.getStringExtra(IntentEvents.string());

			if (eventType != null && eventType.equals(IntentEvents.EXIT_FROM_ACCOUNT))
			{
				return;
			}
			else if (eventType != null && eventType.equals(IntentEvents.AUTHORIZATION_FOR_UPLOAD_FILES))
			{
				return;
			}
		}

		User user = userInstance.getLastAutoLoginUser();

		if (user != null && connection != null)
		{
			((TextView) findViewById(R.id.authorization_login)).setText(user.login);
			((TextView) findViewById(R.id.authorization_password)).setText(user.password);

			NetworkFunctions.authorization(ref, findViewById(R.id.authorization), CloudStorageActivity.class);
		}
	}

	public void authorization(View view)
	{
		HideKeyboard.hideKeyboard(ref);

		if (connection == null)
		{
			Toast.makeText(getApplicationContext(), R.string.empty_server_settings, Toast.LENGTH_LONG).show();

			return;
		}

		if (eventType.equals(IntentEvents.AUTHORIZATION_FOR_UPLOAD_FILES))
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