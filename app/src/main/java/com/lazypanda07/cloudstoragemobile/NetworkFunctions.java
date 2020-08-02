package com.lazypanda07.cloudstoragemobile;

import android.content.Intent;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.lazypanda07.cloudstoragemobile.Activities.CloudStorageActivity;
import com.lazypanda07.networklib.Constants;
import com.lazypanda07.networklib.HTTP;
import com.lazypanda07.networklib.Network;

import java.io.IOException;

public class NetworkFunctions
{
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

						activity.startActivity(new Intent(activity.getApplicationContext(), CloudStorageActivity.class));
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
}
