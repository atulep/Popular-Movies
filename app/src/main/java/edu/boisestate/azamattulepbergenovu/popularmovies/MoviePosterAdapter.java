package edu.boisestate.azamattulepbergenovu.popularmovies;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by atulep on 1/24/2016.
 */
public class MoviePosterAdapter extends ArrayAdapter<Movie> {

    private String LOG_TAG = MoviePosterAdapter.class.getSimpleName();

    /**
     * Constructor.
     * @param context
     * @param movieList
     */
    public MoviePosterAdapter(Context context, List<Movie> movieList) {
        super(context, 0, movieList);

    }

    /**
     * According to Android documentation, I need to override this method in order to return my customized view.
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    public View getView(int position, View convertView, ViewGroup parent) {
        // Gets the AndroidFlavor object from the ArrayAdapter at the appropriate position
        Movie movie = getItem(position);
        // Adapters recycle views to AdapterViews.
        // If this is a new View object we're getting, then inflate the layout.
        // If not, this view already has the layout inflated from a previous call to getView,
        // and we modify the View widgets as usual.
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.movie_item, parent, false);
        }
        // Programmatically maps posterView to the layout I defined.
        // Sets the image resource to this ImageView.

        ImageView posterView = (ImageView) convertView.findViewById(R.id.movie_poster);
        Picasso.with(this.getContext()).load(" http://image.tmdb.org/t/p/w185" + movie.posterImage).into(posterView);
//
        Log.v(LOG_TAG, "*******MOVIE_POSTER " + movie.posterImage);
        return convertView;
    }
}
