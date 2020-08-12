package com.lazypanda07.cloudstoragemobile.Activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.lazypanda07.cloudstoragemobile.R;

public class SettingsActivity extends AppCompatActivity
{
	private AppCompatActivity ref = this;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
	}
}