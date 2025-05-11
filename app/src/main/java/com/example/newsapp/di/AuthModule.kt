package com.example.newsapp.di

import android.content.Context
import com.example.newsapp.data.repo.AuthRepositoryImpl
import com.example.newsapp.domain.repo.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth() : FirebaseAuth{
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun provideAuthRepository(@ApplicationContext context: Context, auth: FirebaseAuth): AuthRepository{
        return AuthRepositoryImpl(context ,auth)
    }


}