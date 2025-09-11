package com.example.firstapp.api.services
// com/example/firstapp/api/services/ApiServices.kt

import com.example.firstapp.api.models.*
import retrofit2.Response
import retrofit2.http.*

// Auth Service
interface AuthService {
    @POST("api/v2/auth/login/")
    suspend fun login(@Body request: LoginRequest): Response<TokenResponse>

    @POST("api/v2/auth/refresh/")
    suspend fun refreshToken(@Body request: TokenRefreshRequest): Response<TokenRefreshResponse>
}

// Dashboard Service
interface DashboardService {
    @GET("api/v2/dashboard/")
    suspend fun getDashboardStats(
        @Query("partner") partner: String? = null
    ): Response<DashboardStats>
}

// Data Types Service
interface DataTypesService {
    @GET("api/v2/data-types/")
    suspend fun getDataTypes(
        @Query("ordering") ordering: String? = null,
        @Query("page") page: Int? = null,
        @Query("search") search: String? = null
    ): Response<PaginatedResponse<DataType>>

    @GET("api/v2/data-types/{id}/")
    suspend fun getDataType(@Path("id") id: Int): Response<DataType>
}

// Inverters Service
interface InvertersService {
    @GET("api/v2/inverters/")
    suspend fun getInverters(
        @Query("city") city: Int? = null,
        @Query("lang") lang: String? = "uk",
        @Query("max_power") maxPower: Double? = null,
        @Query("min_power") minPower: Double? = null,
        @Query("ordering") ordering: String? = null,
        @Query("page") page: Int? = null,
        @Query("page_size") pageSize: Int? = null,
        @Query("partner") partner: String? = null,
        @Query("region") region: Int? = null,
        @Query("search") search: String? = null
    ): Response<PaginatedResponse<Inverter>>

    @GET("api/v2/inverters/{id}/")
    suspend fun getInverter(@Path("id") id: Int): Response<Inverter>

    @GET("api/v2/inverters/{id}/data/")
    suspend fun getInverterData(
        @Path("id") id: Int,
        @Query("aggregation") aggregation: String? = null,
        @Query("start_date") startDate: String? = null,
        @Query("end_date") endDate: String? = null,
        @Query("types[]") types: List<String>? = null,
        @Query("page") page: Int? = null
    ): Response<PaginatedResponse<EnergyData>>

    @GET("api/v2/inverters/{id}/realtime/")
    suspend fun getInverterRealtime(
        @Path("id") id: Int,
        @Query("lang") lang: String? = "uk"
    ): Response<Inverter>

    @GET("api/v2/inverters/{id}/statistics/")
    suspend fun getInverterStatistics(
        @Path("id") id: Int,
        @Query("period") period: String? = null // today, week, month, year
    ): Response<Inverter>
}