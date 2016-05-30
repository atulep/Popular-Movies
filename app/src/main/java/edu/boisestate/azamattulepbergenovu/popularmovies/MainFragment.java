package edu.boisestate.azamattulepbergenovu.popularmovies;

/* My understanding is that since my minimum SDK is IceCreamSandwich, I am safe to use
android.app.Fragment instead android.support.v4.fragment.
 */

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Fragment containing main UI when app is launched.
 *
 * Created by atulep on 1/23/2016.
 */
public class MainFragment extends Fragment {

    String LOG_TAG = getClass().getSimpleName();
    MoviePosterAdapter adapter;
    ArrayList<Movie> movieList;
    public boolean loadFinished = false;
    private final String NO_INET_CONNECTION = "Oops... Looks like you are not connected to Internet.";

    public MainFragment() {
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null || !savedInstanceState.containsKey(getResources().getString(R.string.parcelable_movieList_key))) {
            movieList = new ArrayList<Movie>();
        } else {
            movieList = savedInstanceState.getParcelableArrayList(getResources().getString(R.string.parcelable_movieList_key));
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.gridfragment_main, container, false);
        adapter = new MoviePosterAdapter(getActivity(), new ArrayList<Movie>());
        GridView grid = (GridView) rootView.findViewById(R.id.gridView_main);
        grid.setAdapter(adapter);

        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent newActivityIntent = new Intent(getActivity(), DetailActivity.class);
                updateTrailerAndReview(parent, position);
                // the API calls should be completed here
                newActivityIntent.putExtra(getResources().getString(R.string.parcelable_movie_key), (Movie) parent.getItemAtPosition(position));
                startActivity(newActivityIntent);
            }

        });
        return rootView;
    }



    /**
     * Not sure if this is elegant?
     * @param parent
     * @param position
     */
    public void updateTrailerAndReview(AdapterView<?> parent, int position) {
        if (isConnected()) {
            Movie moi = (Movie) parent.getItemAtPosition(position); // moi = movie of interest
            ArrayList<TextView> trailerList = new ArrayList<>();
            ArrayList<TextView> reviewList = new ArrayList<>();

            LinearLayout trailersLayout = (LinearLayout) getActivity().findViewById(R.id.trailers_layout);
            LinearLayout reviewsLayout = (LinearLayout) getActivity().findViewById(R.id.reviews_layout);

            for (int i=0; i<moi.trailers.size(); i++) {
                LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                // not sure about this one... i haven't really created this one yet, but well we'll see
                TextView tv=(TextView) getActivity().findViewById(R.id.details_trailer_label);
                TextView revTv=(TextView) getActivity().findViewById(R.id.details_review);
                tv.setLayoutParams(lparams);
                revTv.setLayoutParams(lparams);
                trailerList.add(tv);
                reviewList.add(revTv);
            }
            FetchTrailerDataTask trailerTask = new FetchTrailerDataTask(movieList, moi, trailersLayout, trailerList);
            trailerTask.execute();

            FetchReviewDataTask reviewTask = new FetchReviewDataTask(movieList, moi, reviewsLayout, reviewList);
            reviewTask.execute();
        }
    }

    public void onStart() {
        updateMovies();
        super.onStart();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(getResources().getString(R.string.parcelable_movieList_key), movieList);
        super.onSaveInstanceState(outState);
    }

    /**
     * Spins of back thread to fetch data from network.
     */
    public void updateMovies() {
        if (isConnected()) {
            FetchMovieDataTask task = new FetchMovieDataTask(adapter, movieList);
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
            task.execute(prefs.getString(getString(R.string.settings_key), getString(R.string.settings_default_value)));
        } else {
            displayMessage(NO_INET_CONNECTION);
        }
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


