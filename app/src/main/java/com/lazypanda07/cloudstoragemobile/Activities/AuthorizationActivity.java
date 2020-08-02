package com.lazypanda07.cloudstoragemobile.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.lazypanda07.cloudstoragemobile.NetworkFunctions;
import com.lazypanda07.cloudstoragemobile.R;

public class AuthorizationActivity extends AppCompatActivity
{
	private final AppCompatActivity ref = this;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_authorization);
	}

	public void authorization(View view)
	{
		NetworkFunctions.authorization(ref);
	}

	public void toRegistrationScreen(View view)
	{
		Intent intent = new Intent(ref, RegistrationActivity.class);

		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

		startActivity(intent);

		finish();
	}
}