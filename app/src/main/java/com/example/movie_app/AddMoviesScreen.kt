package com.example.movie_app


import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movie_app.data.MovieDatabase
import com.example.movie_app.model.Movie
import kotlinx.coroutines.launch

@Composable
fun AddMoviesScreen() {
    val context = LocalContext.current
    val viewModel: AddMoviesViewModel = viewModel(factory = AddMoviesViewModelFactory(context))
    var added by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = {
            viewModel.addMovies()
            added = true
        }) {
            Text("Add Movies to DB")
        }

        if (added) {
            Spacer(modifier = Modifier.height(16.dp))
            Text("✅ Movies added successfully!", color = MaterialTheme.colorScheme.primary)
        }
    }
}

class AddMoviesViewModel(private val context: Context) : ViewModel() {
    private val movieDao = MovieDatabase.getDatabase(context).movieDao()

    fun addMovies() {
        viewModelScope.launch {
            val movies = listOf(
                Movie(
                    title = "The Shawshank Redemption",
                    year = "1994",
                    rated = "R",
                    released = "14 Oct 1994",
                    runtime = "142 min",
                    genre = "Drama",
                    director = "Frank Darabont",
                    writer = "Stephen King, Frank Darabont",
                    actors = "Tim Robbins, Morgan Freeman, Bob Gunton",
                    plot = "Two imprisoned men bond over a number of years, finding solace and eventual redemption through acts of common decency."
                ),
                Movie(
                    title = "Batman: The Dark Knight Returns, Part 1",
                    year = "2012",
                    rated = "PG-13",
                    released = "25 Sep 2012",
                    runtime = "76 min",
                    genre = "Animation, Action, Crime, Drama, Thriller",
                    director = "Jay Oliva",
                    writer = "Bob Kane, Frank Miller, Klaus Janson, Bob Goodman",
                    actors = "Peter Weller, Ariel Winter, David Selby, Wade Williams",
                    plot = "Batman has not been seen for ten years. A new breed of criminal ravages Gotham City, forcing 55-year-old Bruce Wayne back into the cape and cowl."
                ),
                Movie(
                    title = "The Matrix",
                    year = "1999",
                    rated = "R",
                    released = "31 Mar 1999",
                    runtime = "136 min",
                    genre = "Action, Sci-Fi",
                    director = "Lana Wachowski, Lilly Wachowski",
                    writer = "Lilly Wachowski, Lana Wachowski",
                    actors = "Keanu Reeves, Laurence Fishburne, Carrie-Anne Moss",
                    plot = "Neo discovers the life he knows is an elaborate deception of an evil cyber-intelligence."
                )
                // ➕ You can add more later
            )
            movieDao.insertAll(movies)
        }
    }
}

class AddMoviesViewModelFactory(private val context: Context) : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AddMoviesViewModel(context) as T
    }
}
