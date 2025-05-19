package com.example.newsapp.presentation.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsapp.domain.repo.AuthRepository
import com.example.newsapp.domain.state.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.String

@Suppress("REDUNDANT_ELSE_IN_WHEN")
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private var _loginState = MutableLiveData<Resource<String>>()
    val loginState: LiveData<Resource<String>> get() = _loginState

    private var _signupState = MutableSharedFlow<Resource<String>>()
    val signupState: SharedFlow<Resource<String>> get() = _signupState

    private var _resetPasswordState = MutableSharedFlow<Resource<String>>()
    val resetPasswordState: SharedFlow<Resource<String>> get() = _resetPasswordState

    private var _changePassWordState = MutableSharedFlow<Resource<String>>()
    val changePassWorkState: SharedFlow<Resource<String>> get() = _changePassWordState

    fun login(email: String, password: String) {
        viewModelScope.launch {
            authRepository.login(email, password).collect { rs ->
                when (rs) {
                    is Resource.Loading -> _loginState.value = Resource.Loading
                    is Resource.Success -> _loginState.value = Resource.Success(rs.data)
                    is Resource.Failed -> _loginState.value = Resource.Failed(rs.message)
                }
            }
        }
    }

    fun signup(name: String, email: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _signupState.emit(authRepository.signup(name, email, password))
        }
    }

    fun resetPassword(email: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _resetPasswordState.emit(authRepository.resetPassword(email))
        }
    }

    fun changePassword(old: String, new: String, confirm :  String){
        viewModelScope.launch(Dispatchers.IO) {
            _changePassWordState.emit(authRepository.changePassword(old, new, confirm))
        }
    }
}