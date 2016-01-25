package edu.boisestate.azamattulepbergenovu.popularmovies;

/* My understanding is that since my minimum SDK is IceCreamSandwich, I am safe to use
android.app.Fragment instead android.support.v4.fragment.
 */

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import java.util.Arrays;

/**
 * Created by atulep on 1/23/2016.
 */
public class MainFragment extends Fragment {

    //Hard coded list of movies.
    Movie[] list = {
      new Movie()
    };

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

        View rootView = inflater.inflate(R.layout.activity_main, container, false);
        MoviePosterAdapter adapter = new MoviePosterAdapter(getActivity(), Arrays.asList(list));
        GridView grid = (GridView) rootView.findViewById(R.id.gridView_main);
        grid.setAdapter(adapter);
        return rootView;
    }

}
