package com.lazypanda07.cloudstoragemobile.DataBases;

public class Connection
{
	public String ip;
	public int port;
	public int lastUsed;

	public Connection(String ip, int port, int lastUsed)
	{
		this.ip = ip;
		this.port = port;
		this.lastUsed = lastUsed;
	}
}
