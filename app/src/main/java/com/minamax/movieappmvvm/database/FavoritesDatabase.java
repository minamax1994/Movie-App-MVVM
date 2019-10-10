package com.minamax.movieappmvvm.database;

import android.content.Context;
import android.os.AsyncTask;

import com.minamax.movieappmvvm.model.Movie;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {Movie.class}, version = 1, exportSchema = false)
public abstract class FavoritesDatabase extends RoomDatabase {

    private static FavoritesDatabase instance;

    public abstract FavoritesDao favoritesDao();

    public static synchronized FavoritesDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), FavoritesDatabase.class, "favorites_database").addCallback(roomCallback).fallbackToDestructiveMigration().allowMainThreadQueries().build();
        }
        return instance;
    }

    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new PopulateDbAsyncTask(instance).execute();
        }
    };

    private static class PopulateDbAsyncTask extends AsyncTask<Void, Void, Void> {
        private FavoritesDao favoritesDao;

        private PopulateDbAsyncTask(@NonNull FavoritesDatabase db) {
            favoritesDao = db.favoritesDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            return null;
        }
    }
}
