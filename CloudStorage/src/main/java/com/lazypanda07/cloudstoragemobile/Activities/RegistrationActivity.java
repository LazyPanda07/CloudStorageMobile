package com.lazypanda07.cloudstoragemobile.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.lazypanda07.cloudstoragemobile.DataBases.Connection;
import com.lazypanda07.cloudstoragemobile.DataBases.ConnectionSettingsSingleton;
import com.lazypanda07.cloudstoragemobile.HideKeyboard;
import com.lazypanda07.cloudstoragemobile.NetworkFunctions;
import com.lazypanda07.cloudstoragemobile.R;
import com.lazypanda07.networklib.Constants;

public class RegistrationActivity extends AppCompatActivity
{
	private final AppCompatActivity ref = this;
	private Connection connection;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_registration);

		ConnectionSettingsSingleton connectionInstance = ConnectionSettingsSingleton.getInstance(getApplicationContext());
		connection = connectionInstance.getLastUsed();

		if (connection != null)
		{
			Constants.APIServerIp = connection.ip;
			Constants.APIServerPort = connection.port;
		}
	}

	public void registration(View view)
	{
		HideKeyboard.hideKeyboard(ref);

		if (connection == null)
		{
			Toast.makeText(getApplicationContext(), R.string.empty_server_settings, Toast.LENGTH_LONG).show();

			return;
		}

		NetworkFunctions.registration(ref, findViewById(R.id.registration));
	}

	public void toAuthorizationActivity(View view)
	{
		Intent intent = new Intent(ref, AuthorizationActivity.class);

		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

		startActivity(intent);
	}
}