package edu.boisestate.azamattulepbergenovu.popularmovies;

/* My understanding is that since my minimum SDK is IceCreamSandwich, I am safe to use
android.app.Fragment instead android.support.v4.fragment.
 */

import android.app.Fragment;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by atulep on 1/23/2016.
 */
public class MainFragment extends Fragment {

    // for temporary purposes in developement
    String tempPlot = "This is ";
    String tempDate = "01/26/2015";
    //

    ArrayList<Movie> movieList = new ArrayList<Movie>();
    //Hard coded list of movies.
    Movie[] list = {
      new Movie("Avatar", R.drawable.avatar, tempPlot + "Avatar", 5.0, tempDate),
      new Movie("Avengers", R.drawable.avengers, tempPlot + "Avengers", 5.0, tempDate),
      new Movie("Grey", R.drawable.grey, tempPlot + "Grey", 5.0, tempDate),
      //new Movie("Iron Man", R.drawable.iron_man, tempPlot + "Iron Man", 5.0, tempDate),
      //new Movie("Minions", R.drawable.minions, tempPlot +"Minions", 5.0, tempDate)
    };

    // int posterImage, String plot, double rating, String releaseDate
    /**
     * Pubic no-argument constructor.
     */
    public MainFragment () {
    }

    /**
     * Not sure if I need to define this method.
     * @param savedInstanceState
     */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //TODO: Need to create create an AdapterView -- GridView. Bind the MoviePosterAdapter to it.
        //Return the resulted view.
        //Need to check if savedInstanceState exists, if so then inflate it.
        //Otherwise, need to modify container to be a GridView.
        //Create an adapter to bind it a gridview.
        //Get a view from a GridView and return it.

        View rootView = inflater.inflate(R.layout.gridfragment_main, container, false);
        MoviePosterAdapter adapter = new MoviePosterAdapter(getActivity(), Arrays.asList(list));
        GridView grid = (GridView) rootView.findViewById(R.id.gridView_main);
        grid.setAdapter(adapter);
        return rootView;
    }

    public class FetchMovieDataTask extends AsyncTask<Void, Void, Void> {
        private String LOG_TAG = this.getClass().getSimpleName();

        public Void doInBackground(Void ... params) {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String typeOfSort = params[0];// which sort to perform
            // Will contain the raw JSON response as a string.
            String movieJsonStr = null;

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are available at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                final String MOVIE_BASE_URL =
                        "http://api.themoviedb.org/3/discover/movie?";
                final String QUERY_PARAM = "sort_by=";
                final String APPID_PARAM = "APPID";

                Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                        .appendQueryParameter(QUERY_PARAM, typeOfSort)
                        .appendQueryParameter(APPID_PARAM, BuildConfig.MOVIE_DB_API_KEY)
                        .build();

                URL url = new URL(builtUri.toString());

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
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                movieJsonStr = buffer.toString();
                Log.v(LOG_TAG, "JSON Parsed String:" + movieJsonStr);
                //TODO: Insert the rest of code regarding JSON parsing.
                //TODO: After that I can simply fetch JSON objects and create views with images.
                //TODO: Then I can create a details activity.
                //TODO: Then I can add preferences to sort movies.
                //TODO: Implement parcelable too!
                try {
                    // This one :-) here
                    simplifiedForecast = getWeatherDataFromJson(movieJsonStr, 7);
                } catch (org.json.JSONException e) {
                    Log.e(LOG_TAG, "ERROR with fetching the simpliged forecast.");
                    System.exit(1);
                }

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attempting
                // to parse it.
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
            return simplifiedForecast;
        }
        
        /**
         * Take the String representing the complete forecast in JSON Format and
         * pull out the data we need to construct the Strings needed for the wireframes.
         *
         * Fortunately parsing is easy:  constructor takes the JSON string and converts it
         * into an Object hierarchy for us.
         */
        private void getWeatherDataFromJson(String movieJsonStr, int numDays)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String OMD_TITLE= "original_title";
            final String OMD_POSTER= "poster_path";
            final String OMD_PLOT = "overview";
            final String OMD_RATING = "vote_average";
            final String OMD_RELEASE = "release_date";
            final String OMD_RESULTS = "results";

            JSONObject forecastJson = new JSONObject(movieJsonStr);
            JSONArray movieArray = forecastJson.getJSONArray(OMD_RESULTS);

            for(int i = 0; i < movieArray.length(); i++) {
                // Get the JSON object representing the day
                JSONObject movie = movieArray.getJSONObject(i);
                movieList.add(new Movie(movie.getString(OMD_TITLE), movie.getString(OMD_POSTER), movie.getString(OMD_PLOT)
                , movie.getDouble(OMD_RATING), movie.getString(OMD_RELEASE)));
            }


            for (int i = 0; i < movieList.size(); i ++) {
                Log.v(LOG_TAG, "PRINTING" + movieList.get(i));
            }
        }

    }
    // END OF ASYNC TASK
    }
//END OF FRAGMENT

