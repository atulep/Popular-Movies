package edu.boisestate.azamattulepbergenovu.popularmovies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Activity that displays details of the movie.
 */
public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        if (savedInstanceState == null){
            // Create the detail fragment and add it to the activity
            // using a fragment transaction

            Bundle arguments = new Bundle();
            arguments.putParcelable(DetailFragment.DETAIL_URI, getIntent().getData());

            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(arguments);

            getFragmentManager().beginTransaction()
                    .add(R.id.details_container, fragment)
                    .commit();
        }
    }

}
