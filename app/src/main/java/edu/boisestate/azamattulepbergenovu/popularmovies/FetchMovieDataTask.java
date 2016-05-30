package edu.boisestate.azamattulepbergenovu.popularmovies;

/**
 * Created by atulep on 2/12/2016.
 */

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;

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
 * Service class to perform data fetching on back thread.
 */
public class FetchMovieDataTask extends AsyncTask<String, Void, List<Movie>> {
    private String LOG_TAG = this.getClass().getSimpleName();
    private ArrayAdapter<Movie> adapter;
    private ArrayList<Movie> movieList;

    public FetchMovieDataTask(ArrayAdapter adapter, ArrayList<Movie> movieList) {
        this.adapter = adapter;
        this.movieList = movieList;
    }

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
        final String OMD_ID = "id";
        if (!movieList.isEmpty()) {
            movieList.clear();
        }

        JSONObject forecastJson = new JSONObject(movieJsonStr);
        JSONArray movieArray = forecastJson.getJSONArray(OMD_RESULTS);

        for (int i = 0; i < movieArray.length(); i++) {
            // Get the JSON object representing the day
            JSONObject movie = movieArray.getJSONObject(i);
            // notice I am passing null values for the review and trailer. i will populate those later down the road inside
            // of FetchTrailerTask and FetchReviewTask classes.
            // PLEASE, suggest me a more elegant way to do it.
            movieList.add(new Movie(movie.getLong(OMD_ID), movie.getString(OMD_TITLE), movie.getString(OMD_POSTER), movie.getString(OMD_PLOT)
                    , movie.getDouble(OMD_RATING), movie.getString(OMD_RELEASE), null, null));
        }

        return movieList;
    }

    protected void onPostExecute(List<Movie> list) {
        adapter.clear();
        adapter.addAll(list);
    }
}
