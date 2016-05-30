package edu.boisestate.azamattulepbergenovu.popularmovies;

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
 * Created by atulep on 2/12/2016.
 */

/**
 * Service class to perform data fetching on back thread.
 */
public class FetchTrailerDataTask extends AsyncTask<Void, Void, List<Movie>> {
    private String LOG_TAG = this.getClass().getSimpleName();
    private ArrayAdapter<Movie> adapter;
    private ArrayList<Movie> movieList;
    private Movie movie;

    public FetchTrailerDataTask(ArrayAdapter adapter, ArrayList<Movie> movieList, Movie movie) {
        this.adapter = adapter;
        this.movieList = movieList; // will return movieList so the changes to movies will persist
        this.movie=movie;
    }

    public List<Movie> doInBackground(Void... params) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        Long movieId = movie.getId();// don't neccesarily need this one, but will change it later (since I have reference to a movie).
        String movieJsonStr;

        try {

            final String MOVIE_BASE_URL =
                    "http://api.themoviedb.org/3/movie/"+movieId+"/videos?";
            final String APPID_PARAM = "api_key";

            Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
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
                getMovieDataFromJson(movieJsonStr);
                // for testing
                for (Movie movie:movieList) {
                    if (movie.trailers != null) {
                        Log.v(LOG_TAG, "SIZE OF TRAILER IS: " + movie.trailers.size());
                    }
                }

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

    private void getMovieDataFromJson(String movieJsonStr)
            throws JSONException {

        // These are the names of the JSON objects that need to be extracted.
        final String OMD_KEY="key";
        final String OMD_RESULTS = "results";

        JSONObject forecastJson = new JSONObject(movieJsonStr);
        JSONArray trailerArray = forecastJson.getJSONArray(OMD_RESULTS);
        ArrayList<String> trailerKeys = new ArrayList<>();

        for (int i = 0; i < trailerArray.length(); i++) {
            // Get the JSON object representing the day
            JSONObject trailer = trailerArray.getJSONObject(i);
            // notice I am passing null values for the review and trailer. i will populate those later down the road inside
            // of FetchTrailerTask and FetchReviewTask classes.
            // PLEASE, suggest me a more elegant way to do it.
            trailerKeys.add(trailer.getString(OMD_KEY));
        }

        // Takes O(n). How can it be improved what do you think?
        for (Movie movie:movieList) {
            if (movie.equals(this.movie)) {
                movie.setTrailers(trailerKeys);
                Log.v(LOG_TAG, "Set trailer here!!&!&!&");
            }
        }
    }

    protected void onPostExecute(List<Movie> list) {
        adapter.clear();
        adapter.addAll(list);
    }

}
