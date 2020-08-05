package com.lazypanda07.cloudstoragemobile.Activities;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.lazypanda07.cloudstoragemobile.CustomListView.FileData;
import com.lazypanda07.cloudstoragemobile.CustomListView.PortraitCloudStorageListViewAdapter;
import com.lazypanda07.cloudstoragemobile.NetworkFunctions;
import com.lazypanda07.cloudstoragemobile.R;
import com.lazypanda07.networklib.Constants;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;

public class CloudStorageActivity extends AppCompatActivity
{
	private AppCompatActivity ref = this;
	private String currentPath;
	private ArrayList<FileData> fileData;
	private PortraitCloudStorageListViewAdapter adapter;
	private String login;
	private String password;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cloud_storage);
		currentPath = "Home";
		fileData = new ArrayList<>();
		adapter = new PortraitCloudStorageListViewAdapter(getApplicationContext(), fileData);

		ListView filesList = findViewById(R.id.files_list);
		Intent intent = getIntent();

		login = intent.getStringExtra("login");
		password = intent.getStringExtra("password");

		filesList.setAdapter(adapter);

		NetworkFunctions.getFiles(ref, fileData, adapter, login, password);

		//TODO: refactor
		filesList.setOnItemClickListener(new AdapterView.OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
			{
				String fileName = ((TextView) view.findViewById(R.id.name)).getText().toString();
				String sFileSize = ((TextView) view.findViewById(R.id.size)).getText().toString();
				long fileSize = Long.parseLong(sFileSize.substring(0, sFileSize.indexOf(' ')));

				NetworkFunctions.downloadFile(ref, fileName, fileSize, login, password);
			}
		});

		//TODO: refactor
		filesList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
		{
			@Override
			public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l)
			{
				Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
				intent.setType("*/*");
				intent.addCategory(Intent.CATEGORY_OPENABLE);

				startActivityForResult(Intent.createChooser(intent, getApplicationContext().getResources().getString(R.string.choose_file)), Constants.GET_FILE);

				return true;
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK)
		{
			try
			{
				Uri uri = data.getData();
				int fileSize = getFileSize(uri);
				DataInputStream stream = new DataInputStream(getContentResolver().openInputStream(uri));

				NetworkFunctions.uploadFile(ref, stream, fileSize, getFileName(uri), login, password, fileData, adapter);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			Toast.makeText(getApplicationContext(), "Ошибка", Toast.LENGTH_SHORT).show();
		}
	}

	private int getFileSize(Uri fileUri)
	{
		Cursor cursor = getContentResolver().query(fileUri, null, null, null, null);

		cursor.moveToFirst();

		int result = cursor.getInt(cursor.getColumnIndex(OpenableColumns.SIZE));

		cursor.close();

		return result;
	}

	private String getFileName(Uri uri)
	{
		String result = null;
		if (uri.getScheme().equals("content"))
		{
			Cursor cursor = getContentResolver().query(uri, null, null, null, null);

			cursor.moveToFirst();

			result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));

			cursor.close();
		}
		if (result == null)
		{
			result = uri.getPath();
			int cut = result.lastIndexOf('/');
			if (cut != -1)
			{
				result = result.substring(cut + 1);
			}
		}

		return result;
	}
}