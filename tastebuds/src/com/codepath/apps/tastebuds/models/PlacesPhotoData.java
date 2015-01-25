package com.codepath.apps.tastebuds.models;

import android.os.Parcel;
import android.os.Parcelable;

public class PlacesPhotoData implements Parcelable {
	public String reference;
	public int width;
	public int height;

	public PlacesPhotoData() {
		
	}

    protected PlacesPhotoData(Parcel in) {
        reference = in.readString();
        width = in.readInt();
        height = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(reference);
        dest.writeInt(width);
        dest.writeInt(height);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<PlacesPhotoData> CREATOR = new Parcelable.Creator<PlacesPhotoData>() {
        @Override
        public PlacesPhotoData createFromParcel(Parcel in) {
            return new PlacesPhotoData(in);
        }

        @Override
        public PlacesPhotoData[] newArray(int size) {
            return new PlacesPhotoData[size];
        }
    };
}