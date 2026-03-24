package com.example.tilfaeldig

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.fragment.app.Fragment

class OptionsFragment : Fragment(R.layout.fragment_options) {

    private lateinit var spinnerGenre: Spinner
    private lateinit var spinnerOrigin: Spinner
    private lateinit var spinnerType: Spinner
    private lateinit var etYear: EditText

    private val genres = mapOf(
        "Tous" to null,
        "Action" to 28,
        "Animation" to 16,
        "Comédie" to 35,
        "Crime" to 80,
        "Documentaire" to 99,
        "Drame" to 18,
        "Fantaisie" to 14,
        "Guerre" to 10752,
        "Horreur" to 27,
        "Musique" to 10402,
        "Romance" to 10749,
        "Science-Fiction" to 878
    )

    private val origins = mapOf(
        "🌍 Tous" to null,
        "🇩🇪 DE Allemagne" to "de",
        "🇨🇳 ZH Chine" to "zh",
        "🇰🇷 KO Corée" to "ko",
        "🇩🇰 DA Danemark" to "da",
        "🇪🇸 ES Espagne" to "es",
        "🇺🇸 EN États-Unis" to "en",
        "🇫🇷 FR France" to "fr",
        "🇮🇳 HI Inde (Hindi)" to "hi",
        "🇮🇹 IT Italie" to "it",
        "🇯🇵 JA Japon" to "ja",
        "🇷🇺 RU Russie" to "ru"
    )

    private val types = listOf(
        "Films",
        "Séries"
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        spinnerGenre = view.findViewById(R.id.spinnerGenre)
        spinnerOrigin = view.findViewById(R.id.spinnerOrigin)
        spinnerType = view.findViewById(R.id.spinnerType)
        etYear = view.findViewById(R.id.etYear)

        // remplir les spinners
        spinnerGenre.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            genres.keys.toList()
        )

        spinnerOrigin.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            origins.keys.toList()
        )

        spinnerType.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            types
        )

        // charger valeurs sauvegardées
        loadSavedFilters()

        // sauvegarder les changements
        setupListeners()
    }

    private fun loadSavedFilters() {
        val prefs = requireActivity().getSharedPreferences("filters", AppCompatActivity.MODE_PRIVATE)

        val savedGenreId = prefs.getInt("genre", 0)
        val genreIndex = genres.values.indexOfFirst { it == savedGenreId }
        if (genreIndex >= 0) spinnerGenre.setSelection(genreIndex)

        val savedOrigin = prefs.getString("origin", null)
        val originIndex = origins.values.indexOfFirst { it == savedOrigin }
        if (originIndex >= 0) spinnerOrigin.setSelection(originIndex)

        val savedType = prefs.getString("type", "movie")
        spinnerType.setSelection(
            if (savedType == "movie") 0 else 1
        )

        val savedYear = prefs.getInt("year", 0)
        if (savedYear != 0) etYear.setText(savedYear.toString())
    }

    private fun setupListeners() {
        val prefs = requireActivity().getSharedPreferences("filters", AppCompatActivity.MODE_PRIVATE)

        spinnerGenre.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val genreId = genres.values.toList()[position]
                prefs.edit { putInt("genre", genreId ?: 0) }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        spinnerOrigin.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val originCode = origins.values.toList()[position]
                prefs.edit { putString("origin", originCode) }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        spinnerType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {

                val type = if (position == 0) "movie" else "tv"

                val prefs = requireActivity().getSharedPreferences("filters", AppCompatActivity.MODE_PRIVATE)
                prefs.edit().putString("type", type).apply()

                android.util.Log.d("TYPE_SAVE", "Sauvegardé = $type")
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        etYear.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val year = etYear.text.toString().toIntOrNull() ?: 0
                prefs.edit { putInt("year", year) }
            }
        }
    }
}