package com.lazypanda07.cloudstoragemobile.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.lazypanda07.cloudstoragemobile.DataBases.Connection;
import com.lazypanda07.cloudstoragemobile.DataBases.ConnectionSettingsSingleton;
import com.lazypanda07.cloudstoragemobile.DataBases.UserSettingsSingleton;
import com.lazypanda07.cloudstoragemobile.IntentEvents;
import com.lazypanda07.cloudstoragemobile.NetworkFunctions;
import com.lazypanda07.cloudstoragemobile.R;

import java.util.ArrayList;

public class SettingsActivity extends AppCompatActivity
{
	private AppCompatActivity ref = this;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);

		Toolbar toolbar = findViewById(R.id.toolbar);
		final Switch chooseStorage = findViewById(R.id.choose_storage_switch);
		final Switch autoLogin = findViewById(R.id.auto_login_switch);
		final EditText ip = findViewById(R.id.settings_ip);
		final EditText port = findViewById(R.id.settings_port);
		final String login = getIntent().getStringExtra("login");
		final UserSettingsSingleton userInstance = UserSettingsSingleton.getInstance();
		final ConnectionSettingsSingleton connectionInstance = ConnectionSettingsSingleton.getInstance();
		ArrayList<Connection> serverSettings = connectionInstance.getAllServerSettings();
		Intent intent = getIntent();

		if (userInstance.getStorageType(login).equals(NetworkFunctions.StorageType.INTERNAL))
		{
			chooseStorage.setChecked(true);
		}
		else
		{
			chooseStorage.setChecked(false);
		}
		autoLogin.setChecked(userInstance.getAutoLogin(login));

		if (intent != null)
		{
			String eventType = intent.getStringExtra(IntentEvents.string());

			if (eventType != null)
			{
				TextView textView = findViewById(R.id.server_settings);
				LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) textView.getLayoutParams();

				params.topMargin = 0;

				chooseStorage.setVisibility(View.GONE);
				autoLogin.setVisibility(View.GONE);
				findViewById(R.id.account_settings).setVisibility(View.GONE);
				textView.setLayoutParams(params);
			}
		}

		if (serverSettings.size() > 0)
		{
			ip.setText(serverSettings.get(serverSettings.size() - 1).ip);
			port.setText(String.valueOf(serverSettings.get(serverSettings.size() - 1).port));
		}

		toolbar.setNavigationOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				String serverIp;
				int serverPort;

				try
				{
					serverIp = ip.getText().toString();
					serverPort = Integer.parseInt(port.getText().toString());

					if (!serverIp.equals(""))
					{
						connectionInstance.addNewServerSettings(serverIp, serverPort);

						connectionInstance.setLastUsed(serverIp, serverPort);
					}
				}
				catch (NumberFormatException ignored)
				{

				}

				finish();
			}
		});

		chooseStorage.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				boolean isChecked = chooseStorage.isChecked();

				if (isChecked)
				{
					NetworkFunctions.storageType = NetworkFunctions.StorageType.INTERNAL;
				}
				else
				{
					NetworkFunctions.storageType = NetworkFunctions.StorageType.SDCard;
				}

				userInstance.updateStorageType(login, NetworkFunctions.storageType);
			}
		});

		autoLogin.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				userInstance.updateAutoLogin(login, autoLogin.isChecked());
			}
		});
	}
}