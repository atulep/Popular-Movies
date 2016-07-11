package edu.boisestate.azamattulepbergenovu.popularmovies.data;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;

/**
 * Created by atulep on 7/10/2016.
 */
public class MoviesContract {

    /**
     * Columns for the table containing general movie details used in the detail fragment.
     */
    public interface DetailsColumns {
        @DataType(DataType.Type.INTEGER) @PrimaryKey @AutoIncrement
        public static final String _ID = "_id";

        @DataType(DataType.Type.INTEGER) @NotNull
        String MOVIE_ID = "movie_id";

        @DataType(DataType.Type.TEXT) @NotNull
        String TITLE = "title";

        @DataType(DataType.Type.TEXT) @NotNull
        String POSTER_IMAGE = "poster_image";

        @DataType(DataType.Type.TEXT) @NotNull
        String PLOT = "plot";

        @DataType(DataType.Type.REAL) @NotNull
        String RATING = "rating";

        @DataType(DataType.Type.TEXT) @NotNull
        String RELEASE_DATE = "release_data";
    }

    /**
     * Columns for the table containing the trailer information for the movie_id
     */
    public interface TrailerColumns {
        @DataType(DataType.Type.INTEGER) @PrimaryKey @AutoIncrement
        String _ID = "_id";

        @DataType(DataType.Type.INTEGER) @NotNull
        String MOVIE_ID = "movie_id";

        // some movies may not have trailers
        @DataType(DataType.Type.TEXT)
        String TRAILER_PATH = "trailer_path";
    }

    public interface ReviewColumns {
        @DataType(DataType.Type.INTEGER) @PrimaryKey @AutoIncrement
        String _ID = "_id";

        @DataType(DataType.Type.INTEGER) @NotNull
        String MOVIE_ID = "movie_id";

        // some movies may not have reviews
        @DataType(DataType.Type.TEXT)
        String REVIEW = "review";
    }
}
