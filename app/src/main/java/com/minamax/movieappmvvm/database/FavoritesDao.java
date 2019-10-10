package com.minamax.movieappmvvm.database;

import com.minamax.movieappmvvm.model.Movie;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface FavoritesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addToFavorite(Movie movie);

    @Delete()
    void removeFromFavorite(Movie movie);

    @Query("SELECT * FROM favorites_table")
    List<Movie> getAllFavorites();
}
