package edu.boisestate.azamattulepbergenovu.popularmovies;

/* My understanding is that since my minimum SDK is IceCreamSandwich, I am safe to use
android.app.Fragment instead android.support.v4.fragment.
 */

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import java.util.ArrayList;

import edu.boisestate.azamattulepbergenovu.popularmovies.data.MoviesContract;
import edu.boisestate.azamattulepbergenovu.popularmovies.data.MoviesDatabase;
import edu.boisestate.azamattulepbergenovu.popularmovies.data.MoviesProvider;
import edu.boisestate.azamattulepbergenovu.popularmovies.fetch.FetchMovieDataTask;

/**
 * Fragment containing main UI when app is launched.
 *
 * Created by atulep on 1/23/2016.
 */
public class MainFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    String LOG_TAG = getClass().getSimpleName();
    MoviePosterAdapter adapter;
    private final String NO_INET_CONNECTION = "Oops... Looks like you are not connected to Internet.";
    private static final int MOVIES_LOADER = 0;

    // For the movie grid view I'm showing only a small subset of the stored data.
    // Specify the columns we need.
    private static final String[] MOVIE_COLUMNS = {
            MoviesDatabase.DETAILS + "." + MoviesContract.DetailsColumns._ID,
            MoviesContract.DetailsColumns.POSTER_IMAGE
    };

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        void onItemSelected(Uri movieUri);
    }

    public MainFragment() {
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.gridfragment_main, container, false);
        adapter = new MoviePosterAdapter(getActivity(), null, 0); // context, cursor, flags
        GridView grid = (GridView) rootView.findViewById(R.id.gridView_main);
        grid.setAdapter(adapter);

        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // CursorAdapter returns a cursor at the correct position for getItem(), or null
                // if it cannot seek to that position.
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                ((Callback) getActivity())
                        .onItemSelected(MoviesProvider.Details.withId
                                (cursor.getInt(cursor.getColumnIndex(MoviesContract.DetailsColumns._ID))));
            }
        });
        return rootView;
    }


    public void onStart() {
        updateMovies();
        super.onStart();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIES_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // This is called when a new Loader needs to be created.  This
        // fragment only uses one loader, so we don't care about checking the id.

        // Sort order:  Ascending, by title
        String sortOrder = MoviesContract.DetailsColumns.TITLE + " ASC";

        Uri movieDetailsUri = MoviesProvider.Details.CONTENT_URI;

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
        String selection;
        if (prefs.getString(getString(R.string.settings_key),
                getString(R.string.settings_default_value)).equals(getString(R.string.settings_sort_option_popularity))) {
            selection=MoviesContract.DetailsColumns.POPULAR + "=?";
        } else {
            selection=MoviesContract.DetailsColumns.TOP_RATED + "=?";
        }

        String[] selectionArgs={"1"};
        return new CursorLoader(getActivity(),
                movieDetailsUri,
                MOVIE_COLUMNS,
                selection,
                selectionArgs,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Swap the new cursor in.  (The framework will take care of closing the
        // old cursor once we return.)
        adapter.swapCursor(data);
    }

    public void onLoaderReset(Loader<Cursor> loader) {
        // This is called when the last Cursor provided to onLoadFinished()
        // above is about to be closed.  We need to make sure we are no
        // longer using it.
        adapter.swapCursor(null);
    }

    /**
     * Spins of back thread to fetch data from network.
     */
    public void updateMovies() {
        if (isConnected()) {
            //this.getActivity().deleteDatabase(MoviesDatabase.DETAILS);
            FetchMovieDataTask task = new FetchMovieDataTask(getActivity());
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
            task.execute(prefs.getString(getString(R.string.settings_key), getString(R.string.settings_default_value)));
        } else {
            displayMessage(NO_INET_CONNECTION);
        }
    }
    public void onResume() {
        getLoaderManager().restartLoader(MOVIES_LOADER, null, this);
        super.onResume();
    }
    /**
     * Checks network connection.
     *
     * @return true if connected
     */
    private boolean isConnected() {
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }

    /**
     * Displays a message to user as a toast
     *
     * @param message
     */
    private void displayMessage(String message) {
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(getActivity(), message, duration);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

}


