package com.example.movie_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.example.movie_app.ui.theme.Movie_AppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Movie_AppTheme {
                AppNavigator()
            }
        }
    }
}

@Composable
fun AppNavigator() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {
        composable("home") { HomeScreen(navController) }
        composable("add_movies") { AddMoviesScreen(navController) }
        composable("search_movies") { SearchMoviesScreen(navController) }
        composable("search_actors") { SearchActorsScreen(navController) }
        composable("search_titles") { SearchTitlesScreen(navController) }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "Movie Vault",
                            style = MaterialTheme.typography.headlineMedium
                        )
                    }
                }
            )
        }
    ) { padding ->

        BoxWithConstraints(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(24.dp)
        ) {
            if (maxWidth < 600.dp) {
                // ✅ Portrait mode
                Column(
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxSize()
                ) {

                    Spacer(modifier = Modifier.height(90.dp))

                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = "Movie Logo",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                    )

                    Spacer(modifier = Modifier.height(115.dp))

                    HomeButton(text = "Add Movies to DB") {
                        navController.navigate("add_movies")
                    }

                    HomeButton(text = "Search for Movies") {
                        navController.navigate("search_movies")
                    }

                    HomeButton(text = "Search for Actors") {
                        navController.navigate("search_actors")
                    }

                    HomeButton(text = "Search Titles (OMDb)") {
                        navController.navigate("search_titles")
                    }
                }
            } else {
                // ✅ Landscape mode
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxSize()
                ) {

                    // Logo on LEFT
                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = "Movie Logo",
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    )

                    // Buttons on RIGHT
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {


                        HomeButton(text = "Add Movies to DB") {
                            navController.navigate("add_movies")
                        }

                        HomeButton(text = "Search for Movies") {
                            navController.navigate("search_movies")
                        }

                        HomeButton(text = "Search for Actors") {
                            navController.navigate("search_actors")
                        }

                        HomeButton(text = "Search Titles (OMDb)") {
                            navController.navigate("search_titles")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HomeButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            ,
        shape = RoundedCornerShape(24.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF331A79), // Blue shade
            contentColor = Color.White
        )
    ) {
        Text(text)
    }
}

