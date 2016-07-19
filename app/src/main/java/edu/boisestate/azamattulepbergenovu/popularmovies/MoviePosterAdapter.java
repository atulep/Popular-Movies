package edu.boisestate.azamattulepbergenovu.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import edu.boisestate.azamattulepbergenovu.popularmovies.data.MoviesContract;

/**
 * Custom CursorAdapter to display the movies UI on the main grid.
 *
 * Created by atulep on 1/24/2016.
 */
public class MoviePosterAdapter extends CursorAdapter {
    private String LOG_TAG = MoviePosterAdapter.class.getSimpleName();
    private Context context;

    public static class ViewHolder {
        ImageView posterView;

        public ViewHolder(View v) {
            posterView = (ImageView) v.findViewById(R.id.movie_poster);
        }
    }

    /**
     * Constructor.
     * @param context
     * @param
     */
    public MoviePosterAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        this.context=context;
    }

    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.gridfragment_main, parent, false);
        ViewHolder holder = new ViewHolder(view);
        view.setTag(holder); //TODO: what does set tag do?
        return view;
    }

    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder) view.getTag();
        // XXX: Not sure here.
        String posterImage = cursor.getString(cursor.getColumnIndex(MoviesContract.DetailsColumns.POSTER_IMAGE));
        Picasso.with(this.context).load("http://image.tmdb.org/t/p/w185" + posterImage).into(holder.posterView);
    }

}
