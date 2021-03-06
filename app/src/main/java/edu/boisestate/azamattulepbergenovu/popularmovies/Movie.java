package edu.boisestate.azamattulepbergenovu.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Represents movie.
 * Created by atulep on 1/24/2016.
 */
public class Movie implements Parcelable {
    String title;
    String posterImage;
    String plot;
    double rating;
    String releaseDate;

    // This is one is a temp constructor, just in order to accept no arguments.
    public Movie() {
    }

    private Movie(Parcel in){
        title = in.readString();
        posterImage = in.readString();
        plot = in.readString();
        rating = in.readDouble();
        releaseDate = in.readString();
    }

    public Movie(String title, String posterImage, String plot, double rating, String releaseDate) {
        this.title = title;
        this.posterImage = posterImage;
        this.plot = plot;
        this.rating = rating;
        this.releaseDate = releaseDate;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(title);
        out.writeString(posterImage);
        out.writeString(plot);
        out.writeDouble(rating);
        out.writeString(releaseDate);
    }

    public int describeContents() {
        return 0;
    }

    public String toString() {
        return "Movie: " + title;
    }

    public static final Parcelable.Creator<Movie> CREATOR
            = new Parcelable.Creator<Movie>() {
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

}
