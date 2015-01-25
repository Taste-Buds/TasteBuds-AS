package com.codepath.apps.tastebuds.adapters;

import java.util.List;


import com.codepath.apps.tastebuds.R;
import com.codepath.apps.tastebuds.models.DishReview;
import com.codepath.apps.tastebuds.models.Restaurant;
import com.codepath.apps.tastebuds.models.RestaurantReview;
import com.codepath.apps.tastebuds.models.User;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class FriendsListAdapter extends ArrayAdapter<Object> {
	private TextView tvFriendName;
	private ImageView ivFriendProfImg;
	private TextView tvLocation;
	private TextView tvRestaurantReview;
	private TextView tvDishReview;
	public FriendsListAdapter(Context context, List<Object> friends) {
		super(context, 0, friends);
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ParseUser friend = (ParseUser)getItem(position);
		try {
			friend = friend.fetchIfNeeded();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		View v;
		if (convertView == null) {
			LayoutInflater inflater = LayoutInflater.from(getContext());
			v = inflater.inflate(R.layout.item_friend, parent, false);
		} else {
			v = convertView;
		}
		tvFriendName = (TextView) v.findViewById(R.id.tvFriendName);
		tvFriendName.setText(friend.getUsername());
		ivFriendProfImg = (ImageView) v.findViewById(R.id.ivProfileImg);
		String profImgURL = friend.getString("profileImgURL");
		Picasso.with(getContext()).load(profImgURL).resize(100, 100).centerCrop().into(ivFriendProfImg);
		
		tvLocation = (TextView) v.findViewById(R.id.tvLocation);
		tvLocation.setText( friend.getString("location"));
		tvRestaurantReview = (TextView) v.findViewById(R.id.tvRestaurantReviews);
		tvDishReview = (TextView) v.findViewById(R.id.tvDishReviews);
		//populateReviewNumbers(friend);
		if(friend.getList("restReviews") !=null){
			setRestaurantReviewNum(Integer.parseInt(friend.getList("restReviews").get(0).toString()));
		}
		if(friend.getList("dishReviews") != null){
			setDishReviewNum(Integer.parseInt(friend.getList("dishReviews").get(0).toString()));
		}
		return v;
	}
	public void setRestaurantReviewNum(int size){
		tvRestaurantReview.setText(Integer.toString(size));
	}
	public void setDishReviewNum(int size){
		tvDishReview.setText(Integer.toString(size));
	}
}
