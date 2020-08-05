package com.lazypanda07.cloudstoragemobile.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.lazypanda07.cloudstoragemobile.R;

import java.io.File;

public class DownloadedFilesActivity extends AppCompatActivity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_downloaded_files);

		TextView textView = findViewById(R.id.text);

		File path = getApplicationContext().getExternalFilesDir("Download");

		if (path != null)
		{
			String[] files = path.list();

			textView.setText("");

			for (String i : files)
			{
				textView.append(i + "\n");
			}
		}
	}
}