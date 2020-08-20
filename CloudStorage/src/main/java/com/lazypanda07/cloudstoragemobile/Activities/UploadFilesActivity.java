package com.lazypanda07.cloudstoragemobile.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.lazypanda07.cloudstoragemobile.DataBases.User;
import com.lazypanda07.cloudstoragemobile.DataBases.UserSettingsSingleton;
import com.lazypanda07.cloudstoragemobile.GetDataFromUri.FileDataFromUri;
import com.lazypanda07.cloudstoragemobile.IntentEvents;
import com.lazypanda07.cloudstoragemobile.NetworkFunctions;
import com.lazypanda07.cloudstoragemobile.R;

import java.io.DataInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class UploadFilesActivity extends AppCompatActivity
{
	private AppCompatActivity ref = this;
	private String login;
	private String password;
	private static String fileName = null;
	private static int fileSize = -1;
	private static Uri uri;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_upload_files);

		Intent intent = getIntent();
		Bundle bundle;

		showAllAvailableAccounts();

		if (intent.getAction() != null && intent.getAction().equals(Intent.ACTION_SEND))
		{
			bundle = intent.getExtras();

			if (bundle != null)
			{
				uri = (Uri) bundle.get(Intent.EXTRA_STREAM);
			}

			fileSize = intent.getIntExtra("fileSize", -1);
			fileName = intent.getStringExtra("fileName");
		}
		else
		{
			login = intent.getStringExtra("login");
			password = intent.getStringExtra("password");

			uploadFile();
		}
	}

	private void showAllAvailableAccounts()
	{
		Button button = findViewById(R.id.authorization_in_another_account);
		Button cancelButton = findViewById(R.id.cancel_button);
		UserSettingsSingleton instance = UserSettingsSingleton.getInstance(getApplicationContext());
		ListView variants = findViewById(R.id.variants);
		final ArrayList<User> users = instance.getAllUsers();
		final ArrayList<String> userNames = new ArrayList<>();
		ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, userNames);

		variants.setAdapter(adapter);

		variants.setOnItemClickListener(new AdapterView.OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
			{
				login = users.get(i).login;
				password = users.get(i).password;

				uploadFile();
			}
		});

		for (User i : users)
		{
			userNames.add(i.login);
		}

		adapter.notifyDataSetChanged();

		button.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				Intent toAuthorizationActivity = new Intent(getBaseContext(), AuthorizationActivity.class);

				toAuthorizationActivity.putExtra(IntentEvents.string(), IntentEvents.AUTHORIZATION_FOR_UPLOAD_FILES);

				toAuthorizationActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

				startActivity(toAuthorizationActivity);
			}
		});

		cancelButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				finish();
			}
		});
	}

	private void uploadFile()
	{
		try
		{
			if (uri != null)
			{
				if (fileSize == -1 && fileName == null)
				{
					fileSize = FileDataFromUri.getFileSize(ref, uri);
					fileName = FileDataFromUri.getFileName(ref, uri);
				}

				DataInputStream stream = new DataInputStream(getContentResolver().openInputStream(uri));

				//TODO: alert dialog for replace
				NetworkFunctions.uploadFile(ref, stream, fileSize, fileName, login, password, null, null, new String[]{"Home"}, findViewById(R.id.upload_files), false);
			}
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
	}
}