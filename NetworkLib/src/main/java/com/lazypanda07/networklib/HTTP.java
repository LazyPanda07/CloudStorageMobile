package com.lazypanda07.networklib;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;

public class HTTP
{

	public static final short responseCodeSize = 3;

	public static class HTTPParser
	{
		private String method;
		private String parameters;
		private String HTTPVersion;
		private int responseCode;
		private String responseMessage;
		private HashMap<String, String> headers;
		private ArrayList<Byte> body;

		public HTTPParser(byte[] data)
		{
			headers = new HashMap<>();
			StringBuilder builder = new StringBuilder();
			String HTTPMessage = new String(data);
			method = "";
			body = new ArrayList<>();
			String HTTPHeaders = "";
			boolean isContainsBody = false;

			int firstStringEnd = HTTPMessage.indexOf("\r\n") + 2;
			String firstString = HTTPMessage.substring(0, firstStringEnd);

			switch (firstString.charAt(0))
			{
				case 'H':
					if (firstString.charAt(1) == 'E')
					{
						method = "HEAD";
					}

					break;

				case 'P':
					if (firstString.charAt(1) == 'U')
					{
						method = "PUT";
					}
					else if (firstString.charAt(1) == 'O')
					{
						method = "POST";
					}
					else
					{
						method = "PATCH";
					}

					break;

				case 'O':
					method = "OPTIONS";

					break;

				case 'D':
					method = "DELETE";

					break;

				case 'C':
					method = "CONNECT";

					break;

				case 'G':
					method = "GET";

					break;
			}

			for (int i = firstString.indexOf("HTTP"); i < firstString.length(); i++)
			{
				if (firstString.charAt(i) == ' ')
				{
					break;
				}

				builder.append(firstString.charAt(i));
			}

			HTTPVersion = builder.toString();
			builder = new StringBuilder();

			if (method.isEmpty())
			{
				for (int i = 0; i < firstString.length() - responseCodeSize; i++)
				{
					char[] tem = new char[responseCodeSize];

					System.arraycopy(firstString.toCharArray(), i, tem, 0, responseCodeSize);

					try
					{
						responseCode = Integer.parseInt(String.copyValueOf(tem));
					}
					catch (NumberFormatException e)
					{
						continue;
					}

					if (responseCode >= 100)
					{
						for (int j = i + responseCodeSize + 1; j < firstString.length() - 2; j++)
						{
							builder.append(firstString.charAt(j));
						}

						responseMessage = builder.toString();
						break;
					}
				}
			}

			if (!method.isEmpty())
			{
				parameters = firstString.substring(firstString.indexOf("/"), firstString.indexOf(" ", firstString.indexOf("/")));
			}

			isContainsBody = HTTPMessage.contains("Content-Length");

			if (isContainsBody)
			{
				int bodyStart = HTTPMessage.indexOf("\r\n\r\n") + 4;

				for (int i = bodyStart; i < data.length; i++)
				{
					body.add(data[i]);
				}

				HTTPHeaders = HTTPMessage.substring(firstStringEnd, HTTPMessage.indexOf("\r\n\r\n") + 4);
			}
			else
			{
				HTTPHeaders = HTTPMessage.substring(firstStringEnd, HTTPMessage.lastIndexOf("\r\n") + 2);
			}

			String[] array = HTTPHeaders.split("\r\n");

			for (String i : array)
			{
				String[] tem = i.split(": ");

				headers.put(tem[0], tem[1]);
			}

			if (isContainsBody)
			{
				int contentLength = Integer.parseInt(headers.get("Content-Length"));

				if (contentLength < body.size())
				{
					body.subList(contentLength, body.size()).clear();
				}
			}
		}

		public String getMethod()
		{
			return method;
		}

		public String getParameters()
		{
			return parameters;
		}

		public String getHTTPVersion()
		{
			return HTTPVersion;
		}

		public int getResponseCode()
		{
			return responseCode;
		}

		public String getResponseMessage()
		{
			return responseMessage;
		}

		public HashMap<String, String> getHeaders()
		{
			return headers;
		}

		public byte[] getBody()
		{
			byte[] result = new byte[body.size()];

			for (int i = 0; i < body.size(); i++)
			{
				result[i] = body.get(i);
			}

			return result;
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public static class HTTPBuilder
	{
		private static String calculateHTTPMessageSize(String HTTPMessage)
		{
			int iSize = HTTPMessage.length() + customHTTPHeaderSize.length() + 2;
			String sSize = String.valueOf(iSize);
			iSize += sSize.length();
			sSize = String.valueOf(iSize);

			return sSize;
		}

		public static final String HTTPVersion = "HTTP/1.1";
		public static final String customHTTPHeaderSize = "Total-HTTP-Message-Size: ";

		private String method;
		private String parameters;
		private String responseCode;
		private String headers;

		public HTTPBuilder()
		{
			method = "";
			parameters = "";
			responseCode = "";
			headers = "";
		}

		public static String insertSizeHeaderToHTTPMessage(String HTTPMessage)
		{
			String totalHTTPMessageSize = customHTTPHeaderSize + calculateHTTPMessageSize(HTTPMessage) + "\r\n";
			StringBuilder builder = new StringBuilder(HTTPMessage);

			builder.insert(HTTPMessage.indexOf("\r\n") + 2, totalHTTPMessageSize);


			return builder.toString();
		}

		public HTTPBuilder setMethod(String method)
		{
			this.method = method;

			return this;
		}

		public HTTPBuilder setParameters(String parameters)
		{
			try
			{
				this.parameters = new String(parameters.getBytes(), "CP1251");
			}
			catch (UnsupportedEncodingException e)
			{
				e.printStackTrace();
			}

			return this;
		}

		public HTTPBuilder setResponseCode(String responseCode)
		{
			this.responseCode = responseCode;

			return this;
		}

		public HTTPBuilder setHeaders(String name, String value)
		{
			try
			{
				headers += name + ": " + new String(value.getBytes("CP1251")) + "\r\n";
			}
			catch (UnsupportedEncodingException e)
			{
				e.printStackTrace();
			}

			return this;
		}

		public String build()
		{
			String result;

			if (method.isEmpty())    //response
			{
				result = HTTPVersion + " " + responseCode + "\r\n" + headers;
			}
			else    //request
			{
				if (parameters.isEmpty())
				{
					parameters = "/";
				}

				result = method + " " + parameters + " " + HTTPVersion + "\r\n" + headers;
			}

			return result;
		}

		public String build(byte[] body)
		{
			String result;

			if (method.isEmpty())    //response
			{
				result = HTTPVersion + " " + responseCode + "\r\n" + headers;
			}
			else    //request
			{
				if (parameters.isEmpty())
				{
					parameters = "/";
				}

				result = method + " " + parameters + " " + HTTPVersion + "\r\n" + headers;
			}

			result += "\r\n" + new String(body, StandardCharsets.ISO_8859_1);

			return result;
		}
	}
}
