package com.example.newsapp.presentation.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsapp.domain.repo.AuthRepository
import com.example.newsapp.domain.state.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@Suppress("REDUNDANT_ELSE_IN_WHEN")
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    private var _loginState = MutableLiveData<Resource<String>>()
    val loginState : LiveData<Resource<String>> = _loginState

    fun login(email:String, password:String){
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
}