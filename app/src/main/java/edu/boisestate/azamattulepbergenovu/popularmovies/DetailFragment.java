package edu.boisestate.azamattulepbergenovu.popularmovies;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Displays details of the movie.
 * Created by atulep on 2/3/2016.
 */
public class DetailFragment extends Fragment {
    private String LOG_TAG = getClass().getSimpleName();

    public void onCreate(Bundle savedInstanceBundle) {
        super.onCreate(savedInstanceBundle);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        Movie obj = (Movie) getActivity().getIntent().getParcelableExtra(getResources().getString(R.string.parcelable_movie_key));
        updateMovieDetails(rootView, obj);
        return rootView;
    }

    public void updateMovieDetails(View view, Movie movie){
        ImageView poster = (ImageView) view.findViewById(R.id.details_imageView);
        Picasso.with(this.getActivity()).load("http://image.tmdb.org/t/p/w185" + movie.posterImage).into(poster);

        TextView title = (TextView) view.findViewById(R.id.details_title);
        title.setText(movie.title);

        TextView release = (TextView) view.findViewById(R.id.details_release);
        release.setText(movie.releaseDate);

        TextView rating = (TextView) view.findViewById(R.id.details_rating);
        rating.setText(Double.valueOf(movie.rating).toString());

        TextView plot = (TextView) view.findViewById(R.id.details_plot);
        plot.setText(movie.plot);
    }
}
