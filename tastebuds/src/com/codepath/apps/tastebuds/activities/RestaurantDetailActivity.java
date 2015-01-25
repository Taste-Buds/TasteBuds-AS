package com.codepath.apps.tastebuds.activities;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.codepath.apps.tastebuds.GooglePlacesApiClient;
import com.codepath.apps.tastebuds.R;
import com.codepath.apps.tastebuds.fragments.DishListFragment;
import com.codepath.apps.tastebuds.fragments.DishListFragment.DishListListener;
import com.codepath.apps.tastebuds.fragments.DishReviewDialog;
import com.codepath.apps.tastebuds.fragments.DishReviewDialog.DishReviewDialogListener;
import com.codepath.apps.tastebuds.fragments.RestaurantDetailFragment;
import com.codepath.apps.tastebuds.fragments.RestaurantDetailFragment.RestaurantDetailListener;
import com.codepath.apps.tastebuds.fragments.RestaurantReviewDetailDialog;
import com.codepath.apps.tastebuds.fragments.RestaurantReviewDetailDialog.RestaurantReviewDetailDialogListener;
import com.codepath.apps.tastebuds.fragments.RestaurantReviewDialog;
import com.codepath.apps.tastebuds.fragments.RestaurantReviewDialog.RestaurantReviewDialogListener;
import com.codepath.apps.tastebuds.fragments.RestaurantReviewListFragment;
import com.codepath.apps.tastebuds.fragments.RestaurantReviewListFragment.RestaurantReviewListListener;
import com.codepath.apps.tastebuds.listeners.FragmentTabListener;
import com.codepath.apps.tastebuds.models.DishReview;
import com.codepath.apps.tastebuds.models.PlacesPhotoData;
import com.codepath.apps.tastebuds.models.Restaurant;
import com.codepath.apps.tastebuds.models.RestaurantReview;
import com.loopj.android.http.JsonHttpResponseHandler;

