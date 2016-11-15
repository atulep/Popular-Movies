package edu.boisestate.azamattulepbergenovu.popularmovies;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import edu.boisestate.azamattulepbergenovu.popularmovies.data.MoviesContract;
import edu.boisestate.azamattulepbergenovu.popularmovies.data.MoviesProvider;

/**
 * Displays reviews for the movies. Part of the DetailActivity.
 * Created by atulep on 6/5/2016.
 */
public class ReviewFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private String LOG_TAG=this.getClass().getSimpleName();
    public static String MOVIE_ID = "MOVIE_ID";
    private static final int REVIEW_LOADER = 1;
    private long movieId;

    private LinearLayout insertionPoint;
    private LayoutInflater inflater;

    private static final String[] REVIEW_COLUMNS = {
      MoviesContract.ReviewColumns.REVIEW
    };

    private static final int COL_REVIEW = 0;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = (View) inflater.inflate(R.layout.review_fragment, container, false);
        this.inflater = inflater;

        Bundle arguments = getArguments();
        if (arguments != null) {
            movieId = arguments.getLong(MOVIE_ID);
        }

        insertionPoint=(LinearLayout)rootView.findViewById(R.id.review_container);
        return rootView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(REVIEW_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // This is called when a new Loader needs to be created.  This
        // fragment only uses one loader, so we don't care about checking the id.
        Uri reviewUri = MoviesProvider.Reviews.CONTENT_URI;
        String selection = MoviesContract.ReviewColumns.MOVIE_ID + "=?";
        String[] selectionArgs={String.valueOf(movieId)};

        return new CursorLoader(getActivity(),
                reviewUri,
                REVIEW_COLUMNS,
                selection,
                selectionArgs,
                null);
    }

    public void onLoadFinished(Loader<Cursor> loader, Cursor data){
        DatabaseUtils.dumpCursor(data);
        if (data != null) {
            while (data.moveToNext()) {
                int pos = 0;
                String review = data.getString(COL_REVIEW);

                View reviewView = inflater.inflate(R.layout.review_item, null);
                TextView textView = (TextView) reviewView.findViewById(R.id.details_review);
                textView.setText(review);
                insertionPoint.addView(reviewView, pos, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                pos++;
            }
        } else {
            Log.d(LOG_TAG, "Cursor was null");
        }
    }

    public void onLoaderReset(Loader<Cursor> loader) { /* nop */ }
}
