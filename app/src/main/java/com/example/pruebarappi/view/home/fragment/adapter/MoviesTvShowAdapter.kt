package com.example.pruebarappi.view.home.fragment.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pruebarappi.R
import com.example.pruebarappi.db.model.ResultService
import com.example.pruebarappi.utils.BASE_URL_IMAGE
import com.example.pruebarappi.view.home.fragment.interfaces.MoviesTvShowInterface
import com.facebook.drawee.view.SimpleDraweeView

class MoviesTvShowAdapter(private val listMoviesTvSeasons: ArrayList<ResultService>?, private val moviesTvShowInterface: MoviesTvShowInterface?) :
    RecyclerView.Adapter<MoviesTvShowAdapter.ViewHolder>() {

    class ViewHolder(val imageView: SimpleDraweeView) : RecyclerView.ViewHolder(imageView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.view_image, parent, false) as SimpleDraweeView
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.imageView.setImageURI(Uri.parse(BASE_URL_IMAGE + listMoviesTvSeasons!![position].poster_path))
        holder.imageView.setOnClickListener { moviesTvShowInterface?.clickMovieTvShow( listMoviesTvSeasons[position]) }
    }

    override fun getItemCount() = listMoviesTvSeasons?.size!!
}