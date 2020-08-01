package com.lazypanda07.networklib;

import com.lazypanda07.networklib.HTTP.HTTPParser;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.HashMap;

public class Network
{
    public static final int clientTimeoutReceive = 30000;	//30 seconds
    
    private Socket socket;

    public Network(String ip, int port) throws IOException
    {
        socket = new Socket(ip, port);

        socket.setSoTimeout(clientTimeoutReceive);
    }

    public void sendBytes(byte[] bytes) throws IOException
    {
        socket.getOutputStream().write(bytes);
    }

    public byte[] receiveBytes() throws IOException
    {
        byte[] data = new byte[4096];
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
            } else if (lastPacket == 0)
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
            } else if (size == 0)
            {
                continue;
            }
        } while (totalReceive < size);

        return data;
    }

    public void close() throws IOException
    {
        socket.close();
    }
}