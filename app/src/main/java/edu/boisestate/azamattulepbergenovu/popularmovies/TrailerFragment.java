package edu.boisestate.azamattulepbergenovu.popularmovies;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Displays a
 * Created by atulep on 6/5/2016.
 */
public class TrailerFragment extends Fragment {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = (View) inflater.inflate(R.layout.trailer_fragment, container, false);
        Movie obj = (Movie) getActivity().getIntent().getParcelableExtra(getResources().getString(R.string.parcelable_movie_key));

        LinearLayout insertionPoint=(LinearLayout) rootView.findViewById(R.id.trailer_container);

        for (int i=0; i<obj.trailers.size();i++) {
            View trailerView=(View)inflater.inflate(R.layout.trailer_item, null);
            TextView textView=(TextView)trailerView.findViewById(R.id.details_trailer_label);
            textView.setText("Trailer " + i);
            // adding an item listener
            LinearLayout itemContainer=(LinearLayout)trailerView.findViewById(R.id.trailer_item_container);
            final String youtubeKey=obj.trailers.get(i);
            insertionPoint.addView(trailerView, i, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            itemContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String youtubeLink = "http://www.youtube.com/watch?v=";
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(youtubeLink + youtubeKey)));
                }
            });
        }
        return rootView;
    }

}
