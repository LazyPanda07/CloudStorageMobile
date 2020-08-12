package com.lazypanda07.cloudstoragemobile;

import android.content.Intent;
import android.os.Environment;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.lazypanda07.cloudstoragemobile.Activities.CloudStorageActivity;
import com.lazypanda07.cloudstoragemobile.CustomListView.FileData;
import com.lazypanda07.cloudstoragemobile.CustomListView.PortraitCloudStorageListViewAdapter;
import com.lazypanda07.cloudstoragemobile.NetworkProcessingUI.TransferFileSnackbar;
import com.lazypanda07.cloudstoragemobile.NetworkProcessingUI.WaitResponseSnackbar;
import com.lazypanda07.networklib.Constants;
import com.lazypanda07.networklib.HTTP;
import com.lazypanda07.networklib.Network;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class NetworkFunctions
{
	//TODO: create settings menu and database for settings
	public enum StorageType
	{
		INTERNAL,
		SDCard
	}

	public static StorageType storageType = StorageType.INTERNAL;

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

	private static String folderControlMessages(Network network, final String folderName, final String controlRequest)
	{
		try
		{
			String request = "";

			if (controlRequest.equals(Constants.ControlRequests.PREVIOUS_FOLDER))
			{
				request = (new HTTP.HTTPBuilder()).setMethod("POST").
						setHeaders(Constants.RequestType.CONTROL_TYPE, controlRequest).
						build();
			}
			else
			{
				try
				{
					String body = "folder=" + folderName;

					request = (new HTTP.HTTPBuilder()).setMethod("POST").
							setHeaders(Constants.RequestType.CONTROL_TYPE, controlRequest).
							setHeaders("Content-Length", String.valueOf(body.length())).
							build(body.getBytes("CP1251"));
				}
				catch (UnsupportedEncodingException e)
				{
					e.printStackTrace();
					return "";
				}
			}

			request = HTTP.HTTPBuilder.insertSizeHeaderToHTTPMessage(request);

			try
			{
				network.sendBytes(request.getBytes("CP1251"));
			}
			catch (IOException e)
			{
				e.printStackTrace();
				return "";
			}

			return new String(network.receiveBytes(), "CP1251");
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		return "";
	}

	private static boolean setPath(Network network, final String[] currentPath)
	{
		try
		{
			return new String(new HTTP.HTTPParser
					(
							folderControlMessages(network, currentPath[0], Constants.ControlRequests.SET_PATH).getBytes("CP1251")
					).
					getBody()).
					equals(Constants.Responses.OK_RESPONSE);
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
			return false;
		}
	}

	public static void authorization(final AppCompatActivity activity, View parent)
	{
		final WaitResponseSnackbar waitResponseSnackbar = new WaitResponseSnackbar(parent);

		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					waitResponseSnackbar.show();

					Network network = new Network(Constants.APIServerIp, Constants.APIServerPort);

					String login = ((EditText) activity.findViewById(R.id.authorization_login)).getText().toString();
					String password = ((EditText) activity.findViewById(R.id.authorization_password)).getText().toString();

					if (login.isEmpty())
					{
						waitResponseSnackbar.dismiss();
						ErrorHandling.showError(activity, R.string.empty_login);
						network.close();
						return;
					}
					else if (password.isEmpty())
					{
						waitResponseSnackbar.dismiss();
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

					if (!waitResponseSnackbar.isShown())
					{
						network.close();
						return;
					}

					network.sendBytes(request.getBytes());

					final HTTP.HTTPParser parser = new HTTP.HTTPParser(network.receiveBytes());

					if (parser.getHeaders().get("Error").equals("0"))
					{
						waitResponseSnackbar.dismiss();
						network.close();

						Intent intent = new Intent(activity.getApplicationContext(), CloudStorageActivity.class);

						intent.putExtra("login", login);
						intent.putExtra("password", password);

						activity.startActivity(intent);
					}
					else
					{
						waitResponseSnackbar.dismiss();
						ErrorHandling.showError(activity, parser.getBody());
						network.close();
					}

					waitResponseSnackbar.dismiss();
				}
				catch (IOException e)
				{
					waitResponseSnackbar.dismiss();
					ErrorHandling.showError(activity, R.string.connection_error);
					e.printStackTrace();
				}
			}
		}).start();
	}

	public static void registration(final AppCompatActivity activity, View parent)
	{
		final WaitResponseSnackbar waitResponseSnackbar = new WaitResponseSnackbar(parent);

		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					waitResponseSnackbar.show();

					Network network = new Network(Constants.APIServerIp, Constants.APIServerPort);

					String login = ((EditText) activity.findViewById(R.id.registration_login)).getText().toString();
					String password = ((EditText) activity.findViewById(R.id.registration_password)).getText().toString();
					String repeatPassword = ((EditText) activity.findViewById(R.id.registration_repeat_password)).getText().toString();

					if (login.isEmpty())
					{
						waitResponseSnackbar.dismiss();
						ErrorHandling.showError(activity, R.string.empty_login);
						network.close();
						return;
					}
					else if (password.isEmpty())
					{
						waitResponseSnackbar.dismiss();
						ErrorHandling.showError(activity, R.string.empty_password);
						network.close();
						return;
					}
					else if (!password.equals(repeatPassword))
					{
						waitResponseSnackbar.dismiss();
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

					if (!waitResponseSnackbar.isShown())
					{
						return;
					}

					network.sendBytes(request.getBytes());

					final HTTP.HTTPParser parser = new HTTP.HTTPParser(network.receiveBytes());

					if (parser.getHeaders().get("Error").equals("0"))
					{
						waitResponseSnackbar.dismiss();
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
						waitResponseSnackbar.dismiss();
						ErrorHandling.showError(activity, parser.getBody());
						network.close();
					}

					waitResponseSnackbar.dismiss();
				}
				catch (IOException e)
				{
					waitResponseSnackbar.dismiss();
					ErrorHandling.showError(activity, R.string.connection_error);
					e.printStackTrace();
				}
			}
		}).start();
	}

	public static void getFiles(final AppCompatActivity activity, final ArrayList<FileData> fileData, final PortraitCloudStorageListViewAdapter adapter, final String login, final String password, final String[] currentPath, View parent)
	{
		final WaitResponseSnackbar waitResponseSnackbar = new WaitResponseSnackbar(parent);

		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					waitResponseSnackbar.show();

					Network network = new Network(Constants.APIServerIp, Constants.APIServerPort);

					if (authorization(network, login, password) && waitResponseSnackbar.isShown())
					{
						String request = (new HTTP.HTTPBuilder()).setMethod("POST").
								setHeaders(Constants.RequestType.FILES_TYPE, Constants.FilesRequests.SHOW_ALL_FILES_IN_DIRECTORY).
								build();

						if (setPath(network, currentPath) && waitResponseSnackbar.isShown())
						{
							request = HTTP.HTTPBuilder.insertSizeHeaderToHTTPMessage(request);

							if (!waitResponseSnackbar.isShown())
							{
								network.close();
								return;
							}

							network.sendBytes(request.getBytes());

							HTTP.HTTPParser parser = new HTTP.HTTPParser(network.receiveBytes());

							fileData.clear();

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

										fileData.add(tem);

										curIndex = 0;

										for (StringBuilder j : temData)
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
							}
							else
							{
								ErrorHandling.showError(activity, parser.getBody());
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
					}

					network.close();
					waitResponseSnackbar.dismiss();
				}
				catch (IOException e)
				{
					waitResponseSnackbar.dismiss();
					ErrorHandling.showError(activity, R.string.connection_error);
					e.printStackTrace();
				}
			}
		}).start();
	}

	public static void downloadFile(final AppCompatActivity activity, final String fileName, final long fileSize, final String login, final String password, final String[] currentPath, View parent)
	{
		final WaitResponseSnackbar waitResponseSnackbar = new WaitResponseSnackbar(parent);
		final TransferFileSnackbar transferFileSnackbar = new TransferFileSnackbar(parent, String.format(parent.getContext().getResources().getString(R.string.snackbar_text_load_file), fileName));

		transferFileSnackbar.setRange((int) fileSize);

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
						waitResponseSnackbar.show();

						Network network = new Network(Constants.APIServerIp, Constants.APIServerPort);
						long offset = 0;
						FileOutputStream out = new FileOutputStream(storage);

						if (authorization(network, login, password) && waitResponseSnackbar.isShown())
						{
							if (setPath(network, currentPath) && waitResponseSnackbar.isShown())
							{
								waitResponseSnackbar.dismiss();

								transferFileSnackbar.show();

								while (true)
								{
									if (transferFileSnackbar.isShown())
									{
										break;
									}
								}

								try
								{
									Thread.sleep(500);
								}
								catch (InterruptedException e)
								{
									e.printStackTrace();
								}

								while (true)
								{
									String request = (new HTTP.HTTPBuilder()).setMethod("POST").
											setHeaders(Constants.RequestType.FILES_TYPE, Constants.FilesRequests.DOWNLOAD_FILE).
											setHeaders("File-Name", fileName).
											setHeaders("Range", String.valueOf(offset)).
											build();

									request = HTTP.HTTPBuilder.insertSizeHeaderToHTTPMessage(request);

									if (!transferFileSnackbar.isShown())
									{
										storage.delete();
										network.close();
										return;
									}

									network.sendBytes(request.getBytes("CP1251"));

									HTTP.HTTPParser parser = new HTTP.HTTPParser(network.receiveBytes());

									String checkTotalFileSize = parser.getHeaders().get("Total-File-Size");

									out.write(parser.getBody());

									offset += parser.getBody().length;

									transferFileSnackbar.setProgress((int) offset);

									if (checkTotalFileSize != null)
									{
										totalFileSize = Long.parseLong(checkTotalFileSize);
										break;
									}
								}

								try
								{
									Thread.sleep(1000);
								}
								catch (InterruptedException e)
								{
									e.printStackTrace();
								}

								transferFileSnackbar.dismiss();
							}
							else
							{
								waitResponseSnackbar.dismiss();
							}
						}
						else
						{
							waitResponseSnackbar.dismiss();
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
					waitResponseSnackbar.dismiss();
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

	public static void uploadFile(final AppCompatActivity activity, final DataInputStream stream, final int fileSize, final String fileName, final String login, final String password, final ArrayList<FileData> fileData, final PortraitCloudStorageListViewAdapter adapter, final String[] currentPath, final View parent)
	{
		final WaitResponseSnackbar waitResponseSnackbar = new WaitResponseSnackbar(parent);
		final TransferFileSnackbar transferFileSnackbar = new TransferFileSnackbar(parent, String.format(parent.getContext().getResources().getString(R.string.snackbar_text_upload_file), fileName));

		transferFileSnackbar.setRange(fileSize);

		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				int offset = 0;
				boolean isLast;

				try
				{
					waitResponseSnackbar.show();

					Network network = new Network(Constants.APIServerIp, Constants.APIServerPort);

					if (authorization(network, login, password) && waitResponseSnackbar.isShown())
					{
						if (setPath(network, currentPath) && waitResponseSnackbar.isShown())
						{
							waitResponseSnackbar.dismiss();

							transferFileSnackbar.show();

							while (true)
							{
								if (transferFileSnackbar.isShown())
								{
									break;
								}
							}

							do
							{
								byte[] data;
								int nextOffset = 0;

								if (fileSize - offset >= Constants.FILE_PACKET_SIZE)
								{
									data = new byte[Constants.FILE_PACKET_SIZE];

									try
									{
										nextOffset += stream.read(data, offset, Constants.FILE_PACKET_SIZE);
									}
									catch (IOException e)
									{
										e.printStackTrace();
									}

									isLast = false;
								}
								else
								{
									data = new byte[fileSize - offset];

									try
									{
										nextOffset += stream.read(data, offset, data.length);
									}
									catch (IOException e)
									{
										e.printStackTrace();
									}

									isLast = true;
								}

								String message = (new HTTP.HTTPBuilder()).setMethod("POST").
										setHeaders(Constants.RequestType.FILES_TYPE, Constants.FilesRequests.UPLOAD_FILE).
										setHeaders("File-Name", fileName).
										setHeaders("Range", String.valueOf(offset)).
										setHeaders("Content-Length", String.valueOf(data.length)).
										setHeaders(isLast ? "Total-File-Size" : "Reserved", isLast ? String.valueOf(fileSize) : "0").
										build(data);

								message = HTTP.HTTPBuilder.insertSizeHeaderToHTTPMessage(message);

								network.sendBytes(message.getBytes(StandardCharsets.ISO_8859_1));

								offset += nextOffset;

								transferFileSnackbar.setProgress(offset);
							} while (!isLast);

							try
							{
								Thread.sleep(1000);
							}
							catch (InterruptedException e)
							{
								e.printStackTrace();
							}

							transferFileSnackbar.dismiss();

							HTTP.HTTPParser parser = new HTTP.HTTPParser(network.receiveBytes());

							if (parser.getHeaders().get("Error").equals("1"))
							{
								ErrorHandling.showError(activity, parser.getBody());

								network.close();
							}
							else
							{
								activity.runOnUiThread(new Runnable()
								{
									@Override
									public void run()
									{
										Toast.makeText(activity.getApplicationContext(), "Файл загружен", Toast.LENGTH_SHORT).show();
									}
								});

								network.close();

								getFiles(activity, fileData, adapter, login, password, currentPath, parent);
							}
						}
						else
						{
							waitResponseSnackbar.dismiss();
						}
					}
					else
					{
						network.close();
						waitResponseSnackbar.dismiss();
					}
				}
				catch (IOException e)
				{
					waitResponseSnackbar.dismiss();
					ErrorHandling.showError(activity, R.string.connection_error);
					e.printStackTrace();
				}
			}
		}).start();
	}

	public static void removeFile(final AppCompatActivity activity, final String fileName, final String login, final String password, final ArrayList<FileData> fileData, final PortraitCloudStorageListViewAdapter adapter, final String[] currentPath, final View parent)
	{
		final WaitResponseSnackbar waitResponseSnackbar = new WaitResponseSnackbar(parent);

		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					waitResponseSnackbar.show();

					Network network = new Network(Constants.APIServerIp, Constants.APIServerPort);

					if (authorization(network, login, password) && waitResponseSnackbar.isShown())
					{
						if (setPath(network, currentPath) && waitResponseSnackbar.isShown())
						{
							String request = (new HTTP.HTTPBuilder()).setMethod("POST").
									setHeaders(Constants.RequestType.FILES_TYPE, Constants.FilesRequests.REMOVE_FILE).
									setHeaders("File-Name", fileName)
									.build();

							request = HTTP.HTTPBuilder.insertSizeHeaderToHTTPMessage(request);

							if (!waitResponseSnackbar.isShown())
							{
								network.close();
								return;
							}

							network.sendBytes(request.getBytes());

							HTTP.HTTPParser parser = new HTTP.HTTPParser(network.receiveBytes());

							waitResponseSnackbar.dismiss();

							if (parser.getHeaders().get("Error").equals("0"))
							{
								network.close();

								getFiles(activity, fileData, adapter, login, password, currentPath, parent);

								activity.runOnUiThread(new Runnable()
								{
									@Override
									public void run()
									{
										Toast.makeText(activity.getApplicationContext(), "Файл " + fileName + " успешно удален", Toast.LENGTH_LONG).show();
									}
								});
							}
							else
							{
								ErrorHandling.showError(activity, "Не удалось удалить " + fileName);
								network.close();
							}
						}
						else
						{
							waitResponseSnackbar.dismiss();
						}
					}
					else
					{
						waitResponseSnackbar.dismiss();
					}
				}
				catch (IOException e)
				{
					waitResponseSnackbar.dismiss();
					ErrorHandling.showError(activity, R.string.connection_error);
					e.printStackTrace();
				}
			}
		}).start();
	}

	public static void createFolder(final AppCompatActivity activity, final String folderName, final String login, final String password, final ArrayList<FileData> fileData, final PortraitCloudStorageListViewAdapter adapter, final String[] currentPath, final View parent)
	{
		final WaitResponseSnackbar waitResponseSnackbar = new WaitResponseSnackbar(parent);

		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					waitResponseSnackbar.show();

					Network network = new Network(Constants.APIServerIp, Constants.APIServerPort);

					if (authorization(network, login, password) && waitResponseSnackbar.isShown())
					{
						if (setPath(network, currentPath) && waitResponseSnackbar.isShown())
						{
							String body = "folder=" + folderName;

							String request = (new HTTP.HTTPBuilder()).setMethod("POST").
									setHeaders(Constants.RequestType.FILES_TYPE, Constants.FilesRequests.CREATE_FOLDER).
									setHeaders("Content-Length", String.valueOf(body.length())).
									build(body.getBytes());

							request = HTTP.HTTPBuilder.insertSizeHeaderToHTTPMessage(request);

							if (!waitResponseSnackbar.isShown())
							{
								network.close();
								return;
							}

							network.sendBytes(request.getBytes("CP1251"));

							network.close();

							waitResponseSnackbar.dismiss();

							getFiles(activity, fileData, adapter, login, password, currentPath, parent);
						}
						else
						{
							waitResponseSnackbar.dismiss();
						}
					}
					else
					{
						network.close();
						waitResponseSnackbar.dismiss();
					}
				}
				catch (IOException e)
				{
					waitResponseSnackbar.dismiss();
					ErrorHandling.showError(activity, R.string.connection_error);
					e.printStackTrace();
				}

			}
		}).start();
	}

	public static void nextFolder(final AppCompatActivity activity, final String folderName, final String[] currentPath, final String login, final String password, final ArrayList<FileData> fileData, final PortraitCloudStorageListViewAdapter adapter, final View parent)
	{
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				currentPath[0] = currentPath[0] + Constants.WINDOWS_SEPARATOR + folderName;

				getFiles(activity, fileData, adapter, login, password, currentPath, parent);
			}
		}).start();
	}

	public static void prevFolder(final AppCompatActivity activity, final String login, final String password, final ArrayList<FileData> fileData, final PortraitCloudStorageListViewAdapter adapter, final String[] currentPath, final View parent)
	{
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				if (currentPath[0].equals("Home"))
				{
					return;
				}

				currentPath[0] = currentPath[0].substring(0, currentPath[0].lastIndexOf(Constants.WINDOWS_SEPARATOR));

				getFiles(activity, fileData, adapter, login, password, currentPath, parent);
			}
		}).start();
	}
}
