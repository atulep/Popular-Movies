package edu.boisestate.azamattulepbergenovu.popularmovies;

import android.app.Fragment;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import edu.boisestate.azamattulepbergenovu.popularmovies.data.MoviesContract;
import edu.boisestate.azamattulepbergenovu.popularmovies.fetch.FetchReviewDataTask;
import edu.boisestate.azamattulepbergenovu.popularmovies.fetch.FetchTrailerDataTask;

/**
 * Activity that displays details of the movie.
 */
public class DetailActivity extends AppCompatActivity {

    private final String LOG_TAG = getClass().getSimpleName();
    private String[] DETAIL_COLUMNS = {MoviesContract.DetailsColumns.MOVIE_ID};

    /**
     * Will fetch the review and trailer data for the current movies only.
     */
    private void fetch(long movieId) {
        new FetchReviewDataTask(this).execute(String.valueOf(movieId));
        new FetchTrailerDataTask(this).execute(String.valueOf(movieId));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        if (savedInstanceState == null){
            // Create the detail fragment and add it to the activity
            // using a fragment transaction

            Uri detailUri = getIntent().getData();
            long movieId = getMovieId(detailUri);

            //fetch(movieId);
            //for (int i = 0; i < 1e9; i ++) {

            //}
            Bundle arguments = new Bundle();
            arguments.putParcelable(DetailFragment.DETAIL_URI, detailUri);
            DetailFragment fragment = new DetailFragment();
            createFragmentWithArgs(fragment, arguments);

            // Reset arguments to include the MOVIE_ID instead of the detail URI.
            arguments.clear();
            arguments.putLong(TrailerFragment.MOVIE_ID, movieId);

            TrailerFragment trailerFragment = new TrailerFragment();
            createFragmentWithArgs(trailerFragment, arguments);

            ReviewFragment reviewFragment = new ReviewFragment();
            createFragmentWithArgs(reviewFragment, arguments);

        }
    }

    /**
     * Retrieves movie id from a given movie uri.
     * @param uri
     * @return
     */
    private long getMovieId(Uri uri){
        Cursor movie = getContentResolver().query(
                uri,
                DETAIL_COLUMNS,
                null,
                null,
                null
        );
        if (movie.moveToFirst()){
            return movie.getLong(movie.getColumnIndex(MoviesContract.DetailsColumns.MOVIE_ID));
        }
        Log.d(LOG_TAG, "Cursor doesn't contain anything!");
        return -1;
    }

    /**
     * Creates a fragment, adds it to FragmentManager and sets the arguments.
     */
    private void createFragmentWithArgs(Fragment fragment, Bundle arguments){
        fragment.setArguments(arguments);

        getFragmentManager().beginTransaction()
                .add(R.id.details_container, fragment)
                .commit();
    }
}
