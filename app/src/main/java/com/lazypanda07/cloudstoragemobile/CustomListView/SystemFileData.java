package com.lazypanda07.cloudstoragemobile.CustomListView;

import android.content.Context;

import com.lazypanda07.cloudstoragemobile.NetworkFunctions;
import com.lazypanda07.cloudstoragemobile.R;

import java.io.File;

//TODO: create database with metadata for each file that been downloaded
public class SystemFileData
{
	private Context context;

	public File file;
	public NetworkFunctions.StorageType storage;

	public SystemFileData(Context context, File file, NetworkFunctions.StorageType storage)
	{
		this.file = file;
		this.storage = storage;
		this.context = context;
	}

	public String getFileName()
	{
		return file.getName();
	}

	public String getFilePath()
	{
		return file.getPath();
	}

	public String getStorageType()
	{
		if (storage.equals(NetworkFunctions.StorageType.INTERNAL))
		{
			return context.getResources().getString(R.string.internal);
		}
		else if (storage.equals(NetworkFunctions.StorageType.SDCard))
		{
			return context.getResources().getString(R.string.sd_card);
		}

		return "";
	}

	public String getFileExtension()
	{
		return this.getFileName().substring(this.getFileName().lastIndexOf('.') + 1);
	}

	public long getFileSize()
	{
		return file.length();
	}
}
