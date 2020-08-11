package com.lazypanda07.cloudstoragemobile.NetworkProcessingUI;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.android.material.snackbar.Snackbar;
import com.lazypanda07.cloudstoragemobile.R;

public class WaitResponseSnackbar
{
	private Snackbar snackbar;

	public WaitResponseSnackbar(View parent)
	{
		Context context = parent.getContext();
		snackbar = Snackbar.make(parent, context.getResources().getString(R.string.wait_server_response), Snackbar.LENGTH_INDEFINITE);
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.wait_response_snackbar, null);
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
}
