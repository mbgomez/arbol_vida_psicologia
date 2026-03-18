package com.netah.hakkam.numyah.mind.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.netah.hakkam.numyah.mind.auth.GoogleSignInManager
import com.netah.hakkam.numyah.mind.auth.GoogleUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val googleSignInManager: GoogleSignInManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState

    fun signInWithGoogle() {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading

            val result = googleSignInManager.signIn()
            _uiState.value = result.fold(
                onSuccess = { user -> AuthUiState.Success(user) },
                onFailure = { error -> AuthUiState.Error(error.message ?: "Sign-in failed") }
            )
        }
    }

    fun signOut() {
        viewModelScope.launch {
            googleSignInManager.signOut()
            _uiState.value = AuthUiState.Idle
        }
    }
}

sealed interface AuthUiState {
    data object Idle : AuthUiState
    data object Loading : AuthUiState
    data class Success(val user: GoogleUser) : AuthUiState
    data class Error(val message: String) : AuthUiState
}