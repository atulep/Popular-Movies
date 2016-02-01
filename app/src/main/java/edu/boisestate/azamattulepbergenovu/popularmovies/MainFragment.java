package edu.boisestate.azamattulepbergenovu.popularmovies;

/* My understanding is that since my minimum SDK is IceCreamSandwich, I am safe to use
android.app.Fragment instead android.support.v4.fragment.
 */

import android.app.Fragment;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

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
 * Created by atulep on 1/23/2016.
 */
public class MainFragment extends Fragment {

    String LOG_TAG = getClass().getSimpleName();
    MoviePosterAdapter adapter;

    /**
     * Pubic no-argument constructor.
     */
    public MainFragment() {
    }

    /**
     * Not sure if I need to define this method.
     *
     * @param savedInstanceState
     */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.gridfragment_main, container, false);
        adapter = new MoviePosterAdapter(getActivity(), new ArrayList<Movie>());
        GridView grid = (GridView) rootView.findViewById(R.id.gridView_main);
        grid.setAdapter(adapter);
        return rootView;
    }

    public void onStart() {
        updateMovies();
        super.onStart();
    }

    public void updateMovies() {
        FetchMovieDataTask task = new FetchMovieDataTask();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
        task.execute(prefs.getString(getString(R.string.settings_key), getString(R.string.settings_default_value)));
    }

    public class FetchMovieDataTask extends AsyncTask<String, Void, List<Movie>> {
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
                    movieList = getWeatherDataFromJson(movieJsonStr);
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

        /**
         * Take the String representing the complete forecast in JSON Format and
         * pull out the data we need to construct the Strings needed for the wireframes.
         * <p/>
         * Fortunately parsing is easy:  constructor takes the JSON string and converts it
         * into an Object hierarchy for us.
         */
        private List<Movie> getWeatherDataFromJson(String movieJsonStr)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String OMD_TITLE = "original_title";
            final String OMD_POSTER = "poster_path";
            final String OMD_PLOT = "overview";
            final String OMD_RATING = "vote_average";
            final String OMD_RELEASE = "release_date";
            final String OMD_RESULTS = "results";

            List<Movie> movieList = new ArrayList<>();
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


