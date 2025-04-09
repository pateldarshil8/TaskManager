package org.taskflow.app.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.taskflow.app.data.model.User
import org.taskflow.app.data.repository.AuthRepository
import org.taskflow.app.util.UiState
import javax.inject.Inject

@HiltViewModel
class UserAuthViewModel @Inject constructor(
    private val authRepo: AuthRepository
) : ViewModel() {

    private val _signupState = MutableLiveData<UiState<String>>()
    val signupState: LiveData<UiState<String>> get() = _signupState

    private val _loginState = MutableLiveData<UiState<String>>()
    val loginState: LiveData<UiState<String>> get() = _loginState

    private val _recoveryState = MutableLiveData<UiState<String>>()
    val recoveryState: LiveData<UiState<String>> get() = _recoveryState

    fun signup(email: String, password: String, user: User) {
        _signupState.value = UiState.Loading
        authRepo.registerUser(email, password, user) {
            _signupState.value = it
        }
    }

    fun signIn(email: String, password: String) {
        _loginState.value = UiState.Loading
        authRepo.loginUser(email, password) {
            _loginState.value = it
        }
    }

    fun resetPassword(email: String) {
        _recoveryState.value = UiState.Loading
        authRepo.forgotPassword(email) {
            _recoveryState.value = it
        }
    }

    fun signOut(onComplete: () -> Unit) {
        authRepo.logout(onComplete)
    }

    fun fetchUserSession(onResult: (User?) -> Unit) {
        authRepo.getSession(onResult)
    }
}
