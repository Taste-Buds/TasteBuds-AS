package com.codepath.apps.tastebuds.adapters;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Random;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.text.format.DateFormat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.codepath.apps.tastebuds.R;
import com.codepath.apps.tastebuds.fragments.UserDishReviewsListFragment.UserDishReviewListListener;
import com.codepath.apps.tastebuds.fragments.UserRestaurantReviewsListFragment.UserRestaurantReviewListListener;
import com.codepath.apps.tastebuds.models.DishReview;
import com.codepath.apps.tastebuds.models.RestaurantReview;
import com.parse.ParseException;

import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;
import com.rockerhieu.emojicon.EmojiconTextView;

public class UserDishReviewListAdapter extends ParseQueryAdapter<DishReview> implements OnItemClickListener{
	private UserDishReviewListListener uListener;
	public UserDishReviewListAdapter(Context context, final String googlePlacesId,
			final List<ParseObject> friends) {
		super(context, new ParseQueryAdapter.QueryFactory<DishReview>() {
			public ParseQuery<DishReview> create() {
				ParseQuery<DishReview> query = DishReview.getQuery(googlePlacesId, friends);
				return query;
			}
		});
	}

	public UserDishReviewListAdapter(Context context, final String googlePlacesId,
			final List<ParseObject> friends, final String dishName) {
		super(context, new ParseQueryAdapter.QueryFactory<DishReview>() {
			public ParseQuery<DishReview> create() {
				ParseQuery<DishReview> query = DishReview.getQuery(googlePlacesId, friends,
						dishName);
				return query;
			}
		});
	}

	public UserDishReviewListAdapter(Context context,final ParseUser user) {
		super(context, new ParseQueryAdapter.QueryFactory<DishReview>() {
			public ParseQuery<DishReview> create() {
				ParseQuery<DishReview> query = DishReview.getQuery(user);
				return query;
			}
		});
	}

	public UserDishReviewListAdapter(Context context,final ParseUser user, UserDishReviewListListener listener) {
		super(context, new ParseQueryAdapter.QueryFactory<DishReview>() {
			public ParseQuery<DishReview> create() {
				ParseQuery<DishReview> query = DishReview.getQuery(
						user);
				return query;
			}
			
		});
		this.uListener = listener;
	}
	@Override
	public View getItemView(DishReview review, View view, ViewGroup parent) {
		if (view == null) {
			view = View.inflate(getContext(), R.layout.review_list_item, null);
		}
		super.getItemView(review, view, parent);

		ImageView userImage = (ImageView) view.findViewById(R.id.ivReviewUser);
		Random r = new Random();
		int position = r.nextInt(4);
		switch (position % 4) {
		case 0: userImage.setImageResource(R.drawable.pizza); break;
		case 1: userImage.setImageResource(R.drawable.beer); break;
		case 2: userImage.setImageResource(R.drawable.ceasarsalad); break;
		case 3: userImage.setImageResource(R.drawable.papdi); break;
		default: userImage.setImageResource(R.drawable.pizza);
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

//		ParseUser reviewer;
//		try {
//			reviewer = review.getUser().fetchIfNeeded();
//		
//		} catch (ParseException e) {
//			e.printStackTrace();
//		}

		content.setText(review.getText());
		rating.setRating(review.getRating());
		username.setText(review.getDishName());
		SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
		time.setText(df.format(review.getCreatedAt()));
		
		
		EmojiconTextView mTxtEmojicon = (EmojiconTextView) view.findViewById(R.id.txtEmojicon);
		if(review.getTags() != null){
			mTxtEmojicon.setText(review.getTags());
		}
		return view;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if(uListener != null){
			DishReview review = getItem(position);
			uListener.onDishReviewSelected(review.getObjectId(), review.getDishName());
			Toast.makeText(getContext(), "Review clicked", Toast.LENGTH_LONG).show();
		}else if(uListener != null){
			DishReview review = getItem(position);
			uListener.onDishReviewSelected(review.getObjectId(), review.getDishName());
			Toast.makeText(getContext(), "Review clicked", Toast.LENGTH_LONG).show();
		}
		
	}

	/*@Override
	public View getNextPageView(View view, ViewGroup parent) {
		if (view == null) {
			view = View.inflate(getContext(), R.layout.review_list_item, null);
		}
		TextView
		return view;
	}*/
}
