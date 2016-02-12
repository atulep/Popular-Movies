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
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Fragment containing main UI when app is launched.
 *
 * Created by atulep on 1/23/2016.
 */
public class MainFragment extends Fragment {

    String LOG_TAG = getClass().getSimpleName();
    MoviePosterAdapter adapter;
    ArrayList<Movie> movieList;
    private final String NO_INET_CONNECTION = "Oops... Looks like you are not connected to Internet.";

    public MainFragment() {
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState == null || !savedInstanceState.containsKey(getResources().getString(R.string.parcelable_movieList_key))) {
            movieList = new ArrayList<Movie>();
        }
        else {
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
                newActivityIntent.putExtra(getResources().getString(R.string.parcelable_movie_key), (Movie) parent.getItemAtPosition(position));
                startActivity(newActivityIntent);
            }

        });
        return rootView;
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
        ConnectivityManager cm =
                (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        if (isConnected) {
            FetchMovieDataTask task = new FetchMovieDataTask();
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
            task.execute(prefs.getString(getString(R.string.settings_key), getString(R.string.settings_default_value)));
        } else {
            displayMessage(NO_INET_CONNECTION);
        }
    }

    /**
     * Displays a message to user as a toast
     * @param message
     */
    private void displayMessage(String message) {
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(getActivity(), message, duration);
        toast.show();
    }

    /**
     * Service class to perform data fetching on back thread.
     */
    private class FetchMovieDataTask extends AsyncTask<String, Void, List<Movie>> {
        private String LOG_TAG = this.getClass().getSimpleName();

        public List<Movie> doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String typeOfSort = params[0];// which sort to perform
            String movieJsonStr;
            List<Movie> movieList = null;

            try {

                final String MOVIE_BASE_URL =
                        "http://api.themoviedb.org/3/discover/movie?";
                final String QUERY_PARAM = "sort_by";
                final String APPID_PARAM = "api_key";

                Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                        .appendQueryParameter(QUERY_PARAM, typeOfSort)
                        .appendQueryParameter(APPID_PARAM, edu.boisestate.azamattulepbergenovu.popularmovies.BuildConfig.MOVIE_DB_API_KEY)
                        .build();

                URL url = new URL(builtUri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;

                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }
                movieJsonStr = buffer.toString();

                try {
                    movieList = getMovieDataFromJson(movieJsonStr);
                } catch (org.json.JSONException e) {
                    Log.e(LOG_TAG, "ERROR with fetching the simpliged forecast.");
                    System.exit(1);
                }

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                movieJsonStr = null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            return movieList;
        }

        private List<Movie> getMovieDataFromJson(String movieJsonStr)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String OMD_TITLE = "original_title";
            final String OMD_POSTER = "poster_path";
            final String OMD_PLOT = "overview";
            final String OMD_RATING = "vote_average";
            final String OMD_RELEASE = "release_date";
            final String OMD_RESULTS = "results";

            if (!movieList.isEmpty()) {
                movieList.clear();
            }

            JSONObject forecastJson = new JSONObject(movieJsonStr);
            JSONArray movieArray = forecastJson.getJSONArray(OMD_RESULTS);

            for (int i = 0; i < movieArray.length(); i++) {
                // Get the JSON object representing the day
                JSONObject movie = movieArray.getJSONObject(i);
                movieList.add(new Movie(movie.getString(OMD_TITLE), movie.getString(OMD_POSTER), movie.getString(OMD_PLOT)
                        , movie.getDouble(OMD_RATING), movie.getString(OMD_RELEASE)));
            }

            return movieList;
        }

        protected void onPostExecute(List<Movie> list) {
            adapter.clear();
            adapter.addAll(list);
        }
    }
}


