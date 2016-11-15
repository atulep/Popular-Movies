package edu.boisestate.azamattulepbergenovu.popularmovies.fetch;

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
import java.util.Vector;
import edu.boisestate.azamattulepbergenovu.popularmovies.data.MoviesContract;
import edu.boisestate.azamattulepbergenovu.popularmovies.data.MoviesProvider;

/**
 * Service class to perform data fetching on back thread.
 */
public class FetchMovieDataTask extends AsyncTask<String, Void, Void> {
    private String LOG_TAG = this.getClass().getSimpleName();

    private final Context mContext;

    public FetchMovieDataTask(Context context) {
        this.mContext = context;
    }

    public Void doInBackground(String... params) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String typeOfSort = params[0];// which sort to perform
        Log.v(LOG_TAG, typeOfSort);
        String movieJsonStr;
        assert typeOfSort.equals("top_rated") || typeOfSort.equals("popular");
        try {

            final String MOVIE_BASE_URL =
                    "http://api.themoviedb.org/3/movie/" + typeOfSort + "?";
            final String APPID_PARAM = "api_key";

            Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                    .appendQueryParameter(APPID_PARAM, edu.boisestate.azamattulepbergenovu.popularmovies.BuildConfig.MOVIE_DB_API_KEY)
                    .build();

            URL url = new URL(builtUri.toString());

            Log.v(LOG_TAG, url.toString());

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
                getMovieDataFromJson(movieJsonStr, typeOfSort);
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
        return null;
    }

    private void getMovieDataFromJson(String movieJsonStr, String typeOfSort)
            throws JSONException {

        // These are the names of the JSON objects that need to be extracted.
        final String OMD_TITLE = "original_title";
        final String OMD_POSTER = "poster_path";
        final String OMD_PLOT = "overview";
        final String OMD_RATING = "vote_average";
        final String OMD_RELEASE = "release_date";
        final String OMD_RESULTS = "results";
        final String OMD_ID = "id";

        JSONObject movieJson = new JSONObject(movieJsonStr);
        JSONArray movieArray = movieJson.getJSONArray(OMD_RESULTS);

        Vector<ContentValues> cVVector = new Vector<ContentValues>(movieArray.length());

        for (int i = 0; i < movieArray.length(); i++) {
            JSONObject movie = movieArray.getJSONObject(i);

            ContentValues cv = new ContentValues();
            cv.put(MoviesContract.DetailsColumns.MOVIE_ID, movie.getLong(OMD_ID));
            cv.put(MoviesContract.DetailsColumns.TITLE, movie.getString(OMD_TITLE));
            cv.put(MoviesContract.DetailsColumns.POSTER_IMAGE, movie.getString(OMD_POSTER));
            cv.put(MoviesContract.DetailsColumns.PLOT, movie.getString(OMD_PLOT));
            cv.put(MoviesContract.DetailsColumns.RATING, movie.getDouble(OMD_RATING));
            cv.put(MoviesContract.DetailsColumns.RELEASE_DATE, movie.getString(OMD_RELEASE));

            // 1 is true, 0 is false
            if (typeOfSort.contains("popular")) {
                cv.put(MoviesContract.DetailsColumns.POPULAR, 1);
            } else if (typeOfSort.contains("top_rated")){
                cv.put(MoviesContract.DetailsColumns.TOP_RATED, 1);
            }
            cVVector.add(cv);

        }

        int inserted = 0;
        // add to database
        if ( cVVector.size() > 0 ) {
            ContentValues[] cvArray = new ContentValues[cVVector.size()];
            cVVector.toArray(cvArray);
            inserted = mContext.getContentResolver().bulkInsert(MoviesProvider.Details.CONTENT_URI, cvArray);
        }
        Log.d(LOG_TAG, "FetchMovieTask Complete. " + inserted + " Inserted");
    }

    protected void onPostExecute(Void nothing) {
        // this makes sure that the general movie data, most notably the movie ID had already
        // been downloaded.

        // TODO: Should I pass a list of movie id's to both fetch task, or have each task query db.
        FetchTrailerDataTask trailerTask = new FetchTrailerDataTask(mContext);
        trailerTask.execute();

        FetchReviewDataTask reviewTask = new FetchReviewDataTask(mContext);
        reviewTask.execute();
    }
}
