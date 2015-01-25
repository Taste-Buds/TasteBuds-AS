package com.codepath.apps.tastebuds.models;

import java.util.ArrayList;
import java.util.List;

public class Dish {

	// Fields
	private String name;
	private String googlePlacesId;
	private List<DishReview> reviews;
	private float avgRating;

	public Dish(String name, String googlePlacesId, List<DishReview> reviews) {
		this.name = name;
		this.googlePlacesId =  googlePlacesId;
		this.setReviews(reviews);
	}

	public Dish() {
		reviews = new ArrayList<DishReview>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGooglePlacesId() {
		return this.googlePlacesId;
	}
	public void setGooglePlacesId(String googlePlacesId) {
		this.googlePlacesId = googlePlacesId;
	}

	public List<DishReview> getReviews() {
		return reviews;
	}

	public void setReviews(List<DishReview> reviews) {
		this.reviews = reviews;
		avgRating = 0;
		int count = reviews.size();
		for (DishReview review : reviews) {
			avgRating += review.getRating() / count;
		}
	}

	public void addReview(DishReview review) {
		this.reviews.add(review);
		avgRating = 0;
		int count = reviews.size();
		for (DishReview rv : reviews) {
			avgRating += rv.getRating() / count;
		}
	}

	public float getRating() {
		return avgRating;
	}

	public void setRating(float rating) {
		avgRating = rating;
	}

}
