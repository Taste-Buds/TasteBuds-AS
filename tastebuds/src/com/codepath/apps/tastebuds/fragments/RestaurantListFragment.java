package com.codepath.apps.tastebuds.fragments;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnActionExpandListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.codepath.apps.tastebuds.GooglePlacesApiClient;
import com.codepath.apps.tastebuds.R;
import com.codepath.apps.tastebuds.adapters.RestaurantAdapter;
import com.codepath.apps.tastebuds.listeners.EndlessScrollListener;
import com.codepath.apps.tastebuds.models.PlacesPhotoData;
import com.codepath.apps.tastebuds.models.Restaurant;
import com.codepath.apps.tastebuds.models.RestaurantReview;
import com.codepath.apps.tastebuds.models.Tag;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.rockerhieu.emojicon.EmojiconEditText;
import com.rockerhieu.emojicon.EmojiconGridFragment.OnEmojiconClickedListener;
import com.rockerhieu.emojicon.EmojiconTextView;
import com.rockerhieu.emojicon.EmojiconsFragment;
import com.rockerhieu.emojicon.emoji.Emojicon;


public class RestaurantListFragment extends Fragment implements OnEmojiconClickedListener, EmojiconsFragment.OnEmojiconBackspaceClickedListener {

	List<Restaurant> restaurants;
	List<String> placeIds;
	List<String> newPlaceIds;
	ArrayAdapter<Restaurant> restaurantAdapter;
	ListView lvRestaurants;
	List<ParseObject> friends;
	//List<RestaurantReview> reviews;
	Location mCurrentLocation;
	int parsingPageToken;
	String nextPageToken;
	SearchView searchView;
	ImageView searchImg;
	EmojiconsFragment nf;
	private List<Restaurant> newRestaurants;
	private GooglePlacesApiClient placesApi;
	private RestaurantListListener listener;
	private String search;

