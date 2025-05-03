package com.example.movie_app

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import org.json.JSONObject
import java.net.URL

data class SimpleMovie(val title: String, val year: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchTitlesScreen(navController: NavController) {

    var query by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(""))
    }

    var results by rememberSaveable { mutableStateOf<List<SimpleMovie>>(emptyList()) }

    var error by rememberSaveable { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Search Titles (OMDb)") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("home") }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(24.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    label = { Text("Enter title keyword ") },
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
                    modifier = Modifier.padding(top = 8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF331A79), // ✅ Your selected color
                        contentColor = Color.White
                    )
                ) {
                    Text("Search Titles")
                }

                Spacer(modifier = Modifier.height(16.dp))

                error?.let {
                    Text("Error: $it", color = MaterialTheme.colorScheme.error)
                }

                if (results.isEmpty() && error == null) {
                    Text("No results found.")
                }
            }

            items(results.take(10)) { movie ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE2D6FD))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "🎬 ${movie.title}",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color(0xFF331A79)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Year: ${movie.year}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
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
