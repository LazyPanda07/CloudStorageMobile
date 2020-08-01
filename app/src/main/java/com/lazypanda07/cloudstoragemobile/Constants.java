package com.lazypanda07.cloudstoragemobile;

public class Constants
{
	public static final String APIServerIp = "31.207.166.231";
	public static final int APIServerPort = 8500;

	public static final short HTTPPacketSize = 4096;
	public static final int filePacketSize = 10 * 1024 * 1024;    //10 MB

	public static final String dataDelimiter = "/";
	public static final String dataPartDelimiter = "|";

	public static final int maxFilesFromExplorer = 10;

	public static class RequestType
	{
		public static final String accountType = "Account request";
		public static final String filesType = "Files request";
		public static final String exitType = "Exit request";
		public static final String cancelType = "Cancel request";
		public static final String controlType = "Control request";
	}

	public static class NetworkRequests
	{
		public static final String exit = "EOSS";
		public static final String cancelOperation = "Cancel operation";
	}

	public static class ControlRequests
	{
		public static final String nextFolder = "Next folder";
		public static final String prevFolder = "Previous folder";
		public static final String setPath = "Set path";
	}

	public static class AccountRequests
	{
		public static final String authorization = "Authorization";
		public static final String registration = "Registration";
		public static final String setLogin = "Set login";
	}

	public static class FilesRequests
	{
		public static final String uploadFile = "Upload file";
		public static final String downloadFile = "Download file";
		public static final String showAllFilesInFolder = "Show all files in directory";
		public static final String removeFile = "Remove file";
		public static final String createFolder = "Create folder";
	}

	public static class Responses
	{
		public static final String okResponse = "OK";
		public static final String failResponse = "FAIL";
		public static final String unknownRequest = "Unknown request";
	}
}
