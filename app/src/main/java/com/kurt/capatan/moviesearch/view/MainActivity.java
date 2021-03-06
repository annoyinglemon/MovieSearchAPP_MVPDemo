package com.kurt.capatan.moviesearch.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.kurt.capatan.moviesearch.R;
import com.kurt.capatan.moviesearch.data.Movie;
import com.kurt.capatan.moviesearch.model.MovieRepository;
import com.kurt.capatan.moviesearch.presenter.MainActivityPresenter;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MainActivityViewContract {

    private EditText etSearch;
    private TextView tvGuide;
    private ProgressBar pbSearching;
    private RecyclerView rvMovies;
    private LinearLayout llNoMoviesFound;

    private MovieRecyclerAdapter mAdapter;
    private ArrayList<Movie> mSearchedMovies;

    private MainActivityPresenter mPresenter = new MainActivityPresenter(this, new MovieRepository());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etSearch = (EditText) findViewById(R.id.etSearch);
        tvGuide = (TextView) findViewById(R.id.tvGuide);
        llNoMoviesFound = (LinearLayout) findViewById(R.id.llNoMoviesFound);
        pbSearching = (ProgressBar) findViewById(R.id.pbSearching);
        rvMovies = (RecyclerView) findViewById(R.id.rvMovies);
        CardView cvSearch = (CardView) findViewById(R.id.cvSearch);
        cvSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etSearch.requestFocus();
            }
        });

        etSearch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (etSearch.getRight() - etSearch.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        // your action here
                        if(isNetworkAvailable()) {
                            mAdapter.clearSearch();
                            mAdapter.notifyDataSetChanged();
                        }
                        mPresenter.onInitialSearch(etSearch.getText().toString(), isNetworkAvailable());
                        return true;
                    }
                }
                return false;
            }
        });

        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if(isNetworkAvailable()) {
                        mAdapter.clearSearch();
                        mAdapter.notifyDataSetChanged();
                    }
                    mPresenter.onInitialSearch(etSearch.getText().toString(), isNetworkAvailable());
                    return true;
                }
                return false;
            }
        });

        mSearchedMovies = new ArrayList<>();
        mAdapter = new MovieRecyclerAdapter();

        if (isDeviceTablet()) {
            GridLayoutManager manager = new GridLayoutManager(this, 2);
            manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    if (position == 0) {
                        return 2;
                    } else
                        return 1;
                }
            });
            rvMovies.setLayoutManager(manager);
        } else {
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            rvMovies.setLayoutManager(linearLayoutManager);
        }

        rvMovies.setItemAnimator(new DefaultItemAnimator());
        rvMovies.setAdapter(mAdapter);
        rvMovies.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) { //check for scroll down
                    if (!rvMovies.canScrollVertically(1)) {
                        mPresenter.onNextPageSearch(mSearchedMovies.size(), mAdapter.isSearchingNextPage(), isNetworkAvailable());
                    }
                }
            }
        });
    }


    @Override
    public void hideTextGuide() {
        if (tvGuide.getVisibility() == View.VISIBLE)
            tvGuide.setVisibility(View.GONE);
    }

    @Override
    public void showNoMoviesFound() {
        if (llNoMoviesFound.getVisibility() == View.GONE)
            llNoMoviesFound.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideNoMoviesFound() {
        if (llNoMoviesFound.getVisibility() == View.VISIBLE)
            llNoMoviesFound.setVisibility(View.GONE);
    }

    @Override
    public void showProgress() {
        if (pbSearching.getVisibility() == View.GONE)
            pbSearching.setVisibility(View.VISIBLE);
    }

    @Override
    public void showProgressItemBottom() {
        if (!mAdapter.isSearchingNextPage()) {
            mAdapter.setIsSearchingNextPage(true);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void hideProgress() {
        if (pbSearching.getVisibility() == View.VISIBLE)
            pbSearching.setVisibility(View.GONE);
    }

    @Override
    public void hideProgressItemBottom() {
        if (mAdapter.isSearchingNextPage()) {
            mAdapter.setIsSearchingNextPage(false);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void showMovieRecyclerView() {
        if (rvMovies.getVisibility() == View.GONE)
            rvMovies.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideMovieRecyclerView() {
        if (rvMovies.getVisibility() == View.VISIBLE)
            rvMovies.setVisibility(View.GONE);
    }

    @Override
    public void addMovieItems(ArrayList<Movie> movies) {
        mSearchedMovies.addAll(movies);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void addPosterToMovieItem(Movie movie) {
        for (int i = 0; i < mSearchedMovies.size(); i++) {
            if (mSearchedMovies.get(i).getImdbId().equals(movie.getImdbId())) {
                mSearchedMovies.set(i, movie);
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void showToastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void recyclerViewScrollToTop() {
        rvMovies.scrollToPosition(0);
    }

    @Override
    public void showNoNetworkSnackbar() {
        Snackbar.make(findViewById(R.id.activity_main), "No internet connection", Snackbar.LENGTH_LONG).show();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        mPresenter.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    @TargetApi(21)
    public void showMovieDetails(Movie movie, ImageView moviePosterImg, boolean applyCurve) {
        Intent movieDetailIntent = new Intent(this, MovieDetailsActivity.class);
        movieDetailIntent.putExtra("movie", movie);
        movieDetailIntent.putExtra(MovieDetailsActivity.EXTRA_CURVE, applyCurve);
        Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(this, moviePosterImg, moviePosterImg.getTransitionName()).toBundle();
        startActivity(movieDetailIntent, bundle);
    }

    @Override
    public void showMovieDetails(Movie movie) {
        Intent movieDetailIntent = new Intent(this, MovieDetailsActivity.class);
        movieDetailIntent.putExtra("movie", movie);
        startActivity(movieDetailIntent);
    }

    @Override
    public void hideSoftKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onResume() {
        mPresenter.onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        mPresenter.onDestroy();
        super.onDestroy();
    }

    boolean isDeviceTablet() {
        return (getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }


    public class MovieRecyclerAdapter extends RecyclerView.Adapter<MovieRecyclerAdapter.MyViewHolder> {


        private boolean isSearchingNextPage = false;
        private int itemsAdded = 1;

        class MyViewHolder extends RecyclerView.ViewHolder {

            CardView cvMovieItem;
            ImageView ivThumbImage;
            TextView tvTitle, tvDirector, tvYear;
            ProgressBar pbSearchingBottom;


            MyViewHolder(View view) {
                super(view);
                cvMovieItem = (CardView) view.findViewById(R.id.cvMovieItem);
                ivThumbImage = (ImageView) view.findViewById(R.id.ivThumbImage);
                tvTitle = (TextView) view.findViewById(R.id.tvTitle);
                tvDirector = (TextView) view.findViewById(R.id.tvDirector);
                tvYear = (TextView) view.findViewById(R.id.tvYear);
                pbSearchingBottom = (ProgressBar) view.findViewById(R.id.pbSearchingBottom);
            }
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_result_row, parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            if (position == 0) {
                holder.cvMovieItem.setVisibility(View.GONE);
            } else if (position == (getItemCount() - 1)) {
                if (this.isSearchingNextPage) {
                    holder.cvMovieItem.setVisibility(View.GONE);
                    holder.pbSearchingBottom.setVisibility(View.VISIBLE);
                } else {
                    final Movie movie = mSearchedMovies.get(position - 1);
                    if (movie.getPoster() != null) {
                        holder.ivThumbImage.setImageBitmap(BitmapFactory.decodeByteArray(movie.getPoster(), 0, movie.getPoster().length));
                    }
                    holder.tvTitle.setText(movie.getTitle());
                    holder.tvDirector.setText(movie.getDirector());
                    holder.tvYear.setText(Integer.toString(movie.getYear()));
                    holder.cvMovieItem.setVisibility(View.VISIBLE);
                    holder.cvMovieItem.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int currentapiVersion = android.os.Build.VERSION.SDK_INT;
                            if (currentapiVersion >= android.os.Build.VERSION_CODES.LOLLIPOP){
                                // Do something for lollipop and above versions
                                boolean curve = (position % 2 == 0);
                                showMovieDetails(movie, holder.ivThumbImage, curve);
                            } else{
                                // do something for phones running an SDK before lollipop
                                showMovieDetails(movie);
                            }
                        }
                    });
                    holder.pbSearchingBottom.setVisibility(View.GONE);
                }
            } else {
                final Movie movie2 = mSearchedMovies.get(position - 1);
                if (movie2.getPoster() != null) {
                    holder.ivThumbImage.setImageBitmap(BitmapFactory.decodeByteArray(movie2.getPoster(), 0, movie2.getPoster().length));
                } else {
                    holder.ivThumbImage.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_movie));
                }
                holder.tvTitle.setText(movie2.getTitle());
                holder.tvDirector.setText(movie2.getDirector());
                holder.tvYear.setText(Integer.toString(movie2.getYear()));
                holder.cvMovieItem.setVisibility(View.VISIBLE);
                holder.cvMovieItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
                        if (currentapiVersion >= android.os.Build.VERSION_CODES.LOLLIPOP){
                            // Do something for lollipop and above versions
                            boolean curve = (position % 2 == 0);
                            showMovieDetails(movie2, holder.ivThumbImage, curve);
                        } else{
                            // do something for phones running an SDK before lollipop
                            showMovieDetails(movie2);
                        }
                    }
                });
                holder.pbSearchingBottom.setVisibility(View.GONE);
            }
        }

        @Override
        public int getItemCount() {
            return mSearchedMovies.size() + this.itemsAdded;
        }

        public void clearSearch() {
            mSearchedMovies.clear();
        }

        public void setIsSearchingNextPage(boolean isSearchingNextPage) {
            this.isSearchingNextPage = isSearchingNextPage;
            if (this.isSearchingNextPage) {
                this.itemsAdded = 2;
            } else {
                this.itemsAdded = 1;
            }
        }

        public boolean isSearchingNextPage() {
            return this.isSearchingNextPage;
        }
    }

    private boolean isNetworkAvailable() {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE));
        if (connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected())
            return true;
        else
            return false;
    }

}
