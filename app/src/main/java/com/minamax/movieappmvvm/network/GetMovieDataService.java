package com.minamax.movieappmvvm.network;

import com.minamax.movieappmvvm.model.MoviePageResult;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

@SuppressWarnings("ALL")
public interface GetMovieDataService {
    @GET("movie/now_playing")
    Call<MoviePageResult> getNowPlayingMovies(@Query("api_key") String userkey);

    @GET("movie/now_playing")
    Call<MoviePageResult> getNowPlayingPage(@Query("api_key") String userkey, @Query("page") int pageNumber);

    @GET("movie/top_rated")
    Call<MoviePageResult> getTopRatedMovies(@Query("api_key") String userkey);

    @GET("movie/top_rated")
    Call<MoviePageResult> getTopRatedPage(@Query("api_key") String userkey, @Query("page") int pageNumber);
}