package com.minamax.movieappmvvm.ui.adapter;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.minamax.movieappmvvm.R;
import com.minamax.movieappmvvm.model.Movie;
import com.minamax.movieappmvvm.ui.utils.FavoriteButtonClickListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static com.minamax.movieappmvvm.view_model.MoviesListViewModel.movieImagePathBuilder;

@SuppressWarnings("ALL")
public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private List<Movie> movieList = new ArrayList<>();
    private List<Movie> favoritesList = new ArrayList<>();
    private FavoriteButtonClickListener favoriteButtonClickListener;

    public MovieAdapter(List<Movie> movieList, List<Movie> favoritesList, FavoriteButtonClickListener favoriteButtonClickListener) {
        this.movieList = movieList;
        this.favoritesList = favoritesList;
        this.favoriteButtonClickListener = favoriteButtonClickListener;
        notifyDataSetChanged();
    }

    public void setData(List<Movie> movieList, List<Movie> favoritesList) {
        this.movieList = movieList;
        this.favoritesList = favoritesList;
        notifyDataSetChanged();
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_card_view, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        Movie movie = movieList.get(position);
        ImageButton icon = holder.itemView.findViewById(R.id.favorite_icon);
        if (movie.isFav()) {
            icon.setImageResource(R.drawable.ic_favorite_red_24dp);
        } else {
            icon.setImageResource(R.drawable.ic_favorite_black_24dp);
        }
        holder.bind(movie, favoriteButtonClickListener);
    }

    @Override
    public int getItemCount() {
        if (movieList == null) return 0;
        else return movieList.size();
    }

    class MovieViewHolder extends RecyclerView.ViewHolder {
        private ImageView mMoviePoster;
        private CardView mMovieCard;
        private ImageButton favoriteIcon;

        public MovieViewHolder(final View itemView) {
            super(itemView);
            mMoviePoster = itemView.findViewById(R.id.iv_movie_poster);
            mMovieCard = itemView.findViewById(R.id.cv_movie_card);
            favoriteIcon = itemView.findViewById(R.id.favorite_icon);
        }

        public void bind(final Movie movie, final FavoriteButtonClickListener favoriteButtonClickListener) {
            mMovieCard.setLayoutParams(new ViewGroup.LayoutParams(getScreenWidth()/2, getMeasuredPosterHeight(getScreenWidth()/2)));
            Picasso.with(mMoviePoster.getContext()).load(movieImagePathBuilder(movie.getPosterPath())).placeholder(R.drawable.placeholder).fit().centerCrop().into(mMoviePoster);
            favoriteIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i("OnClick" ,"OnClick");
                    if (movie.isFav())
                        movie.setFav(false);
                    else
                        movie.setFav(true);
                    favoriteButtonClickListener.onFavorite(movie);
                    notifyItemChanged(getAdapterPosition());
                }
            });
        }

        private int getScreenWidth() {
            return Resources.getSystem().getDisplayMetrics().widthPixels;
        }

        private int getMeasuredPosterHeight(int width) {
            return (int) (width * 1.5f);
        }
    }

}