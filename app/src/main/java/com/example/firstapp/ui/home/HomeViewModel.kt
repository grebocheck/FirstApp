package com.example.firstapp.ui.home

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.firstapp.api.client.NetworkResult
import com.example.firstapp.api.models.Inverter
import com.example.firstapp.api.models.PaginatedResponse
import com.example.firstapp.api.repository.AsempvRepository
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AsempvRepository.getInstance(application)

    private val _inverters = MutableLiveData<NetworkResult<PaginatedResponse<Inverter>>>()
    val inverters: LiveData<NetworkResult<PaginatedResponse<Inverter>>> = _inverters

    private val _isRefreshing = MutableLiveData<Boolean>()
    val isRefreshing: LiveData<Boolean> = _isRefreshing

    init {
        Log.d("HomeViewModel", "HomeViewModel initialized")

        // Перевіряємо чи користувач авторизований перед завантаженням
        if (repository.isLoggedIn()) {
            Log.d("HomeViewModel", "User is logged in, loading inverters")
            loadInverters()
        } else {
            Log.e("HomeViewModel", "User is not logged in! This should not happen")
            _inverters.value = NetworkResult.Error("Користувач не авторизований")
        }
    }

    fun loadInverters() {
        // Додаткова перевірка авторизації
        if (!repository.isLoggedIn()) {
            Log.e("HomeViewModel", "Attempted to load inverters but user is not logged in")
            _inverters.value = NetworkResult.Error("Потрібна авторизація")
            return
        }

        Log.d("HomeViewModel", "Starting to load inverters...")
        _inverters.value = NetworkResult.Loading()
        _isRefreshing.value = true

        viewModelScope.launch {
            try {
                val result = repository.getInverters(
                    pageSize = 20,
                    ordering = "-id"
                )
                Log.d("HomeViewModel", "Inverters loading result: ${result::class.simpleName}")

                when (result) {
                    is NetworkResult.Success -> {
                        Log.d("HomeViewModel", "Successfully loaded ${result.data?.results?.size ?: 0} inverters")
                    }
                    is NetworkResult.Error -> {
                        Log.e("HomeViewModel", "Error loading inverters: ${result.message}")
                    }
                    is NetworkResult.Loading -> {
                        Log.d("HomeViewModel", "Still loading...")
                    }
                }

                _inverters.value = result
                _isRefreshing.value = false
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Exception while loading inverters", e)
                _inverters.value = NetworkResult.Error("Помилка: ${e.message}")
                _isRefreshing.value = false
            }
        }
    }

    fun refreshInverters() {
        Log.d("HomeViewModel", "Refreshing inverters...")
        loadInverters()
    }
}
