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

    // New properties for pagination
    private val _allInverters = MutableLiveData<List<Inverter>>()
    val allInverters: LiveData<List<Inverter>> = _allInverters

    private val _isLoadingMore = MutableLiveData<Boolean>()
    val isLoadingMore: LiveData<Boolean> = _isLoadingMore

    private var currentPage = 1
    private var hasNextPage = true
    private var isLoadingInProgress = false
    private val pageSize = 20
    private val invertersList = mutableListOf<Inverter>()

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
        loadInvertersPage(1, clearExisting = true)
    }

    fun loadMoreInverters() {
        if (hasNextPage && !isLoadingInProgress) {
            loadInvertersPage(currentPage + 1, clearExisting = false)
        }
    }

    fun refreshInverters() {
        Log.d("HomeViewModel", "Refreshing inverters...")
        _isRefreshing.value = true
        currentPage = 1
        hasNextPage = true
        isLoadingInProgress = false
        invertersList.clear()
        loadInvertersPage(1, clearExisting = true)
    }

    private fun loadInvertersPage(page: Int, clearExisting: Boolean) {
        // Додаткова перевірка авторизації
        if (!repository.isLoggedIn()) {
            Log.e("HomeViewModel", "Attempted to load inverters but user is not logged in")
            _inverters.value = NetworkResult.Error("Потрібна авторизація")
            return
        }

        Log.d("HomeViewModel", "Loading inverters page: $page")

        if (clearExisting) {
            _inverters.value = NetworkResult.Loading()
        } else {
            _isLoadingMore.value = true
        }

        isLoadingInProgress = true

        viewModelScope.launch {
            try {
                val result = repository.getInverters(
                    page = page,
                    pageSize = pageSize,
                    ordering = "-id"
                )

                isLoadingInProgress = false
                _isRefreshing.value = false
                _isLoadingMore.value = false

                when (result) {
                    is NetworkResult.Success -> {
                        result.data?.let { paginatedResponse ->
                            Log.d("HomeViewModel", "Successfully loaded ${paginatedResponse.results.size} inverters for page $page")

                            if (clearExisting) {
                                invertersList.clear()
                            }

                            invertersList.addAll(paginatedResponse.results)
                            currentPage = page
                            hasNextPage = paginatedResponse.next != null

                            // Create a new paginated response with all inverters
                            val allInvertersResponse = PaginatedResponse(
                                count = paginatedResponse.count,
                                next = if (hasNextPage) "next" else null,
                                previous = if (page > 1) "prev" else null,
                                results = invertersList.toList()
                            )

                            _inverters.value = NetworkResult.Success(allInvertersResponse)
                            _allInverters.value = invertersList.toList()

                            Log.d("HomeViewModel", "Total inverters: ${invertersList.size}, hasNextPage: $hasNextPage")
                        }
                    }
                    is NetworkResult.Error -> {
                        Log.e("HomeViewModel", "Error loading inverters page $page: ${result.message}")
                        _inverters.value = result
                    }
                    is NetworkResult.Loading -> {
                        Log.d("HomeViewModel", "Still loading page $page...")
                    }
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Exception while loading inverters page $page", e)
                isLoadingInProgress = false
                _isRefreshing.value = false
                _isLoadingMore.value = false
                _inverters.value = NetworkResult.Error("Помилка: ${e.message}")
            }
        }
    }

    fun canLoadMore(): Boolean {
        return hasNextPage && !isLoadingInProgress
    }

    fun getCurrentPage(): Int = currentPage
    fun getHasNextPage(): Boolean = hasNextPage
}
