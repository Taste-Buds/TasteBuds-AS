package com.codepath.apps.tastebuds.fragments;

import com.codepath.apps.tastebuds.R;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class LoginSwipePageFragment extends Fragment {
	public static final String ARG_OBJECT = "object";

	@Override
	public View onCreateView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		// The last two arguments ensure LayoutParams are inflated
		// properly.
		View rootView = inflater.inflate(
				R.layout.fragment_collection_login_object, container, false);
		Bundle args = getArguments();
		String val =Integer.toString(args.getInt(ARG_OBJECT)); 
		((TextView) rootView.findViewById(R.id.text1)).setText(
				val);
		ImageView ivLogin= (ImageView) rootView.findViewById(R.id.ivLogin);
		int id = getResources().getIdentifier("com.codepath.apps.tastebuds:drawable/" + "res" +val, null, null);
		ivLogin.setImageResource(id);
		return rootView;
	}
}
