
package com.kurt.capatan.moviesearch.model;

import com.kurt.capatan.moviesearch.data.Movie;

import java.util.ArrayList;

public interface MovieRepositoryContract {

    interface OnSearchCompletedListener{
        void onSearchCompleted(ArrayList<Movie> movies);
    }

    interface OnNextPageCompletedListener{
        void onNextPageCompleted(ArrayList<Movie> movies);
    }

    interface OnPosterDownloadCompletedListener{
        void onDownloadCompleted(Movie movie);
    }

    void newMovieSearch(String searchQuery, OnSearchCompletedListener onSearchCompletedListener);

    void nextPageSearch(String searchQuery, int pageNumber, OnNextPageCompletedListener onNextPageCompleted);

    void downloadPoster(Movie movie, OnPosterDownloadCompletedListener onPosterDownloadCompletedListener);


}
