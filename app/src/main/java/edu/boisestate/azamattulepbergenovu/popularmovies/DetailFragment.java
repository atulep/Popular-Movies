package edu.boisestate.azamattulepbergenovu.popularmovies;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Displays details of the movie.
 * Created by atulep on 2/3/2016.
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private String LOG_TAG = getClass().getSimpleName();
    private static final int MOVIES_LOADER = 0;

    @Bind(R.id.details_imageView) ImageView poster;
    @Bind(R.id.details_title) TextView title;
    @Bind(R.id.details_release) TextView release;
    @Bind(R.id.details_rating) TextView rating;
    @Bind(R.id.details_plot) TextView plot;

    public void onCreate(Bundle savedInstanceBundle) {
        super.onCreate(savedInstanceBundle);
    }

    public void onActivityCreated(Bundle savedInstanceState){
        getLoaderManager().initLoader(MOVIES_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // TODO: here
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Called when a previously created loader has finished its load.
        //TODO: need to implement the updating movie details here.
    }

    public void onLoaderReset(Loader<Cursor> loader) {
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        ButterKnife.bind(this, rootView);

        Movie obj = (Movie) getActivity().getIntent().getParcelableExtra(getResources().getString(R.string.parcelable_movie_key));
        updateMovieDetails(rootView, obj);

        return rootView;
    }

    public void updateMovieDetails(View view, Movie movie){
        Picasso.with(this.getActivity()).load("http://image.tmdb.org/t/p/w185" + movie.posterImage).into(poster);
        title.setText(movie.title);
        release.setText(movie.releaseDate);
        rating.setText(Double.valueOf(movie.rating).toString());
        plot.setText(movie.plot);
    }
}
