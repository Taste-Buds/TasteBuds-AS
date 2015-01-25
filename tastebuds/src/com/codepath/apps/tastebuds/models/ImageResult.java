package com.codepath.apps.tastebuds.models;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

public class ImageResult implements  Parcelable {
	private String photoReference;
	private float width;
	private float height;


	public String getPhotoReference() {
		return photoReference;
	}

	public void setPhotoReference(String tbUrl) {
		this.photoReference = photoReference;
	}

	public float getWidth() {
		return width;
	}

	public void setWidth(float width) {
		this.width = width;
	}

	public float getHeight() {
		return height;
	}

	public void setHeight(float height) {
		this.height = height;
	}


	public ImageResult(JSONObject json) {
		try {
			this.photoReference = json.getString("photo_reference");
			this.width = Float.parseFloat(json.getString("width"));
			this.height = Float.parseFloat(json.getString("height"));
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	// parser that results arraylist with ImageResult objects
	public static ArrayList<ImageResult> fromJSONArray(JSONArray array) {
		ArrayList<ImageResult> results = new ArrayList<ImageResult>();
		for (int i = 0; i < array.length(); i++) {
			try {
				results.add(new ImageResult(array.getJSONObject(i)));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return results;
	}


    protected ImageResult(Parcel in) {
        photoReference = in.readString();
        width = in.readFloat();
        height = in.readFloat();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(photoReference);
        dest.writeFloat(width);
        dest.writeFloat(height);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<ImageResult> CREATOR = new Parcelable.Creator<ImageResult>() {
        @Override
        public ImageResult createFromParcel(Parcel in) {
            return new ImageResult(in);
        }

        @Override
        public ImageResult[] newArray(int size) {
            return new ImageResult[size];
        }
    };
}