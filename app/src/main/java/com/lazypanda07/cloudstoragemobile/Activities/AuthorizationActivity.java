package com.lazypanda07.cloudstoragemobile.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

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