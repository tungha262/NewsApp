package com.example.newsapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.newsapp.data.model.Article
import kotlinx.coroutines.flow.Flow

@Dao
interface ArticleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavoriteArticle(article: Article)

    @Query("SELECT * FROM articles WHERE userId = :userId ORDER BY pubDate ASC")
    fun getAllFavoriteArticle(userId: String): Flow<List<Article>>

    @Query("DELETE FROM articles WHERE articleId = :articleId AND userId = :userId")
    suspend fun deleteFavoriteArticle(articleId: String, userId: String)

    @Query("DELETE FROM articles WHERE userId = :userId")
    suspend fun deleteAllFavoriteArticle(userId: String)

    @Query("SELECT EXISTS (SELECT 1 FROM articles WHERE articleId = :id AND userId = :userId)")
    fun findArticleById(id: String, userId: String): Flow<Boolean>
}
