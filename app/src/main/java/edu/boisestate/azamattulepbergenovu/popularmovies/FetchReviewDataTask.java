package edu.boisestate.azamattulepbergenovu.popularmovies;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

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

/**
 * Created by atulep on 2/12/2016.
 */

/**
 * Service class to perform data fetching on back thread.
 */
public class FetchReviewDataTask extends AsyncTask<Void, Integer, Integer> {
    private String LOG_TAG = this.getClass().getSimpleName();
    private ArrayList<Movie> movieList;
    private Movie movie;
    private MainFragment f;

    public FetchReviewDataTask(ArrayList<Movie> movieList, Movie movie, MainFragment f) {
        this.movieList = movieList; // will return movieList so the changes to movies will persist
        this.movie=movie;
        this.f=f;
    }

    public Integer doInBackground(Void... params) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String movieJsonStr;
        Long movieId = movie.getId();
        try {

            final String MOVIE_BASE_URL =
                    "http://api.themoviedb.org/3/movie/"+movieId+"/reviews?";
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
        return 0;
    }

    private void getMovieDataFromJson(String movieJsonStr)
            throws JSONException {

        // These are the names of the JSON objects that need to be extracted.
        final String OMD_CONTENT="content";
        final String OMD_RESULTS = "results";

        JSONObject forecastJson = new JSONObject(movieJsonStr);
        JSONArray reviewArray = forecastJson.getJSONArray(OMD_RESULTS);
        ArrayList<String> reviewKeys = new ArrayList<String>();

        for (int i = 0; i < reviewArray.length(); i++) {
            // Get the JSON object representing the day
            JSONObject review = reviewArray.getJSONObject(i);
            // notice I am passing null values for the review and trailer. i will populate those later down the road inside
            // of FetchTrailerTask and FetchReviewTask classes.
            // PLEASE, suggest me a more elegant way to do it.
            reviewKeys.add(review.getString(OMD_CONTENT));
        }

        // Takes O(n). How can it be improved what do you think?
        for (Movie movie:movieList) {
            if (movie.equals(this.movie)) {
                movie.setReviews(reviewKeys);
            }
        }
        f.loadFinished=true;
    }

    protected void onPostExecute(Integer i) {
        // again adding views to layout here to ensure data fetching got completed.
        f.loadFinished=true;
    }

}
