package com.kurt.capatan.moviesearch.view;

import android.annotation.TargetApi;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.TransitionInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.kurt.capatan.moviesearch.R;
import com.kurt.capatan.moviesearch.data.Movie;

public class MovieDetailsActivity extends AppCompatActivity {

    public static final String EXTRA_CURVE = "extra_curve";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        Movie mMovie = (Movie) getIntent().getExtras().getSerializable("movie");

        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.ctbMovieTitleYear);
        collapsingToolbarLayout.setTitle(mMovie.getTitle());
        collapsingToolbarLayout.setExpandedTitleColor(ContextCompat.getColor(this, R.color.md_blue_grey_100));



        ImageView ivMoviePoster = (ImageView) findViewById(R.id.ivMoviePoster);
        ImageView ivMoviePoster2 = (ImageView) findViewById(R.id.ivMoviePoster2);

        TextView tvGenInfo_Title = (TextView) findViewById(R.id.tvGenInfo_Title);
        TextView tvGenInfo_Year = (TextView) findViewById(R.id.tvGenInfo_Year);
        TextView tvGenInfo_Rated = (TextView) findViewById(R.id.tvGenInfo_Rated);
        TextView tvGenInfo_Released = (TextView) findViewById(R.id.tvGenInfo_Released);
        TextView tvGenInfo_Runtime = (TextView) findViewById(R.id.tvGenInfo_Runtime);
        TextView tvGenInfo_Language = (TextView) findViewById(R.id.tvGenInfo_Language);
        TextView tvGenInfo_Country = (TextView) findViewById(R.id.tvGenInfo_Country);
        TextView tvGenre = (TextView) findViewById(R.id.tvGenre);
        TextView tvMovieDirector = (TextView) findViewById(R.id.tvMovieDirector);
        TextView tvMovieWriter = (TextView) findViewById(R.id.tvMovieWriter);
        TextView tvMovieActor = (TextView) findViewById(R.id.tvMovieActor);
        TextView tvMoviePlot = (TextView) findViewById(R.id.tvMoviePlot);
        TextView tvMovieAwards = (TextView) findViewById(R.id.tvMovieAwards);
        TextView tvMetascore = (TextView) findViewById(R.id.tvMetascore);
        TextView tvIMDb = (TextView) findViewById(R.id.tvIMDb);

        if(mMovie.getPoster()!=null){
            ivMoviePoster.setImageBitmap(BitmapFactory.decodeByteArray(mMovie.getPoster(), 0, mMovie.getPoster().length));
            ivMoviePoster2.setImageBitmap(BitmapFactory.decodeByteArray(mMovie.getPoster(), 0, mMovie.getPoster().length));
        }
        tvGenInfo_Title.setText(mMovie.getTitle());
        tvGenInfo_Year.setText(Integer.toString(mMovie.getYear()));
        tvGenInfo_Rated.setText(mMovie.getRated());
        tvGenInfo_Released.setText(mMovie.getRelease());
        tvGenInfo_Runtime.setText(mMovie.getRunTime());
        tvGenInfo_Language.setText(mMovie.getLanguage());
        tvGenInfo_Country.setText(mMovie.getCountry());
        tvGenre.setText(mMovie.getGenre());
        tvMovieDirector.setText(mMovie.getDirector());
        tvMovieWriter.setText(mMovie.getWriter());
        tvMovieActor.setText(mMovie.getActor());
        tvMoviePlot.setText(mMovie.getPlot());
        tvMovieAwards.setText(mMovie.getAwards());
        tvMetascore.setText(Integer.toString(mMovie.getMetascore()));
        tvIMDb.setText(Double.toString(mMovie.getImdb()));

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP){
            applyCurve();
        }

    }

    @TargetApi(21)
    void applyCurve(){
        boolean curve = getIntent().getBooleanExtra(EXTRA_CURVE, false);
        getWindow().setSharedElementEnterTransition(TransitionInflater.from(this)
                .inflateTransition(curve ? R.transition.curve : R.transition.move));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
