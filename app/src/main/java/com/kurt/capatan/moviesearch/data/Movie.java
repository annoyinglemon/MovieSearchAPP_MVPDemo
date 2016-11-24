package com.kurt.capatan.moviesearch.data;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by Lemon on 11/22/2016.
 */

public class Movie implements Serializable{
    private String imdbId;
    private String title;
    private int year;
    private String rated;
    private String release;
    private String runTime;
    private String genre;
    private String director;
    private String writer;
    private String actor;
    private String plot;
    private String language;
    private String country;
    private String awards;
    private String posterURL;
    private byte[] poster = null;
    private int metascore;
    private double imdb;

    public Movie(){

    }

    public void setImdbId(String imdbId){
        this.imdbId = imdbId;
    }

    public String getImdbId(){
        return this.imdbId;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public String getTitle(){
        return this.title;
    }

    public void setYear(int year){
        this.year = year;
    }

    public int getYear(){
        return this.year;
    }

    public void setRated(String rated){
        this.rated = rated;
    }

    public String getRated(){
        return this.rated;
    }

    public void setRelease(String release){
        this.release = release;
    }

    public String getRelease(){
        return this.release;
    }

    public void setRunTime(String runTime){
        this.runTime = runTime;
    }

    public String getRunTime(){
        return this.runTime;
    }

    public void setGenre(String genre){
        this.genre = genre;
    }

    public String getGenre(){
        return this.genre;
    }

    public void setDirector(String director){
        this.director = director;
    }

    public String getDirector(){
        return this.director;
    }

    public void setWriter(String writer){
        this.writer = writer;
    }

    public String getWriter(){
        return this.writer;
    }

    public void setActor(String actor){
        this.actor = actor;
    }

    public String getActor(){
        return this.actor;
    }

    public void setPlot(String plot){
        this.plot = plot;
    }

    public String getPlot(){
        return this.plot;
    }

    public  void setLanguage(String language){
        this.language = language;
    }

    public String getLanguage(){
        return this.language;
    }

    public void setCountry(String country){
        this.country = country;
    }

    public String getCountry(){
        return this.country;
    }

    public void setAwards(String awards){
        this.awards = awards;
    }

    public String getAwards(){
        return this.awards;
    }

    public void setPoster(byte[] poster){
        this.poster = poster;
    }

    public byte[] getPoster(){
        return this.poster;
    }

    public void setMetascore(int metascore){
        this.metascore = metascore;
    }

    public int getMetascore(){
        return this.metascore;
    }

    public void setImdb(double imdb){
        this.imdb = imdb;
    }

    public double getImdb(){
        return this.imdb;
    }

    public void setPosterUrl(String posterURL){
        this.posterURL = posterURL;
    }

    public String getPosterURL(){
        return this.posterURL;
    }
}
