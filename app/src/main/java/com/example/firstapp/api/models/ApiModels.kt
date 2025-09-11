// com/example/firstapp/api/models/ApiModels.kt

package com.example.firstapp.api.models

import com.google.gson.annotations.SerializedName

// Auth Models
data class LoginRequest(
    val username: String,
    val password: String
)

data class TokenResponse(
    val access: String,
    val refresh: String
)

data class TokenRefreshRequest(
    val refresh: String
)

data class TokenRefreshResponse(
    val access: String,
    val refresh: String
)

// Region and City Models
data class Region(
    val id: Int,
    val title: String
)

data class City(
    val id: Int,
    val title: String,
    val region: Region
)

// Data Type Models
data class DataType(
    @SerializedName("found_key")
    val foundKey: String?,
    val title: String,
    val measure: String
)

// Energy Data Models
data class EnergyData(
    val id: Int,
    @SerializedName("data_type_key")
    val dataTypeKey: String,
    @SerializedName("data_type_name")
    val dataTypeName: String,
    val value: Double,
    val measure: String,
    @SerializedName("created_at")
    val createdAt: String
)

// Inverter Models
data class Inverter(
    val id: Int,
    val title: String,
    @SerializedName("device_type")
    val deviceType: String,
    val address: String,
    val owner: String,
    @SerializedName("battery_size")
    val batterySize: Double?,
    @SerializedName("inverter_max_power")
    val inverterMaxPower: Double?,
    @SerializedName("solar_max_power")
    val solarMaxPower: Double?,
    val latitude: Double,
    val longitude: Double,
    val region: Region,
    val city: City,
    @SerializedName("power_station")
    val powerStation: String,
    @SerializedName("partner_logo")
    val partnerLogo: String?,
    @SerializedName("latest_data")
    val latestData: String
)

// Pagination Models
data class PaginatedResponse<T>(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<T>
)

// Dashboard Model (можна розширити пізніше на основі реального відповіді API)
data class DashboardStats(
    val totalInverters: Int = 0,
    val totalPower: Double = 0.0,
    val totalEnergy: Double = 0.0
)

// API Response wrapper
data class ApiResponse<T>(
    val success: Boolean,
    val data: T?,
    val message: String?
)

// Error Model
data class ApiError(
    val code: Int,
    val message: String,
    val details: String?
)