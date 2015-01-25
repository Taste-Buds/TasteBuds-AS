package com.codepath.apps.tastebuds.activities;

import java.util.ArrayList;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Application;

import android.content.Intent;
import android.content.IntentSender;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.codepath.apps.tastebuds.R;
import com.codepath.apps.tastebuds.adapters.NavDrawerListAdapter;
import com.codepath.apps.tastebuds.fragments.FriendsListFragment;
import com.codepath.apps.tastebuds.fragments.RestaurantListFragment;
import com.codepath.apps.tastebuds.fragments.RestaurantReviewDialog;
import com.codepath.apps.tastebuds.fragments.FriendsListFragment.FriendsListListener;
import com.codepath.apps.tastebuds.fragments.RestaurantListFragment.RestaurantListListener;
import com.codepath.apps.tastebuds.fragments.RestaurantReviewDialog.RestaurantReviewDialogListener;
import com.codepath.apps.tastebuds.listeners.FragmentTabListener;
import com.codepath.apps.tastebuds.models.NavDrawerItem;
import com.codepath.apps.tastebuds.models.PlacesPhotoData;
import com.codepath.apps.tastebuds.models.Restaurant;
import com.codepath.apps.tastebuds.models.RestaurantReview;
import com.facebook.Session;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;

public class HomeActivity extends FragmentActivity implements
	GooglePlayServicesClient.ConnectionCallbacks,
	GooglePlayServicesClient.OnConnectionFailedListener,
	RestaurantListListener{

	private ParseUser user;
	ArrayList<Restaurant> restaurants = new ArrayList<Restaurant>();
	public Location mCurrentLocation;
	LocationClient mLocationClient;
	private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

	   private DrawerLayout mDrawerLayout;
	    private ListView mDrawerList;
	    private ActionBarDrawerToggle mDrawerToggle;
	 
	    // nav drawer title
	    private CharSequence mDrawerTitle;
	    
	 // slide menu items
	    private String[] navMenuTitles;
	    private TypedArray navMenuIcons;
	 
	    private ArrayList<NavDrawerItem> navDrawerItems;
	    private NavDrawerListAdapter adapter;
	 // used to store app title
	    private CharSequence mTitle;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		user = ParseUser.getCurrentUser();
		mLocationClient = new LocationClient(this, this, this);
		setContentView(R.layout.activity_home);		
		Log.d("Debug", "Home Activity OnCreate");
		
	       mTitle = mDrawerTitle = getTitle();
	       
	        // load slide menu items
	        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);
	 
	        // nav drawer icons from resources
	        navMenuIcons = getResources()
	                .obtainTypedArray(R.array.nav_drawer_icons);
	        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
	        mDrawerList = (ListView) findViewById(R.id.list_slidermenu);
	 
	        navDrawerItems = new ArrayList<NavDrawerItem>();
	 
	        // adding nav drawer items to array
	        // profile
	        navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));
	        //logout
	        
	        navDrawerItems.add(new NavDrawerItem(navMenuTitles[1],navMenuIcons.getResourceId(1, -1)));
//	        //logout
//	        navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(2, -1)));
	         
	 
	        // Recycle the typed array
	        navMenuIcons.recycle();
	        // setting the nav drawer list adapter
	        adapter = new NavDrawerListAdapter(getApplicationContext(),
	                navDrawerItems);
	        mDrawerList.setAdapter(adapter);
	 
	        // enabling action bar app icon and behaving it as toggle button
	        getActionBar().setDisplayHomeAsUpEnabled(true);
	        getActionBar().setHomeButtonEnabled(true);
	 
	        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
	                R.drawable.ic_drawer, //nav menu toggle icon
	                R.string.app_name, // nav drawer open - description for accessibility
	                R.string.app_name // nav drawer close - description for accessibility
	        ){
	            public void onDrawerClosed(View view) {
	               // getActionBar().setTitle(mTitle);
	                // calling onPrepareOptionsMenu() to show action bar icons
	                invalidateOptionsMenu();
	            }
	 
	            public void onDrawerOpened(View drawerView) {
	               // getActionBar().setTitle(mDrawerTitle);
	                // calling onPrepareOptionsMenu() to hide action bar icons

	                invalidateOptionsMenu();
	            }
	        };
	        mDrawerLayout.setDrawerListener(mDrawerToggle);
	 
	        
