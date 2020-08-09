package com.lazypanda07.cloudstoragemobile.CustomListView;

import java.io.File;

//TODO: create database with metadata for each file that been downloaded
public class SystemFileData
{
	public File file;

	public SystemFileData(File file)
	{
		this.file = file;
	}

	public String getFileName()
	{
		return file.getName();
	}

	public String getFilePath()
	{
		return file.getPath();
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
