# PopularMovies
An Android app that allows to scroll through selection of latest movies and get details about certain movies. 

## How to use
Download the source code. You will need to use your own API key for [database](https://www.themoviedb.org #link). 

Next,

1. Make sure to define "MyMovieDbApiKey" in gradle.properties located inside of ~/.gradle/ folder. 
2. Assign it a value of your custom API key.  
3. Add this code to build.gradle(Module: app):
```
buildTypes.each {
        it.buildConfigField 'String', 'MOVIE_DB_API_KEY', MyMovieDbApiKey
    }
```

You should be good to go!