package com.codepath.apps.tastebuds.activities;

import java.util.ArrayList;
import java.util.List;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuInflater;
import com.codepath.apps.tastebuds.R;
import com.codepath.apps.tastebuds.R.anim;
import com.codepath.apps.tastebuds.R.drawable;
import com.codepath.apps.tastebuds.R.id;
import com.codepath.apps.tastebuds.R.layout;
import com.codepath.apps.tastebuds.fragments.DishReviewDetailDialog;
import com.codepath.apps.tastebuds.fragments.RestaurantReviewDetailDialog;
import com.codepath.apps.tastebuds.fragments.UserDishReviewsListFragment;
import com.codepath.apps.tastebuds.fragments.UserRestaurantReviewsListFragment;
import com.codepath.apps.tastebuds.fragments.DishReviewDetailDialog.DishReviewDetailDialogListener;
import com.codepath.apps.tastebuds.fragments.RestaurantReviewDetailDialog.RestaurantReviewDetailDialogListener;
import com.codepath.apps.tastebuds.fragments.UserDishReviewsListFragment.UserDishReviewListListener;
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

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ActionBar.Tab;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

//public class SearchTagActivity extends SherlockFragmentActivity implements UserRestaurantReviewListListener ,UserDishReviewListListener {
public class SearchActivity extends SherlockFragmentActivity {


	private String searchTerm;
	private String searchType;
	
	ArrayList<Restaurant> restaurants = new ArrayList<Restaurant>();

	/*
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		//inflater.inflate(R.menu.profile, menu);

		return super.onCreateOptionsMenu(menu);
	}
	*/

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_user_profile);
		searchTerm = getIntent().getStringExtra("SearchTerm");
		searchType = getIntent().getStringExtra("SearchType");
		setupTabs();
		getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM); 
		//getSupportActionBar().setCustomView(R.layout.action_image_button);
		getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME); 
		getActionBar().setDisplayShowHomeEnabled(true);
		getActionBar().setDisplayShowTitleEnabled(true);
		
		
	}

	private void setupTabs() {
		ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.setDisplayShowTitleEnabled(true);
		Bundle args = new Bundle();
		if (searchType.equals("Tags")) {
			args.putString("searchTem", searchTerm);
		}
		Tab tab1 = actionBar
			.newTab()
			.setText("User Restaurant Reviews")
			.setIcon(R.drawable.ic_launcher)
			.setTag("UserRestaurantReviewsListFragment")
			.setTabListener(
				new FragmentTabListener<UserRestaurantReviewsListFragment>(R.id.flProfile, this, "first",
						UserRestaurantReviewsListFragment.class, args));

		actionBar.addTab(tab1);
		actionBar.selectTab(tab1);

		Tab tab2 = actionBar
			.newTab()
			.setText("User Dish Reviews")
			.setIcon(R.drawable.ic_launcher)
			.setTag("UserDishReviewsListFragment")
			.setTabListener(
			    new FragmentTabListener<UserDishReviewsListFragment>(R.id.flProfile, this, "second",
			    		UserDishReviewsListFragment.class, args));

		actionBar.addTab(tab2);
	}
	/*
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
	*/
    @Override
    public void onBackPressed() {
	finish();
	overridePendingTransition(R.anim.left_in, R.anim.right_out);
    }
}
