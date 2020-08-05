package com.lazypanda07.networklib;

public class Constants
{
	public static final int GET_FILE = 1;

	public static final String APIServerIp = "31.207.166.231";
	public static final int APIServerPort = 8500;

	public static final short HTTP_PACKET_SIZE = 4096;
	public static final int FILE_PACKET_SIZE = 10 * 1024 * 1024;    //10 MB

	public static final int CLIENT_TIMEOUT_RECEIVE = 30000;    //30 seconds

	public static final String DATA_DELIMITER = "/";
	public static final String DATA_PART_DELIMITER = "|";

	public static final int MAX_FILES_FROM_EXPLORER = 10;

	public static class RequestType
	{
		public static final String ACCOUNT_TYPE = "Account request";
		public static final String FILES_TYPE = "Files request";
		public static final String EXIT_TYPE = "Exit request";
		public static final String CANCEL_TYPE = "Cancel request";
		public static final String CONTROL_TYPE = "Control request";
	}

	public static class NetworkRequests
	{
		public static final String EXIT = "EOSS";
		public static final String CANCEL_OPERATION = "Cancel operation";
	}

	public static class ControlRequests
	{
		public static final String NEXT_FOLDER = "Next folder";
		public static final String PREVIOUS_FOLDER = "Previous folder";
		public static final String SET_PATH = "Set path";
	}

	public static class AccountRequests
	{
		public static final String AUTHORIZATION = "Authorization";
		public static final String REGISTRATION = "Registration";
		public static final String SET_LOGIN = "Set login";
	}

	public static class FilesRequests
	{
		public static final String UPLOAD_FILE = "Upload file";
		public static final String DOWNLOAD_FILE = "Download file";
		public static final String SHOW_ALL_FILES_IN_DIRECTORY = "Show all files in directory";
		public static final String REMOVE_FILE = "Remove file";
		public static final String CREATE_FOLDER = "Create folder";
	}

	public static class Responses
	{
		public static final String OK_RESPONSE = "OK";
		public static final String FAIL_RESPONSE = "FAIL";
		public static final String UNKNOWN_REQUEST = "Unknown request";
	}
}
