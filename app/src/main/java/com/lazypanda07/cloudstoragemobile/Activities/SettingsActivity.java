package com.lazypanda07.cloudstoragemobile.Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.lazypanda07.cloudstoragemobile.NetworkFunctions;
import com.lazypanda07.cloudstoragemobile.R;

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

		toolbar.setNavigationOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
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
			}
		});

		autoLogin.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				boolean isChecked = autoLogin.isChecked();
			}
		});
	}
}