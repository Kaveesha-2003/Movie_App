package com.example.movie_app

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL

data class SimpleMovie(val title: String, val year: String)

@Composable
fun SearchTitlesScreen() {
    var query by remember { mutableStateOf(TextFieldValue("")) }
    var results by remember { mutableStateOf<List<SimpleMovie>>(emptyList()) }
    var error by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Top
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            label = { Text("Enter title keyword (e.g. 'mat')") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                error = null
                results = emptyList()
                if (query.text.trim().isNotEmpty()) {
                    fetchMovies(query.text) { movies, err ->
                        results = movies
                        error = err
                    }
                }
            },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Search Titles")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (error != null) {
            Text("Error: $error", color = MaterialTheme.colorScheme.error)
        } else if (results.isEmpty()) {
            Text("No results found.")
        } else {
            results.take(10).forEach { movie ->
                Text("🎬 ${movie.title} (${movie.year})")
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

private fun fetchMovies(query: String, onResult: (List<SimpleMovie>, String?) -> Unit) {
    val apiKey = "67b59f05"
    val encodedQuery = query.trim().replace(" ", "+")

    Thread {
        try {
            val url = "https://www.omdbapi.com/?s=$encodedQuery&apikey=$apiKey"
            val response = URL(url).readText()
            val json = JSONObject(response)

            if (json.getString("Response") == "True") {
                val searchResults = json.getJSONArray("Search")
                val movies = mutableListOf<SimpleMovie>()
                for (i in 0 until searchResults.length()) {
                    val obj = searchResults.getJSONObject(i)
                    val title = obj.getString("Title")
                    val year = obj.getString("Year")
                    movies.add(SimpleMovie(title, year))
                }
                onResult(movies, null)
            } else {
                val errorMsg = json.optString("Error", "Unknown error")
                onResult(emptyList(), errorMsg)
            }
        } catch (e: Exception) {
            Log.e("OMDb", "Exception: ${e.message}")
            onResult(emptyList(), "Error: ${e.localizedMessage}")
        }
    }.start()
}
