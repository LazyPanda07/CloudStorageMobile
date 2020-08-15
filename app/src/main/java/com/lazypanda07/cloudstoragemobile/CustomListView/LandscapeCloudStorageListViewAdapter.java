package com.lazypanda07.cloudstoragemobile.CustomListView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lazypanda07.cloudstoragemobile.R;

import java.util.ArrayList;

public class LandscapeCloudStorageListViewAdapter extends BaseAdapter
{
	private ArrayList<FileData> data;
	private LayoutInflater inflater;

	public LandscapeCloudStorageListViewAdapter(Context context, ArrayList<FileData> data)
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

		if (result == null)
		{
			result = inflater.inflate(R.layout.listview_files_list, null);
		}

		TextView name = result.findViewById(R.id.name);
		TextView type = result.findViewById(R.id.type);
		TextView dateOfChange = result.findViewById(R.id.date_of_change);
		TextView size = result.findViewById(R.id.size);
		String textSize;

		if (data.get(i).fileExtension.equals("Папка с файлами"))
		{
			textSize = "";
		}
		else
		{
			textSize = data.get(i).fileSize + " Б";
		}

		name.setText(data.get(i).fileName);
		type.setText(data.get(i).fileExtension);
		dateOfChange.setText(data.get(i).dateOfChange);
		size.setText(textSize);

		return result;
	}
}
