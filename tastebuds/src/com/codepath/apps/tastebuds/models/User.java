package com.codepath.apps.tastebuds.models;

import java.util.ArrayList;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;

@ParseClassName("Users")
public class User extends ParseObject {

	private long userId;
	private ArrayList<User> friends;
	private int numRestReview;
	private int numDishReview;
	
	 public static ParseQuery<User> getQuery() {
		    return ParseQuery.getQuery(User.class);
	 }

	public int getNumRestReview() {
		return numRestReview;
	}

	public void setNumRestReview(int numRestReview) {
		this.numRestReview = numRestReview;
	}

	public int getNumDishReview() {
		return numDishReview;
	}

	public void setNumDishReview(int numDishReview) {
		this.numDishReview = numDishReview;
	}
}
