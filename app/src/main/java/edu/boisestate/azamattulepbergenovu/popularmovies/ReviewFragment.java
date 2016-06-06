package edu.boisestate.azamattulepbergenovu.popularmovies;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Displays reviews for the movies. Part of the DetailActivity.
 * Created by atulep on 6/5/2016.
 */
public class ReviewFragment extends Fragment {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = (View) inflater.inflate(R.layout.review_fragment, container, false);
        Movie obj = (Movie) getActivity().getIntent().getParcelableExtra(getResources().getString(R.string.parcelable_movie_key));

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                getActivity(),
                R.layout.review_item,
                R.id.details_review,
                new ArrayList<String>());

        ListView listView = (ListView) rootView.findViewById(R.id.review_listview);
        listView.setAdapter(adapter);

        ArrayList<String>reviewList=obj.reviews;

        adapter.addAll(reviewList);
        return rootView;
    }
}
