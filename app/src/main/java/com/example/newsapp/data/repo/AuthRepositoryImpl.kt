package com.example.newsapp.data.repo

import android.content.Context
import com.example.newsapp.domain.repo.AuthRepository
import com.example.newsapp.domain.state.Resource
import com.example.newsapp.network.NetworkConfig
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val context: Context,
    private val auth: FirebaseAuth
) : AuthRepository {
    override fun login(
        email: String,
        password: String
    ): Flow<Resource<String>> = flow {
        if (!NetworkConfig.isInternetConnected(context)) {
            emit(Resource.Failed("Không có kết nối mạng!"))
            return@flow
        }
        when {
            email.isEmpty() -> {
                emit(Resource.Failed("Vui lòng nhập địa chỉ email"))
                return@flow
            }

            password.isBlank() -> {
                emit(Resource.Failed("Vui lòng nhập mật khẩu!"))
                return@flow
            }
        }
        emit(Resource.Loading)

        try {
            auth.signInWithEmailAndPassword(email, password).await()
            emit(Resource.Success("Đăng nhập thành công!"))
        } catch (e: Exception) {
            when (e) {
                is FirebaseAuthInvalidUserException,
                is FirebaseAuthInvalidCredentialsException -> {
                    emit(Resource.Failed("Tài khoản hoặc mật khẩu không chính xác!"))
                }
                else -> emit(Resource.Failed(e.message.toString()))
            }
        }
    }.flowOn(Dispatchers.IO)

}