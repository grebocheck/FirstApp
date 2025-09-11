// com/example/firstapp/ui/auth/LoginViewModel.kt

package com.example.firstapp.ui.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.firstapp.api.client.NetworkResult
import com.example.firstapp.api.models.TokenResponse
import com.example.firstapp.api.repository.AsempvRepository
import kotlinx.coroutines.launch

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AsempvRepository.getInstance(application)

    private val _loginResult = MutableLiveData<NetworkResult<TokenResponse>>()
    val loginResult: LiveData<NetworkResult<TokenResponse>> = _loginResult

    fun login(username: String, password: String) {
        _loginResult.value = NetworkResult.Loading()

        viewModelScope.launch {
            val result = repository.login(username, password)
            _loginResult.value = result
        }
    }

    fun isLoggedIn(): Boolean {
        return repository.isLoggedIn()
    }
}
