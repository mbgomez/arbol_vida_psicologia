package com.netah.hakkam.numyah.mind.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.netah.hakkam.numyah.mind.domain.usecase.GetCachedPostsUseCase
import com.netah.hakkam.numyah.mind.domain.usecase.GetPostsUseCase
import com.netah.hakkam.numyah.mind.model.Post
import com.netah.hakkam.numyah.mind.model.Status
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val postsUseCase: GetPostsUseCase,
    private val cachedPostsUseCase: GetCachedPostsUseCase
) : ViewModel() {

    val postMutableState: MutableState<List<Post>> = mutableStateOf(emptyList())
    val isLoadingMutableState: MutableState<Boolean> = mutableStateOf(false)

    var job: Job? = null

    fun getPosts() {
        isLoadingMutableState.value = true
        job?.cancel()
        job = viewModelScope.launch {
            postsUseCase.run().catch { exception ->
                Timber.e(exception)
            }.collect { result ->
                when (result.status) {
                    Status.SUCCESS -> {
                        postMutableState.value = result.data ?: emptyList()
                        isLoadingMutableState.value = false
                    }

                    Status.ERROR -> {
                        isLoadingMutableState.value = false
                    }

                    else -> {
                        Timber.e("Unexpected state")
                    }
                }
            }
        }
    }

    fun getCachedPostsUseCase() {
        isLoadingMutableState.value = true
        job?.cancel()
        job = viewModelScope.launch {
            cachedPostsUseCase.run().catch { exception ->
                Timber.e(exception)
            }.collect { result ->
                when (result.status) {
                    Status.SUCCESS -> {
                        postMutableState.value = result.data ?: emptyList()
                        isLoadingMutableState.value = false
                    }

                    Status.ERROR -> {
                        isLoadingMutableState.value = false
                    }

                    else -> {
                        Timber.e("Unexpected state")
                    }
                }
            }
        }
    }
}