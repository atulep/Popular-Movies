package edu.boisestate.azamattulepbergenovu.popularmovies.fetch;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Vector;

import edu.boisestate.azamattulepbergenovu.popularmovies.Movie;
import edu.boisestate.azamattulepbergenovu.popularmovies.data.MoviesContract;
import edu.boisestate.azamattulepbergenovu.popularmovies.data.MoviesProvider;

/**
 * Created by atulep on 2/12/2016.
 */

/**
 * Service class to perform data fetching on back thread.
 */
public class FetchReviewDataTask extends AsyncTask<Void, Void, Void> {
    private String LOG_TAG = this.getClass().getSimpleName();
    private Context mContext;
    private String[] DETAIL_COLUMNS = {
            MoviesContract.DetailsColumns.MOVIE_ID
    };

    public FetchReviewDataTask(Context context){
        mContext = context;
    }
    public Void doInBackground(Void... params) {
        Cursor movieList = mContext.getContentResolver().query(
                MoviesProvider.Details.CONTENT_URI,
                DETAIL_COLUMNS,
                null,
                null,
                null
        );

        if (movieList != null)
            while (movieList.moveToNext()) {
                HttpURLConnection urlConnection = null;
                BufferedReader reader = null;
                String movieJsonStr;
                Long movieId = movieList.getLong(movieList.getColumnIndex(MoviesContract.DetailsColumns.MOVIE_ID));

                try {

                    final String MOVIE_BASE_URL =
                            "http://api.themoviedb.org/3/movie/" + movieId + "/reviews?";
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
                        getMovieDataFromJson(movieJsonStr, movieId);
                    } catch (org.json.JSONException e) {
                        Log.e(LOG_TAG, "ERROR with fetching the simpliged forecast.");
                        System.exit(1);
                    }

                } catch (FileNotFoundException e){
                    Log.e(LOG_TAG, "Error", e);
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
            }
        return null;
    }

    private void getMovieDataFromJson(String movieJsonStr, long movie_id)
            throws JSONException {

        // These are the names of the JSON objects that need to be extracted.
        final String OMD_CONTENT="content";
        final String OMD_RESULTS = "results";
        Log.v(LOG_TAG, movieJsonStr);
        JSONObject reviewsJson = new JSONObject(movieJsonStr);
        JSONArray reviewArray = reviewsJson.getJSONArray(OMD_RESULTS);

        Vector<ContentValues> cVVector = new Vector<ContentValues>(reviewArray.length());

        for (int i = 0; i < reviewArray.length(); i++) {
            // Get the JSON object representing the day
            JSONObject review = reviewArray.getJSONObject(i);

            ContentValues cv = new ContentValues();
            cv.put(MoviesContract.ReviewColumns.MOVIE_ID, movie_id);
            cv.put(MoviesContract.ReviewColumns.REVIEW, review.getString(OMD_CONTENT));
            cVVector.add(cv);
        }

        int inserted = 0;
        // add to database
        if ( cVVector.size() > 0 ) {
            ContentValues[] cvArray = new ContentValues[cVVector.size()];
            cVVector.toArray(cvArray);
            inserted = mContext.getContentResolver().bulkInsert(MoviesProvider.Reviews.CONTENT_URI, cvArray);
        }
        Log.d(LOG_TAG, "FetchReviewTask Complete. " + inserted + " Inserted");
    }
}
