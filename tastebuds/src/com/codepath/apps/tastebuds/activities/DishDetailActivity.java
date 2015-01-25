package com.codepath.apps.tastebuds.activities;

import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.codepath.apps.tastebuds.R;
import com.codepath.apps.tastebuds.adapters.DishReviewListAdapter;
import com.codepath.apps.tastebuds.fragments.DishReviewDetailDialog;
import com.codepath.apps.tastebuds.fragments.DishReviewDetailDialog.DishReviewDetailDialogListener;
import com.codepath.apps.tastebuds.models.DishReview;
import com.parse.ParseObject;
import com.parse.ParseUser;

public class DishDetailActivity extends Activity {

	private String restaurantName;
	private String dishName;
	private String placesId;
	private DishReviewListAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dish_detail);
		restaurantName = getIntent().getStringExtra("restaurant_name");
		dishName = getIntent().getStringExtra("dish_name");
		String actionBarTitle = dishName + " Reviews";
		ActionBar actionBar = getActionBar();
		actionBar.setTitle(actionBarTitle);
		placesId = getIntent().getStringExtra("placesId");
		ListView lvReviews = (ListView) findViewById(R.id.lvDishReview);
		List<ParseObject> friends = ParseUser.getCurrentUser().getList("userFriends");
		adapter = new DishReviewListAdapter(this, placesId, friends, dishName);
		lvReviews.setAdapter(adapter);

		lvReviews.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				DishReview review = adapter.getItem(position);
			    FragmentTransaction ft = getFragmentManager().beginTransaction();
			    Fragment prev = getFragmentManager().findFragmentByTag("dialog");
			    if (prev != null) {
			        ft.remove(prev);
			    }
			    ft.addToBackStack(null);
				DishReviewDetailDialog dialog =
						DishReviewDetailDialog.newInstance(
								review.getObjectId(), restaurantName);
				dialog.show(ft, "compose");
				dialog.listener = new DishReviewDetailDialogListener() {
					@Override
					public void onFinishReviewDialog(DishReview review) {}
				};				
			}
		});
	}

    @Override
    public void onBackPressed() {
	finish();
	overridePendingTransition(R.anim.left_in, R.anim.right_out);
    }
}
