package edu.boisestate.azamattulepbergenovu.popularmovies;

import android.app.Fragment;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import edu.boisestate.azamattulepbergenovu.popularmovies.data.MoviesContract;

/**
 * Activity that displays details of the movie.
 */
public class DetailActivity extends AppCompatActivity {

    private String[] DETAIL_COLUMNS = {MoviesContract.DetailsColumns.MOVIE_ID};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        if (savedInstanceState == null){
            // Create the detail fragment and add it to the activity
            // using a fragment transaction

            Uri detailUri = getIntent().getData();
            long movieId = getMovieId(detailUri);

            Bundle arguments = new Bundle();
            arguments.putParcelable(DetailFragment.DETAIL_URI, detailUri);
            DetailFragment fragment = new DetailFragment();
            createFragmentWithArgs(fragment, arguments);

            // Reset arguments to include the MOVIE_ID instead of the detail URI.
            arguments.clear();
            arguments.putLong(TrailerFragment.MOVIE_ID, movieId);

            ReviewFragment reviewFragment = new ReviewFragment();
            createFragmentWithArgs(reviewFragment, arguments);

            TrailerFragment trailerFragment = new TrailerFragment();
            createFragmentWithArgs(trailerFragment, arguments);
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
        return movie.getLong(movie.getColumnIndex(MoviesContract.DetailsColumns.MOVIE_ID));
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
