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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun SearchActorsScreen() {
    val context = LocalContext.current
    val viewModel: SearchActorsViewModel = viewModel(factory = SearchActorsViewModelFactory(context))

    var actorQuery by remember { mutableStateOf(TextFieldValue("")) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = actorQuery,
            onValueChange = { actorQuery = it },
            label = { Text("Enter Actor Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = { viewModel.searchMovies(actorQuery.text) },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Search")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (viewModel.results.isEmpty()) {
            Text("No matching movies.")
        } else {
            viewModel.results.forEach { movie ->
                Text("🎬 ${movie.title} (${movie.year})")
                Text("Actors: ${movie.actors}")
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

class SearchActorsViewModel(private val context: Context) : ViewModel() {
    var results by mutableStateOf<List<Movie>>(emptyList())
    private val dao = MovieDatabase.getDatabase(context).movieDao()

    fun searchMovies(name: String) {
        viewModelScope.launch(Dispatchers.IO) {
            results = dao.searchMoviesByActor(name)
        }
    }
}

class SearchActorsViewModelFactory(private val context: Context) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SearchActorsViewModel(context) as T
    }
}
