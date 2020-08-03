package com.lazypanda07.cloudstoragemobile;

import android.content.Intent;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.lazypanda07.cloudstoragemobile.Activities.CloudStorageActivity;
import com.lazypanda07.cloudstoragemobile.CustomListView.FileData;
import com.lazypanda07.cloudstoragemobile.CustomListView.PortraitCloudStorageListViewAdapter;
import com.lazypanda07.networklib.Constants;
import com.lazypanda07.networklib.HTTP;
import com.lazypanda07.networklib.Network;

import java.io.IOException;
import java.util.ArrayList;

public class NetworkFunctions
{
	private static boolean authorization(Network network, String login, String password)
	{
		String body = "login=" + login + "&password=" + password;

		String request = (new HTTP.HTTPBuilder()).setMethod("POST").
				setHeaders(Constants.RequestType.accountType, Constants.AccountRequests.authorization).
				setHeaders("Content-Length", String.valueOf(body.length())).
				build(body.getBytes());

		request = HTTP.HTTPBuilder.insertSizeHeaderToHTTPMessage(request);

		try
		{
			network.sendBytes(request.getBytes());

			String response = new String(network.receiveBytes());

			final HTTP.HTTPParser parser = new HTTP.HTTPParser(response.getBytes());

			return parser.getHeaders().get("Error").equals("0");
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return false;
		}
	}

	public static void authorization(final AppCompatActivity activity)
	{
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					Network network = new Network(Constants.APIServerIp, Constants.APIServerPort);

					String login = ((EditText) activity.findViewById(R.id.authorization_login)).getText().toString();
					String password = ((EditText) activity.findViewById(R.id.authorization_password)).getText().toString();

					if (login.isEmpty())
					{
						ErrorHandling.showError(activity, R.string.empty_login);
						network.close();
						return;
					}
					else if (password.isEmpty())
					{
						ErrorHandling.showError(activity, R.string.empty_password);
						network.close();
						return;
					}

					String body = "login=" + login + "&password=" + password;

					String request = (new HTTP.HTTPBuilder()).setMethod("POST").
							setHeaders(Constants.RequestType.accountType, Constants.AccountRequests.authorization).
							setHeaders("Content-Length", String.valueOf(body.length())).
							build(body.getBytes());

					request = HTTP.HTTPBuilder.insertSizeHeaderToHTTPMessage(request);

					network.sendBytes(request.getBytes());

					String response = new String(network.receiveBytes());

					final HTTP.HTTPParser parser = new HTTP.HTTPParser(response.getBytes());

					if (parser.getHeaders().get("Error").equals("0"))
					{
						network.close();

						Intent intent = new Intent(activity.getApplicationContext(), CloudStorageActivity.class);

						intent.putExtra("login", login);
						intent.putExtra("password", password);

						activity.startActivity(intent);
					}
					else
					{
						network.close();

						ErrorHandling.showError(activity, parser.getBody());
					}
				}
				catch (IOException e)
				{
					ErrorHandling.showError(activity, R.string.connection_error);
					e.printStackTrace();
				}
			}
		}).start();
	}

	public static void registration(final AppCompatActivity activity)
	{
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					Network network = new Network(Constants.APIServerIp, Constants.APIServerPort);

					String login = ((EditText) activity.findViewById(R.id.registration_login)).getText().toString();
					String password = ((EditText) activity.findViewById(R.id.registration_password)).getText().toString();
					String repeatPassword = ((EditText) activity.findViewById(R.id.registration_repeat_password)).getText().toString();

					if (login.isEmpty())
					{
						ErrorHandling.showError(activity, R.string.empty_login);
						network.close();
						return;
					}
					else if (password.isEmpty())
					{
						ErrorHandling.showError(activity, R.string.empty_password);
						network.close();
						return;
					}
					else if (!password.equals(repeatPassword))
					{
						ErrorHandling.showError(activity, R.string.password_mismatch);
						network.close();
						return;
					}

					String body = "login=" + login + "&password=" + password;

					String request = (new HTTP.HTTPBuilder()).setMethod("POST").
							setHeaders(Constants.RequestType.accountType, Constants.AccountRequests.registration).
							setHeaders("Content-Length", String.valueOf(body.length())).
							build(body.getBytes());

					request = HTTP.HTTPBuilder.insertSizeHeaderToHTTPMessage(request);

					network.sendBytes(request.getBytes());

					String response = new String(network.receiveBytes());

					final HTTP.HTTPParser parser = new HTTP.HTTPParser(response.getBytes());

					if (parser.getHeaders().get("Error").equals("0"))
					{
						network.close();

						Intent intent = new Intent(activity.getApplicationContext(), CloudStorageActivity.class);

						intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

						intent.putExtra("login", login);
						intent.putExtra("password", password);

						activity.startActivity(intent);

						activity.finish();
					}
					else
					{
						network.close();

						ErrorHandling.showError(activity, parser.getBody());
					}
				}
				catch (IOException e)
				{
					ErrorHandling.showError(activity, R.string.connection_error);
					e.printStackTrace();
				}
			}
		}).start();
	}

	public static void getFiles(final AppCompatActivity activity, final ArrayList<FileData> data, final PortraitCloudStorageListViewAdapter adapter, final String login, final String password)
	{
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					Network network = new Network(Constants.APIServerIp, Constants.APIServerPort);

					if (authorization(network, login, password))
					{
						String request = (new HTTP.HTTPBuilder()).setMethod("POST").
								setHeaders(Constants.RequestType.filesType, Constants.FilesRequests.showAllFilesInFolder).
								build();

						request = HTTP.HTTPBuilder.insertSizeHeaderToHTTPMessage(request);

						network.sendBytes(request.getBytes());

						String response = new String(network.receiveBytes());
						HTTP.HTTPParser parser = new HTTP.HTTPParser(response.getBytes());

						if (parser.getHeaders().get("Error").equals("0"))
						{
							String body = parser.getBody();
							StringBuilder[] temData = new StringBuilder[6];    //each index equals member position in FileData class
							int curIndex = 0;

							for (int i = 0; i < temData.length; i++)
							{
								temData[i] = new StringBuilder();
							}

							for (int i = 0; i < body.length(); i++)
							{
								if (body.charAt(i) == Constants.dataDelimiter.charAt(0))
								{
									FileData tem = new FileData
											(
													temData[0].toString(),
													temData[1].toString(),
													temData[2].toString(),
													temData[3].toString(),
													temData[4].toString(),
													Integer.parseInt(temData[5].toString())
											);

									data.add(tem);

									curIndex = 0;

									for (StringBuilder j: temData)
									{
										j.setLength(0);
									}
								}
								else if (body.charAt(i) == Constants.dataPartDelimiter.charAt(0))
								{
									curIndex++;
								}
								else
								{
									temData[curIndex].append(body.charAt(i));
								}
							}

							activity.runOnUiThread(new Runnable()
							{
								@Override
								public void run()
								{
									adapter.notifyDataSetChanged();
								}
							});
						}
						else
						{
							ErrorHandling.showError(activity, parser.getBody());
						}
					}
				}
				catch (IOException e)
				{
					ErrorHandling.showError(activity, R.string.connection_error);
					e.printStackTrace();
				}
			}
		}).start();
	}
}
