package com.codepath.apps.tastebuds.activities;

import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ActionBar.Tab;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;


import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import com.codepath.apps.tastebuds.R;
import com.codepath.apps.tastebuds.adapters.ReviewListAdapter;
import com.codepath.apps.tastebuds.fragments.DishReviewDetailDialog;
import com.codepath.apps.tastebuds.fragments.DishReviewDetailDialog.DishReviewDetailDialogListener;
import com.codepath.apps.tastebuds.fragments.RestaurantReviewDetailDialog;
import com.codepath.apps.tastebuds.fragments.UserDishReviewsListFragment;
import com.codepath.apps.tastebuds.fragments.UserDishReviewsListFragment.UserDishReviewListListener;
import com.codepath.apps.tastebuds.fragments.UserRestaurantReviewsListFragment;
import com.codepath.apps.tastebuds.fragments.RestaurantReviewDetailDialog.RestaurantReviewDetailDialogListener;
import com.codepath.apps.tastebuds.fragments.RestaurantReviewListFragment.RestaurantReviewListListener;
import com.codepath.apps.tastebuds.fragments.UserRestaurantReviewsListFragment.UserRestaurantReviewListListener;
import com.codepath.apps.tastebuds.listeners.FragmentTabListener;
import com.codepath.apps.tastebuds.models.DishReview;
import com.codepath.apps.tastebuds.models.Restaurant;
import com.codepath.apps.tastebuds.models.RestaurantReview;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

public class UserProfileActivity extends SherlockFragmentActivity implements UserRestaurantReviewListListener ,UserDishReviewListListener {
	private ImageView barImage;
	private String profImgURL;
	private ParseUser user;
	private String userId;
	private String searchType;
	ArrayList<Restaurant> restaurants = new ArrayList<Restaurant>();
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		MenuItem actionViewItem = (MenuItem) menu.findItem(R.id.itemImg);
		View v = actionViewItem.getActionView();
		barImage = (ImageView) v.findViewById(R.id.ibButton);
		
		if(user == null){
			setUserInfo();
		}
		// Handle button click here
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.profile, menu);
//		View v = actionViewItem.getActionView();
//		ImageButton b = (ImageButton) v.findViewById(R.id.itemImg);
		// Handle button click here
		return super.onCreateOptionsMenu(menu);
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_user_profile);
		//searchType = getIntent().getStringExtra("SearchType");
		userId = getIntent().getStringExtra("user_id");
		if(userId == null){
			user = ParseUser.getCurrentUser();
			userId = (String) user.get("fbId");
		}
		setupTabs();
		getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM); 
		//getSupportActionBar().setCustomView(R.layout.action_image_button);
		getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME); 
		getActionBar().setDisplayShowHomeEnabled(true);
		getActionBar().setDisplayShowTitleEnabled(true);
		
		
		
	}
 private void setUserInfo(){
	 ParseQuery<ParseUser> userQuery = ParseUser.getQuery().whereEqualTo("fbId", userId);
     userQuery.findInBackground(new FindCallback<ParseUser>() {

			@Override
			public void done(List<ParseUser> users, ParseException arg1) {
				
				profImgURL = users.get(0).getString("profileImgURL");
				if(profImgURL != null){
					
					Picasso.with(getApplicationContext()).load(profImgURL).resize(70, 70).centerCrop().into(barImage);
				}
				setTitle(users.get(0).getUsername());
				
			}
     });
 }
	private void setupTabs() {
		ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.setDisplayShowTitleEnabled(true);
		Bundle args = new Bundle();
		//args.putString("searchType", searchType);
		args.putString("user_id", userId);
		Tab tab1 = actionBar
			.newTab()
			.setText("Restaurants")
			.setIcon(R.drawable.ic_restaurant)
			.setTag("UserRestaurantReviewsListFragment")
			.setTabListener(
				new FragmentTabListener<UserRestaurantReviewsListFragment>(R.id.flProfile, this, "first",
						UserRestaurantReviewsListFragment.class, args));

		actionBar.addTab(tab1);
		actionBar.selectTab(tab1);

		Tab tab2 = actionBar
			.newTab()
			.setText("Dishes")
			.setIcon(R.drawable.ic_dish_review)
			.setTag("UserDishReviewsListFragment")
			.setTabListener(
			    new FragmentTabListener<UserDishReviewsListFragment>(R.id.flProfile, this, "second",
			    		UserDishReviewsListFragment.class, args));

		actionBar.addTab(tab2);
	}
	@Override
	public void onReviewSelected(String reviewId, String restaurantName) {
	    FragmentTransaction ft = getFragmentManager().beginTransaction();
	    Fragment prev = getFragmentManager().findFragmentByTag("dialog");
	    if (prev != null) {
	        ft.remove(prev);
	    }
	    ft.addToBackStack(null);
		RestaurantReviewDetailDialog dialog = RestaurantReviewDetailDialog.newInstance(reviewId,
				restaurantName);
		dialog.show(ft, "detail");
		dialog.listener = new RestaurantReviewDetailDialogListener() {
			@Override
			public void onFinishReviewComposeDialog(RestaurantReview review) {}
		};
	}
//	public void onReview(View view) {
//	    FragmentTransaction ft = getFragmentManager().beginTransaction();
//	    Fragment prev = getFragmentManager().findFragmentByTag("dialog");
//	    if (prev != null) {
//	        ft.remove(prev);
//	    }
//	    ft.addToBackStack(null);
//		RestaurantReviewDialog dialog = RestaurantReviewDialog.newInstance(
//				"Shree Datta", 12345678);
//		dialog.show(ft, "compose");
//		dialog.listener = new RestaurantReviewDialogListener() {
//			@Override
//			public void onFinishReviewComposeDialog(RestaurantReview review) {
//				review.setUser(user);
//				review.saveInBackground(new SaveCallback() {
//					@Override
//					public void done(ParseException arg0) {
//						if (arg0 == null) {
//							Toast.makeText(UserProfileActivity.this, "Saved Review", Toast.LENGTH_LONG).show();
//						} else {
//							Toast.makeText(UserProfileActivity.this, "Saved Review FAiled" + arg0.toString(), Toast.LENGTH_LONG).show();
//						}
//					}
//					
//					
//				});
//			}
//		};
//	}

	@Override
	public void onDishReviewSelected(String reviewId, String dishName) {
		 FragmentTransaction ft = getFragmentManager().beginTransaction();
		    Fragment prev = getFragmentManager().findFragmentByTag("dialog");
		    if (prev != null) {
		        ft.remove(prev);
		    }
		    ft.addToBackStack(null);
		    DishReviewDetailDialog dialog = DishReviewDetailDialog.newInstance(reviewId,
					dishName);
			dialog.show(ft, "detail");
			dialog.listener = new DishReviewDetailDialogListener() {

				@Override
				public void onFinishReviewDialog(DishReview review) {
					// TODO Auto-generated method stub
					
				}
			};
		
	}

    @Override
    public void onBackPressed() {
	finish();
	overridePendingTransition(R.anim.left_in, R.anim.right_out);
    }
}
