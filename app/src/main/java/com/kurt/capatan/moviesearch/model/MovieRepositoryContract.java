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
