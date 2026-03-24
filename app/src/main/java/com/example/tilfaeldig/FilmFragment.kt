package com.example.tilfaeldig

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.tilfaeldig.api.RetrofitInstance
import com.example.tilfaeldig.model.LibraryMovie
import com.example.tilfaeldig.model.MovieResponse
import com.google.firebase.firestore.FirebaseFirestore
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.random.Random

class FilmFragment : Fragment(R.layout.fragment_film) {

    private lateinit var tvTitle: TextView
    private lateinit var tvOverview: TextView
    private lateinit var tvImdbRating: TextView
    private lateinit var yearRelease: TextView
    private lateinit var imgPoster: ImageView
    private lateinit var btnRandom: Button
    private lateinit var btnAdd: Button

    private val db = FirebaseFirestore.getInstance()

    private var currentMovie: LibraryMovie? = null

    private var retryCount = 0
    private val maxRetries = 5

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvTitle = view.findViewById(R.id.tvTitle)
        tvOverview = view.findViewById(R.id.tvOverview)
        tvImdbRating = view.findViewById(R.id.tvImdbRating)
        yearRelease = view.findViewById(R.id.etYear)
        imgPoster = view.findViewById(R.id.imgPoster)
        btnRandom = view.findViewById(R.id.btnRandom)
        btnAdd = view.findViewById(R.id.btnAddLibrary)

        fetchRandomMovie()

        btnRandom.setOnClickListener {
            fetchRandomMovie()
        }

