package com.example.newsapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.newsapp.data.model.Article
import com.example.newsapp.utils.Converters

@Database(entities = [Article::class], version = 2)
@TypeConverters(Converters::class)
abstract class ArticleDataBase : RoomDatabase() {
    abstract fun getArticleDao() : ArticleDao
}