public class RestaurantDetailActivity extends FragmentActivity 
	implements RestaurantReviewListListener, DishListListener, RestaurantDetailListener {
	
	private String placeId;
	private PlacesPhotoData photoData;
	private float rating;
	private Restaurant restaurant;
	private	GooglePlacesApiClient placesApi = new GooglePlacesApiClient();		

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_restaurant_detail);
		placeId = getIntent().getStringExtra("place_id");
		photoData = getIntent().getParcelableExtra("photo_data");
		rating = getIntent().getFloatExtra("rating", 0);
		restaurantDetailFromGooglePlacesApi();	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//getMenuInflater().inflate(R.menu.review, menu);
		return true;
	}

	private void restaurantDetailFromGooglePlacesApi() {
		placesApi.getRestaurantDetailfromGooglePlaces(placeId, 
				new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(JSONObject response) {
        		JSONObject restaurantDetailJson = null;
        		try {
        			restaurantDetailJson = response.getJSONObject("result");
        			restaurant = Restaurant.fromJSONDetail(restaurantDetailJson);
        		    restaurant.setFriendRating(rating);
	       			setupTabs();
				} catch (JSONException e) {
					e.printStackTrace();
				}
        	}
			@Override
    		public void onFailure(Throwable e, JSONObject errorResponse) {
				Log.e("Error", e.toString());
    		}		
		});
	}

	private void setupTabs() {
		ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.setDisplayShowTitleEnabled(true);

		Bundle args = new Bundle();
		args.putString("placeId", placeId);
		args.putParcelable("photo_data", photoData);
		if (restaurant.getName() != null) {
			getActionBar().setTitle(restaurant.getName());
			args.putString("restaurantName", restaurant.getName());
		} else {
			args.putString("restaurantName", "ABCD");
		}
		args.putParcelable("restaurant", restaurant);
		Tab tab1 = actionBar
			.newTab()
			.setText("Details")
			.setTag("RestaurantDetailFragment")
			.setTabListener(
				new FragmentTabListener<RestaurantDetailFragment>(R.id.ctRestaurantsLists, this, "first",
								RestaurantDetailFragment.class, args));

		actionBar.addTab(tab1);
		actionBar.selectTab(tab1);
		
		Tab tab2 = actionBar
				.newTab()
				.setText("Reviews")
				.setTag("RestaurantReviewListFragment")
				.setTabListener(
					new FragmentTabListener<RestaurantReviewListFragment>(R.id.ctRestaurantsLists, this, "second",
									RestaurantReviewListFragment.class, args));
		actionBar.addTab(tab2);
		
		Tab tab3 = actionBar
			.newTab()
			.setText("Dishes")
			.setTag("DishListFragment")
			.setTabListener(
			    new FragmentTabListener<DishListFragment>(R.id.ctRestaurantsLists, this, "third",
								DishListFragment.class, args));

		actionBar.addTab(tab3);


	}


	public void onReviewComposeAction(MenuItem mi) {
	    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
	    Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
	    if (prev != null) {
	        ft.remove(prev);
	    }
	    ft.addToBackStack(null);
	    if (getActionBar().getSelectedTab().getTag() != "DishListFragment") {
			RestaurantReviewDialog dialog = RestaurantReviewDialog.newInstance(
				restaurant.getPlace_id(), restaurant.getName());
			dialog.show(ft, "compose");
			dialog.listener = new RestaurantReviewDialogListener() {
				@Override
				public void onFinishReviewComposeDialog(RestaurantReview review) {
					if (review != null) {
						review.saveInBackground();
					}
				}
			};
	    } else {
			DishReviewDialog dialog = DishReviewDialog.newInstance(
					restaurant.getName(), restaurant.getPlace_id());
			dialog.show(ft, "compose");
			dialog.listener = new DishReviewDialogListener() {
				@Override
				public void onFinishReviewComposeDialog(DishReview review) {
					if (review != null) {
						review.saveInBackground();
					}
				}
			};
	    }
	}

	@Override
	public void onReviewSelected(String reviewId, String restaurantName) {
	    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
	    Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
	    if (prev != null) {
	        ft.remove(prev);
	    }
	    ft.addToBackStack(null);
		RestaurantReviewDetailDialog dialog = 
				RestaurantReviewDetailDialog.newInstance(reviewId, restaurant.getName());
		
		dialog.show(getFragmentManager(), "detail");
		dialog.listener = new RestaurantReviewDetailDialogListener() {
			@Override
			public void onFinishReviewComposeDialog(RestaurantReview review) {}
		};
	}

	@Override
	public void onDishSelected(String googlePlacesId, String dishName) {
		Intent i = new Intent(RestaurantDetailActivity.this, DishDetailActivity.class);
		i.putExtra("placesId", placeId);
		i.putExtra("restaurant_name", restaurant.getName());
		i.putExtra("dish_name", dishName);
		startActivity(i);
		overridePendingTransition(R.anim.right_in, R.anim.left_out);
	}

    @Override
    public void onBackPressed() {
		finish();
		overridePendingTransition(R.anim.left_in, R.anim.right_out);
    }

	@Override
	public void onCallRestaurant(String phoneNumber) {
		try {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:"+ phoneNumber));
            startActivity(callIntent);
        } catch (ActivityNotFoundException activityException) {
             Log.e("Calling a Phone Number", "Call failed", activityException);
        }
	}

	@Override
	public void onDirections(String address) {
		startActivity(new Intent(android.content.Intent.ACTION_VIEW,
				Uri.parse("google.navigation:q="+ restaurant.getAddress())));
	}

	@Override
	public void onAddReview(Restaurant restaurant) {
	    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
	    Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
	    if (prev != null) {
	        ft.remove(prev);
	    }
	    ft.addToBackStack(null);
		RestaurantReviewDialog dialog = RestaurantReviewDialog.newInstance(
			restaurant.getPlace_id(), restaurant.getName());
		dialog.show(ft, "compose");
		dialog.listener = new RestaurantReviewDialogListener() {
			@Override
			public void onFinishReviewComposeDialog(RestaurantReview review) {
				if (review != null) {
					review.saveInBackground();
				}
			}
		};
	}

	@Override
	public void onAddDish(Restaurant restaurant) {
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
	    Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
	    if (prev != null) {
	        ft.remove(prev);
	    }
	    ft.addToBackStack(null);
		DishReviewDialog dialog = DishReviewDialog.newInstance(
				restaurant.getName(), restaurant.getPlace_id());
		dialog.show(ft, "compose");
		dialog.listener = new DishReviewDialogListener() {
			@Override
			public void onFinishReviewComposeDialog(DishReview review) {
				if (review != null) {
					review.saveInBackground();
				}
			}
		};
		
	}
}
