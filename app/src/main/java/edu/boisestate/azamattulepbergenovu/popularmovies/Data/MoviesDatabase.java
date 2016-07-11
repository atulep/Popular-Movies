package edu.boisestate.azamattulepbergenovu.popularmovies.Data;

import net.simonvt.schematic.annotation.Table;

/**
 * Created by atulep on 7/10/2016.
 */
public class MoviesDatabase {

    public static final int VERSION = 1;

    @Table(MoviesContract.DetailsColumns.class) public static final String DETAILS = "details";

    @Table(MoviesContract.ReviewColumns.class) public static final String REVIEWS = "reviews";

    @Table(MoviesContract.TrailerColumns.class) public static final String TRAILERS = "trailers";
}
