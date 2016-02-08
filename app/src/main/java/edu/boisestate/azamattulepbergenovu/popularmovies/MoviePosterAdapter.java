package edu.boisestate.azamattulepbergenovu.popularmovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Custom ArrayAdapter to handle data processing on the main UI.
 *
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
        Movie movie = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.movie_item, parent, false);
        }
        ImageView posterView = (ImageView) convertView.findViewById(R.id.movie_poster);
        Picasso.with(this.getContext()).load("http://image.tmdb.org/t/p/w185" + movie.posterImage).into(posterView);
        return convertView;
    }
}