	EmojiconEditText mEditEmojicon;
	EmojiconTextView mTxtEmojicon;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mCurrentLocation = getArguments().getParcelable("mCurrentLocation");
		restaurants = new ArrayList<Restaurant>();
		restaurantAdapter = new RestaurantAdapter(getActivity(), restaurants);
		placeIds = new ArrayList<String>();
		newPlaceIds = new ArrayList<String>();
		friendsFromFacebook();
		parsingPageToken = 0;
		nextPageToken = "None";
		search = "None";
		restaurantsFromGooglePlacesApi(search, nextPageToken);
		setHasOptionsMenu(true);
		setEmojiconFragment(false);
	}

	public interface RestaurantListListener {
		void onRestaurantSelected(String place_id, float rating,
				PlacesPhotoData photoData);
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.fragment_restaurant_list, container, false);


		lvRestaurants = (ListView) v.findViewById(R.id.lvRestaurants);
		lvRestaurants.setAdapter(restaurantAdapter);
		lvRestaurants.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView parentView, View childView, int position, long id) {
				listener.onRestaurantSelected(restaurants.get(position).getPlace_id(),
						restaurants.get(position).getFriendRating(),
						restaurants.get(position).getDisplayPhotoReference());
			} 
		});		
		lvRestaurants.setOnScrollListener(new EndlessScrollListener() {
			@Override
			public void onLoadMore(int page, int totalItemsCount) {
				// Triggered only when new data needs to be appended to the list
				// Add whatever code is needed to append new items to your AdapterView
				restaurantsFromGooglePlacesApi(search, nextPageToken);
				restaurantAdapter.notifyDataSetChanged();
			}
		});        
		return v;
	}

	private void friendsFromFacebook() {
		// Get array of friends from Facebook API
		friends = ParseUser.getCurrentUser().getList("userFriends");
		friends.add(ParseUser.getCurrentUser());
	}

	private void restaurantsFromGooglePlacesApi(String search, String nextPageToken) {

		placesApi = new GooglePlacesApiClient();
		// 700 Illinois Street, SF = 37.764046, -122.387863
		//double latitude = 37.764046; // Static for 700 Illinois
		//double longitude = -122.387863; // Static for 700 Illinois

		double latitude = 37.770525; // mCurrentLocation.getLatitude();
		double longitude = -122.4043456; //mCurrentLocation.getLongitude();

		placesApi.getRestaurantListfromGooglePlaces(search, nextPageToken, latitude, longitude,
				new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(JSONObject response) {
				JSONArray placesApiResultsJson = null;
				newRestaurants = new ArrayList<Restaurant>();
				try {
					placesApiResultsJson = response.getJSONArray("results");
					String newNextPageToken = "";
					try {
						newNextPageToken = response.getString("next_page_token");
					} catch (Exception e) {
						newNextPageToken = "QueryLimitReached";
					}
					updateNextPageToken(newNextPageToken);
					newRestaurants.addAll(Restaurant.fromJSONArray(placesApiResultsJson));

					newPlaceIds.clear();
					for(int i=0; i<newRestaurants.size(); i++) {
						Restaurant restaurant = newRestaurants.get(i);
						String placeId = restaurant.getPlace_id();
						newPlaceIds.add(placeId);
						calcuateDistancetoUser(restaurant);
						restaurantTags(placeId , i);
					}
					placeIds.addAll(newPlaceIds);
					restaurants.addAll(newRestaurants);
					//Log.d("Debug", "Added more Restaurants");
					restaurantReviewsWithGoogleAndFacebookData(newRestaurants);

				} catch (JSONException e) {
					e.printStackTrace();
				}
				restaurantAdapter.notifyDataSetChanged();
            	for (Restaurant rest : newRestaurants) {
            		if (rest.getDisplayPhotoReference() != null) {
            			PhotosAsyncTask photostask = new PhotosAsyncTask();
            			photostask.execute(rest);
            		}
            	}
			}
			@Override
			public void onFailure(Throwable e, JSONObject errorResponse) {
				Log.e("Error", e.toString());
			}

		});	
	}

	private void calcuateDistancetoUser(Restaurant restaurant) {
		Location locationRestaurant = new Location("");
		try {locationRestaurant = restaurant.getLocation();}
		catch (Exception e) { Log.d("Debug", "No Rest Location"); }
		if (mCurrentLocation == null) {
			Log.d("Debug", "No User Location");
		}
		if (restaurant.getLocation() == null) {
			Log.d("Debug", "No Restaurant Location");
		}
		float distance = 0;
		try { distance = mCurrentLocation.distanceTo(locationRestaurant); }
		catch (Exception e){ //Log.d("Debug", "Can't get Location B"); 

		}
		restaurant.setCurrentDistancetoUser(distance);
	}

	private void updateNextPageToken (String newNextPageToken) {
		setNextPageToken(newNextPageToken);
	}


	public String getNextPageToken() {
		return nextPageToken;
	}

	public void setNextPageToken(String nextPageToken) {
		this.nextPageToken = nextPageToken;
	}

	private void restaurantReviewsWithGoogleAndFacebookData(List<Restaurant> newRestaurants) {

		
		ParseQuery<RestaurantReview> query = RestaurantReview.getQuery(newPlaceIds, friends);
		query.findInBackground(new FindCallback<RestaurantReview>() {
			@Override
			public void done(List<RestaurantReview> results, ParseException e) {
				// results has the list of user reviews from Parse
				List<RestaurantReview> newReviews = new ArrayList<RestaurantReview>();
				newReviews = results;
				parseReviews(newReviews);
			}
		});
		
	}

	private void restaurantTags(String googlePlacesId, final int restaurantPos) {

		ParseQuery<Tag> query = Tag.getQuery(googlePlacesId);
		query.findInBackground(new FindCallback<Tag>() {
			@Override
			public void done(List<Tag> results, ParseException e) {
				// results has the list of user reviews from Parse
				String tagString ="";
				for(int i=0;i<results.size();i++){
					tagString += results.get(i).getTag() +" ";
				}
				storeTagString(restaurantPos, tagString);
			}
		});
		
	}
	
	private void storeTagString(int pos, String tagString){

	//	int restaurantArrayPosition = (parsingPageToken*20)+pos;
		Restaurant restaurant = newRestaurants.get(pos);
		restaurant.setTagString(tagString);
		restaurantAdapter.notifyDataSetChanged();
	}
	private void parseReviews(List<RestaurantReview> newReviews) {
		for(int i=0; i<newPlaceIds.size();i++) {
			String placeId = newPlaceIds.get(i);
			int restaurantArrayPosition = (parsingPageToken*20)+i;
			Restaurant restaurant = restaurants.get(restaurantArrayPosition);
			int numberOfReviews = 0;
			int sumOfRatings = 0;
			for(int j=0; j<newReviews.size();j++) {
				RestaurantReview review = newReviews.get(j);
				String placeIdReview = review.getGooglePlacesId();
				if (placeId.equals(placeIdReview)) {
					int rating = review.getRating();
					numberOfReviews++;
					sumOfRatings = sumOfRatings + rating;
				}
			}
			float friendRating = 0;
			if (numberOfReviews > 0) {
				friendRating = sumOfRatings/numberOfReviews;
			}
			else {
				friendRating = 0;
				numberOfReviews = 0;
			}
			restaurant.setFriendRating(friendRating);
			restaurant.setNumOfReviews(numberOfReviews);		
		}
		parsingPageToken++;

		restaurantAdapter.notifyDataSetChanged();
	}

	/*public void showRestaurantDetail(int position) {
		//Log.d("Debug", "P: " + position);
		Intent i = new Intent(getActivity(), RestaurantDetailActivity.class);
		i.putExtra("place_id", restaurants.get(position).getPlace_id());
		startActivity(i);
	}*/

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		
		if (menu.size() == 0){
			inflater.inflate(R.menu.search, menu);
		}
		super.onCreateOptionsMenu(menu, inflater);
		final MenuItem searchItem = menu.findItem(R.id.action_search);
		/** Get the action view of the menu item whose id is search */
		View v = (View) searchItem.getActionView();
		//setEmojiconFragment(false);
		/** Get the edit text from the action view */
		mEditEmojicon = ( EmojiconEditText ) v.findViewById(R.id.txt_search);
		searchImg = (ImageView) v.findViewById(R.id.searchImg);

		searchImg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				restaurantAdapter.clear();
				placeIds.clear();
				newPlaceIds.clear();
				parsingPageToken = 0;
				String search = mEditEmojicon.getText().toString();
				String nextPageToken = "None";
				restaurantsFromGooglePlacesApi(search, nextPageToken);
				// Dismiss Keyboard
				hideSoftKeyBoard();	   
				searchItem.collapseActionView();

			}
		});
		searchItem.setOnActionExpandListener(new OnActionExpandListener() {
			@Override
			public boolean onMenuItemActionExpand(MenuItem item) {
				Log.d("Debug","onMenuItemActionExpand");
				
				mEditEmojicon.requestFocus();
				FragmentTransaction ft = getChildFragmentManager().beginTransaction();
				ft.show(nf);
				ft.commit();
				showSoftKeyBoard();
				return true;

			}

			@Override
			public boolean onMenuItemActionCollapse(MenuItem item) {
				Log.d("Debug","onMenuItemActionCollapse");
				FragmentTransaction ft = getChildFragmentManager().beginTransaction();
				ft.hide(nf);
				ft.commit();
				restaurants.clear();
				placeIds.clear();
				newPlaceIds.clear();
				parsingPageToken = 0;
				//String search = "None";
				//String nextPageToken = "None";
				//restaurantsFromGooglePlacesApi(search, nextPageToken);
				return true;
			}
		});
		//setEmojiconFragment(false);
		//    mEditEmojicon = (EmojiconEditText) searchItem.getActionView();

		
		//    
		mEditEmojicon.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				FragmentTransaction ft = getChildFragmentManager().beginTransaction();
				ft.show(nf);
				ft.commit();
			
			}
		});
	
		mEditEmojicon.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEARCH) {
					restaurants.clear();
					placeIds.clear();
					newPlaceIds.clear();
					parsingPageToken = 0;
					String search = v.getText().toString();
					String nextPageToken = "None";
					restaurantsFromGooglePlacesApi(search, nextPageToken);
					// Dismiss Keyboard
					hideSoftKeyBoard();	    	   
					return true;
				}
				return false;
			}
		});

	}

