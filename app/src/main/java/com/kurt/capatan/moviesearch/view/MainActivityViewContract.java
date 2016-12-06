
package com.kurt.capatan.moviesearch.view;

import com.kurt.capatan.moviesearch.data.Movie;

import java.util.ArrayList;

public interface MainActivityViewContract {

    void hideTextGuide();

    void showNoMoviesFound();

    void hideNoMoviesFound();

    void showProgress();

    void showProgressItemBottom();

    void hideProgress();

    void hideProgressItemBottom();

    void showMovieRecyclerView();

    void hideMovieRecyclerView();

    void addMovieItems(ArrayList<Movie> movies);

    void addPosterToMovieItem(Movie movie);

    void showMovieDetails(Movie movie);

    void showToastMessage(String message);

    void hideSoftKeyboard();

    void recyclerViewScrollToTop();

    void showNoNetworkSnackbar();
}
