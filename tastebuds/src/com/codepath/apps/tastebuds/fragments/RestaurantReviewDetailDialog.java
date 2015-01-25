package com.codepath.apps.tastebuds.fragments;

import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;

import com.codepath.apps.tastebuds.R;
import com.codepath.apps.tastebuds.models.RestaurantReview;
import com.parse.ParseException;
import com.parse.ParseQuery;

public class RestaurantReviewDetailDialog extends DialogFragment {

	private TextView tvRestaurantName;
	private TextView tvTags;
	private TextView etTags;
	//private TextView etWords;
	private EditText etReview;
	private RatingBar rbRating;
	private Button btnTaste;
	private ImageButton btnCancel;
	private String restaurantName;
	public RestaurantReviewDetailDialogListener listener;

    public static RestaurantReviewDetailDialog newInstance(String reviewId, String restaurantName) {
    	RestaurantReviewDetailDialog dialog = new RestaurantReviewDetailDialog();
        Bundle args = new Bundle();
        args.putString("review_id", reviewId);
        args.putString("restaurant_name", restaurantName);
        dialog.setArguments(args);
        return dialog;
    }
    
    @Override
    public void onActivityCreated(Bundle arg0) {
        super.onActivityCreated(arg0);
        getDialog().getWindow()
        .getAttributes().windowAnimations = R.style.DialogAnimationBottom;
    }

    public interface RestaurantReviewDetailDialogListener {
    	void onFinishReviewComposeDialog(RestaurantReview review);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_review_compose, container);
        String reviewId = getArguments().getString("review_id");
        restaurantName = getArguments().getString("restaurant_name");
        tvRestaurantName = (TextView) view.findViewById(R.id.tvRestaurantNameCompose);
        tvRestaurantName.setText(restaurantName);
        tvTags = (TextView) view.findViewById(R.id.tvComposeTags);
        etTags = (EditText) view.findViewById(R.id.etComposeTags);
        etReview = (EditText) view.findViewById(R.id.etComposeReview);
        etReview.setEnabled(false);

        rbRating = (RatingBar) view.findViewById(R.id.rbComposeReviewRatings);
		LayerDrawable stars = (LayerDrawable) rbRating.getProgressDrawable();
		//stars.getDrawable(0).setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_ATOP);
		//stars.getDrawable(1).setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_ATOP);
		stars.getDrawable(2).setColorFilter(Color.argb(255, 255, 153, 51),
				PorterDuff.Mode.SRC_ATOP);

        ParseQuery<RestaurantReview> query = RestaurantReview.getQuery();
        RestaurantReview review;
		try {
			review = query.get(reviewId).fetchIfNeeded();
	        etReview.setText(review.getText());
	        etReview.setEnabled(false);
	        etTags.setText(review.getTags());
	        etTags.setEnabled(false);
	        rbRating.setRating(review.getRating());
	        rbRating.setEnabled(false);
		} catch (ParseException e) {
			e.printStackTrace();
		}

        btnCancel = (ImageButton) view.findViewById(R.id.btnComposeBack);


        btnTaste = (Button) view.findViewById(R.id.btnComposeReview);
        btnTaste.setVisibility(Button.GONE);
        //etWords = (TextView) view.findViewById(R.id.etWords);
        //etWords.setVisibility(Button.GONE);

        btnCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
			    listener.onFinishReviewComposeDialog(null);
				getDialog().dismiss();
			}
		});

    	Window window = getDialog().getWindow();
        window.requestFeature(Window.FEATURE_NO_TITLE);
        window.setGravity(Gravity.CENTER);
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        return view;
    }

    @Override
    public void onStart() {
    	super.onStart();
    	// safety check
    	if (getDialog() == null) {
    		return;
    	}
    	getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
    			ViewGroup.LayoutParams.WRAP_CONTENT);
    }
}
