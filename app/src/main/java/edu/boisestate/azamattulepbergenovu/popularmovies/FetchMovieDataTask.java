package edu.boisestate.azamattulepbergenovu.popularmovies;

/**
 * Created by atulep on 2/12/2016.
 */

import android.content.ContentValues;
import android.content.Context;
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
import java.util.List;
import java.util.Vector;

import edu.boisestate.azamattulepbergenovu.popularmovies.data.MoviesContract;
import edu.boisestate.azamattulepbergenovu.popularmovies.data.MoviesProvider;

/**
 * Service class to perform data fetching on back thread.
 */
public class FetchMovieDataTask extends AsyncTask<String, Void, List<Movie>> {
    private String LOG_TAG = this.getClass().getSimpleName();
    private MoviePosterAdapter adapter;
    private ArrayList<Movie> movieList;

    private final Context mContext;

    public FetchMovieDataTask(MoviePosterAdapter adapter, ArrayList<Movie> movieList, Context context) {
        this.adapter = adapter;
        this.movieList = movieList;
        this.mContext = context;
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
                movieList = getMovieDataFromJson(movieJsonStr, typeOfSort);
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

    private List<Movie> getMovieDataFromJson(String movieJsonStr, String typeOfSort)
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

        JSONObject movieJson = new JSONObject(movieJsonStr);
        JSONArray movieArray = movieJson.getJSONArray(OMD_RESULTS);

        Vector<ContentValues> cVVector = new Vector<ContentValues>(movieArray.length());

        for (int i = 0; i < movieArray.length(); i++) {
            // Get the JSON object representing the day
            JSONObject movie = movieArray.getJSONObject(i);
            // notice I am passing null values for the review and trailer. i will populate those later down the road inside
            // of FetchTrailerTask and FetchReviewTask classes.
            // PLEASE, suggest me a more elegant way to do it.

            ContentValues cv = new ContentValues();

            cv.put(MoviesContract.DetailsColumns.MOVIE_ID, movie.getLong(OMD_ID));
            cv.put(MoviesContract.DetailsColumns.TITLE, movie.getString(OMD_TITLE));
            cv.put(MoviesContract.DetailsColumns.POSTER_IMAGE, movie.getString(OMD_POSTER));
            cv.put(MoviesContract.DetailsColumns.PLOT, movie.getString(OMD_PLOT));
            cv.put(MoviesContract.DetailsColumns.RATING, movie.getDouble(OMD_RATING));
            cv.put(MoviesContract.DetailsColumns.RELEASE_DATE, movie.getString(OMD_RELEASE));

            // 1 is true, 0 is false
            if (typeOfSort.contains("popularity")) {
                cv.put(MoviesContract.DetailsColumns.POPULAR, 1);
            } else if (typeOfSort.contains("vote_average")){
                cv.put(MoviesContract.DetailsColumns.TOP_RATED, 1);
            }
            cVVector.add(cv);

            movieList.add(new Movie(movie.getLong(OMD_ID), movie.getString(OMD_TITLE), movie.getString(OMD_POSTER), movie.getString(OMD_PLOT)
                    , movie.getDouble(OMD_RATING), movie.getString(OMD_RELEASE), null, null));
        }

        int inserted = 0;
        // add to database
        if ( cVVector.size() > 0 ) {
            ContentValues[] cvArray = new ContentValues[cVVector.size()];
            cVVector.toArray(cvArray);
            inserted = mContext.getContentResolver().bulkInsert(MoviesProvider.Details.CONTENT_URI, cvArray);
        }

        Log.d(LOG_TAG, "FetchWeatherTask Complete. " + inserted + " Inserted");

        return movieList;
    }

    protected void onPostExecute(List<Movie> list) {
       // adapter.swapCursor(null); // clears data in the cursor

        // this makes sure that the general movie data, most notably the movie ID had already
        // been downloaded.
        FetchTrailerDataTask trailerTask = new FetchTrailerDataTask(movieList);
        trailerTask.execute();

        FetchReviewDataTask reviewTask = new FetchReviewDataTask(movieList);
        reviewTask.execute();

    }
}
