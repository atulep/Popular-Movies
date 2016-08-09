package edu.boisestate.azamattulepbergenovu.popularmovies.Data;

import android.net.Uri;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.InexactContentUri;
import net.simonvt.schematic.annotation.TableEndpoint;

/**
 * Created by atulep on 7/10/2016.
 * Defines Content Provider for 3 Database tables.
 */
@ContentProvider(authority = MoviesProvider.AUTHORITY, database = MoviesDatabase.class)
public class MoviesProvider {
    public static final String AUTHORITY = "edu.boisestate.azamattulepbergenovu.popularmovies.Data.MoviesProvider";

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

    @TableEndpoint(table = MoviesDatabase.REVIEWS) public static class Reviews {
        @ContentUri(
                path = Path.REVIEWS,
                type = "vnd.android.cursor.dir/review",
                defaultSort = MoviesContract.ReviewColumns.MOVIE_ID + " ASC")
        public static final Uri CONTENT_URI = Uri.parse(BASE_CONTENT_URI + "/details");

        @InexactContentUri(
                path = Path.REVIEWS + "/#",
                name = "REVIEW_ID",
                type = "vnd.android.cursor.item/review",
                whereColumn = MoviesContract.ReviewColumns._ID,
                pathSegment = 1)
        public static Uri withId(long id) {
            return Uri.parse("content://" + AUTHORITY + "/reviews/" + id);
        }
    }

    @TableEndpoint(table = MoviesDatabase.TRAILERS) public static class Trailers {
        @ContentUri(
                path = Path.TRAILERS,
                type = "vnd.android.cursor.dir/trailer",
                defaultSort = MoviesContract.TrailerColumns.MOVIE_ID + " ASC")
        public static final Uri CONTENT_URI = Uri.parse(BASE_CONTENT_URI + "/trailers");

        @InexactContentUri(
                path = Path.TRAILERS + "/#",
                name = "TRAILER_ID",
                type = "vnd.android.cursor.item/trailer",
                whereColumn = MoviesContract.TrailerColumns._ID,
                pathSegment = 1)
        public static Uri withId(long id) {
            return Uri.parse("content://" + AUTHORITY + "/trailers/" + id);
        }
    }


}
