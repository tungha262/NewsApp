package com.example.newsapp.presentation.viewModel

import androidx.lifecycle.ViewModel
import com.example.newsapp.domain.repo.LocalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LocalViewModel @Inject constructor(
    private val localRepository: LocalRepository
) : ViewModel() {

}