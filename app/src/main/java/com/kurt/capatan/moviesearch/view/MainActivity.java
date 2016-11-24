package com.kurt.capatan.moviesearch.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
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

import com.kurt.capatan.moviesearch.R;
import com.kurt.capatan.moviesearch.data.Movie;
import com.kurt.capatan.moviesearch.model.MovieRepository;
import com.kurt.capatan.moviesearch.presenter.MainActivityPresenter;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MainActivityViewContract{

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

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (etSearch.getRight() - etSearch.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        // your action here
                        mAdapter.clearSearch();
                        mAdapter.notifyDataSetChanged();
                        mPresenter.onInitialSearch(etSearch.getText().toString());
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
                    mAdapter.clearSearch();
                    mAdapter.notifyDataSetChanged();
                    mPresenter.onInitialSearch(etSearch.getText().toString());
                    return true;
                }
                return false;
            }
        });

        mSearchedMovies = new ArrayList<>();
        mAdapter = new MovieRecyclerAdapter();

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvMovies.setLayoutManager(linearLayoutManager);
        rvMovies.setItemAnimator(new DefaultItemAnimator());
        rvMovies.setAdapter(mAdapter);
        rvMovies.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) { //check for scroll down
                    if(!rvMovies.canScrollVertically(1)&&(mSearchedMovies.size()%10)==0){
                        mPresenter.onNextPageSearch();
                    }
                }
            }
        });
    }

    @Override
    public void hideTextGuide() {
        if(tvGuide.getVisibility()==View.VISIBLE)
            tvGuide.setVisibility(View.GONE);
    }

    @Override
    public void showNoMoviesFound() {
        if(llNoMoviesFound.getVisibility()==View.GONE)
            llNoMoviesFound.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideNoMoviesFound() {
        if(llNoMoviesFound.getVisibility()==View.VISIBLE)
            llNoMoviesFound.setVisibility(View.GONE);
    }

    @Override
    public void showSnackBarNoInternet(){
        Snackbar.make(findViewById(R.id.activity_main), "No internet connection", Snackbar.LENGTH_SHORT);
    }

    @Override
    public void showProgress() {
        if(pbSearching.getVisibility()==View.GONE)
            pbSearching.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        if(pbSearching.getVisibility()==View.VISIBLE)
            pbSearching.setVisibility(View.GONE);
    }

    @Override
    public void showMovieRecyclerView() {
        if(rvMovies.getVisibility()==View.GONE)
            rvMovies.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideMovieRecyclerView() {
        if(rvMovies.getVisibility()==View.VISIBLE)
            rvMovies.setVisibility(View.GONE);
    }

    @Override
    public void addMovieItems(ArrayList<Movie> movies) {
        mSearchedMovies.addAll(movies);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void addPosterToMovieItem(Movie movie){
        for(int i = 0; i < mSearchedMovies.size(); i++){
            if(mSearchedMovies.get(i).getImdbId().equals(movie.getImdbId())){
                mSearchedMovies.set(i,movie);
                mAdapter.notifyItemChanged(i);
            }
        }
    }

//    private void mockSearch(){
//        View view = this.getCurrentFocus();
//        if (view != null) {
//            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
//            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
//        }
//        Movie deadpool = new Movie("125152", "Captain America: Civil War", 2016, "R", "12 May 2016", "148 min", "Action",
//                "Russo Brothers", "Rhett Reese, Paul Wernick", "Ryan Reynolds, Karan Soni, Ed Skrein, Michael Benyaer", "This is the origin story of former Special Forces operative turned mercenary Wade Wilson, who after being subjected to a rogue experiment that leaves him with accelerated healing powers, adopts the alter ego Deadpool." +
//                " Armed with his new abilities and a dark, twisted sense of humor, Deadpool hunts down the man who nearly destroyed his life.", "English", "USA", "7 awards and 11 nominations", 65, 8.1 ,"https://images-na.ssl-images-amazon.com/images/M/MV5BMjQyODg5Njc4N15BMl5BanBnXkFtZTgwMzExMjE3NzE@._V1_SX300.jpg");
//        mSearchedMovies.add(deadpool);
//        mAdapter.notifyDataSetChanged();
//        tvGuide.setVisibility(View.GONE);
//        rvMovies.setVisibility(View.VISIBLE);
//    }

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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        mPresenter.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void showMovieDetails(Movie movie) {
        Intent movieDetailIntent = new Intent(this, MovieDetailsActivity.class);
        movieDetailIntent.putExtra("movie", movie);
        startActivity(movieDetailIntent);
    }

    public class MovieRecyclerAdapter extends RecyclerView.Adapter<MovieRecyclerAdapter.MyViewHolder> {

        public class MyViewHolder extends RecyclerView.ViewHolder {

            public CardView cvMovieItem;
            public ImageView ivThumbImage;
            public TextView tvTitle, tvDirector, tvYear;

            public MyViewHolder(View view) {
                super(view);
                cvMovieItem = (CardView) view.findViewById(R.id.cvMovieItem);
                ivThumbImage = (ImageView) view.findViewById(R.id.ivThumbImage);
                tvTitle = (TextView) view.findViewById(R.id.tvTitle);
                tvDirector = (TextView) view.findViewById(R.id.tvDirector);
                tvYear = (TextView) view.findViewById(R.id.tvYear);
            }
        }


        public MovieRecyclerAdapter() {
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_result_row, parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position) {
            if(position > 0) {
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
                        showMovieDetails(movie);
                    }
                });
            } else {
                holder.cvMovieItem.setVisibility(View.GONE);
            }
        }

        @Override
        public int getItemCount() {
            return mSearchedMovies.size()+1;
        }

        public void clearSearch(){
            mSearchedMovies.clear();
        }
    }


}
