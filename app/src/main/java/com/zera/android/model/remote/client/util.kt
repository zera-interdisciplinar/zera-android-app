package com.zera.android.model.remote.client

import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

internal fun OkHttpClient.Builder.zeraTimeouts(): OkHttpClient.Builder =
    connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)

internal fun serviceBaseUrl(raw: String, stripApiV1: Boolean): String {
    val url = raw.trim().toHttpUrl()
    val segments = url.pathSegments.filter { it.isNotEmpty() }.toMutableList()
    if (stripApiV1 && segments.takeLast(2) == listOf("api", "v1")) {
        segments.removeAt(segments.lastIndex)
        segments.removeAt(segments.lastIndex)
    } else if (!stripApiV1 && segments.lastOrNull() == "v1") {
        segments.removeAt(segments.lastIndex)
    }
    val builder = url.newBuilder().encodedPath("/")
    segments.forEach { builder.addPathSegment(it) }
    return trailingSlash(builder.build()).toString()
}

private fun trailingSlash(url: HttpUrl): HttpUrl {
    val path = url.encodedPath
    if (path.endsWith("/")) return url
    return url.newBuilder().encodedPath("$path/").build()
}
