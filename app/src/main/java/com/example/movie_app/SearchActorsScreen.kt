package com.example.movie_app


import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.movie_app.data.MovieDatabase
import com.example.movie_app.model.Movie
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchActorsScreen(navController: NavController) {
    val context = LocalContext.current
    val viewModel: SearchActorsViewModel = viewModel(factory = SearchActorsViewModelFactory(context))

    var actorQuery by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(""))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Search Actors") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("home") }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
                .fillMaxSize(),
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
                modifier = Modifier.padding(top = 8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF331A79), // ✅ Your selected color
                    contentColor = Color.White
                )

            ) {
                Text("Search")
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (viewModel.error != null) {
                Text(
                    text = "Error: ${viewModel.error}",
                    color = MaterialTheme.colorScheme.error
                )
            } else if (viewModel.results.isEmpty()) {
                Text("No matching movies.")
            } else {
                viewModel.results.forEach { movie ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE2D6FD))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "🎬 ${movie.title} (${movie.year})",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color(0xFF331A79)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Actors: ${movie.actors}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }
}


class SearchActorsViewModel(private val context: Context) : ViewModel() {
    var results by mutableStateOf<List<Movie>>(emptyList())
    var error by mutableStateOf<String?>(null)
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
