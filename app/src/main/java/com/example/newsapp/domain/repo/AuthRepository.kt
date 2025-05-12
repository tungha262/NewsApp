package com.example.newsapp.domain.repo

import com.example.newsapp.domain.state.Resource
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun login(email:String, password: String) : Flow<Resource<String>>

    suspend fun signup(name:String, email: String, password: String) : Resource<String>

}