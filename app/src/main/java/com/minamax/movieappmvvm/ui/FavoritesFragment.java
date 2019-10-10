package com.minamax.movieappmvvm.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.minamax.movieappmvvm.R;
import com.minamax.movieappmvvm.model.Movie;
import com.minamax.movieappmvvm.ui.adapter.MovieAdapter;
import com.minamax.movieappmvvm.ui.utils.FavoriteButtonClickListener;
import com.minamax.movieappmvvm.view_model.MoviesListViewModel;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class FavoritesFragment extends Fragment {

    private MoviesListViewModel viewModel;
    private MovieAdapter movieAdapter;
    private RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.movies_fragment, container, false);
        recyclerView = view.findViewById(R.id.rv_movies);
        GridLayoutManager manager = new GridLayoutManager(getActivity(), 2);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return 1;
            }
        });
        recyclerView.setLayoutManager(manager);
        viewModel = new MoviesListViewModel(((MoviesListActivity)getActivity()).getApplication());
        viewModel.fetchMoviesList("FAVORITES");
        movieAdapter = new MovieAdapter(new ArrayList<>(), viewModel.getFavorites().getValue(), new FavoriteButtonClickListener() {
            @Override
            public void onFavorite(Movie movie) {
                viewModel.favorite(movie);
                Log.i("OnFavorite", "OnFavorite");
            }
        });
        viewModel.getFavorites().observe(getViewLifecycleOwner(),new Observer<List<Movie>>() {
            @Override
            public void onChanged(List<Movie> movies) {
                movieAdapter.setData(movies, viewModel.getFavorites().getValue());
            }
        });
        recyclerView.setAdapter(movieAdapter);
        return view;
    }
}
