package com.zera.android.model.remote.client

import okhttp3.logging.HttpLoggingInterceptor

internal fun httpLoggingInterceptor(): HttpLoggingInterceptor {
    return HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
        redactHeader("Authorization")
        redactHeader("apikey")
        redactHeader("apiKey")
    }
}
