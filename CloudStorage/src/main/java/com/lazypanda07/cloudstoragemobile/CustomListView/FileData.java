package com.lazypanda07.cloudstoragemobile.CustomListView;

public class FileData
{
	public String fileName;
	public String filePath;
	public String fileExtension;
	public String uploadDate;
	public String dateOfChange;
	public long fileSize;

	public FileData(String fileName, String filePath, String fileExtension, String uploadDate, String dateOfChange, long fileSize)
	{
		this.fileName = fileName;
		this.filePath = filePath;
		this.fileExtension = fileExtension;
		this.uploadDate = uploadDate;
		this.dateOfChange = dateOfChange;
		this.fileSize = fileSize;
	}
}
