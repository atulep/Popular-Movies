package edu.boisestate.azamattulepbergenovu.popularmovies;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.List;

/**
 * Created by atulep on 1/24/2016.
 */
public class MoviePosterAdapter<Movie> extends ArrayAdapter<Movie> {

    public MoviePosterAdapter(Context context, List<Movie> movieList) {
        super(context, 0, movieList);
    }




}
