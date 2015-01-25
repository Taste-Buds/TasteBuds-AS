package com.codepath.apps.tastebuds;

import android.content.Context;

import com.codepath.apps.tastebuds.models.DishReview;
import com.codepath.apps.tastebuds.models.RestaurantReview;
import com.codepath.apps.tastebuds.models.Tag;
import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;

/*
 * This is the Android application itself and is used to configure various settings
 * including the image cache in memory and on disk. This also adds a singleton
 * for accessing the relevant rest client.
 * 
 *     RestClient client = RestClientApp.getRestClient();
 *     // use client to send requests to API
 *     
 */
public class TasteBudsApp extends com.activeandroid.app.Application {
	private static Context context;
	public static final String APPLICATION_ID =
			"p0cAY8ltaiACamNHPDakm697pr9mXcftuVIQxYBo";
	public static final String CLIENT_KEY =
			"yC7vqVd2E7MXrF0FkFkItsQTBEpk5h2Y71cZrI99";

	@Override
	public void onCreate() {
		super.onCreate();
		ParseObject.registerSubclass(DishReview.class);
		ParseObject.registerSubclass(RestaurantReview.class);
		//ParseObject.registerSubclass(Dish.class);
		ParseObject.registerSubclass(Tag.class);
		Parse.initialize(this, APPLICATION_ID, CLIENT_KEY);
		ParseFacebookUtils.initialize("280855298792275");
		TasteBudsApp.context = this;

		/*ParseUser user = new ParseUser();
		user.setUsername("abcd12345");
		user.setPassword("secret1235");
		user.setEmail("abcd21@mail.com");

		try {
			user.signUp();
		} catch (ParseException e) {
			e.printStackTrace();
		}

		RestaurantReview review = new RestaurantReview();
		review.setGooglePlacesId(678901241);
		review.setRating(4);
		review.setText("Awesome Restaurant");
		review.setUser(ParseUser.getCurrentUser());
		review.saveInBackground(new SaveCallback() {
			@Override
			public void done(ParseException arg0) {
				if (arg0 != null) {
					Toast.makeText(context, "Saved review failed" + arg0.toString(), Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(context, "Saved review", Toast.LENGTH_LONG).show();
				}
			}
		});*/

		// Create global configuration and initialize ImageLoader with this configuration
		/*DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder().
				cacheInMemory().cacheOnDisc().build();
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
		.defaultDisplayImageOptions(defaultOptions)
		.build();
		ImageLoader.getInstance().init(config);*/
	}

	//public static GoogleClient getRestClient() {
		//return (GoogleClient) GoogleClient.getInstance(GoogleClient.class, TasteBudsApp.context);
	//}
}