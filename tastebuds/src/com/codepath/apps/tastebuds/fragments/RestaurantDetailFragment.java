package com.codepath.apps.tastebuds.fragments;


import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.codepath.apps.tastebuds.GooglePlacesApiClient;
import com.codepath.apps.tastebuds.R;
import com.codepath.apps.tastebuds.adapters.GridImageAdapter;
import com.codepath.apps.tastebuds.fragments.UserRestaurantReviewsListFragment.UserRestaurantReviewListListener;
import com.codepath.apps.tastebuds.models.PlacesPhotoData;
import com.codepath.apps.tastebuds.models.Restaurant;
import com.codepath.apps.tastebuds.ui.CircleButton;

public class RestaurantDetailFragment extends Fragment {

	private String placeId;
	private RatingBar rbRating;
	private TextView tvFriendsRating;
	private TextView tvPrice;
	private TextView tvPhone;
	private TextView tvHours;
	private TextView tvOpenNow;
	private TextView tvAddress;
	private TextView tvStatus;
	private TextView tvWebsite;
	private TextView tvName;
	private ListView lvPhotos;
	private RelativeLayout rlDetail;
	private CircleButton btnCall;
	private CircleButton btnReview;
	private CircleButton btnDish;
	private CircleButton btnMap;
	private CircleButton btnBookmark;
	private RestaurantDetailListener listener;
	private Bitmap bgImage;
	private PlacesPhotoData photoData;
	private GooglePlacesApiClient placesApi;
	public Restaurant restaurant;
	private GridImageAdapter aImageResults;
	private ArrayList<Bitmap> images;
	private GridView gvImages;
	
	public interface RestaurantDetailListener {
		public void onCallRestaurant(String phoneNumber);
		public void onDirections(String address);
		public void onAddReview(Restaurant restaurant);
		public void onAddDish(Restaurant restaurant);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		placeId = getArguments().getString("placeId");
		restaurant = (Restaurant) getArguments().getParcelable("restaurant");
		photoData = (PlacesPhotoData) getArguments().getParcelable("photo_data");
		placesApi = new GooglePlacesApiClient();
		PhotosAsyncTask task = new PhotosAsyncTask();
		images = new ArrayList<Bitmap>();
		aImageResults = new GridImageAdapter(getActivity(), images);
		if(photoData != null){
			task.execute(new String[] { 
				photoData.reference, Integer.toString(
						photoData.width > 300 ? 300 : photoData.width)
				});
		}
		if(restaurant.getPhotoReferences() != null){
			for (PlacesPhotoData photo : restaurant.getPhotoReferences()) {
				AllPhotosAsyncTask allPhotostask = new AllPhotosAsyncTask();
				allPhotostask.execute(new String[] { photo.reference,
						Integer.toString(photo.width > 300 ? 300 : photo.width)});
			}
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_restaurant_detail, container, false);
		rbRating = (RatingBar) v.findViewById(R.id.rbRatingRestaurantDetail);
		tvPrice = (TextView) v.findViewById(R.id.tvDollarsRestaurantDetail);
		tvHours = (TextView) v.findViewById(R.id.tvHoursRestaurantDetail);
		tvOpenNow = (TextView) v.findViewById(R.id.tvOpenRestaurantDetail);
		tvWebsite = (TextView) v.findViewById(R.id.tvWebsiteRestaurantDetail);
		tvAddress = (TextView) v.findViewById(R.id.tvAddressRestaurantDetail);
		tvPhone = (TextView) v.findViewById(R.id.tvPhoneRestaurantDetail);
		tvName = (TextView) v.findViewById(R.id.tvNameRestaurantDetail);
		rlDetail = (RelativeLayout) v.findViewById(R.id.rldetailImage);
		btnCall = (CircleButton) v.findViewById(R.id.btnCallRestaurantDetail);
		btnReview = (CircleButton) v.findViewById(R.id.btnReviewRestaurantDetail);
		btnDish = (CircleButton) v.findViewById(R.id.btnDishRestaurantDetail);
		btnMap = (CircleButton) v.findViewById(R.id.btnDirectionsRestaurantDetail);
		btnBookmark = (CircleButton) v.findViewById(R.id.btnBookmarkRestaurantDetail);
		gvImages = (GridView) v.findViewById(R.id.gvImages);
		gvImages.setAdapter(aImageResults);
		
		LayerDrawable stars = (LayerDrawable) rbRating.getProgressDrawable();
		//stars.getDrawable(0).setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_ATOP);
		//stars.getDrawable(1).setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_ATOP);
		stars.getDrawable(2).setColorFilter(Color.argb(255, 255, 153, 51),
				PorterDuff.Mode.SRC_ATOP);
		rbRating.setRating(restaurant.getFriendRating());

		btnCall.setOnClickListener(new OnClickListener() {
	        public void onClick(View v) {
	        	listener.onCallRestaurant(restaurant.getPhone());
	        }
	    });

