package com.lazypanda07.cloudstoragemobile.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupMenu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

import com.lazypanda07.cloudstoragemobile.BuildConfig;
import com.lazypanda07.cloudstoragemobile.CustomListView.PortraitDownloadedFilesListViewAdapter;
import com.lazypanda07.cloudstoragemobile.CustomListView.SystemFileData;
import com.lazypanda07.cloudstoragemobile.R;

import java.io.File;
import java.util.ArrayList;

import static com.lazypanda07.cloudstoragemobile.NetworkFunctions.storageType;

public class DownloadedFilesActivity extends AppCompatActivity
{
	private final AppCompatActivity ref = this;
	private ArrayList<SystemFileData> data;
	private PortraitDownloadedFilesListViewAdapter adapter;
	private String login;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_downloaded_files);

		Toolbar toolbar = findViewById(R.id.toolbar);
		ListView downloadedFilesList = findViewById(R.id.downloaded_files_list);
		data = new ArrayList<>();
		adapter = new PortraitDownloadedFilesListViewAdapter(getApplicationContext(), data);
		login = getIntent().getStringExtra("login");
		File path = getApplicationContext().getExternalFilesDirs(Environment.DIRECTORY_DOWNLOADS)[storageType.ordinal()];
		path = new File(path, login);

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

		downloadedFilesList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
		{
			@Override
			public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l)
			{
				showPopupMenuOnItemLongClick(view, i);

				return true;
			}
		});
		//TODO: onItemLongClickListener upload file, remove file
	}

	private void showPopupMenuOnItemLongClick(final View view, final int i)
	{
		PopupMenu popupMenu = new PopupMenu(getApplicationContext(), view);
		popupMenu.inflate(R.menu.downloaded_files_list_on_item_long_click_popup_menu);

		popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
		{
			@Override
			public boolean onMenuItemClick(MenuItem menuItem)
			{
				switch (menuItem.getItemId())
				{
					case R.id.remove_file:
						File file = data.get(i).file;

						if (file.delete())
						{
							data.remove(i);

							adapter.notifyDataSetChanged();

							return true;
						}

						return false;

					case R.id.upload_file:
						//TODO: upload file through special activity

						break;
				}

				return false;
			}
		});

		popupMenu.show();
	}
}