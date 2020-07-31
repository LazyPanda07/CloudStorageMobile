package com.lazypanda07.cloudstoragemobile.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.lazypanda07.cloudstoragemobile.Constants;
import com.lazypanda07.cloudstoragemobile.R;
import com.lazypanda07.networklib.HTTP;
import com.lazypanda07.networklib.Network;

import java.io.IOException;

public class AuthorizationActivity extends AppCompatActivity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_authorization);

		findViewById(R.id.authorization_enter).setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				new Thread(new Runnable()
				{
					@Override
					public void run()
					{
						try
						{
							Network network = new Network(Constants.APIServerIp, Constants.APIServerPort);

							String body = "login=" + ((EditText) findViewById(R.id.authorization_login)).getText().toString() +
									"&password=" + ((EditText) findViewById(R.id.authorization_password)).getText().toString();

							String request = (new HTTP.HTTPBuilder()).setMethod("POST").
									setHeaders("Account request", "Authorization").
									setHeaders("Content-Length", "25").
									build(body.getBytes());

							request = HTTP.HTTPBuilder.insertSizeHeaderToHTTPMessage(request);

							network.sendBytes(request.getBytes());

							String response = new String(network.receiveBytes());

							HTTP.HTTPParser parser = new HTTP.HTTPParser(response.getBytes());

							if (parser.getHeaders().get("Error").equals("0"))
							{
								network.close();

								startActivity(new Intent(getApplicationContext(), CloudStorageActivity.class));
							}
							else
							{
								//TODO: error handling
							}
						}
						catch (IOException e)
						{
							e.printStackTrace();
						}
					}
				}).start();
			}
		});
	}
}