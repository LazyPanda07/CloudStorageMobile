package com.lazypanda07.cloudstoragemobile.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.lazypanda07.cloudstoragemobile.HideKeyboard;
import com.lazypanda07.cloudstoragemobile.NetworkFunctions;
import com.lazypanda07.cloudstoragemobile.R;

public class RegistrationActivity extends AppCompatActivity
{
	private final AppCompatActivity ref = this;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_registration);
	}

	public void registration(View view)
	{
		HideKeyboard.hideKeyboard(ref);

		NetworkFunctions.registration(ref, findViewById(R.id.registration));
	}

	public void toAuthorizationActivity(View view)
	{
		Intent intent = new Intent(ref, AuthorizationActivity.class);

		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

		startActivity(intent);
	}
}