package com.lazypanda07.cloudstoragemobile.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

import com.lazypanda07.cloudstoragemobile.BuildConfig;
import com.lazypanda07.cloudstoragemobile.CustomListView.PortraitDownloadedFilesListViewAdapter;
import com.lazypanda07.cloudstoragemobile.CustomListView.SystemFileData;
import com.lazypanda07.cloudstoragemobile.R;

import java.io.File;
import java.util.ArrayList;

public class DownloadedFilesActivity extends AppCompatActivity
{
	private final AppCompatActivity ref = this;
	private ArrayList<SystemFileData> data;
	private PortraitDownloadedFilesListViewAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_downloaded_files);

		Toolbar toolbar = findViewById(R.id.toolbar);
		ListView downloadedFilesList = findViewById(R.id.downloaded_files_list);
		data = new ArrayList<>();
		adapter = new PortraitDownloadedFilesListViewAdapter(getApplicationContext(), data);
		File path = getApplicationContext().getExternalFilesDir("Download");

		downloadedFilesList.setAdapter(adapter);

		File[] files = path.listFiles();

		if (files != null)
		{
			for (File i : files)
			{
				data.add(new SystemFileData(i));
			}

			adapter.notifyDataSetChanged();
		}

		toolbar.setNavigationOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				finish();
			}
		});

		downloadedFilesList.setOnItemClickListener(new AdapterView.OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
			{
				Intent intent = new Intent();
				MimeTypeMap instance = MimeTypeMap.getSingleton();
				SystemFileData tem = data.get(i);
				String mimeType = instance.getMimeTypeFromExtension(tem.getFileExtension());

				intent.setAction(Intent.ACTION_VIEW);
				intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

				intent.setDataAndType(FileProvider.getUriForFile(ref.getBaseContext(), BuildConfig.APPLICATION_ID, tem.file), mimeType);

				startActivity(intent);
			}
		});
	}
}