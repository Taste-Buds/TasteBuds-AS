package com.codepath.apps.tastebuds.fragments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.codepath.apps.tastebuds.R;
import com.codepath.apps.tastebuds.adapters.DishListAdapter;
import com.codepath.apps.tastebuds.fragments.RestaurantReviewListFragment.RestaurantReviewListListener;
import com.codepath.apps.tastebuds.models.Dish;
import com.codepath.apps.tastebuds.models.DishReview;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class DishListFragment extends Fragment {

	private DishListAdapter adapter;
	private ListView lvDishes;
	private List<Dish> dishes;
	private String googlePlacesId;
	private DishListListener listener;

    public interface DishListListener {
    	void onDishSelected(String googlePlacesId, String dishName);
    }

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		googlePlacesId = getArguments().getString("placeId");
        List<ParseObject> friends = ParseUser.getCurrentUser().getList("userFriends");
        friends.add(ParseUser.getCurrentUser());
        ParseQuery<DishReview> query = DishReview.getQuery(googlePlacesId, friends);
        dishes = new ArrayList<Dish>();
		HashMap<String, Dish> reviewMap = new HashMap<String, Dish>();
		try {
			List<DishReview>  dishReviews = query.find();
			for (DishReview review : dishReviews) {
				String dishName = review.getDishName();
				if (!reviewMap.containsKey(dishName)) {
					Dish dish = new Dish();
					dish.setName(dishName);
					dish.setGooglePlacesId(googlePlacesId);
					reviewMap.put(dishName, dish);
				}
				reviewMap.get(dishName).addReview(review);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		dishes.addAll(reviewMap.values());
		adapter = new DishListAdapter(getActivity(), dishes);
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_dish_list, container, false);
		lvDishes = (ListView) view.findViewById(R.id.lvDishes);
		lvDishes.setAdapter(adapter);
		lvDishes.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Dish dish = adapter.getItem(position);
				listener.onDishSelected(googlePlacesId, dish.getName());
			}
		});

		adapter.notifyDataSetChanged();
		return view;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (activity instanceof RestaurantReviewListListener) {
			listener = (DishListListener) activity;
		} else {
			throw new ClassCastException(activity.toString()
					+ " must implement RestaurantReviewDialog.RestaurantReviewListListener");
		}
	}
}
