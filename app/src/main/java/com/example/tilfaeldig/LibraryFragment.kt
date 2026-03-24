package com.example.tilfaeldig

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tilfaeldig.model.LibraryMovie
import com.example.tilfaeldig.model.MovieAdapter
import com.google.firebase.firestore.FirebaseFirestore


class LibraryFragment : Fragment(R.layout.fragment_library) {

    private val db = FirebaseFirestore.getInstance()
    private lateinit var recyclerView: RecyclerView
    private val movies = mutableListOf<LibraryMovie>()
    private lateinit var adapter: MovieAdapter

    // private val movie = mutableListOf<LibraryMovie>()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = MovieAdapter(movies) { movie ->
            // Supprimer le film de Firebase
            db.collection("library")
                .document(movie.docId)
                .delete()
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "${movie.title} supprimé", Toast.LENGTH_SHORT).show()
                    loadMovies() // recharge la liste après suppression
                }
        }

        recyclerView.adapter = adapter

        loadMovies()
    }

    private fun loadMovies() {
        db.collection("library").get()
            .addOnSuccessListener { result ->
                movies.clear()
                for (doc in result) {
                    val docId = doc.id
                    val id = doc.getLong("id")?.toInt() ?: 0
                    val title = doc.getString("title") ?: ""
                    val year = doc.getLong("year")?.toInt() ?: 0
                    val poster = doc.getString("poster") ?: ""
                    val type = doc.getString("type") ?: ""

                    movies.add(LibraryMovie(id, title, year, docId, poster, type))
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Log.e("Firebase", "Erreur chargement films", e)
            }
    }
}

