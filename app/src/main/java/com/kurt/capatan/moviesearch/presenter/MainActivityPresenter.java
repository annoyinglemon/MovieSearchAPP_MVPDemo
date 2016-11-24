/*
 *
 *  * Copyright (C) 2014 Antonio Leiva Gordillo.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.kurt.capatan.moviesearch.presenter;

import android.os.Bundle;

import com.kurt.capatan.moviesearch.data.Movie;
import com.kurt.capatan.moviesearch.model.MovieRepositoryContract;
import com.kurt.capatan.moviesearch.view.MainActivityViewContract;

import java.util.ArrayList;


public class MainActivityPresenter implements MainActivityPresenterContract ,MovieRepositoryContract.OnSearchCompletedListener, MovieRepositoryContract.OnPosterDownloadCompletedListener {

    private ArrayList<Movie> mMovies;
    private String mSearchQuery;
    private int mPageNumber = 0;

    private MainActivityViewContract mainActivityViewContract;
    private MovieRepositoryContract movieRepositoryContract;

    public MainActivityPresenter(MainActivityViewContract mainActivityViewContract, MovieRepositoryContract movieRepositoryContract) {
        this.mainActivityViewContract = mainActivityViewContract;
        this.movieRepositoryContract = movieRepositoryContract;
        mMovies = new ArrayList<>();
    }

    @Override
    public void onInitialSearch(String searchQuery) {
        mSearchQuery = searchQuery;
        mainActivityViewContract.showProgress();
        mainActivityViewContract.hideMovieRecyclerView();
        mainActivityViewContract.hideNoMoviesFound();
        mainActivityViewContract.hideTextGuide();
        movieRepositoryContract.newMovieSearch(mSearchQuery, this);
        mPageNumber = 1;
    }

    @Override
    public void onNextPageSearch() {
        mPageNumber = mPageNumber + 1;
        movieRepositoryContract.nextPageSearch(mSearchQuery, mPageNumber, this);
    }

    @Override
    public void onSearchCompleted(ArrayList<Movie> movies) {
        if(movies!=null){
            mainActivityViewContract.addMovieItems(movies);
            mainActivityViewContract.hideProgress();
            mainActivityViewContract.showMovieRecyclerView();
            for(Movie movie: movies){
                downloadPoster(movie);
            }
        }else {
            mainActivityViewContract.hideProgress();
            mainActivityViewContract.showNoMoviesFound();
        }
    }

    @Override
    public void downloadPoster(Movie movie) {
        movieRepositoryContract.downloadPoster(movie, this);
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

    @Override public void onDestroy() {
        mainActivityViewContract = null;
    }



    public MainActivityViewContract getMainActivityViewContract() {
        return mainActivityViewContract;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

    }
}
