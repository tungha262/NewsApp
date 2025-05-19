package com.example.newsapp.data.repo

import android.content.Context
import com.example.newsapp.data.local.SharedPreferenceHelper
import com.example.newsapp.domain.repo.AuthRepository
import com.example.newsapp.domain.state.Resource
import com.example.newsapp.network.NetworkConfig
import com.example.newsapp.utils.InputCheckField
import com.google.firebase.auth.EmailAuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val context: Context,
    private val auth: FirebaseAuth,
    private val sharedPreferenceHelper: SharedPreferenceHelper
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

    override suspend fun signup(name: String, email: String, password: String): Resource<String> {
        if (!NetworkConfig.isInternetConnected(context)) {
            return Resource.Failed("Không có kết nối mạng!")
        }

        when {
            name.isEmpty() -> return Resource.Failed("Vui lòng nhập tên!")
            name.length < 3 -> return Resource.Failed("Tên phải từ 3 kí tự trở lên!")
            email.isEmpty() -> return Resource.Failed("Vui lòng nhập email!")
            password.isBlank() -> return Resource.Failed("Vui lòng nhập mật khẩu!")
            !InputCheckField.isValidEmail(email) -> return Resource.Failed("Email không đúng định dạng!")
            !InputCheckField.isValidPassword(password) -> return Resource.Failed("Mật khẩu phải từ 6 kí tự trở lên!")
        }

        try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val userId = result.user?.uid
            if (userId != null) {
                sharedPreferenceHelper.setUserName(userId, name)
                return Resource.Success("Đăng ký thành công!")
            } else {
                return Resource.Failed("Đăng ký thất bại, Vui lòng thử lại!")
            }
        } catch (e: Exception) {
            val error = when (e) {
                is FirebaseAuthUserCollisionException -> "Email đã tồn tại, vui lòng thử lại!"
                else -> "Có lỗi đã xảy ra. Vui lòng kiểm tra lại thông tin!"
            }
            return Resource.Failed(error)
        }
    }

    override suspend fun changePassword(
        old: String,
        new: String,
        confirm: String
    ): Resource<String> {
        when {
            old.isEmpty() -> return Resource.Failed("Vui lòng nhập mật khẩu hiện tại!")
            new.isEmpty() -> return Resource.Failed("Vui lòng nhập mật khẩu mới!")
            confirm.isEmpty() -> return Resource.Failed("Vui lòng xác nhận lại mật khẩu!")
            !InputCheckField.isValidPassword(new) -> return Resource.Failed("Mật khẩu mới phải từ 6 kí tự trở lên!")
            (new != confirm) -> return Resource.Failed("Mật khẩu mới không khớp!")
        }
        val user = auth.currentUser!!

        try {
            if (reAuthenticate(old)) {
                user.updatePassword(new).await()
                return Resource.Success("Đổi mật khẩu thành công!")
            } else {
                return Resource.Failed("Mật khẩu hiện tại không đúng!")
            }

        } catch (e: Exception) {
            return Resource.Failed("Có lỗi xảy ra, vui lòng thử lại!")
        }
    }

    override suspend fun resetPassword(email: String): Resource<String> {
        if (!NetworkConfig.isInternetConnected(context)) {
            return Resource.Failed("Không có kết nối mạng!")
        }

        when {
            email.isEmpty() -> return Resource.Failed("Vui lòng nhập email!")
            !(InputCheckField.isValidEmail(email)) -> return Resource.Failed("Email không đúng định dạng!")
        }

        try {
            FirebaseAuth.getInstance().sendPasswordResetEmail(email).await()
            return Resource.Success("Đã gửi email đặt lại mật khẩu! Vui lòng kiểm tra hộp thư đến.")
        } catch (e: Exception) {
            return Resource.Failed("Có lỗi đã xảy ra. Vui lòng kiểm tra lại thông tin!")
        }

    }

    suspend fun reAuthenticate(password: String): Boolean {
        val user = auth.currentUser ?: return false
        val email = user.email ?: return false
        return try {
            val credential = EmailAuthProvider.getCredential(email, password)
            user.reauthenticate(credential).await()
            true
        } catch (e: Exception) {
            false
        }
    }

}