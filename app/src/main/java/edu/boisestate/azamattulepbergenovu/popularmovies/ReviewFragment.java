package edu.boisestate.azamattulepbergenovu.popularmovies;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Displays reviews for the movies. Part of the DetailActivity.
 * Created by atulep on 6/5/2016.
 */
public class ReviewFragment extends Fragment {

    private String LOG_TAG=this.getClass().getSimpleName();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = (View) inflater.inflate(R.layout.review_fragment, container, false);
        Movie obj = (Movie) getActivity().getIntent().getParcelableExtra(getResources().getString(R.string.parcelable_movie_key));

        LinearLayout insertionPoint=(LinearLayout)rootView.findViewById(R.id.review_container);

        for (int i=0; i<obj.reviews.size(); i++) {
            View reviewView=inflater.inflate(R.layout.review_item, null);
            TextView textView=(TextView)reviewView.findViewById(R.id.details_review);
            textView.setText(obj.reviews.get(i));
            insertionPoint.addView(reviewView, i, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }
        return rootView;
    }
}
