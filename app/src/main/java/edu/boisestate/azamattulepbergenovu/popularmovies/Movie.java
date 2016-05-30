package edu.boisestate.azamattulepbergenovu.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

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
    long id;
    ArrayList<String> trailers=new ArrayList<>();
    ArrayList<String> reviews=new ArrayList<>();

    // This is one is a temp constructor, just in order to accept no arguments.
    public Movie() {
    }

    private Movie(Parcel in){
        id=in.readLong();
        title = in.readString();
        posterImage = in.readString();
        plot = in.readString();
        rating = in.readDouble();
        releaseDate = in.readString();
        in.readStringList(trailers);
        in.readStringList(reviews);
    }

    public Movie(long id, String title, String posterImage, String plot, double rating, String releaseDate, ArrayList<String> trailers, ArrayList<String> reviews) {
        this.id=id;
        this.title = title;
        this.posterImage = posterImage;
        this.plot = plot;
        this.rating = rating;
        this.releaseDate = releaseDate;
        this.trailers=trailers;
        this.reviews=reviews;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeLong(id);
        out.writeString(title);
        out.writeString(posterImage);
        out.writeString(plot);
        out.writeDouble(rating);
        out.writeString(releaseDate);
        out.writeStringList(reviews);
        out.writeStringList(trailers);
    }

    public void setTrailers(ArrayList<String> trailers) {
        this.trailers=trailers;
    }

    public void setReviews(ArrayList<String> reviews) {
        this.reviews=reviews;
    }

    public long getId() {
        return id;
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
