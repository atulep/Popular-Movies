package edu.boisestate.azamattulepbergenovu.popularmovies.data;

import android.net.Uri;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.InexactContentUri;
import net.simonvt.schematic.annotation.TableEndpoint;

/**
 * Created by atulep on 7/10/2016.
 */
@ContentProvider(authority = MoviesProvider.AUTHORITY, database = MoviesDatabase.class)
public class MoviesProvider {
    public static final String AUTHORITY = "edu.boisestate.azamattulepbergenovu.popularmovies.data.MoviesProvider";

    static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    interface Path{
        String DETAILS = "details";
        String REVIEWS="reviews";
        String TRAILERS="trailers";
    }

    @TableEndpoint(table = MoviesDatabase.DETAILS) public static class Details {
        @ContentUri(
                path = Path.DETAILS,
                type = "vnd.android.cursor.dir/detail",
                defaultSort = MoviesContract.DetailsColumns.TITLE + " ASC")
        public static final Uri CONTENT_URI = Uri.parse(BASE_CONTENT_URI + "/details");

        @InexactContentUri(
                path = Path.DETAILS + "/#",
                name = "DETAIL_ID",
                type = "vnd.android.cursor.item/detail", // note, here we use the item, and not the dir
                whereColumn = MoviesContract.DetailsColumns._ID,
                pathSegment = 1)
        public static Uri withId(long id) {
            return Uri.parse("content://" + AUTHORITY + "/details/" + id);
        }
    }

    

}
