package com.codepath.apps.tastebuds.adapters;

import java.text.SimpleDateFormat;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.codepath.apps.tastebuds.R;
import com.codepath.apps.tastebuds.models.DishReview;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;
import com.rockerhieu.emojicon.EmojiconTextView;
import com.squareup.picasso.Picasso;

public class DishReviewListAdapter extends ParseQueryAdapter<DishReview> {

	public DishReviewListAdapter(Context context, final String googlePlacesId,
			final List<ParseObject> friends) {
		super(context, new ParseQueryAdapter.QueryFactory<DishReview>() {
			public ParseQuery<DishReview> create() {
				ParseQuery<DishReview> query = DishReview.getQuery(googlePlacesId, friends);
				return query;
			}
		});
	}

	public DishReviewListAdapter(Context context, final String googlePlacesId,
			final List<ParseObject> friends, final String dishName) {
		super(context, new ParseQueryAdapter.QueryFactory<DishReview>() {
			public ParseQuery<DishReview> create() {
				ParseQuery<DishReview> query = DishReview.getQuery(googlePlacesId, friends,
						dishName);
				return query;
			}
		});
	}

	public DishReviewListAdapter(Context context,final ParseUser user) {
		super(context, new ParseQueryAdapter.QueryFactory<DishReview>() {
			public ParseQuery<DishReview> create() {
				ParseQuery<DishReview> query = DishReview.getQuery(user);
				return query;
			}
		});
	}

	@Override
	public View getItemView(DishReview review, View view, ViewGroup parent) {
		if (view == null) {
			view = View.inflate(getContext(), R.layout.review_list_item, null);
		}
		super.getItemView(review, view, parent);

		ImageView userImage = (ImageView) view.findViewById(R.id.ivReviewUser);
		TextView username = (TextView) view.findViewById(R.id.tvReviewUsername);
		TextView content = (TextView) view.findViewById(R.id.tvReviewContent);
		RatingBar rating = (RatingBar) view.findViewById(R.id.rbReviewRating);
		LayerDrawable stars = (LayerDrawable) rating.getProgressDrawable();
		stars.getDrawable(0).setColorFilter(Color.argb(255, 255, 153, 51), PorterDuff.Mode.SRC_ATOP);
		stars.getDrawable(1).setColorFilter(Color.argb(255, 255, 153, 51), PorterDuff.Mode.SRC_ATOP);
		stars.getDrawable(2).setColorFilter(Color.argb(255, 255, 153, 51),
				PorterDuff.Mode.SRC_ATOP);
		TextView time = (TextView) view.findViewById(R.id.tvReviewTime);
		EmojiconTextView mTxtEmojicon = (EmojiconTextView) view.findViewById(R.id.txtEmojicon);
		
		ParseUser reviewer;
		try {
			reviewer = review.getUser().fetchIfNeeded();
			username.setText(reviewer.getUsername());
			String profImgURL = reviewer.getString("profileImgURL");
			if(profImgURL != null){
				
				Picasso.with(getContext()).load(profImgURL).resize(70, 70).centerCrop().into(userImage);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}

		if(review.getTags() != null){
			mTxtEmojicon.setText(review.getTags());
		}
		content.setText(review.getText());
		rating.setRating(review.getRating());
		
		SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
		time.setText(df.format(review.getCreatedAt()));
		return view;
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
