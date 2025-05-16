package com.example.newsapp.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.newsapp.data.model.Article
import com.example.newsapp.domain.state.Resource
import kotlinx.coroutines.flow.Flow

@Dao
interface ArticleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavoriteArticle(article: Article)

    @Query("SELECT * FROM articles ORDER BY pubDate ASC")
    fun getAllFavoriteArticle() : Flow<List<Article>>

    @Delete
    suspend fun deleteFavoriteArticle(article: Article)

    @Query("DELETE FROM articles")
    suspend fun deleteAllFavoriteArticle()

}