// com/example/firstapp/api/repository/AsempvRepository.kt

package com.example.firstapp.api.repository

import android.content.Context
import com.example.firstapp.api.client.ApiClient
import com.example.firstapp.api.client.ApiException
import com.example.firstapp.api.client.NetworkResult
import com.example.firstapp.api.models.*
import retrofit2.Response
import java.io.IOException

class AsempvRepository private constructor(context: Context) {

    companion object {
        @Volatile
        private var INSTANCE: AsempvRepository? = null

        fun getInstance(context: Context): AsempvRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: AsempvRepository(context.applicationContext).also { INSTANCE = it }
            }
        }
    }

    private val apiClient = ApiClient.getInstance(context)

    // Generic function to handle API calls
    private suspend fun <T> safeApiCall(apiCall: suspend () -> Response<T>): NetworkResult<T> {
        return try {
            val response = apiCall()
            if (response.isSuccessful) {
                response.body()?.let {
                    NetworkResult.Success(it)
                } ?: NetworkResult.Error("Порожня відповідь від сервера")
            } else {
                NetworkResult.Error("Помилка: ${response.code()} ${response.message()}")
            }
        } catch (e: IOException) {
            NetworkResult.Error("Помилка мережі. Перевірте з'єднання з інтернетом.")
        } catch (e: Exception) {
            NetworkResult.Error("Невідома помилка: ${e.message}")
        }
    }

    // Auth methods
    suspend fun login(username: String, password: String): NetworkResult<TokenResponse> {
        val result = safeApiCall {
            apiClient.authService.login(LoginRequest(username, password))
        }

        if (result is NetworkResult.Success) {
            result.data?.let { tokenResponse ->
                apiClient.saveTokens(tokenResponse.access, tokenResponse.refresh)
            }
        }

        return result
    }

    suspend fun refreshToken(): NetworkResult<TokenRefreshResponse> {
        val refreshToken = apiClient.getRefreshToken()
            ?: return NetworkResult.Error("Відсутній refresh token")

        val result = safeApiCall {
            apiClient.authService.refreshToken(TokenRefreshRequest(refreshToken))
        }

        if (result is NetworkResult.Success) {
            result.data?.let { tokenResponse ->
                apiClient.saveTokens(tokenResponse.access, tokenResponse.refresh)
            }
        }

        return result
    }

    fun logout() {
        apiClient.clearTokens()
    }

    fun isLoggedIn(): Boolean {
        return apiClient.isLoggedIn()
    }

    // Dashboard methods
    suspend fun getDashboardStats(partner: String? = null): NetworkResult<DashboardStats> {
        return safeApiCall {
            apiClient.dashboardService.getDashboardStats(partner)
        }
    }

    // Data Types methods
    suspend fun getDataTypes(
        ordering: String? = null,
        page: Int? = null,
        search: String? = null
    ): NetworkResult<PaginatedResponse<DataType>> {
        return safeApiCall {
            apiClient.dataTypesService.getDataTypes(ordering, page, search)
        }
    }

    suspend fun getDataType(id: Int): NetworkResult<DataType> {
        return safeApiCall {
            apiClient.dataTypesService.getDataType(id)
        }
    }

    // Inverters methods
    suspend fun getInverters(
        city: Int? = null,
        lang: String? = "uk",
        maxPower: Double? = null,
        minPower: Double? = null,
        ordering: String? = null,
        page: Int? = null,
        pageSize: Int? = null,
        partner: String? = null,
        region: Int? = null,
        search: String? = null
    ): NetworkResult<PaginatedResponse<Inverter>> {
        return safeApiCall {
            apiClient.invertersService.getInverters(
                city, lang, maxPower, minPower, ordering,
                page, pageSize, partner, region, search
            )
        }
    }

    suspend fun getInverter(id: Int): NetworkResult<Inverter> {
        return safeApiCall {
            apiClient.invertersService.getInverter(id)
        }
    }

    suspend fun getInverterData(
        id: Int,
        aggregation: String? = null,
        startDate: String? = null,
        endDate: String? = null,
        types: List<String>? = null,
        page: Int? = null
    ): NetworkResult<PaginatedResponse<EnergyData>> {
        return safeApiCall {
            apiClient.invertersService.getInverterData(
                id, aggregation, startDate, endDate, types, page
            )
        }
    }

    suspend fun getInverterRealtime(
        id: Int,
        lang: String? = "uk"
    ): NetworkResult<Inverter> {
        return safeApiCall {
            apiClient.invertersService.getInverterRealtime(id, lang)
        }
    }

    suspend fun getInverterStatistics(
        id: Int,
        period: String? = null
    ): NetworkResult<Inverter> {
        return safeApiCall {
            apiClient.invertersService.getInverterStatistics(id, period)
        }
    }
}