        btnAdd.setOnClickListener {

            val movieToAdd = currentMovie

            if (movieToAdd != null) {

                val movieMap = mapOf(
                    "id" to movieToAdd.id,
                    "title" to movieToAdd.title,
                    "year" to movieToAdd.year,
                    "poster" to movieToAdd.poster,
                    "type" to movieToAdd.type
                )

                db.collection("library")
                    .document(movieToAdd.id.toString())
                    .set(movieMap)
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(),"Ajouté à la bibliothèque",Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(requireContext(),"Erreur Firebase",Toast.LENGTH_SHORT).show()
                    }

            } else {

                Toast.makeText(requireContext(),"Aucun film chargé",Toast.LENGTH_SHORT).show()

            }
        }
    }

    private fun fetchRandomMovie() {

        val prefs = requireActivity().getSharedPreferences("filters", AppCompatActivity.MODE_PRIVATE)

        val genre = prefs.getInt("genre",0)
        val origin = prefs.getString("origin",null)
        val year = prefs.getInt("year",0)
        val type = prefs.getString("type", "movie")

        if (type == "movie") {
            Log.d("TYPE_DEBUG", "Type sélectionné = $type")


            RetrofitInstance.api.discoverMovies(
                page = Random.nextInt(1,500),
                genreId = if (genre != 0) genre else null,
                language = origin,
                year = if (year != 0) year else null
            ).enqueue(object : Callback<MovieResponse> {

                override fun onResponse(call: Call<MovieResponse>, response: Response<MovieResponse>) {

                    if (!isAdded) return

                    if (!response.isSuccessful) {
                        Log.e("TMDB","Erreur API ${response.code()}")
                        return
                    }

                    val movies = response.body()?.results

                    if (movies.isNullOrEmpty()) {

                        if (retryCount < maxRetries) {
                            retryCount++
                            fetchRandomMovie()
                        } else {

                            tvTitle.text = "Aucun film trouvé"
                            tvOverview.text = "Essayez d'autres filtres"
                            tvImdbRating.text = "-"
                            yearRelease.text = "-"
                            imgPoster.setImageDrawable(null)

                            retryCount = 0
                        }

                        return
                    }

                    retryCount = 0

                    val movie = movies.random()

                    val posterPath = movie.posterpath

                    if (!posterPath.isNullOrEmpty()) {

                        val posterUrl = "https://image.tmdb.org/t/p/w500$posterPath"

                        Glide.with(this@FilmFragment)
                            .load(posterUrl)
                            .into(imgPoster)

                    } else {

                        imgPoster.setImageDrawable(null)

                    }

                    tvTitle.text = movie.title ?: "Titre indisponible"
                    tvOverview.text = movie.overview ?: "Résumé indisponible"

                    val rating = movie.voteAverage ?: 0.0
                    tvImdbRating.text = "IMDb : %.1f/10".format(rating)

                    val color = when {
                        rating >= 7 -> ContextCompat.getColor(requireContext(),R.color.rating_good)
                        rating >= 5 -> ContextCompat.getColor(requireContext(),R.color.rating_medium)
                        else -> ContextCompat.getColor(requireContext(),R.color.rating_bad)
                    }

                    tvImdbRating.setTextColor(color)

                    val date = movie.release_date

                    val yearInt = date
                        ?.takeIf { it.length >= 4 }
                        ?.substring(0,4)
                        ?.toIntOrNull() ?: 0

                    yearRelease.text = "($yearInt)"

                    val posterUrl = "https://image.tmdb.org/t/p/w500${movie.posterpath}"

                    currentMovie = LibraryMovie(
                        id = movie.id.toInt(),
                        title = movie.title ?: "Titre indisponible",
                        year = yearInt,
                        poster = posterUrl,
                        type = "movie"
                    )
                }

                override fun onFailure(call: Call<MovieResponse>, t: Throwable) {

                    if (!isAdded) return

                    Log.e("TMDB","Erreur réseau",t)

                }
            })

        } else {

            RetrofitInstance.api.discoverTv(
                page = Random.nextInt(1,500),
                language = origin
            ).enqueue(object : Callback<MovieResponse> {

                override fun onResponse(call: Call<MovieResponse>, response: Response<MovieResponse>) {

                    if (!isAdded) return

                    if (!response.isSuccessful) {
                        Log.e("TMDB","Erreur API ${response.code()}")
                        return
                    }

                    val series = response.body()?.results

                    if (series.isNullOrEmpty()) {

                        if (retryCount < maxRetries) {
                            retryCount++
                            fetchRandomMovie()
                        } else {

                            tvTitle.text = "Aucune série trouvée"
                            tvOverview.text = "Essayez d'autres filtres"
                            tvImdbRating.text = "-"
                            yearRelease.text = "-"
                            imgPoster.setImageDrawable(null)

                            retryCount = 0
                        }

                        return
                    }

                    retryCount = 0

                    val show = series.random()

                    val posterPath = show.posterpath

                    if (!posterPath.isNullOrEmpty()) {

                        val posterUrl = "https://image.tmdb.org/t/p/w500$posterPath"

                        Glide.with(this@FilmFragment)
                            .load(posterUrl)
                            .into(imgPoster)

                    } else {
                        Log.d("TYPE_DEBUG", "Type sélectionné = $type")

                        imgPoster.setImageDrawable(null)

                    }

                    tvTitle.text = show.name ?: "Titre indisponible"
                    tvOverview.text = show.overview ?: "Résumé indisponible"

                    val rating = show.voteAverage ?: 0.0
                    tvImdbRating.text = "IMDb : %.1f/10".format(rating)

                    val color = when {
                        rating >= 7 -> ContextCompat.getColor(requireContext(),R.color.rating_good)
                        rating >= 5 -> ContextCompat.getColor(requireContext(),R.color.rating_medium)
                        else -> ContextCompat.getColor(requireContext(),R.color.rating_bad)
                    }

                    tvImdbRating.setTextColor(color)

                    val date = show.first_air_date

                    val yearInt = date
                        ?.takeIf { it.length >= 4 }
                        ?.substring(0,4)
                        ?.toIntOrNull() ?: 0

                    yearRelease.text = "($yearInt)"

                    val posterUrl = "https://image.tmdb.org/t/p/w500${show.posterpath}"

                    currentMovie = LibraryMovie(
                        id = show.id.toInt(),
                        title = show.name ?: "Titre indisponible",
                        year = yearInt,
                        poster = posterUrl,
                        type = "tv"
                    )
                }

                override fun onFailure(call: Call<MovieResponse>, t: Throwable) {

                    if (!isAdded) return

                    Log.e("TMDB","Erreur réseau",t)

                }
            })
        }
    }
}