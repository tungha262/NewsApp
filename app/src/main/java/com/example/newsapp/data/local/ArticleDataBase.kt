package com.example.newsapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.newsapp.data.model.Article

@Database(entities = [Article::class], version = 1)
abstract class ArticleDataBase : RoomDatabase() {
    abstract fun getArticleDao() : ArticleDao
}