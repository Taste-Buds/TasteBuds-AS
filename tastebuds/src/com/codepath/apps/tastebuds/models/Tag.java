package com.codepath.apps.tastebuds.models;

import java.util.ArrayList;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;


@ParseClassName("Tags")
public class Tag extends ParseObject {
	//Fields:
	//private long googlePlacesId;
	//private long reviewId;
	//private String tag;
	
	public Tag(String googlePlacesId, String tag) {
		super();
		setGooglePlacesId(googlePlacesId);
		setTag(tag);
	}

	public Tag() {
		super();
	}

	public static ParseQuery<Tag> getQuery() {
		ParseQuery<Tag> query = ParseQuery.getQuery(Tag.class);
		query.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK);
		return query;
	}
	
	public static ParseQuery<Tag> getQuery(ArrayList<String> tags) {
		ParseQuery<Tag> query = ParseQuery.getQuery(Tag.class);
		query.whereContainedIn("tag", tags);
		query.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK);
		return query;
	}

	public static ParseQuery<Tag> getQuery(String googlePlacesId) {
		ParseQuery<Tag> query = ParseQuery.getQuery(Tag.class);
		query.whereEqualTo("googlePlacesId", googlePlacesId);
		query.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK);
		return query;
	}
	
	public String getGooglePlacesId() {
		return getString("googlePlacesId");
	}

	public void setGooglePlacesId(String googlePlacesId) {
		put("googlePlacesId", googlePlacesId);
	}

	public String getTag() {
		return getString("tag");
	}

	public void setTag(String tag) {
		put("tag", tag);
	}

	public ParseUser getUser() {
		return getParseUser("owner");
	}

	public void setUser(ParseUser user) {
		put("owner", user);
	}
}
