package com.example.movie_app.data


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.movie_app.model.Movie

@Dao
interface MovieDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMovie(movie: Movie)


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(movies: List<Movie>)



    @Query("SELECT * FROM movies")
    suspend fun getAllMovies(): List<Movie>

    @Query("SELECT * FROM movies WHERE actors LIKE '%' || :name || '%' COLLATE NOCASE")
    suspend fun searchMoviesByActor(name: String): List<Movie>

}
