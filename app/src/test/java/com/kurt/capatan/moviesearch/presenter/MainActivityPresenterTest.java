package com.kurt.capatan.moviesearch.presenter;

import com.kurt.capatan.moviesearch.data.Movie;
import com.kurt.capatan.moviesearch.model.MovieRepositoryContract;
import com.kurt.capatan.moviesearch.view.MainActivityViewContract;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.Mockito.verify;

/**
 * Created by Lemon on 12/1/2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class MainActivityPresenterTest {

    @Mock
    MovieRepositoryContract mRepository;
    @Mock
    MainActivityViewContract mActivity;

    @Mock
    MainActivityPresenterContract mPresenterContract;

    MainActivityPresenter mPresenter;

    @Mock
    ArrayList<Movie> mMovies;

    @Before
    public void setUp() throws Exception {
        mPresenter = new MainActivityPresenter(mActivity, mRepository);
    }

    @Test
    public void shouldShowNoInternetSnackBarWhenDeviceIsDisconnectedUponInitialSearch() throws Exception {
        String searchQuery = "";
        boolean isConnected = false;
        mPresenter.onInitialSearch(searchQuery, isConnected);
        verify(mActivity).showNoNetworkSnackbar();
    }

    @Test
    public void shouldUpdateViewsUponInitialSearch() throws Exception {
        String searchQuery = "movie";
        boolean isConnected = true;
        mPresenter.onInitialSearch(searchQuery, isConnected);
        verify(mActivity).showProgress();
        verify(mActivity).hideMovieRecyclerView();
        verify(mActivity).hideTextGuide();
        verify(mActivity).hideNoMoviesFound();
        verify(mActivity).hideProgressItemBottom();
        verify(mActivity).hideSoftKeyboard();
        verify(mActivity).recyclerViewScrollToTop();
        verify(mRepository).newMovieSearch(searchQuery, mPresenter);
        assertEquals(mPresenter.getPageCurrentPageNumber(), 1);
    }

    @Test
    public void shouldShowNoInternetSnackBarWhenDeviceIsDisconnectedUponNextPageSearch() throws Exception {
        int numberOfSearchedMovies = 10;
        boolean isSearching = false;
        boolean isNetworkConnected = false;
        mPresenter.onNextPageSearch(numberOfSearchedMovies, isSearching, isNetworkConnected);
        verify(mActivity).showNoNetworkSnackbar();
    }

    @Test
    public void shouldShowProgressBottomUponNextPageSearch() throws Exception {
        int numberOfSearchedMovies = 100;
        boolean isSearching = false;
        boolean isNetworkConnected = true;
        mPresenter.onNextPageSearch(numberOfSearchedMovies, isSearching, isNetworkConnected);
        verify(mRepository).nextPageSearch(mPresenter.getSearchQueryString(), mPresenter.getPageCurrentPageNumber(), mPresenter);
        verify(mActivity).showProgressItemBottom();
    }

    @Test
    public void shouldHideProgressBottomUponNextPageSearchWhenNumberOfSearchItemsIsNotDivisibleByTen() throws Exception {
        int numberOfSearchedMovies = 86;
        boolean isSearching = false;
        boolean isNetworkConnected = true;
        mPresenter.onNextPageSearch(numberOfSearchedMovies, isSearching, isNetworkConnected);
    }

    @Test
    public void shouldHideProgressBottomUponNextPageSearchWhenStillSearching() throws Exception {
        int numberOfSearchedMovies = 80;
        boolean isSearching = true;
        boolean isNetworkConnected = true;
        mPresenter.onNextPageSearch(numberOfSearchedMovies, isSearching, isNetworkConnected);
    }

    @Test
    public void showResultsIfMoviesAreFoundDuringSearch() throws Exception {
        mMovies = new ArrayList<>();
        mMovies.add(new Movie());
        mMovies.add(new Movie());
        mPresenter.onSearchCompleted(mMovies);
        verify(mActivity).hideProgress();
        verify(mActivity).hideProgressItemBottom();
        verify(mActivity).addMovieItems(mMovies);
        verify(mActivity).showMovieRecyclerView();
        for (Movie m : mMovies) {
            verify(mRepository).downloadPoster(m, mPresenter);
        }
    }

    @Test
    public void showNoMoviesFoundIfNoMoviesAreFoundDuringSearch() throws Exception {
        mMovies = new ArrayList<>();
        mPresenter.onSearchCompleted(mMovies);
        verify(mActivity).hideProgress();
        verify(mActivity).hideProgressItemBottom();
        verify(mActivity).showNoMoviesFound();
    }

    @Test
    public void addResultsIfMoviesAreFoundDuringNextPageSearch() throws Exception {
        mMovies = new ArrayList<>();
        mMovies.add(new Movie());
        mMovies.add(new Movie());
        mPresenter.onNextPageCompleted(mMovies);
        verify(mActivity).hideProgressItemBottom();
        verify(mActivity).addMovieItems(mMovies);
        for (Movie m : mMovies) {
            verify(mRepository).downloadPoster(m, mPresenter);
        }
    }

    @Test
    public void showToastIfNoMoviesAreFoundDuringNextPageSearch() throws Exception {
        mMovies = new ArrayList<>();
        mPresenter.onNextPageCompleted(mMovies);
        verify(mActivity).hideProgressItemBottom();
        verify(mActivity).showToastMessage("No movies to follow");
    }

    @Test
    public void attachPosterToMovieItem() throws Exception {
        Movie movie = new Movie();
        mPresenter.onDownloadCompleted(movie);
        verify(mActivity).addPosterToMovieItem(movie);
    }
}