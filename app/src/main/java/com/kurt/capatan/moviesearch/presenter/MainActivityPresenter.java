
package com.kurt.capatan.moviesearch.presenter;

import android.os.Bundle;

import com.kurt.capatan.moviesearch.data.Movie;
import com.kurt.capatan.moviesearch.model.MovieRepositoryContract;
import com.kurt.capatan.moviesearch.view.MainActivityViewContract;

import java.util.ArrayList;


public class MainActivityPresenter implements MainActivityPresenterContract, MovieRepositoryContract.OnSearchCompletedListener, MovieRepositoryContract.OnPosterDownloadCompletedListener, MovieRepositoryContract.OnNextPageCompletedListener {

    private String mSearchQuery = "";
    private int mPageNumber = 0;

    private MainActivityViewContract mainActivityViewContract;
    private MovieRepositoryContract movieRepositoryContract;

    public MainActivityPresenter(MainActivityViewContract mainActivityViewContract, MovieRepositoryContract movieRepositoryContract) {
        this.mainActivityViewContract = mainActivityViewContract;
        this.movieRepositoryContract = movieRepositoryContract;
    }

    @Override
    public void onInitialSearch(String searchQuery, boolean isNetworkConnected) {
        if (isNetworkConnected) {
            this.setSearchQueryString(searchQuery);
            mainActivityViewContract.showProgress();
            mainActivityViewContract.hideMovieRecyclerView();
            mainActivityViewContract.hideNoMoviesFound();
            mainActivityViewContract.hideTextGuide();
            mainActivityViewContract.hideProgressItemBottom();
            mainActivityViewContract.hideSoftKeyboard();
            mainActivityViewContract.recyclerViewScrollToTop();
            movieRepositoryContract.newMovieSearch(this.getSearchQueryString(), this);
            this.setPageCurrentPageNumber(1);
        } else {
            mainActivityViewContract.showNoNetworkSnackbar();
        }
    }

    @Override
    public void onNextPageSearch(int numberSearchMovies, boolean isSearching, boolean isNetworkConnected) {
        if (isNetworkConnected) {
            if ((numberSearchMovies % 10) == 0 && !isSearching) {
                this.setPageCurrentPageNumber(getPageCurrentPageNumber() + 1);
                movieRepositoryContract.nextPageSearch(mSearchQuery, this.getPageCurrentPageNumber(), this);
                mainActivityViewContract.showProgressItemBottom();
            }
        } else {
            mainActivityViewContract.showNoNetworkSnackbar();
        }
    }

    @Override
    public void onSearchCompleted(ArrayList<Movie> movies) {
        mainActivityViewContract.hideProgress();
        mainActivityViewContract.hideProgressItemBottom();
        if (movies != null && movies.size() > 0) {
            mainActivityViewContract.addMovieItems(movies);
            mainActivityViewContract.showMovieRecyclerView();
            this.downloadPoster(movies);
        } else {
            mainActivityViewContract.showNoMoviesFound();
        }
    }

    @Override
    public void onNextPageCompleted(ArrayList<Movie> movies) {
        mainActivityViewContract.hideProgressItemBottom();
        if (movies != null && movies.size() > 0) {
            mainActivityViewContract.addMovieItems(movies);
            this.downloadPoster(movies);
        } else {
            mainActivityViewContract.showToastMessage("No movies to follow");
        }
    }

    @Override
    public void downloadPoster(ArrayList<Movie> movies) {
        for (Movie movie : movies) {
            movieRepositoryContract.downloadPoster(movie, this);
        }
    }

    @Override
    public void onDownloadCompleted(Movie movie) {
        mainActivityViewContract.addPosterToMovieItem(movie);
    }

    @Override
    public void onResume() {
//        if (mainActivityViewContract != null) {
//            mainActivityViewContract.showProgress();
//        }
//
//        movieRepositoryContract.findItems(this);
    }

    @Override
    public void onDestroy() {
        mainActivityViewContract = null;
    }


    public MainActivityViewContract getMainActivityViewContract() {
        return mainActivityViewContract;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

    }

    @Override
    public void setSearchQueryString(String searchQueryString){
        this.mSearchQuery = searchQueryString;
    }

    @Override
    public String getSearchQueryString(){
        return this.mSearchQuery;
    }


    @Override
    public int getPageCurrentPageNumber() {
        return this.mPageNumber;
    }

    @Override
    public void setPageCurrentPageNumber(int pageNumber){
        this.mPageNumber = pageNumber;
    }


}
