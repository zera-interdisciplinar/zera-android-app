package com.zera.android.model.remote.client

import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.TimeUnit

/**
 * Falha de rede (sem internet, servidor inalcançável ou timeout), olhando também as causas.
 * O app mostra a tela cheia de conexão para esses erros, e não o popup da tela.
 * Timeout entra aqui mesmo podendo ser servidor lento (ver spec `telas-de-erro.md`).
 */
fun Throwable.isConnectionError(): Boolean =
    generateSequence(this) { it.cause }
        .take(MAX_CAUSE_DEPTH)
        .any { it is UnknownHostException || it is ConnectException || it is SocketTimeoutException }

private const val MAX_CAUSE_DEPTH = 10

internal fun OkHttpClient.Builder.zeraTimeouts(): OkHttpClient.Builder =
    connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)

/** O grafo do chat (FAQ + juiz) passa fácil de 30s; reports de PDF também. */
internal fun OkHttpClient.Builder.aiTimeouts(): OkHttpClient.Builder =
    connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(120, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)

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
