package com.minamax.movieappmvvm.repository;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import com.minamax.movieappmvvm.database.FavoritesDatabase;
import com.minamax.movieappmvvm.database.FavoritesDao;
import com.minamax.movieappmvvm.model.Movie;
import com.minamax.movieappmvvm.model.MoviePageResult;
import com.minamax.movieappmvvm.network.GetMovieDataService;
import com.minamax.movieappmvvm.network.RetrofitInstance;

import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.MutableLiveData;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MoviesListRepository {

    public static final String API_KEY = "31521ab741626851b73c684539c33b5a";
    private FavoritesDao favoritesDao;
    private List<Movie> favoritesList;
    private List<Movie> npl,trl;
    private MutableLiveData<List<Movie>> fl = new MutableLiveData<>();
    private MutableLiveData<List<Movie>> nowPlayingList = new MutableLiveData<>();
    private MutableLiveData<List<Movie>> topRatedList = new MutableLiveData<>();
    private boolean nowPlayingLastPage = false;
    private boolean topRatedLastPage = false;
    private Call<MoviePageResult> call;

    public MoviesListRepository(Application application) {
        FavoritesDatabase favoritesDatabase = FavoritesDatabase.getInstance(application);
        favoritesDao = favoritesDatabase.favoritesDao();
        favoritesList = favoritesDao.getAllFavorites();
        fl.setValue(favoritesList);
        Log.i("getAllFavorites", favoritesDao.getAllFavorites().toString());
    }

    public void fetchMoviesList(String string){
        GetMovieDataService movieDataService = RetrofitInstance.getRetrofitInstance().create(GetMovieDataService.class);
        if (string.equals("FAVORITES")) {
            favoritesList = favoritesDao.getAllFavorites();
            fl.setValue(favoritesList);
            Log.i("getAllFavorites2", favoritesDao.getAllFavorites().toString());
        } else {
            if (string.equals("NOW PLAYING")) {
                call = movieDataService.getNowPlayingMovies(API_KEY);
            } else if (string == "TOP RATED") {
                call = movieDataService.getTopRatedMovies(API_KEY);
            }
            call.enqueue(new Callback<MoviePageResult>() {
                @Override
                public void onResponse(Call<MoviePageResult> call, Response<MoviePageResult> response) {
                    List<Movie> result = response.body().getMovieResult();
                    for (int i=0; i<result.size(); i++) {
                        List<Integer> idList = new ArrayList<>();
                        for (int j=0; j<favoritesList.size(); j++) {
                            idList.add(favoritesList.get(j).getId());
                        }
                        if (idList.contains(result.get(i).getId())) {
                            result.get(i).setFav(true);
                        } else {
                            result.get(i).setFav(false);
                        }
                    }
                    if (string == "NOW PLAYING") {
                        nowPlayingList.setValue(result);
                        nowPlayingLastPage = false;
                    } else if (string == "TOP RATED") {
                        topRatedList.setValue(result);
                        topRatedLastPage = false;
                    }
                }
                @Override
                public void onFailure(Call<MoviePageResult> call, Throwable t) {
                    Log.i("FAILURE", "Something Went Wrong");
                }
            });
        }
    }

    public void fetchMoviesList(String string, int pageNumber){
        GetMovieDataService movieDataService = RetrofitInstance.getRetrofitInstance().create(GetMovieDataService.class);
        if (string.equals("NOW PLAYING") && !nowPlayingLastPage) {
            call = movieDataService.getNowPlayingPage(API_KEY,pageNumber);
        } else if (string.equals("TOP RATED") && !topRatedLastPage) {
            call = movieDataService.getTopRatedPage(API_KEY,pageNumber);
        } else {
            return;
        }
        call.enqueue(new Callback<MoviePageResult>() {
            @Override
            public void onResponse(Call<MoviePageResult> call, Response<MoviePageResult> response) {
                List<Movie> result = response.body().getMovieResult();
                if (result.isEmpty()) {
                    switch (string) {
                        case "NOW PLAYING":
                            nowPlayingLastPage = true;
                            break;
                        case "TOP RATED":
                            topRatedLastPage = true;
                            break;
                        default:
                            break;
                    }
                } else {
                    for (int i = 0; i < result.size(); i++) {
                        List<Integer> idList = new ArrayList<>();
                        for (int j = 0; j < favoritesList.size(); j++) {
                            idList.add(favoritesList.get(j).getId());
                        }
                        if (idList.contains(result.get(i).getId())) {
                            result.get(i).setFav(true);
                        } else {
                            result.get(i).setFav(false);
                        }
                    }
                    switch (string) {
                        case "NOW PLAYING": {
                            List<Movie> total = nowPlayingList.getValue();
                            total.addAll(result);
                            nowPlayingList.setValue(total);
                            break;
                        }
                        case "TOP RATED": {
                            List<Movie> total = topRatedList.getValue();
                            total.addAll(result);
                            topRatedList.setValue(total);
                            break;
                        }
                        default:
                            break;
                    }
                }
            }
            @Override
            public void onFailure(Call<MoviePageResult> call, Throwable t) {
                Log.i("FAILURE", "Last Page Reached");
            }
        });
    }

    public void favorite(Movie movie) {
        new FavoriteAsyncTask().execute(movie);
    }

    private class FavoriteAsyncTask extends AsyncTask<Movie, Void, Void> {
        @Override
        protected Void doInBackground(Movie... movies) {
            if (favoritesList == null || favoritesList.isEmpty()) {
                favoritesDao.addToFavorite(movies[0]);
                npl = nowPlayingList.getValue();
                trl = topRatedList.getValue();
                if (npl != null) {
                    for (int i=0; i<npl.size(); i++) {
                        if (movies[0].getId() == npl.get(i).getId()) {
                            npl.get(i).setFav(true);
                        }
                    }
                }
                if (trl != null) {
                    for (int i = 0; i < trl.size(); i++) {
                        if (movies[0].getId() == trl.get(i).getId()) {
                            trl.get(i).setFav(true);
                        }
                    }
                }
                Log.i("addToFavorite2", "addToFavorite2");
            } else {
                List<Integer> idList = new ArrayList<>();
                for (int i=0; i<favoritesList.size(); i++) {
                    idList.add(favoritesList.get(i).getId());
                }
                if (idList.contains(movies[0].getId())) {
                    favoritesDao.removeFromFavorite(movies[0]);
                    npl = nowPlayingList.getValue();
                    trl = topRatedList.getValue();
                    if (npl != null) {
                        for (int i=0; i<npl.size(); i++) {
                            if (movies[0].getId() == npl.get(i).getId()) {
                                npl.get(i).setFav(false);
                            }
                        }
                    }
                    if (trl != null) {
                        for (int i = 0; i < trl.size(); i++) {
                            if (movies[0].getId() == trl.get(i).getId()) {
                                trl.get(i).setFav(false);
                            }
                        }
                    }
                    Log.i("removeFromFavorite", "removeFromFavorite");
                } else {
                    favoritesDao.addToFavorite(movies[0]);
                    npl = nowPlayingList.getValue();
                    trl = topRatedList.getValue();
                    if (npl != null) {
                        for (int i=0; i<npl.size(); i++) {
                            if (movies[0].getId() == npl.get(i).getId()) {
                                npl.get(i).setFav(true);
                            }
                        }
                    }
                    if (trl != null) {
                        for (int i = 0; i < trl.size(); i++) {
                            if (movies[0].getId() == trl.get(i).getId()) {
                                trl.get(i).setFav(true);
                            }
                        }
                    }
                    Log.i("addToFavorite", "addToFavorite");
                }
            }
            favoritesList = favoritesDao.getAllFavorites();
            fl.postValue(favoritesList);
//            nowPlayingList.postValue(npl);
//            topRatedList.postValue(trl);
            return null;
        }
    }

    public MutableLiveData<List<Movie>> getNowPlaying() {
        return nowPlayingList;
    }

    public MutableLiveData<List<Movie>> getTopRated() {
        return topRatedList;
    }

    public MutableLiveData<List<Movie>> getFavorites() {
        fl.setValue(favoritesList);
        return fl;
    }
}
