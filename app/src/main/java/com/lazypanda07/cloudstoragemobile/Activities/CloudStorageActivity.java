package com.lazypanda07.cloudstoragemobile.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
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
	private DrawerLayout drawerLayout;
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
		drawerLayout = findViewById(R.id.drawer_layout);
		NavigationView navigationView = findViewById(R.id.navigation_view);
		Toolbar toolbar = findViewById(R.id.toolbar);
		final ActionBar actionBar;
		toolbar.setTitleTextColor(getResources().getColor(R.color.colorWhite));

		final ListView filesList = findViewById(R.id.files_list);
		Intent intent = getIntent();

		login = intent.getStringExtra("login");
		password = intent.getStringExtra("password");

		filesList.setAdapter(adapter);

		setSupportActionBar(toolbar);

		actionBar = getSupportActionBar();

		actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);

		actionBar.setDisplayHomeAsUpEnabled(true);

		NetworkFunctions.getFiles(ref, fileData, adapter, login, password);

		filesList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
		{
			@Override
			public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l)
			{
				showPopupMenuOnItemLongClick(view);

				return true;
			}
		});

		findViewById(R.id.cloud_storage).setOnLongClickListener(new View.OnLongClickListener()
		{
			@Override
			public boolean onLongClick(View view)
			{
				showPopupMenuOnLayoutLongClick(filesList);

				return true;
			}
		});

		filesList.setOnItemClickListener(new AdapterView.OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
			{
				//TODO: open file or directory
			}
		});

		navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener()
		{
			@Override
			public boolean onNavigationItemSelected(@NonNull MenuItem item)
			{
				return menuItemsEventsHandler(item);
			}
		});

		drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener()
		{
			@Override
			public void onDrawerSlide(@NonNull View drawerView, float slideOffset)
			{

			}

			@Override
			public void onDrawerOpened(@NonNull View drawerView)
			{
				actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_open);
			}

			@Override
			public void onDrawerClosed(@NonNull View drawerView)
			{
				actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
			}

			@Override
			public void onDrawerStateChanged(int newState)
			{

			}
		});
	}

	private void showPopupMenuOnLayoutLongClick(final View view)
	{
		PopupMenu popupMenu = new PopupMenu(getApplicationContext(), view);
		popupMenu.inflate(R.menu.on_layout_long_click_popup_menu);

		popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
		{
			@Override
			public boolean onMenuItemClick(MenuItem menuItem)
			{
				return menuItemsEventsHandler(menuItem);
			}
		});

		popupMenu.show();
	}

	private void showPopupMenuOnItemLongClick(final View view)
	{
		PopupMenu popupMenu = new PopupMenu(getApplicationContext(), view);
		popupMenu.inflate(R.menu.on_item_long_click_popup_menu);

		popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
		{
			@Override
			public boolean onMenuItemClick(MenuItem menuItem)
			{
				String fileName = ((TextView) view.findViewById(R.id.name)).getText().toString();

				switch (menuItem.getItemId())
				{
					case R.id.download_file:
						String sFileSize = ((TextView) view.findViewById(R.id.size)).getText().toString();
						long fileSize = Long.parseLong(sFileSize.substring(0, sFileSize.indexOf(' ')));

						NetworkFunctions.downloadFile(ref, fileName, fileSize, login, password);

						break;

					case R.id.remove_file:
						NetworkFunctions.removeFile(ref, fileName, login, password, fileData, adapter);

						break;

					case R.id.upload_file:
						Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
						intent.setType("*/*");
						intent.addCategory(Intent.CATEGORY_OPENABLE);

						startActivityForResult(Intent.createChooser(intent, getApplicationContext().getResources().getString(R.string.choose_file)), Constants.GET_FILE);

						break;
				}

				return false;
			}
		});

		popupMenu.show();
	}

	private boolean menuItemsEventsHandler(MenuItem item)
	{
		switch (item.getItemId())
		{
			case R.id.upload_file:
				Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
				intent.setType("*/*");
				intent.addCategory(Intent.CATEGORY_OPENABLE);

				startActivityForResult(Intent.createChooser(intent, getApplicationContext().getResources().getString(R.string.choose_file)), Constants.GET_FILE);

				return true;

			case R.id.create_folder:
				chooseFolderName();

				return true;

			case R.id.downloaded_files:
				startActivity(new Intent(getApplicationContext(), DownloadedFilesActivity.class));

				return true;
		}

		return false;
	}

	private void chooseFolderName()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		final EditText folderNameEdit = new EditText(builder.getContext());
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

		builder.setPositiveButton(R.string.save_folder_name, new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialogInterface, int i)
			{
				String folderName = folderNameEdit.getText().toString();

				if (folderName.isEmpty())
				{
					return;
				}

				NetworkFunctions.createFolder(ref, folderName, login, password, fileData, adapter);
			}
		});

		builder.setNegativeButton(R.string.cancel_folder_name, new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialogInterface, int i)
			{

			}
		});

		builder.setTitle(R.string.choose_folder_name_title);
		builder.setMessage(R.string.choose_folder_name);
		builder.setCancelable(true);

		folderNameEdit.setLayoutParams(params);
		folderNameEdit.setSingleLine(true);

		builder.setView(folderNameEdit);

		builder.show();
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item)
	{
		if (item.getItemId() == android.R.id.home)
		{
			if (drawerLayout.isDrawerOpen(GravityCompat.START))
			{
				drawerLayout.closeDrawer(GravityCompat.START);
			}
			else
			{
				drawerLayout.openDrawer(GravityCompat.START);
			}
		}

		return super.onOptionsItemSelected(item);
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