package com.codepath.apps.tastebuds.fragments;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import android.view.View;
import android.view.ViewGroup;
import android.view.MenuItem.OnActionExpandListener;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.codepath.apps.tastebuds.R;
import com.codepath.apps.tastebuds.activities.SearchActivity;
import com.codepath.apps.tastebuds.activities.UserProfileActivity;
import com.codepath.apps.tastebuds.adapters.FriendsListAdapter;
import com.codepath.apps.tastebuds.fragments.RestaurantListFragment.RestaurantListListener;
import com.codepath.apps.tastebuds.models.DishReview;
import com.codepath.apps.tastebuds.models.RestaurantReview;
import com.codepath.apps.tastebuds.models.User;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.rockerhieu.emojicon.EmojiconEditText;
import com.rockerhieu.emojicon.EmojiconTextView;
import com.rockerhieu.emojicon.EmojiconsFragment;
import com.rockerhieu.emojicon.EmojiconGridFragment.OnEmojiconClickedListener;
import com.rockerhieu.emojicon.emoji.Emojicon;


public class FriendsListFragment extends Fragment implements OnEmojiconClickedListener, EmojiconsFragment.OnEmojiconBackspaceClickedListener{

	ArrayList<Object> friends;
	ArrayAdapter<Object> friendsListAdapter;
	ListView lvFriends;
	SearchView searchView;
	ImageView searchImg;
	EmojiconsFragment nf;
	EmojiconEditText mEditEmojicon;
	EmojiconTextView mTxtEmojicon;
	private FriendsListListener listener;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		friends = new ArrayList<Object>();
		friendsListAdapter = new FriendsListAdapter(getActivity(), friends);
		friends.addAll(ParseUser.getCurrentUser().getList("userFriends"));
		friendsListAdapter.notifyDataSetChanged();
		for(int i=0;i<friends.size();i++){
			populateReviewNumbers((ParseUser)(friends.get(i)));
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_friends_list, container, false);
		lvFriends = (ListView) v.findViewById(R.id.lvFriends);
		lvFriends.setAdapter(friendsListAdapter);
		lvFriends.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			   public void onItemClick(AdapterView parentView, View childView, int position, long id) 
			   {
				   showUserDetail(position);
			   }
			});
		return v;
	}
	
	public interface FriendsListListener {
		void onFriendSelected(String place_id);
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);

		inflater.inflate(R.menu.search, menu);
		MenuItem searchItem = menu.findItem(R.id.action_search);
		/** Get the action view of the menu item whose id is search */
		View v = (View) searchItem.getActionView();

		/** Get the edit text from the action view */
		mEditEmojicon = ( EmojiconEditText ) v.findViewById(R.id.txt_search);
		searchImg = (ImageView) v.findViewById(R.id.searchImg);

		searchImg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String search = mEditEmojicon.getText().toString();
				// Dismiss Keyboard
				hideSoftKeyBoard();	
				Intent i = new Intent(getActivity(), SearchActivity.class);
				i.putExtra("SearchTerm", search);
				i.putExtra("SearchType", "Tags");
				startActivity(i);
			}
		});
		searchItem.setOnActionExpandListener(new OnActionExpandListener() {
			@Override
			public boolean onMenuItemActionExpand(MenuItem item) {
				Log.d("Debug","onMenuItemActionExpand");
				FragmentTransaction ft = getChildFragmentManager().beginTransaction();
				ft.show(nf);
				ft.commit();
				return true;

			}

			@Override
			public boolean onMenuItemActionCollapse(MenuItem item) {
				Log.d("Debug","onMenuItemActionCollapse");
				return true;
			}
		});
		//    mEditEmojicon = (EmojiconEditText) searchItem.getActionView();

		//setEmojiconFragment(false);
		//    
		mEditEmojicon.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				FragmentTransaction ft = getChildFragmentManager().beginTransaction();
				ft.show(nf);
				ft.commit();

			}
		});
		// mEditEmojicon.setOnQueryTextListener(queryTextListener);
		//searchView = (SearchView) searchItem.


		mEditEmojicon.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEARCH) {

					String search = v.getText().toString();
					// Dismiss Keyboard
					hideSoftKeyBoard();	    	   
					return true;
				}
				return false;
			}
		});
	}
	
	public void showUserDetail(int position) {
		Log.d("Debug", "P: " + position);
		Intent i = new Intent(getActivity(), UserProfileActivity.class);
		i.putExtra("user_id", ((ParseUser)friends.get(position)).getString("fbId"));
		i.putExtra("SearchType", "User");
		startActivity(i);
	}
	public void  populateReviewNumbers( final ParseUser user){
	
		ParseQuery<RestaurantReview> restQuery = RestaurantReview.getQuery(user);
		
        restQuery.findInBackground(new FindCallback<RestaurantReview>() {

			@Override
			public void done(List<RestaurantReview> reviews, ParseException e) {
				//tvRestaurantReview.setText(Integer.toString(reviews.size()));
				if(user.has("restReviews")){
					user.remove("restReviews");
				}
				user.add("restReviews", reviews.size());
				//user.setNumRestReview(reviews.size());
				//((FriendsListAdapter) friendsListAdapter).setRestaurantReviewNum(reviews.size());
				friendsListAdapter.notifyDataSetChanged();
			}
		});
        
        ParseQuery<DishReview> dishQuery = DishReview.getQuery(user);
        dishQuery.findInBackground(new FindCallback<DishReview>() {

			@Override
			public void done(List<DishReview> reviews, ParseException e) {
				//tvDishReview.setText(Integer.toString(reviews.size()));	
				//tempUser.setNumDishReview(reviews.size());
				user.remove("dishReviews");
				user.add("dishReviews", reviews.size());
				//((FriendsListAdapter) friendsListAdapter).setDishReviewNum(reviews.size());
				friendsListAdapter.notifyDataSetChanged();
			}
		});
	}
	
	private void hideSoftKeyBoard() {
		Context context = getActivity().getBaseContext();
		InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		if(imm.isAcceptingText()) {                      
			imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
//		if (activity instanceof FriendsListListener) {
//			listener = (FriendsListListener) activity;
//		} else {
//			throw new ClassCastException(activity.toString()
//					+ " must implement RestaurantReviewDialog.RestaurantReviewListListener");
//		}
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
}