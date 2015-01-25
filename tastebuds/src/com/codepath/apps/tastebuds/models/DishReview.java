package com.codepath.apps.tastebuds.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;


@ParseClassName("DishReviews")
public class DishReview extends ParseObject implements Review {

	// Following fields:
	//private long googlePlacesId;
	//private ParseUser user;
	//private int rating;
	//private String text;

	public DishReview() {
		super();
	}

	public DishReview(String googlePlacesId, int rating, String text) {
		super();
		setGooglePlacesId(googlePlacesId);
		setRating(rating);
		setText(text);
	}
	public static ParseQuery<DishReview> getQuery(String ownerId){
		ParseQuery<DishReview> dishQuery =  ParseQuery.getQuery(
	    		DishReview.class).whereEqualTo("owner", ownerId);
	    dishQuery.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK);
		return dishQuery;
	}
	
	public static ParseQuery<DishReview> getQuery(String googlePlacesId,
			List<ParseObject> owners) {
	    ParseQuery<DishReview> query = ParseQuery.getQuery(
	    		DishReview.class)
	    		.orderByDescending("createdAt");
	    if (owners != null) {
	    	query.whereContainedIn("owner", owners);
	    }
	    if (googlePlacesId != null) {
	    	query.whereEqualTo("googlePlacesId", googlePlacesId);
	    }
	    query.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK);
	    return query;
	}

	public static ParseQuery<DishReview> getQuery(String googlePlacesId,
			List<ParseObject> owners, String dishName) {
	    ParseQuery<DishReview> query = ParseQuery.getQuery(
	    		DishReview.class)
	    		.orderByDescending("createdAt");
	    if (owners != null) {
	    	query.whereContainedIn("owner", owners);
	    }
	    if (googlePlacesId != null) {
	    	query.whereEqualTo("googlePlacesId", googlePlacesId);
	    }
	    if (dishName != null) {
	    	query.whereEqualTo("dishName", dishName);
	    }
	    query.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK);
	    return query;
	}

	public static ParseQuery<DishReview> getQuery() {
	    ParseQuery query = ParseQuery.getQuery(DishReview.class);
	    query.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK);
	    return query;
	}

	public String getGooglePlacesId() {
		return getString("googlePlacesId");
	}

	public void setGooglePlacesId(String googlePlacesId) {
		put("googlePlacesId", googlePlacesId);
	}

	public String getRestaurantName() {
		return getString("restaurantName");
	}

	public void setRestaurantName(String restaurantName) {
		put("restaurantName", restaurantName);
	}

	public static ParseQuery<DishReview> getQuery(ParseObject owner) {
	    ParseQuery dishQuery = ParseQuery.getQuery(DishReview.class)
	    		.whereEqualTo("owner", owner)
	    		.orderByDescending("createdAt");
	    dishQuery.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK);
	    return dishQuery;
	}
	
	public ParseUser getUser() {
		return getParseUser("owner");
	}

	public void setUser(ParseUser user) {
		put("owner", user);
	}

	public Date getCreatedTimestamp() {
		 return getDate("createdAt");
	}

	public int getRating() {
		return getInt("rating");
	}

	public void setRating(float f) {
		put("rating", f);
	}

	public String getText() {
		return getString("comment");
	}

	public void setText(String text) {
		put("comment", text);
	}

	public String getDishName() {
		return getString("dishName");
	}

	public void setDishName(String name) {
		put("dishName", name);
	}

	public String getTags() {
		List<Tag> tags = getList("tags");
		StringBuffer buffer = new StringBuffer();
		if (tags != null) {
			for (Tag tag : tags) {
				try {
					buffer.append(tag.fetchIfNeeded().getString("tag"));
				} catch (ParseException e) {
					e.printStackTrace();
				}
				buffer.append(" ");
			}
		}
		return buffer.toString();
	}

	public void setTags(String tagString, String googlePlacesId) {
		String[] values = tagString.replaceAll("^[,\\s]+", "").split("[,\\s]+");
		ArrayList<Tag> tags = new ArrayList<Tag>();
		for (String value : values) {
			tags.add(new Tag(googlePlacesId, value));
		}
		put("tags", tags);
	}
}
