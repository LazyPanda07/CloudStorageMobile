package com.lazypanda07.networklib;

public class Constants
{
	public static final int GET_FILE = 1;

	public static String APIServerIp;
	public static int APIServerPort;

	public static final short HTTP_PACKET_SIZE = 4096;
	public static final int FILE_PACKET_SIZE = 10 * 1024 * 1024;    //10 MB

	public static final int CLIENT_TIMEOUT_RECEIVE = 30000;    //30 seconds

	public static final String DATA_DELIMITER = "/";
	public static final String DATA_PART_DELIMITER = "|";
	public static final char WINDOWS_SEPARATOR = '\\';

	public static class RequestType
	{
		public static final String ACCOUNT_TYPE = "Account-Request";
		public static final String FILES_TYPE = "Files-Request";
		public static final String EXIT_TYPE = "Exit-Request";
		public static final String CANCEL_TYPE = "Cancel-Request";
		public static final String CONTROL_TYPE = "Control-Request";
	}

	public static class NetworkRequests
	{
		public static final String EXIT = "EOSS";
		public static final String CANCEL_OPERATION = "Cancel-Operation";
	}

	public static class ControlRequests
	{
		public static final String NEXT_FOLDER = "Next-Folder";
		public static final String PREVIOUS_FOLDER = "Previous-Folder";
		public static final String SET_PATH = "Set-Path";
	}

	public static class AccountRequests
	{
		public static final String AUTHORIZATION = "Authorization";
		public static final String REGISTRATION = "Registration";
		public static final String SET_LOGIN = "Set-Login";
	}

	public static class FilesRequests
	{
		public static final String UPLOAD_FILE = "Upload-File";
		public static final String DOWNLOAD_FILE = "Download-File";
		public static final String SHOW_ALL_FILES_IN_DIRECTORY = "Show-All-Files-In-Folder";
		public static final String REMOVE_FILE = "Remove-File";
		public static final String CREATE_FOLDER = "Create-Folder";
	}

	public static class Responses
	{
		public static final String OK_RESPONSE = "OK";
		public static final String FAIL_RESPONSE = "FAIL";
		public static final String UNKNOWN_REQUEST = "Unknown request";
	}
}
