package com.lazypanda07.cloudstoragemobile.CustomListView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lazypanda07.cloudstoragemobile.R;

import java.util.ArrayList;

public class PortraitDownloadedFilesListViewAdapter extends BaseAdapter
{
	private ArrayList<SystemFileData> data;
	private LayoutInflater inflater;

	public PortraitDownloadedFilesListViewAdapter(Context context, ArrayList<SystemFileData> data)
	{
		this.data = data;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount()
	{
		return data.size();
	}

	@Override
	public Object getItem(int i)
	{
		return data.get(i);
	}

	@Override
	public long getItemId(int i)
	{
		return i;
	}

	@Override
	public View getView(int i, View view, ViewGroup viewGroup)
	{
		View result = view;
		String tem = data.get(i).getFileSize() + " Б";

		if (result == null)
		{
			result = inflater.inflate(R.layout.downloaded_files_list, null);
		}

		((TextView) result.findViewById(R.id.name)).setText(data.get(i).getFileName());
		((TextView) result.findViewById(R.id.size)).setText(tem);

		return result;
	}
}
