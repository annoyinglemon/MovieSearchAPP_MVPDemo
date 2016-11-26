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

import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.kurt.capatan.moviesearch.data.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MovieRepository implements MovieRepositoryContract {
    //    http://www.omdbapi.com/?s=[Civil+War]&type=movie&page=[1]
    private String searchURL = "";

    @Override
    public void newMovieSearch(String searchQuery, OnSearchCompletedListener onSearchCompletedListener) {
        String urlEnabled = searchQuery.replaceAll("\\s", "+");
        searchURL = "http://www.omdbapi.com/?s=" + urlEnabled + "&type=movie";
        new SearchMovies(onSearchCompletedListener).execute(searchURL);
    }

    @Override
    public void nextPageSearch(String searchQuery, int pageNumber, OnNextPageCompletedListener onNextPageCompletedListener) {
        String urlEnabled = searchQuery.replaceAll("\\s", "+");
        searchURL = "http://www.omdbapi.com/?s=" + urlEnabled + "&type=movie&page=" + pageNumber;
        new SearchMovies(onNextPageCompletedListener).execute(searchURL);
    }

    @Override
    public void downloadPoster(Movie movie, OnPosterDownloadCompletedListener onPosterDownloadCompletedListener) {
        new DownloadPoster(onPosterDownloadCompletedListener).execute(movie);
    }

    private class SearchMovies extends AsyncTask<String, Void, ArrayList<Movie>> {

        OnSearchCompletedListener onSearchCompletedListener;
        OnNextPageCompletedListener onNextPageCompletedListener;

        SearchMovies(OnSearchCompletedListener onSearchCompletedListener) {
            this.onSearchCompletedListener = onSearchCompletedListener;
            this.onNextPageCompletedListener = null;
        }

        SearchMovies(OnNextPageCompletedListener onNextPageCompletedListener) {
            this.onSearchCompletedListener = null;
            this.onNextPageCompletedListener = onNextPageCompletedListener;
        }

        @Override
        protected ArrayList<Movie> doInBackground(String... urls) {
            ArrayList<Movie> movies = null;
            try {
                ArrayList<String> movieIds = new ArrayList<>();
                JSONObject wholeJSON = new JSONObject(downloadJSONString(urls[0]));
                boolean response = wholeJSON.getBoolean("Response");
                if (response) {
                    JSONArray searchResults = wholeJSON.getJSONArray("Search");
                    for (int i = 0; i < searchResults.length(); i++) {
                        JSONObject movieItem = searchResults.getJSONObject(i);
                        String id = movieItem.getString("imdbID");
                        movieIds.add(id);
                    }
                    movies = new ArrayList<>();
                    for (String imdbID : movieIds) {
                        String movieInfoURL = "http://www.omdbapi.com/?i=" + imdbID + "&plot=full&r=json";
                        JSONObject movieInfoJSON = new JSONObject(downloadJSONString(movieInfoURL));
                        Movie movie = new Movie();
                        movie.setImdbId(movieInfoJSON.getString("imdbID"));
                        movie.setTitle(movieInfoJSON.getString("Title"));
                        movie.setYear(movieInfoJSON.getInt("Year"));
                        movie.setRated(movieInfoJSON.getString("Rated"));
                        movie.setRelease(movieInfoJSON.getString("Released"));
                        movie.setRunTime(movieInfoJSON.getString("Runtime"));
                        movie.setGenre(movieInfoJSON.getString("Genre"));
                        movie.setDirector(movieInfoJSON.getString("Director"));
                        movie.setWriter(movieInfoJSON.getString("Writer"));
                        movie.setActor(movieInfoJSON.getString("Actors"));
                        movie.setPlot(movieInfoJSON.getString("Plot"));
                        movie.setLanguage(movieInfoJSON.getString("Language"));
                        movie.setCountry(movieInfoJSON.getString("Country"));
                        movie.setAwards(movieInfoJSON.getString("Awards"));
                        movie.setPosterUrl(movieInfoJSON.getString("Poster"));
                        movie.setMetascore(movieInfoJSON.getInt("Metascore"));
                        movie.setImdb(movieInfoJSON.getDouble("imdbRating"));
                        movies.add(movie);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return movies;
        }

        @Override
        protected void onPostExecute(ArrayList<Movie> movies) {
            super.onPostExecute(movies);
            if(onSearchCompletedListener!=null)
                onSearchCompletedListener.onSearchCompleted(movies);
            else {
                onNextPageCompletedListener.onNextPageCompleted(movies);
            }
        }
    }

    private class DownloadPoster extends AsyncTask<Movie, Void, Movie> {

        OnPosterDownloadCompletedListener onPosterDownloadCompletedListener;

        DownloadPoster(OnPosterDownloadCompletedListener onPosterDownloadCompletedListener) {
            this.onPosterDownloadCompletedListener = onPosterDownloadCompletedListener;
        }

        @Override
        protected Movie doInBackground(Movie... params) {
            byte[] imageByte = downloadByteArrayImage(params[0].getPosterURL());
            if (imageByte != null) {
                params[0].setPoster(imageByte);
            }
            return params[0];
        }

        @Override
        protected void onPostExecute(Movie movie) {
            super.onPostExecute(movie);
            onPosterDownloadCompletedListener.onDownloadCompleted(movie);
        }
    }

    private String downloadJSONString(String reqUrl) {
        String result = "";
        URL url = null;
        HttpURLConnection httpURLConnection = null;
        InputStream inputStream = null;
        try {
            url = new URL(reqUrl);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            inputStream = httpURLConnection.getInputStream();
            InputStreamReader reader = new InputStreamReader(inputStream);
            int data = reader.read();
            while (data > -1) {
                char current = (char) data;
                result += current;
                data = reader.read();
            }
            return result;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (httpURLConnection != null)
                    httpURLConnection.disconnect();
                if (inputStream != null)
                    inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    private byte[] downloadByteArrayImage(String imgURL){
        URL url = null;
        HttpURLConnection httpURLConnection = null;
        InputStream inputStream = null;
        ByteArrayOutputStream out = null;
        try {
            url = new URL(imgURL);
            httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.connect();
                inputStream = new BufferedInputStream(httpURLConnection.getInputStream(), 8192);
                out = new ByteArrayOutputStream();
                byte bytes[] = new byte[8192];
                int count;
                while ((count = inputStream.read(bytes)) != -1) {
                    out.write(bytes, 0, count);
                }
                return out.toByteArray();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        } finally {
            try {
                if (httpURLConnection != null)
                    httpURLConnection.disconnect();
                if (inputStream != null)
                    inputStream.close();
                if (out != null) {
                    out.flush();
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

}
