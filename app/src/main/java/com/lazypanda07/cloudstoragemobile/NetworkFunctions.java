package com.lazypanda07.cloudstoragemobile;

import android.content.Intent;
import android.os.Environment;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.lazypanda07.cloudstoragemobile.Activities.CloudStorageActivity;
import com.lazypanda07.cloudstoragemobile.CustomListView.FileData;
import com.lazypanda07.cloudstoragemobile.CustomListView.PortraitCloudStorageListViewAdapter;
import com.lazypanda07.networklib.Constants;
import com.lazypanda07.networklib.HTTP;
import com.lazypanda07.networklib.Network;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class NetworkFunctions
{
	public enum Storage
	{
		INTERNAL,
		SDCard
	}

	public static Storage storageType = Storage.INTERNAL;

	private static boolean authorization(@org.jetbrains.annotations.NotNull Network network, String login, String password)
	{
		String body = "login=" + login + "&password=" + password;

		String request = (new HTTP.HTTPBuilder()).setMethod("POST").
				setHeaders(Constants.RequestType.ACCOUNT_TYPE, Constants.AccountRequests.AUTHORIZATION).
				setHeaders("Content-Length", String.valueOf(body.length())).
				build(body.getBytes());

		request = HTTP.HTTPBuilder.insertSizeHeaderToHTTPMessage(request);

		try
		{
			network.sendBytes(request.getBytes());

			final HTTP.HTTPParser parser = new HTTP.HTTPParser(network.receiveBytes());

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
							setHeaders(Constants.RequestType.ACCOUNT_TYPE, Constants.AccountRequests.AUTHORIZATION).
							setHeaders("Content-Length", String.valueOf(body.length())).
							build(body.getBytes());

					request = HTTP.HTTPBuilder.insertSizeHeaderToHTTPMessage(request);

					network.sendBytes(request.getBytes());

					final HTTP.HTTPParser parser = new HTTP.HTTPParser(network.receiveBytes());

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
							setHeaders(Constants.RequestType.ACCOUNT_TYPE, Constants.AccountRequests.REGISTRATION).
							setHeaders("Content-Length", String.valueOf(body.length())).
							build(body.getBytes());

					request = HTTP.HTTPBuilder.insertSizeHeaderToHTTPMessage(request);

					network.sendBytes(request.getBytes());

					final HTTP.HTTPParser parser = new HTTP.HTTPParser(network.receiveBytes());

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
								setHeaders(Constants.RequestType.FILES_TYPE, Constants.FilesRequests.SHOW_ALL_FILES_IN_DIRECTORY).
								build();

						request = HTTP.HTTPBuilder.insertSizeHeaderToHTTPMessage(request);

						network.sendBytes(request.getBytes());

						HTTP.HTTPParser parser = new HTTP.HTTPParser(network.receiveBytes());

						if (parser.getHeaders().get("Error").equals("0"))
						{
							String body = new String(parser.getBody(), "CP1251");
							StringBuilder[] temData = new StringBuilder[6];    //each index equals member position in FileData class
							int curIndex = 0;

							for (int i = 0; i < temData.length; i++)
							{
								temData[i] = new StringBuilder();
							}

							for (int i = 0; i < body.length(); i++)
							{
								if (body.charAt(i) == Constants.DATA_DELIMITER.charAt(0))
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
								else if (body.charAt(i) == Constants.DATA_PART_DELIMITER.charAt(0))
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
						network.close();
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

	public static void downloadFile(final AppCompatActivity activity, final String fileName, final long fileSize, final String login, final String password)
	{
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				File storage = activity.getApplicationContext().getExternalFilesDirs(Environment.DIRECTORY_DOWNLOADS)[storageType.ordinal()];
				storage = new File(storage, fileName);
				long totalFileSize = 0;

				try
				{
					boolean isCreated = storage.createNewFile();

					if (isCreated | storage.exists())
					{
						Network network = new Network(Constants.APIServerIp, Constants.APIServerPort);
						long offset = 0;
						FileOutputStream out = new FileOutputStream(storage);

						if (authorization(network, login, password))
						{
							while (true)
							{
								String request = (new HTTP.HTTPBuilder()).setMethod("POST").
										setHeaders(Constants.RequestType.FILES_TYPE, Constants.FilesRequests.DOWNLOAD_FILE).
										setHeaders("File-Name", fileName).
										setHeaders("Range", String.valueOf(offset)).
										build();

								request = HTTP.HTTPBuilder.insertSizeHeaderToHTTPMessage(request);

								network.sendBytes(request.getBytes("CP1251"));

								HTTP.HTTPParser parser = new HTTP.HTTPParser(network.receiveBytes());

								String checkTotalFileSize = parser.getHeaders().get("Total-File-Size");

								out.write(parser.getBody());

								offset += parser.getBody().length;

								if (checkTotalFileSize != null)
								{
									totalFileSize = Long.parseLong(checkTotalFileSize);
									break;
								}
							}
						}

						network.close();
					}
					else
					{
						ErrorHandling.showError(activity, R.string.cant_create_file);
					}
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}

				if (totalFileSize != fileSize)
				{
					ErrorHandling.showError(activity, R.string.send_not_full_data);
				}
				else
				{
					activity.runOnUiThread(new Runnable()
					{
						@Override
						public void run()
						{
							String success = fileName + "\r\n" + "Файл успешно скачан";

							Toast.makeText(activity.getApplicationContext(), success, Toast.LENGTH_SHORT).show();
						}
					});
				}
			}
		}).start();
	}
}
