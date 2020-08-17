package com.lazypanda07.cloudstoragemobile.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
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
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.lazypanda07.cloudstoragemobile.CustomListView.FileData;
import com.lazypanda07.cloudstoragemobile.CustomListView.LandscapeCloudStorageListViewAdapter;
import com.lazypanda07.cloudstoragemobile.CustomListView.PortraitCloudStorageListViewAdapter;
import com.lazypanda07.cloudstoragemobile.DataBases.UserSettingsSingleton;
import com.lazypanda07.cloudstoragemobile.GetDataFromUri.FileDataFromUri;
import com.lazypanda07.cloudstoragemobile.IntentEvents;
import com.lazypanda07.cloudstoragemobile.NetworkFunctions;
import com.lazypanda07.cloudstoragemobile.R;
import com.lazypanda07.networklib.Constants;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;

//TODO: add multiple upload files
//TODO: add custom file path in Download folder must be like Download/$USER_NAME/$COPY_CURRENT_PATH/$FILE_NAME
public class CloudStorageActivity extends AppCompatActivity
{
	private AppCompatActivity ref = this;
	private DrawerLayout drawerLayout;
	private String[] currentPath;
	private ArrayList<FileData> fileData;
	private BaseAdapter adapter;
	private String login;
	private String password;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cloud_storage);

		currentPath = new String[1];
		currentPath[0] = "Home";
		fileData = new ArrayList<>();
		drawerLayout = findViewById(R.id.drawer_layout);
		NavigationView navigationView = findViewById(R.id.navigation_view);
		Toolbar toolbar = findViewById(R.id.toolbar);
		final ActionBar actionBar;
		toolbar.setTitleTextColor(ResourcesCompat.getColor(getResources(), R.color.colorWhite, null));
		final ListView filesList = findViewById(R.id.files_list);
		final Intent intent = getIntent();
		UserSettingsSingleton instance = UserSettingsSingleton.getInstance();

		login = intent.getStringExtra("login");
		password = intent.getStringExtra("password");

		if (!instance.addNewUser(login, password))
		{
			instance.updateLastAuthorizationDate(login);
		}

		if (instance.getStorageType(login).equals(NetworkFunctions.StorageType.INTERNAL))
		{
			NetworkFunctions.storageType = NetworkFunctions.StorageType.INTERNAL;
		}
		else
		{
			NetworkFunctions.storageType = NetworkFunctions.StorageType.SDCard;
		}

		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
		{
			adapter = new PortraitCloudStorageListViewAdapter(getBaseContext(), fileData);
		}
		else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
		{
			adapter = new LandscapeCloudStorageListViewAdapter(getBaseContext(), fileData);
		}

		filesList.setAdapter(adapter);

		setSupportActionBar(toolbar);

		actionBar = getSupportActionBar();

		actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);

		actionBar.setDisplayHomeAsUpEnabled(true);

		NetworkFunctions.getFiles(ref, fileData, adapter, login, password, currentPath, findViewById(R.id.cloud_storage_wrapper));

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
				if (((TextView) view.findViewById(R.id.size)).getText().toString().isEmpty())    //folder
				{
					NetworkFunctions.nextFolder(ref, ((TextView) view.findViewById(R.id.name)).getText().toString(), currentPath, login, password, fileData, adapter, findViewById(R.id.cloud_storage_wrapper));
				}
				else    //file
				{
					//TODO: open file
				}
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

		toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener()
		{
			@Override
			public boolean onMenuItemClick(MenuItem item)
			{
				return menuItemsEventsHandler(item);
			}
		});

		toolbar.findViewById(R.id.refresh).setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				refresh();
			}
		});
	}

	private void showPopupMenuOnLayoutLongClick(final View view)
	{
		PopupMenu popupMenu = new PopupMenu(getBaseContext(), view);
		popupMenu.inflate(R.menu.cloud_storage_on_layout_long_click_popup_menu);

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
		PopupMenu popupMenu = new PopupMenu(getBaseContext(), view);
		popupMenu.inflate(R.menu.files_list_on_item_long_click_popup_menu);

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

						NetworkFunctions.downloadFile(ref, fileName, fileSize, login, password, currentPath, findViewById(R.id.cloud_storage_wrapper));

						break;

					case R.id.remove_file:
						NetworkFunctions.removeFile(ref, fileName, login, password, fileData, adapter, currentPath, findViewById(R.id.cloud_storage_wrapper));

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
		Intent intent;

		switch (item.getItemId())
		{
			case R.id.upload_file:
				intent = new Intent(Intent.ACTION_GET_CONTENT);
				intent.setType("*/*");
				intent.addCategory(Intent.CATEGORY_OPENABLE);

				startActivityForResult(Intent.createChooser(intent, getApplicationContext().getResources().getString(R.string.choose_file)), Constants.GET_FILE);

				drawerLayout.closeDrawer(GravityCompat.START);

				return true;

			case R.id.create_folder:
				chooseFolderName();

				return true;

			case R.id.downloaded_files:
				intent = new Intent(getBaseContext(), DownloadedFilesActivity.class);

				intent.putExtra("login", login);

				startActivity(intent);

				drawerLayout.closeDrawer(GravityCompat.START);

				return true;

			case R.id.settings:
				intent = new Intent(getBaseContext(), SettingsActivity.class);

				intent.putExtra("login", login);

				startActivity(intent);

				return true;

			case R.id.exit_from_account:
				Intent toAuthorizationActivity = new Intent(getBaseContext(), AuthorizationActivity.class);

				login = "";
				password = "";

				toAuthorizationActivity.putExtra(IntentEvents.string(), IntentEvents.EXIT_FROM_ACCOUNT);

				toAuthorizationActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

				startActivity(toAuthorizationActivity);

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

				NetworkFunctions.createFolder(ref, folderName, login, password, fileData, adapter, currentPath, findViewById(R.id.cloud_storage_wrapper));
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
		folderNameEdit.requestFocus();

		builder.show();
	}

	private void refresh()
	{
		NetworkFunctions.getFiles(ref, fileData, adapter, login, password, currentPath, findViewById(R.id.cloud_storage_wrapper));
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
				int fileSize = FileDataFromUri.getFileSize(ref, uri);
				String fileName = FileDataFromUri.getFileName(ref, uri);
				DataInputStream stream = new DataInputStream(getContentResolver().openInputStream(uri));

				NetworkFunctions.uploadFile(ref, stream, fileSize, fileName, login, password, fileData, adapter, currentPath, findViewById(R.id.cloud_storage_wrapper));
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		else if (resultCode == RESULT_CANCELED)
		{
			return;
		}
		else
		{
			Toast.makeText(getApplicationContext(), "Ошибка", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.settings_cloud_storage, menu);
		return true;
	}

	public void previousFolder(View view)
	{
		NetworkFunctions.prevFolder(ref, login, password, fileData, adapter, currentPath, findViewById(R.id.cloud_storage_wrapper));
	}
}