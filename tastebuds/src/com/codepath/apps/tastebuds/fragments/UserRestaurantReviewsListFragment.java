package com.codepath.apps.tastebuds.fragments;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.codepath.apps.tastebuds.GooglePlacesApiClient;
import com.codepath.apps.tastebuds.R;
import com.codepath.apps.tastebuds.adapters.FriendsListAdapter;
import com.codepath.apps.tastebuds.adapters.UserReviewListAdapter;
import com.codepath.apps.tastebuds.models.PlacesPhotoData;
import com.codepath.apps.tastebuds.models.Restaurant;
import com.codepath.apps.tastebuds.models.RestaurantReview;
import com.codepath.apps.tastebuds.models.Tag;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class UserRestaurantReviewsListFragment extends Fragment {

	private UserReviewListAdapter adapter;
	private UserRestaurantReviewListListener listener;
	private ListView lvUserReviews;
	private String userId;
	private static ParseUser user;
	private ProgressBar mProgress;
	private String searchType;
	private String searchTerm;
	private ArrayList<String> tags;
	private List<Object> friends;

    public interface UserRestaurantReviewListListener {
    	void onReviewSelected(String reviewId, String restaurantName);
    }

	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userId = getArguments().getString("user_id");
        user = ParseUser.getCurrentUser();
        //searchType = getArguments().getString("searchType");
        //searchTerm = getArguments().getString("searchType");
        //List<String> tags = Arrays.asList(searchTerm.split("\\s+"));
        
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_user_restaurant_review_list, container, false);
		lvUserReviews = (ListView) view.findViewById(R.id.lvUserReviews);
		mProgress = (ProgressBar) view.findViewById(R.id.pbReviewList);
		mProgress.setVisibility(ProgressBar.VISIBLE);
		/*
		ParseQuery<Tag> tagQuery = Tag.getQuery(tags);		
	    if (searchTerm.equals("Tags")) {
	        tagQuery.findInBackground(new FindCallback<Tag>() {
				@Override
				public void done(List<Tag> tags, ParseException arg1) {
						ArrayList<String> placeIds = new ArrayList<String>();
						for(int i=0; i<tags.size(); i++) {
							Tag tag = tags.get(i);
							String placeId = tag.getGooglePlacesId();
							placeIds.add(placeId);
						}
						friends = ParseUser.getCurrentUser().getList("userFriends");
						ArrayList<ParseObject> friendList = new ArrayList<ParseObject>();
						for(int i=0; i<friends.size(); i++) {
							ParseObject friend = (ParseObject) friends.get(i);
							friendList.add(friend);
						}
						
						ParseQuery<RestaurantReview> reviewQuery = RestaurantReview.getQuery(placeIds,
								friendList);						
						reviewQuery.findInBackground(new FindCallback<RestaurantReview>() {
							@Override
							
							public void done(List<RestaurantReview> reviews, ParseException arg1) {
							
							//adapter = new UserReviewListAdapter(getActivity(), reviews, listener);
							lvUserReviews.setAdapter(adapter);
							adapter.notifyDataSetChanged();
							lvUserReviews.setOnItemClickListener(adapter);
							mProgress.setVisibility(ProgressBar.GONE);
							
							}							
						}
						
						
				}

			});
	    }
	    */		
	    ParseQuery<ParseUser> userQuery = ParseUser.getQuery().whereEqualTo("fbId", userId);
	    //if (searchTerm.equals("User")) {
	        userQuery.findInBackground(new FindCallback<ParseUser>() {

				@Override
				public void done(List<ParseUser> users, ParseException arg1) {
						adapter = new UserReviewListAdapter(getActivity(),  users.get(0), listener);
						lvUserReviews.setAdapter(adapter);
						adapter.notifyDataSetChanged();
						lvUserReviews.setOnItemClickListener(adapter);
						mProgress.setVisibility(ProgressBar.GONE);
				}

			});
	    //}


		return view;
	}
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (activity instanceof UserRestaurantReviewListListener) {
			listener = (UserRestaurantReviewListListener) activity;
		} else {
			throw new ClassCastException(activity.toString()
					+ " must implement RestaurantReviewDialog.UserRestaurantReviewListListener");
		}
	}
}
