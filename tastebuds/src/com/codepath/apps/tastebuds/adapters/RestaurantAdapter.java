package com.codepath.apps.tastebuds.adapters;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;


import com.codepath.apps.tastebuds.GooglePlacesApiClient;
import com.codepath.apps.tastebuds.R;
import com.codepath.apps.tastebuds.models.PlacesPhotoData;
import com.codepath.apps.tastebuds.models.Restaurant;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.rockerhieu.emojicon.EmojiconTextView;
import com.squareup.picasso.Picasso;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.LayerDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

public class RestaurantAdapter extends ArrayAdapter<Restaurant> {

	public RestaurantAdapter(Context context, List<Restaurant> restaurants) {
		super(context, 0, restaurants);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		GooglePlacesApiClient apiClient = new GooglePlacesApiClient();
		Restaurant restaurant = getItem(position);
		View v;
		if (convertView == null) {
			LayoutInflater inflater = LayoutInflater.from(getContext());
			v = inflater.inflate(R.layout.restaurant_list_item, parent, false);
		} else {
			v = convertView;
		}

		TextView tvRestaurantName = (TextView) v.findViewById(R.id.tvRestaurantName);
		tvRestaurantName.setText(restaurant.getName());
		RatingBar rbTasteBudsRating = (RatingBar) v.findViewById(R.id.rbTasteBudsRating);
		String numberOfReviewString = Integer.toString(restaurant.getNumOfReviews());
		float friendRating = (restaurant.getFriendRating());
		
		rbTasteBudsRating.setRating(friendRating);
		LayerDrawable stars = (LayerDrawable) rbTasteBudsRating.getProgressDrawable();
		//stars.getDrawable(0).setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_ATOP);
		//stars.getDrawable(1).setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_ATOP);
		stars.getDrawable(2).setColorFilter(Color.argb(255, 255, 153, 51),
				PorterDuff.Mode.SRC_ATOP);

		TextView tvNumberofReviews = (TextView) v.findViewById(R.id.tvNumberofReviews);
		tvNumberofReviews.setText(numberOfReviewString);
		TextView tvDistance = (TextView) v.findViewById(R.id.tvDistance);
		float distance = (float) (restaurant.getCurrentDistancetoUser() * 0.00062137);
		String distanceString = String.format("%.1f", distance);
		tvDistance.setText(distanceString);
		ImageView ivRestaurantImage = (ImageView) v.findViewById(R.id.ivRestaurantImage);
		/*Bitmap photo;// = BitmapFactory.decodeStream(
			apiClient.getRestaurantDisplayPhoto(restaurant.getDisplayPhotoReference(), 56,
					new JsonHttpResponseHandler() {
				
			});*/
		if (restaurant.getDisplayPhoto() != null) {
			ivRestaurantImage.setImageBitmap(restaurant.getDisplayPhoto());
		} else {
			if (restaurant.getIcon() != null) {
				Picasso.with(getContext()).load(restaurant.getIcon()).into(ivRestaurantImage);
			}
		}

		TextView tvDollars = (TextView) v.findViewById(R.id.tvDollars);
		int price_level = restaurant.getPrice_level();
		String dollar_string = "$";
		for (int ii = 0; ii < price_level-1; ii++) {
			dollar_string += "$";
		}
		tvDollars.setText(dollar_string);
		
		EmojiconTextView mTxtEmojicon = (EmojiconTextView) v.findViewById(R.id.txtEmojicon);
		if(restaurant.getTagString() != null){
			mTxtEmojicon.setText(restaurant.getTagString());
		}
		return v;
	}
}
