
package com.kurt.capatan.moviesearch.presenter;

import android.os.Bundle;

import com.kurt.capatan.moviesearch.data.Movie;

import java.util.ArrayList;

public interface MainActivityPresenterContract {

    void onInitialSearch(String searchQuery, boolean isNetworkConnected);

    void onNextPageSearch(int numberSearchMovies, boolean isSearching, boolean isNetworkConnected);

    void downloadPoster(ArrayList<Movie> movies);

    void onResume();

    void onDestroy();

    void onSaveInstanceState(Bundle outState);

    void setSearchQueryString(String searchQueryString);

    String getSearchQueryString();

    void setPageCurrentPageNumber(int pageNumber);

    int getPageCurrentPageNumber();

}
