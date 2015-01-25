package com.codepath.apps.tastebuds.adapters;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Random;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.apps.tastebuds.R;
import com.codepath.apps.tastebuds.fragments.RestaurantReviewListFragment.RestaurantReviewListListener;
import com.codepath.apps.tastebuds.fragments.UserRestaurantReviewsListFragment.UserRestaurantReviewListListener;
import com.codepath.apps.tastebuds.models.RestaurantReview;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;
import com.rockerhieu.emojicon.EmojiconTextView;

public class UserReviewListAdapter extends ParseQueryAdapter<RestaurantReview>
	implements OnItemClickListener {

	private RestaurantReviewListListener listener;
	private UserRestaurantReviewListListener uListener;
	public UserReviewListAdapter(Context context, final String googlePlacesId,
			final List<ParseObject> friends, RestaurantReviewListListener listener) {
		super(context, new ParseQueryAdapter.QueryFactory<RestaurantReview>() {
			public ParseQuery<RestaurantReview> create() {
				ParseQuery<RestaurantReview> query = RestaurantReview.getQuery(googlePlacesId, friends);
				return query;
			}
		});
		this.listener = listener;
	}

	public UserReviewListAdapter(Context context,final ParseUser user, UserRestaurantReviewListListener listener) {
		super(context, new ParseQueryAdapter.QueryFactory<RestaurantReview>() {
			public ParseQuery<RestaurantReview> create() {
				ParseQuery<RestaurantReview> query = RestaurantReview.getQuery(
						user);
				return query;
			}
			
		});
		this.uListener = listener;
	}
	@Override
	public View getItemView(RestaurantReview review, View view, ViewGroup parent) {
		if (view == null) {
			view = View.inflate(getContext(), R.layout.review_list_item, null);
		}
		super.getItemView(review, view, parent);

		ImageView userImage = (ImageView) view.findViewById(R.id.ivReviewUser);
		Random r = new Random();
		int position = r.nextInt(4);
		switch (position % 4) {
		case 0: userImage.setImageResource(R.drawable.res1); break;
		case 1: userImage.setImageResource(R.drawable.res2); break;
		case 2: userImage.setImageResource(R.drawable.res3); break;
		case 3: userImage.setImageResource(R.drawable.res4); break;
		default: userImage.setImageResource(R.drawable.res1);
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

		username.setText(review.getRestaurantName());
		content.setText(review.getText());
		rating.setRating(review.getRating());
		rating.setEnabled(false);
		SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
		time.setText(df.format(review.getCreatedAt()));
		
		EmojiconTextView mTxtEmojicon = (EmojiconTextView) view.findViewById(R.id.txtEmojicon);
		if(review.getTags() != null){
			mTxtEmojicon.setText(review.getTags());
		}
		return view;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if(listener != null){
			RestaurantReview review = getItem(position);
			listener.onReviewSelected(review.getObjectId(), review.getRestaurantName());
			Toast.makeText(getContext(), "Review clicked", Toast.LENGTH_LONG).show();
		}else if(uListener != null){
			RestaurantReview review = getItem(position);
			uListener.onReviewSelected(review.getObjectId(), review.getRestaurantName());
			Toast.makeText(getContext(), "Review clicked", Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public View getNextPageView(View view, ViewGroup parent) {
		if (view == null) {
			view = View.inflate(getContext(), R.layout.review_list_item, null);
		}
		return view;
	}
}
