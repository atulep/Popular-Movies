package edu.boisestate.azamattulepbergenovu.popularmovies;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;
import edu.boisestate.azamattulepbergenovu.popularmovies.Data.MoviesContract;
import edu.boisestate.azamattulepbergenovu.popularmovies.Data.MoviesDatabase;

/**
 * Displays details of the movie.
 * Created by atulep on 2/3/2016.
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private String LOG_TAG = getClass().getSimpleName();
    private static final int MOVIES_LOADER = 0;
    static final String DETAIL_URI = "URI";
    private Uri mUri;
    private static final String[] DETAIL_COLUMNS={
            MoviesDatabase.DETAILS + "." + MoviesContract.DetailsColumns._ID,
            MoviesContract.DetailsColumns.POSTER_IMAGE,
            MoviesContract.DetailsColumns.PLOT,
            MoviesContract.DetailsColumns.TITLE,
            MoviesContract.DetailsColumns.RELEASE_DATE,
            MoviesContract.DetailsColumns.TITLE
    };

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
        if ( null != mUri ) {
            // Now create and return a CursorLoader that will take care of
            // creating a Cursor for the data being displayed.
            return new CursorLoader(
                    getActivity(),
                    mUri,
                    DETAIL_COLUMNS,
                    null,
                    null,
                    null
            );
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Called when a previously created loader has finished its load.
        String posterImage = cursor.getString(cursor.getColumnIndex(MoviesContract.DetailsColumns.POSTER_IMAGE));
        String releaseDate = cursor.getString(cursor.getColumnIndex(MoviesContract.DetailsColumns.RELEASE_DATE));
        double ratingNum = cursor.getDouble(cursor.getColumnIndex(MoviesContract.DetailsColumns.RATING));
        String plotDesc = cursor.getString(cursor.getColumnIndex(MoviesContract.DetailsColumns.PLOT));
        String movieTitle = cursor.getString(cursor.getColumnIndex(MoviesContract.DetailsColumns.TITLE));

        Picasso.with(this.getActivity()).load("http://image.tmdb.org/t/p/w185" + posterImage).into(poster);
        title.setText(movieTitle);
        release.setText(releaseDate);
        rating.setText(Double.valueOf(ratingNum).toString());
        plot.setText(plotDesc);
    }

    public void onLoaderReset(Loader<Cursor> loader) { /* nop */ }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(DetailFragment.DETAIL_URI);
        }

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }
}
