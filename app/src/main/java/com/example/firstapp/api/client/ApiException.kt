// com/example/firstapp/api/client/ApiException.kt

package com.example.firstapp.api.client

class ApiException(
    message: String,
    val code: Int? = null,
    cause: Throwable? = null
) : Exception(message, cause)
