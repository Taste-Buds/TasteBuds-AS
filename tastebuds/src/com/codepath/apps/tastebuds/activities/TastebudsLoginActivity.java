package com.codepath.apps.tastebuds.activities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;

import com.codepath.apps.tastebuds.R;
import com.codepath.apps.tastebuds.adapters.LoginPagerAdapter;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class TastebudsLoginActivity extends FragmentActivity {
	private static Session session;
	//	@Override
	//	protected void onResume() {
	//		// TODO Auto-generated method stub
	//		super.onResume();
	//		    
	//	}

	private Button loginButton;
	private Dialog progressDialog;
	private LoginPagerAdapter loginPagerAdapter;
	private  ViewPager viewPager;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getActionBar().hide();
		session = Session.getActiveSession();
		if (session !=null && session.isOpened()) {
			goToHomeActivity();
		}else{
			setContentView(R.layout.activity_login);
			//loginPagerAdapter =
	         //       new LoginPagerAdapter(getSupportFragmentManager());
			///viewPager = (ViewPager) findViewById(R.id.pager);
			//viewPager.setAdapter(loginPagerAdapter);
			

			loginButton = (Button) findViewById(R.id.loginButton);
			loginButton.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						onLoginButtonClicked();
					}
			});

			// Check if there is a currently logged in user
			// and they are linked to a Facebook account.
			ParseUser currentUser = ParseUser.getCurrentUser();

			if ((currentUser != null) && ParseFacebookUtils.isLinked(currentUser)) {
				// Go to the user info activity
				//	showUserDetailsActivity();
			}
		}
	}



	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		ParseFacebookUtils.finishAuthentication(requestCode, resultCode, data);
	}

	private void onLoginButtonClicked() {
		session = Session.getActiveSession();
		if (session !=null && session.isOpened()) {
			goToHomeActivity();
		}else{
			TastebudsLoginActivity.this.progressDialog = ProgressDialog.show(
					TastebudsLoginActivity.this, "", "Logging in...", true);
			List<String> permissions = Arrays.asList("public_profile", "user_about_me","user_friends",
					"user_relationships", "user_birthday", "user_location");

			ParseFacebookUtils.logIn(permissions, this, new LogInCallback() {
				@Override
				public void done(ParseUser user, ParseException err) {
					TastebudsLoginActivity.this.progressDialog.dismiss();
					if(user != null){
						session = Session.getActiveSession();

						getFacebookIdInBackground();
						Intent i = new Intent(TastebudsLoginActivity.this, HomeActivity.class);
						startActivity(i);
						overridePendingTransition(R.anim.right_in, R.anim.left_out);
					}
				}
			});
		}
	}
	private static void getFacebookFriends(){
		try {
			Request.newMyFriendsRequest(session, new Request.GraphUserListCallback() {

				@Override
				public void onCompleted(List<GraphUser> users, Response response) {
					if (users != null) {
						List<String> friendsList = new ArrayList<String>();
						for (GraphUser user : users) {
							friendsList.add(user.getId());
						}
						// Also add the friends from user's friends array field
						List<String> tableFriendsId = (ArrayList<String>) ParseUser.getCurrentUser().get("friends");
						for (String id: tableFriendsId){
							friendsList.add(id);
						}

						// Construct a ParseUser query that will find friends whose
						// facebook IDs are contained in the current user's friend list.
						ParseQuery friendQuery = ParseQuery.getUserQuery();
						friendQuery.whereContainedIn("fbId", friendsList);

						// findObjects will return a list of ParseUsers
						// that are friends with the current user
						try {

							List<ParseObject> friendUsers = friendQuery.find();
							ParseUser.getCurrentUser().remove("userFriends");
							ParseUser.getCurrentUser().addAll("userFriends", friendUsers);

						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}).executeAsync().get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private static void getFacebookIdInBackground() {

		Request.newMeRequest(session, new Request.GraphUserCallback(){
			@Override
			public void onCompleted(GraphUser user, Response response) {
				if (user != null) {
					ParseUser.getCurrentUser().put("username", user.getName());
					ParseUser.getCurrentUser().put("fbId", user.getId());
					ParseUser.getCurrentUser().put("location", user.getLocation().getName());
					getFacebookFriends();
					ParseUser.getCurrentUser().saveInBackground();

				}
			}
		}).executeAsync();
	}
	private void goToHomeActivity(){
		
		getFacebookFriends();
		Intent i = new Intent(TastebudsLoginActivity.this, HomeActivity.class);
		startActivity(i);
		overridePendingTransition(R.anim.right_in, R.anim.left_out);
	}

    @Override
    public void onBackPressed() {
	finish();
	overridePendingTransition(R.anim.left_in, R.anim.right_out);
    }
}
