package com.minamax.movieappmvvm.view_model;

import android.app.Application;
import com.minamax.movieappmvvm.model.Movie;
import com.minamax.movieappmvvm.repository.MoviesListRepository;

import java.util.List;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

public class MoviesListViewModel extends AndroidViewModel {

    private MoviesListRepository repository;
    private MutableLiveData<List<Movie>> nowPlayingList;
    private MutableLiveData<List<Movie>> topRatedList;
    private MutableLiveData<List<Movie>> favoritesList;

    public MoviesListViewModel(@NonNull Application application) {
        super(application);
        repository = new MoviesListRepository(application);
        nowPlayingList = repository.getNowPlaying();
        topRatedList = repository.getTopRated();
        favoritesList = repository.getFavorites();
    }

    public void fetchMoviesList(String string) {
        if (string == "FAVORITES") {
            repository.fetchMoviesList("FAVORITES");
        } else if (string == "NOW PLAYING"){
            repository.fetchMoviesList("NOW PLAYING");
        } else if (string == "TOP RATED"){
            repository.fetchMoviesList("TOP RATED");
        }
    }

    public void fetchMoviesList(String string, int pageNumber) {
        if (string == "NOW PLAYING"){
            repository.fetchMoviesList("NOW PLAYING", pageNumber);
        } else if (string == "TOP RATED"){
            repository.fetchMoviesList("TOP RATED", pageNumber);
        }
    }

    public MutableLiveData<List<Movie>> getNowPlaying() {
        return nowPlayingList;
    }

    public MutableLiveData<List<Movie>> getTopRated() {
        return topRatedList;
    }

    public MutableLiveData<List<Movie>> getFavorites() {
        return favoritesList;
    }

    public void favorite(Movie movie) {
        repository.favorite(movie);
    }

    public static String movieImagePathBuilder(String imagePath) {
        return "https://image.tmdb.org/t/p/" +
                "w500" +
                imagePath;
    }
}
