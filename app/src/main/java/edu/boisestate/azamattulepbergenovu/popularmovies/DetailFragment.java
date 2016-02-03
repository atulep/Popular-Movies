package edu.boisestate.azamattulepbergenovu.popularmovies;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by atulep on 2/3/2016.
 */
public class DetailFragment extends Fragment {

    public DetailFragment() {

    }

    public void onCreate(Bundle savedInstanceBundle) {
        super.onCreate(savedInstanceBundle);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState,) {
       // super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        //TODO: May want to rewatch podcast on Parcelable and implement my movie object as parcelable to send it to DetailActivity.
        //TODO: Then I can extract all information from the Parcelable object.

        //TODO: However, probably the first step would be to simply use serializable just to test that my details layout
        //TODO: works properly. 
        return rootView;
    }


}
