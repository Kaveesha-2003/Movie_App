package com.example.movie_app


import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.movie_app.data.MovieDatabase
import com.example.movie_app.model.Movie
import kotlinx.coroutines.*
import org.json.JSONObject
import java.net.URL

@Composable
fun SearchMoviesScreen() {
    val context = LocalContext.current
    val viewModel: SearchMoviesViewModel = viewModel(factory = SearchMoviesViewModelFactory(context))

    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Enter Movie Title") },
            modifier = Modifier.fillMaxWidth()
        )

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            Button(onClick = { viewModel.searchMovie(searchQuery.text) }) {
                Text("Retrieve Movie")
            }
            Button(onClick = { viewModel.saveMovieToDB() }) {
                Text("Save Movie to DB")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        viewModel.foundMovie?.let { movie ->
            Text("Title: ${movie.title}")
            Text("Year: ${movie.year}")
            Text("Rated: ${movie.rated}")
            Text("Released: ${movie.released}")
            Text("Runtime: ${movie.runtime}")
            Text("Genre: ${movie.genre}")
            Text("Director: ${movie.director}")
            Text("Writer: ${movie.writer}")
            Text("Actors: ${movie.actors}")
            Text("Plot: ${movie.plot}")
        }

        viewModel.error?.let { error ->
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = error, color = MaterialTheme.colorScheme.error)
        }
    }
}

class SearchMoviesViewModel(private val context: Context) : ViewModel() {
    var foundMovie: Movie? by mutableStateOf(null)
    var error: String? by mutableStateOf(null)
    private val dao = MovieDatabase.getDatabase(context).movieDao()

    fun searchMovie(title: String) {
        error = null
        foundMovie = null
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val url =
                    "https://www.omdbapi.com/?t=${title.trim().replace(" ", "+")}&apikey=67b59f05"
                val response = URL(url).readText()
                val json = JSONObject(response)

                if (json.getString("Response") == "True") {
                    foundMovie = Movie(
                        title = json.getString("Title"),
                        year = json.getString("Year"),
                        rated = json.getString("Rated"),
                        released = json.getString("Released"),
                        runtime = json.getString("Runtime"),
                        genre = json.getString("Genre"),
                        director = json.getString("Director"),
                        writer = json.getString("Writer"),
                        actors = json.getString("Actors"),
                        plot = json.getString("Plot")
                    )
                } else {
                    error = "Movie not found."
                }
            } catch (e: Exception) {
                error = "Error: ${e.localizedMessage}"
            }
        }
    }

    fun saveMovieToDB() {
        viewModelScope.launch(Dispatchers.IO) {
            foundMovie?.let {
                dao.insertMovie(it)
            }
        }
    }
}

class SearchMoviesViewModelFactory(private val context: Context) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SearchMoviesViewModel(context) as T
    }
}
