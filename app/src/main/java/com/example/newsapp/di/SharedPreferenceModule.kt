package com.example.newsapp.di

import android.content.Context
import android.content.SharedPreferences
import com.example.newsapp.data.local.SharedPreferenceHelper
import com.example.newsapp.utils.Constant
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SharedPreferenceModule {

    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context) : SharedPreferences{
        return context.getSharedPreferences(Constant.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun provideSharedPreferenceHelper(sharedPreferences: SharedPreferences): SharedPreferenceHelper {
        return SharedPreferenceHelper(sharedPreferences)
    }
}