//	        if (savedInstanceState == null) {
//	        	 if(mCurrentLocation == null){
//	        		 mLocationClient = new LocationClient(this, this, this);
//	 	        	//mCurrentLocation = getLocation(mLocationClient);
//	 	        }
//	 	        // on first time display view for first nav item
//	 	        	displayView(0);
//	        }
	       
	        mDrawerList.setOnItemClickListener(new SlideMenuClickListener());
	}
	/**
     * Slide menu item click listener
     * */
    private class SlideMenuClickListener implements
            ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                long id) {
            // display view for selected nav drawer item
            displayView(position);
        }
    }
    
    /**
     * Diplaying fragment view for selected nav drawer list item
     * */
    private void displayView(int position) {
        // update the main content by replacing fragments
        Fragment fragment = null;
        switch (position) {
//        case 0: 
//        	this.setupTabs(mCurrentLocation);
//        	View decorView = getWindow().getDecorView();
//        	int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//        	              | View.SYSTEM_UI_FLAG_FULLSCREEN;
//        	decorView.setSystemUiVisibility(uiOptions);
//        	break;
        case 0:
           // fragment = new HomeFragment();
        	this.onProfileClick(null);
            break;
        case 1:
        	this.onLogoutAction(null);
           // fragment = new FindPeopleFragment();
            break;
 
        default:
            break;
        }

    }
	 @Override
	    public boolean onOptionsItemSelected(MenuItem item) {
	        // toggle nav drawer on selecting action bar app icon/title
	        if (mDrawerToggle.onOptionsItemSelected(item)) {
	            return true;
	        }
	        // Handle action bar actions click
	        switch (item.getItemId()) {
	        case R.id.action_settings:
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	        }
	 }
	 
	    /***
	     * Called when invalidateOptionsMenu() is triggered
	     */
	    @Override
	    public boolean onPrepareOptionsMenu(Menu menu) {
	        // if nav drawer is opened, hide the action items
	        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
	        //menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
	        return super.onPrepareOptionsMenu(menu);
	    }
	 
	    @Override
	    public void setTitle(CharSequence title) {
	        mTitle = title;
	        getActionBar().setTitle(mTitle);
	    }
	 
	    /**
	     * When using the ActionBarDrawerToggle, you must call it during
	     * onPostCreate() and onConfigurationChanged()...
	     */
	 
	    @Override
	    protected void onPostCreate(Bundle savedInstanceState) {
	        super.onPostCreate(savedInstanceState);
	        // Sync the toggle state after onRestoreInstanceState has occurred.
	        mDrawerToggle.syncState();
	    }
	 
	    @Override
	    public void onConfigurationChanged(Configuration newConfig) {
	        super.onConfigurationChanged(newConfig);
	        // Pass any configuration change to the drawer toggls
	        mDrawerToggle.onConfigurationChanged(newConfig);
	    }
	 
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

    /*
     * Called when the Activity becomes visible.
     */
    @Override
    protected void onStart() {
        super.onStart();
        // Connect the client.
        mLocationClient.connect();
    }
    /*
     * Called when the Activity is no longer visible.
     */
    @Override
    protected void onStop() {
        // Disconnecting the client invalidates it.
        mLocationClient.disconnect();
        super.onStop();
    }
	
	public void onLogoutAction(MenuItem mi){
		Session session = Session.getActiveSession();
		if (session != null) {
			if (!session.isClosed()) {
				session.closeAndClearTokenInformation();
				//clear your preferences if saved
				startActivity(new Intent(getApplicationContext(), TastebudsLoginActivity.class));
				overridePendingTransition(R.anim.right_in, R.anim.left_out);
				// make sure the user can not access the page after he/she is logged out
				// clear the activity stack
				finish();
			}
		} else {

			session.closeAndClearTokenInformation();
			//clear your preferences if saved

		}
    	Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//***Change Here***
        startActivity(intent);
        finish();
        System.exit(0);
	}
	
	public void onProfileClick(MenuItem mi){
		Intent i = new Intent(this, UserProfileActivity.class);
		i.putExtra("user_id", ParseUser.getCurrentUser().getString("fbId"));
		startActivity(i);
		overridePendingTransition(R.anim.right_in, R.anim.left_out);
	}
	private void setupTabs(Location mCurrentLocation) {
		ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.removeAllTabs();
		actionBar.setTitle("TasteBuds");
		getSupportFragmentManager().executePendingTransactions();
		Bundle args = new Bundle();
		args.putParcelable("mCurrentLocation", mCurrentLocation);
		
		Tab tab1 = actionBar
				.newTab()
				.setText("Restaurants")
				.setIcon(R.drawable.ic_restaurant)
				.setTag("RestaurantListFragment")
				.setTabListener(
						new FragmentTabListener<RestaurantListFragment>(R.id.ctRestaurantsLists, this, "first",
								RestaurantListFragment.class, args));

		actionBar.addTab(tab1);
		actionBar.selectTab(tab1);

		Tab tab2 = actionBar
				.newTab()
				.setText("Friends")
				.setIcon(R.drawable.ic_friends)
				.setTag("FriendsListFragment")
				.setTabListener(
						new FragmentTabListener<FriendsListFragment>(R.id.ctRestaurantsLists, this, "second",
								FriendsListFragment.class));

		actionBar.addTab(tab2);
	}

	public void onReview(View view) {
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
		if (prev != null) {
			ft.remove(prev);
		}
		ft.addToBackStack(null);
		RestaurantReviewDialog dialog = RestaurantReviewDialog.newInstance(
				"Shree Datta", "czswrtv");
		dialog.show(ft, "compose");
		dialog.listener = new RestaurantReviewDialogListener() {
			@Override
			public void onFinishReviewComposeDialog(RestaurantReview review) {
				review.setUser(user);
				review.saveInBackground(new SaveCallback() {
					@Override
					public void done(ParseException arg0) {
					}
				});
			}
		};
	}
	
    /*
     * Called by Location Services when the request to connect the
     * client finishes successfully. At this point, you can
     * request the current location or start periodic updates
     */
    @Override
    public void onConnected(Bundle dataBundle) {
    	if(!mLocationClient.isConnected()){
    		 mLocationClient.connect();
    	}
        Log.d("Debug", "onConnected");
        if(mCurrentLocation == null){
        	mCurrentLocation = getLocation(mLocationClient);
        	setupTabs(mCurrentLocation);
        }
      //  mCurrentLocation = mLocationClient.getLastLocation();
       // Log.d("Debug", "Location" + mCurrentLocation.toString());
		

    }
    
    public Location getLocation(LocationClient locationClient){

        if(locationClient.getLastLocation() == null){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return getLocation(locationClient);
        }else{
            return locationClient.getLastLocation();
        }
    }
    /*
     * Called by Location Services if the connection to the
     * location client drops because of an error.
     */
    @Override
    public void onDisconnected() {
        // Display the connection status
        Toast.makeText(this, "Disconnected. Please re-connect.",
                Toast.LENGTH_SHORT).show();
    }
    /*
     * Called by Location Services if the attempt to
     * Location Services fails.
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        /*
         * Google Play services can resolve some errors it detects.
         * If the error has a resolution, try sending an Intent to
         * start a Google Play services activity that can resolve
         * error.
         */
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(
                        this,
                        CONNECTION_FAILURE_RESOLUTION_REQUEST);
                /*
                 * Thrown if Google Play services canceled the original
                 * PendingIntent
                 */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
            /*
             * If no resolution is available, display a dialog to the
             * user with the error.
             */
            //showErrorDialog(connectionResult.getErrorCode());
        }
    }

	@Override
	public void onRestaurantSelected(String place_id, float rating, 
			PlacesPhotoData image) {
		Intent i = new Intent(this, RestaurantDetailActivity.class);
		i.putExtra("place_id", place_id);
		i.putExtra("photo_data", image);
		i.putExtra("rating", rating);
		startActivity(i);
		overridePendingTransition(R.anim.right_in, R.anim.left_out);
	}

    @Override
    public void onBackPressed() {    	
    	Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//***Change Here***
        startActivity(intent);
        finish();
        System.exit(0);
    }
//	@Override
//	public void onFriendSelected(String place_id) {
//		Intent i = new Intent(this, UserProfileActivity.class);
//		i.putExtra("place_id", place_id);
//		startActivity(i);
//		overridePendingTransition(R.anim.right_in, R.anim.left_out);
//		
//	}
}
	

