package com.codepath.apps.tastebuds.adapters;

import java.text.SimpleDateFormat;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.codepath.apps.tastebuds.R;
import com.codepath.apps.tastebuds.models.Dish;

public class DishListAdapter extends ArrayAdapter<Dish> {

	/*public DishListAdapter(Context context, final String googlePlacesId,
			final List<ParseObject> friends) {
		super(context, new ParseQueryAdapter.QueryFactory<DishReview>() {
			public ParseQuery<DishReview> create() {
				ParseQuery<DishReview> query = DishReview.getQuery(
						googlePlacesId, friends);
				return query;
			}
		});
	}

	public DishListAdapter(Context context, final ParseUser user) {
		super(context, new ParseQueryAdapter.QueryFactory<DishReview>() {
			public ParseQuery<DishReview> create() {
				ParseQuery<DishReview> query = DishReview.getQuery(user);
				return query;
			}
		});
	}*/

	public DishListAdapter(Context context, List<Dish> dishes) {
		super(context, 0, dishes);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Dish dish = getItem(position);
		View view;
		if (convertView == null) {
			LayoutInflater inflater = LayoutInflater.from(getContext());
			view = inflater.inflate(R.layout.review_list_item, parent, false);
		} else {
			view = convertView;
		}

		ImageView userImage = (ImageView) view.findViewById(R.id.ivReviewUser);
		switch (position % 4) {
		case 0: userImage.setImageResource(R.drawable.pizza); break;
		case 1: userImage.setImageResource(R.drawable.beer); break;
		case 2: userImage.setImageResource(R.drawable.ceasarsalad); break;
		case 3: userImage.setImageResource(R.drawable.papdi); break;
		default: userImage.setImageResource(R.drawable.pizza); break;
		}
		

		TextView username = (TextView) view.findViewById(R.id.tvReviewUsername);
		TextView content = (TextView) view.findViewById(R.id.tvReviewContent);
		RatingBar rating = (RatingBar) view.findViewById(R.id.rbReviewRating);
		LayerDrawable stars = (LayerDrawable) rating.getProgressDrawable();
		stars.getDrawable(0).setColorFilter(Color.argb(255, 255, 153, 51), PorterDuff.Mode.SRC_ATOP);
		stars.getDrawable(1).setColorFilter(Color.argb(255, 255, 153, 51), PorterDuff.Mode.SRC_ATOP);
		stars.getDrawable(2).setColorFilter(Color.argb(255, 255, 153, 51),
				PorterDuff.Mode.SRC_ATOP);
		TextView time = (TextView) view.findViewById(R.id.tvReviewTime);
		time.setVisibility(TextView.GONE);

		content.setVisibility(TextView.GONE);
		username.setText(dish.getName());
		rating.setRating(dish.getRating());
		rating.setEnabled(false);
		return view;
	}

	/*@Override
	public View getItem(Dish dish, View view, ViewGroup parent) {
		if (view == null) {
			view = View.inflate(getContext(), R.layout.review_list_item, null);
		}
		super.getItemView(dish, view, parent);

		ImageView userImage = (ImageView) view.findViewById(R.id.ivReviewUser);
		TextView username = (TextView) view.findViewById(R.id.tvReviewUsername);
		TextView content = (TextView) view.findViewById(R.id.tvReviewContent);
		RatingBar rating = (RatingBar) view.findViewById(R.id.rbReviewRating);

		username.setText("abcdefgh");//review.getUser().getUsername());
		content.setText(dish.getName());
		rating.setRating(4);
		return view;
	}*/

	/*@Override
	public View getNextPageView(View view, ViewGroup parent) {
		if (view == null) {
			view = View.inflate(getContext(), R.layout.review_list_item, null);
		}
		TextView
		return view;
	}*/
}
