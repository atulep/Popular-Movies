package edu.boisestate.azamattulepbergenovu.popularmovies;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
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
 * Displays a
 * Created by atulep on 6/5/2016.
 */
public class TrailerFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    public static String MOVIE_ID = "MOVIE_ID";
    private final String LOG_TAG = getClass().getSimpleName();
    private final static int TRAILER_LOADER = 2;

    private static final String[] TRAILER_COLUMNS = {
        MoviesContract.TrailerColumns.TRAILER_PATH
    };
    private static final int COL_TRAILER = 0;

    private long movieId;
    private LayoutInflater inflater;
    private LinearLayout insertionPoint;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = (View) inflater.inflate(R.layout.trailer_fragment, container, false);
        insertionPoint=(LinearLayout) rootView.findViewById(R.id.trailer_container);

        Bundle arguments = getArguments();
        if (arguments != null) {
            movieId = arguments.getLong(DetailFragment.DETAIL_URI);
        }

        this.inflater = inflater;
        return rootView;
    }

    public void onLoadFinished(Loader<Cursor> loader, Cursor data){
        if (data != null) {
            while (data.moveToNext()) {
                int pos = 0;
                final String trailer = data.getString(COL_TRAILER);
                View trailerView = (View) inflater.inflate(R.layout.trailer_item, null);
                TextView textView=(TextView) trailerView.findViewById(R.id.details_trailer_label);
                textView.setText("Trailer " + (pos+1));

                trailerView.setOnClickListener(new View.OnClickListener(){
                    public void onClick(View v) {
                        String youtubeLink = "http://www.youtube.com/watch?v=";
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(youtubeLink + trailer)));
                    }
                });

                insertionPoint.addView(trailerView, pos, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                pos++;
            }
        } else {
            Log.d(LOG_TAG, "Cursor was null");
        }

    }

    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        // This is called when a new Loader needs to be created.  This
        // fragment only uses one loader, so we don't care about checking the id.
        Uri trailerUri = MoviesProvider.Trailers.CONTENT_URI;
        String selection = MoviesContract.TrailerColumns.MOVIE_ID + "=?";
        String[] selectionArgs={String.valueOf(movieId)};

        return new CursorLoader(getActivity(),
                trailerUri,
                TRAILER_COLUMNS,
                selection,
                selectionArgs,
                null);
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(TRAILER_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    public void onLoaderReset(Loader<Cursor>loader){/*nop*/}
}
