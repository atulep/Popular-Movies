package edu.boisestate.azamattulepbergenovu.popularmovies;

import java.io.Serializable;

/**
 * Created by atulep on 1/24/2016.
 */
public class Movie implements Serializable {
    String title;
    String posterImage;
    String plot;
    double rating;
    String releaseDate;

    // This is one is a temp constructor, just in order to accept no arguments.
    public Movie() {
    }

    /**
     * Constructor.
     * @param title
     * @param posterImage
     * @param plot
     * @param rating
     * @param releaseDate
     */
    public Movie(String title, String posterImage, String plot, double rating, String releaseDate) {
        this.title = title;
        this.posterImage = posterImage;
        this.plot = plot;
        this.rating = rating;
        this.releaseDate = releaseDate;
    }

    public String toString() {
        return "Movie: " + title;
    }
}