		btnMap.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				listener.onDirections(restaurant.getAddress());
			}
		});

		btnReview.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				listener.onAddReview(restaurant);
			}
		});

		btnDish.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				listener.onAddDish(restaurant);
			}
		});

		
		//tvStatus = (TextView) v.findViewById(R.id.tvStatus);
		//tvWebsite = (TextView) v.findViewById(R.id.tvWebsite);
		//tvFriendsRating= (TextView) v.findViewById(R.id.tvFriendRating);

		/*lvPhotos = (ListView) v.findViewById(R.id.lvPhotos);
		//wvGoogleMap = (WebView) v.findViewById(R.id.wvGoogleMap);
		updateDetailFragmentView();
		
		wvGoogleMap = (WebView) v.findViewById(R.id.wvGoogleMap);*/
		updateDetailFragmentView();
		return v;		
	}

	
	@SuppressLint("NewApi")
	public void updateDetailFragmentView() {
		//tvGoogleRating.setText(Double.toString(restaurant.getGoogle_rating()));	
		switch (restaurant.getPrice_level()) {
			case 0: tvPrice.setText("$"); break;
			case 1: tvPrice.setText("$"); break;
			case 2: tvPrice.setText("$$"); break;
			case 3: tvPrice.setText("$$$"); break;
			case 4: tvPrice.setText("$$$$"); break;
			default: tvPrice.setText("$");
		}

		tvName.setText(Html.fromHtml("<b>" + restaurant.getName() + "</b>"));
		tvAddress.setText(restaurant.getAddress().replace(",", "\n"));
		if (restaurant.getWebsite() != null) {
			tvWebsite.setText(Html.fromHtml("<b>Website: </b><span>" +
					"<font color=\"blue\">" + restaurant.getWebsite() + "</font>"));
		} else {
			tvWebsite.setVisibility(TextView.GONE);
		}

		if (restaurant.getOpenHours() != null) {
			tvHours.setText(Html.fromHtml("<b>Hours: </b><span>" + restaurant.getOpenHours()));
		} else {
			tvHours.setVisibility(TextView.GONE);
		}		
		
		if (restaurant.isOpen_now()) {
			tvOpenNow.setText(Html.fromHtml("<font color=\"green\"> Open Now </font>"));
			tvOpenNow.setTextColor(Color.GREEN);
		} else {
			tvOpenNow.setText(Html.fromHtml("<font color=\"red\"> Closed </font>"));
			tvOpenNow.setTextColor(Color.RED);
		}
		if (restaurant.getPhone() != null) {
			tvPhone.setText(Html.fromHtml("<b>Phone: </b><span>" + restaurant.getPhone()));
		} else {
			tvPhone.setVisibility(TextView.GONE);
		}
		if (restaurant.getDisplayPhoto() != null) {
			rlDetail.setBackground(new BitmapDrawable(getResources(), restaurant.getDisplayPhoto()));
			//rlDetail.setAlpha(2/10);
		}
		
		
		/*int imageResource = getResources().getIdentifier("res1", "drawable",
				"com.codepath.apps.tastebuds.fragments");
		Drawable res = getResources().getDrawable(imageResource);
		rlDetail.setBackground(res);*/
		//tvFriendsRating.setText(Double.toString(restaurant.getFriendRating()));
		
//		wvGoogleMap.loadUrl(restaurant.getWeb_map());
//		wvGoogleMap.setWebViewClient(new WebViewClient() {
//			public boolean shouldOverrideUrlLoading(WebView view, String url) {
//			          view.loadUrl(url);
//			          return true;
//			           }});
//		if (restaurant.isOpen_now()) {
//			tvStatus.setText("Open");
//			tvStatus.setTextColor(Color.GREEN);
//		} else {
//			tvStatus.setText("Closed");
//			tvStatus.setTextColor(Color.RED);
//		}
//		wvGoogleMap.getSettings().setLoadsImagesAutomatically(true);
//
//		wvGoogleMap.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

	}
//	
//	   // Manages the behavior when URLs are loaded
//	   private class MyBrowser extends WebViewClient {
//	      @Override
//	      public boolean shouldOverrideUrlLoading(WebView view, String url) {
//	         view.loadUrl(url);
//	         return true;
//	      }
//	   }
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (activity instanceof RestaurantDetailListener) {
			listener = (RestaurantDetailListener) activity;
		} else {
			throw new ClassCastException(activity.toString()
					+ " must implement RestaurantReviewDialog.UserRestaurantReviewListListener");
		}
	}
	
	//The types specified here are the input data type, the progress type, and the result type
	private class PhotosAsyncTask extends AsyncTask<String, Void, Bitmap> {

	  protected void onPreExecute() {}

	  protected Bitmap doInBackground(String... strings) {
	      String reference = strings[0];
	      int maxWidth = Integer.parseInt(strings[1]);
	      return placesApi.getRestaurantDisplayPhoto(reference, maxWidth);
	  }

	  //protected void onProgressUpdate(Progress... values) {
	     // Executes whenever publishProgress is called from doInBackground
	     // Used to update the progress indicator
	    // progressBar.setProgress(values[0]);
	  //}  

	  @SuppressLint("NewApi")
	  protected void onPostExecute(Bitmap result) {
	      // This method is executed in the UIThread
	      // with access to the result of the long running task
		  if (result != null && getActivity() != null) {
			  BitmapDrawable image = new BitmapDrawable(getActivity().getResources(), result);
			  image.setAlpha(50);
			  rlDetail.setBackground(image);
		  }
	  }
	}
	
	//The types specified here are the input data type, the progress type, and the result type
	private class AllPhotosAsyncTask extends AsyncTask<String, Void, Bitmap> {

	  protected void onPreExecute() {}

	  protected Bitmap doInBackground(String... strings) {
	      String reference = strings[0];
	      int maxWidth = Integer.parseInt(strings[1]);
	      return placesApi.getRestaurantDisplayPhoto(reference, maxWidth);
	  }

	  //protected void onProgressUpdate(Progress... values) {
	     // Executes whenever publishProgress is called from doInBackground
	     // Used to update the progress indicator
	    // progressBar.setProgress(values[0]);
	  //}  

	  @SuppressLint("NewApi")
	  protected void onPostExecute(Bitmap result) {
	      // This method is executed in the UIThread
	      // with access to the result of the long running task
		  if (result != null) {
			  if(getActivity() != null && getActivity().getResources() != null){
				  BitmapDrawable image = new BitmapDrawable(getActivity().getResources(), result);
				  aImageResults.add(result);
				  aImageResults.notifyDataSetChanged();
			  }
		  }
	  }
	}
}