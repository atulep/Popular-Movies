package edu.boisestate.azamattulepbergenovu.popularmovies;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by atulep on 2/3/2016.
 */
public class DetailFragment extends Fragment {
    private String LOG_TAG = getClass().getSimpleName();
    public DetailFragment() {

    }

    public void onCreate(Bundle savedInstanceBundle) {
        super.onCreate(savedInstanceBundle);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        //TODO: May want to rewatch podcast on Parcelable and implement my movie object as parcelable to send it to DetailActivity.
        //TODO: Then I can extract all information from the Parcelable object.

        //TODO: However, probably the first step would be to simply use serializable just to test that my details layout
        //TODO: works properly.
        Movie obj = (Movie) getActivity().getIntent().getSerializableExtra("Movie");
        updateMovieDetails(rootView, obj);
        return rootView;
    }

    public void updateMovieDetails(View view, Movie movie){
        ImageView poster = (ImageView) view.findViewById(R.id.details_imageView);
        Picasso.with(this.getActivity()).load("http://image.tmdb.org/t/p/w154" + movie.posterImage).into(poster);

        TextView title = (TextView) view.findViewById(R.id.details_title);
        title.setText(movie.title);

        TextView release = (TextView) view.findViewById(R.id.details_release);
        release.setText(movie.releaseDate);

        TextView rating = (TextView) view.findViewById(R.id.details_rating);
        rating.setText(new Double(movie.rating).toString());

        TextView plot = (TextView) view.findViewById(R.id.details_plot);
        plot.setText(movie.plot);
    }



}
