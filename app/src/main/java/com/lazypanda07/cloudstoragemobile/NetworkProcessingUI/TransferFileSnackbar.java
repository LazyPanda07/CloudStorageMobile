package com.lazypanda07.cloudstoragemobile.NetworkProcessingUI;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.google.android.material.snackbar.Snackbar;
import com.lazypanda07.cloudstoragemobile.R;

public class TransferFileSnackbar
{
	private Snackbar snackbar;
	private ProgressBar progressBar;

	public TransferFileSnackbar(View parent, String message)
	{
		snackbar = Snackbar.make(parent, message, Snackbar.LENGTH_INDEFINITE);
		LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.transfer_file_snackbar, null);
		Snackbar.SnackbarLayout content = (Snackbar.SnackbarLayout) snackbar.getView();

		content.addView(layout);

		((Button) content.findViewById(com.google.android.material.R.id.snackbar_action)).setAllCaps(false);

		snackbar.setAction(R.string.cancel_operation, new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				snackbar.dismiss();
			}
		});

		progressBar = layout.findViewById(R.id.progress_horizontal);
	}

	public boolean isShown()
	{
		return snackbar.isShown();
	}

	public void show()
	{
		snackbar.show();
	}

	public void dismiss()
	{
		snackbar.dismiss();
	}

	public void setRange(int value)
	{
		progressBar.setMax(value);
	}

	public void setProgress(int value)
	{
		progressBar.setProgress(value);
	}

	public int getProgress()
	{
		return progressBar.getProgress();
	}
}