//	private void hideSoftKeyBoard() {
//		Context context = getActivity().getBaseContext();
//		InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
//		if(imm.isAcceptingText()) {                      
//			imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
//		}
//	}
//	
//	private void showSoftKeyBoard() {
//		Context context = getActivity().getBaseContext();
//		InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
//		if(imm != null) {     
//			imm.showSoftInput(mEditEmojicon, InputMethodManager.SHOW_IMPLICIT);
//		}
//	}
	private void hideSoftKeyBoard() {
		Context context = getActivity().getBaseContext();
		InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
	//	if(imm.isAcceptingText()) {      
		imm.hideSoftInputFromWindow(mEditEmojicon.getWindowToken(), 0);
			//imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
	//	}
		
	}
	
	private void showSoftKeyBoard() {
		Context context = getActivity().getBaseContext();
		InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		if(imm != null) {     
			imm.showSoftInput(mEditEmojicon, InputMethodManager.SHOW_IMPLICIT);
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (activity instanceof RestaurantListListener) {
			listener = (RestaurantListListener) activity;
		} else {
			throw new ClassCastException(activity.toString()
					+ " must implement RestaurantReviewDialog.RestaurantReviewListListener");
		}
	}


	private void setEmojiconFragment(boolean useSystemDefault) {
		FragmentTransaction ft = getChildFragmentManager().beginTransaction();
		nf = (EmojiconsFragment) EmojiconsFragment.newInstance(useSystemDefault);
		ft.add(R.id.emojicons, nf,"main");
		ft.hide(nf);
		ft.commit();
	}

	@Override
	public void onEmojiconClicked(Emojicon emojicon) {
		EmojiconsFragment.input(mEditEmojicon, emojicon);
	}



	@Override
	public void onEmojiconBackspaceClicked(View v) {
		EmojiconsFragment.backspace(mEditEmojicon);
	}

	//The types specified here are the input data type, the progress type, and the result type
	private class PhotosAsyncTask extends AsyncTask<Restaurant, Void, Bitmap> {

	  private Restaurant restaurant;
	  protected void onPreExecute() {}
	  
		@Override
		protected Bitmap doInBackground(Restaurant... params) {
			restaurant = params[0];
			int maxWidth = restaurant.getDisplayPhotoReference().width == 0 ? 300 :
				restaurant.getDisplayPhotoReference().width;
			maxWidth = maxWidth > 300 ? 300 : maxWidth;
			Bitmap photo = placesApi.getRestaurantDisplayPhoto(
					restaurant.getDisplayPhotoReference().reference, maxWidth);
			return photo;
		}


	  @SuppressLint("NewApi")
	  protected void onPostExecute(Bitmap result) {
	      // This method is executed in the UIThread
	      // with access to the result of the long running task
		  if (result != null) {
				restaurant.setDisplayPhoto(result);
				restaurantAdapter.notifyDataSetChanged();
		  }
	  }
	}
}