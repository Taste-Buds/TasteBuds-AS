package com.codepath.apps.tastebuds.adapters;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.codepath.apps.tastebuds.R;

public class GridImageAdapter extends ArrayAdapter<Bitmap> {

	public GridImageAdapter(Context context, List<Bitmap> images) {
		super(context, android.R.layout.simple_list_item_1, images);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Bitmap image = getItem(position);
		if (convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_image, parent, false);
		}
		ImageView ivImage = (ImageView) convertView.findViewById(R.id.ivImage);
		//float heightRatio =  (imageInfo.height / imageInfo.width);
		//ivImage.setHeightRatio(heightRatio);
		ivImage.setImageResource(0);
		ivImage.setImageBitmap(image);
		return convertView;
	}
}