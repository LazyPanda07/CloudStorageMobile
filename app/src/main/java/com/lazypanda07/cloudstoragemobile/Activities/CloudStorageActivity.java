package com.lazypanda07.cloudstoragemobile.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.lazypanda07.cloudstoragemobile.CustomListView.FileData;
import com.lazypanda07.cloudstoragemobile.CustomListView.PortraitCloudStorageListViewAdapter;
import com.lazypanda07.cloudstoragemobile.NetworkFunctions;
import com.lazypanda07.cloudstoragemobile.R;

import java.util.ArrayList;

public class CloudStorageActivity extends AppCompatActivity
{
	private AppCompatActivity ref = this;
	private String currentPath;
	private ArrayList<FileData> data;
	private PortraitCloudStorageListViewAdapter adapter;
	private String login;
	private String password;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cloud_storage);
		currentPath = "Home";
		data = new ArrayList<>();
		adapter = new PortraitCloudStorageListViewAdapter(getApplicationContext(), data);

		ListView filesList = findViewById(R.id.files_list);
		Intent intent = getIntent();

		login = intent.getStringExtra("login");
		password = intent.getStringExtra("password");

		filesList.setAdapter(adapter);

		NetworkFunctions.getFiles(ref, data, adapter, login, password);
	}
}