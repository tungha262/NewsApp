package com.example.newsapp.di

import android.content.Context
import androidx.room.Room
import com.example.newsapp.data.local.ArticleDao
import com.example.newsapp.data.local.ArticleDataBase
import com.example.newsapp.data.repo.LocalRepositoryImpl
import com.example.newsapp.domain.repo.LocalRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoomModule {

    @Provides
    @Singleton
    fun provideArticleDataBase(
        @ApplicationContext context: Context
    ): ArticleDataBase = Room.databaseBuilder(
        context,
        ArticleDataBase::class.java,
        "favorite_database"
    ).fallbackToDestructiveMigration(false).build()

    @Provides
    @Singleton
    fun provideArticleDao(dataBase: ArticleDataBase) : ArticleDao{
        return dataBase.getArticleDao()
    }

    @Provides
    @Singleton
    fun provideLocalRepository(dao: ArticleDao, auth: FirebaseAuth) : LocalRepository{
        return LocalRepositoryImpl(dao,auth)
    }

}