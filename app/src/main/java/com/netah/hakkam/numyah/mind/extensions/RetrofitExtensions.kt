@file:JvmName(name = "RetrofitExtensions")

package com.netah.hakkam.numyah.mind.extensions

import com.netah.hakkam.numyah.mind.model.Resource
import retrofit2.Response
import java.net.HttpURLConnection

// Fetch  errorEvent code
const val AUTH_ERROR_CODE = 101
const val NO_DATA_ERROR_CODE = 102
const val INTERNAL_ERROR_CODE = 103
const val UNHANDLED_ERROR_CODE = 104

fun <T> Response<T>.parse(): Resource<T> =
    if (!this.isSuccessful && this.code() != HttpURLConnection.HTTP_NOT_MODIFIED) {
        this.handleHttpError()
    } else {
        this.body()?.let { response ->
            Resource.success(response)
        }.otherwise { Resource.error(NO_DATA_ERROR_CODE) }
    }

fun <T> Response<T>.handleHttpError(): Resource<T> =
    if (this.code() == HttpURLConnection.HTTP_INTERNAL_ERROR ||
        this.code() == HttpURLConnection.HTTP_GATEWAY_TIMEOUT
    ) {
        Resource.error(INTERNAL_ERROR_CODE)
    } else if (this.code() == HttpURLConnection.HTTP_UNAUTHORIZED ||
        this.code() == HttpURLConnection.HTTP_FORBIDDEN
    ) {
        Resource.error(AUTH_ERROR_CODE)
    } else {
        Resource.error(UNHANDLED_ERROR_CODE)
    }