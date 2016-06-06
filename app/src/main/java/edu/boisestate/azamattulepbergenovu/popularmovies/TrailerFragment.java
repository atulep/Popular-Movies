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

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                getActivity(),
                R.layout.trailer_item,
                R.id.details_trailer_label,
                new ArrayList<String>());

        ListView listView = (ListView) rootView.findViewById(R.id.trailer_listview);
        listView.setAdapter(adapter);

        ArrayList<String>trailerList=obj.trailers;

        for (int i=0;i<trailerList.size();i++){
            adapter.add("Trailer " + (i+1));
        }
        
        return rootView;
    }




}
