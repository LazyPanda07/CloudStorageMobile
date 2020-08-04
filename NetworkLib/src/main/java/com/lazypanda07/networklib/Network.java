package com.lazypanda07.networklib;

import com.lazypanda07.networklib.HTTP.HTTPParser;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.HashMap;

public class Network
{
	private Socket socket;

	public Network(String ip, int port) throws IOException
	{
		socket = new Socket();

		socket.connect(new InetSocketAddress(ip, port), Constants.CLIENT_TIMEOUT_RECEIVE);

		socket.setSoTimeout(Constants.CLIENT_TIMEOUT_RECEIVE);
	}

	public void sendBytes(byte[] bytes) throws IOException
	{
		socket.getOutputStream().write(bytes);
	}

	public byte[] receiveBytes() throws IOException
	{
		byte[] data = new byte[Constants.HTTP_PACKET_SIZE];
		InputStream in = socket.getInputStream();
		int size = 0;
		int lastPacket = 0;
		int totalReceive = 0;

		do
		{
			lastPacket = in.read(data, totalReceive, data.length - totalReceive);

			if (lastPacket < 0)
			{
				throw new IOException();
			}
			else if (lastPacket == 0)
			{
				return data;
			}

			totalReceive += lastPacket;

			if (totalReceive > 25 && size == 0)
			{
				HTTPParser parser = new HTTPParser(data);
				HashMap<String, String> headers = parser.getHeaders();

				size = Integer.parseInt(headers.get("Total-HTTP-Message-Size"));

				if (data.length < size)
				{
					byte[] newData = new byte[size];

					System.arraycopy(data, 0, newData, 0, data.length);

					data = newData;
				}
			}
			else if (size == 0)
			{
				continue;
			}
		}
		while (totalReceive < size);

		return data;
	}

	public void close() throws IOException
	{
		String exit = (new HTTP.HTTPBuilder()).setMethod("POST")
				.setHeaders(Constants.RequestType.EXIT_TYPE, Constants.NetworkRequests.EXIT).build();

		exit = HTTP.HTTPBuilder.insertSizeHeaderToHTTPMessage(exit);

		this.sendBytes(exit.getBytes());

		try
		{
			Thread.sleep(600);
		}
		catch (InterruptedException e)
		{
			socket.close();
			e.printStackTrace();
		}

		socket.close();
	}
}