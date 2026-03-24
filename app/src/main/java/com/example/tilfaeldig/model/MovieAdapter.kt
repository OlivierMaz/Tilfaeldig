package com.example.tilfaeldig.model

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tilfaeldig.R

class MovieAdapter(

    private val movies: MutableList<LibraryMovie>,
    private val onDelete: (LibraryMovie) -> Unit

) : RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {

    inner class MovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.movieTitle)
        val year: TextView = itemView.findViewById(R.id.movieYear)
        val poster: ImageView = itemView.findViewById(R.id.imgPoster)
        val btnDelete: Button = itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_movie, parent, false)
        return MovieViewHolder(view)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = movies[position]

        holder.title.text = movie.title
        holder.year.text = movie.year.toString()

        Glide.with(holder.itemView.context)
            .load(movie.poster)
            .into(holder.poster)

        holder.btnDelete.setOnClickListener {
            onDelete(movie)
        }
    }

    override fun getItemCount() = movies.size
}