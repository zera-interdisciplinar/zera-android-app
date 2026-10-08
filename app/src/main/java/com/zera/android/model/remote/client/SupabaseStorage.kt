package com.zera.android.model.remote.client

import com.zera.android.model.config.AppConfig
import com.zera.android.model.entity.config.Environments
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

object SupabaseStorage {
    private val json = Json { ignoreUnknownKeys = true }
    private val http = OkHttpClient.Builder().zeraTimeouts().build()

    fun isConfigured(environments: Environments = AppConfig.environments): Boolean =
        environments.supabaseUrl.isNotBlank() &&
            environments.supabaseAnonKey.isNotBlank() &&
            environments.supabaseAvatarsBucket.isNotBlank()

    fun canonicalMime(mimeType: String): String = when (mimeType.lowercase()) {
        "image/png" -> "image/png"
        "image/webp" -> "image/webp"
        "image/jpeg", "image/jpg" -> "image/jpeg"
        else -> mimeType
    }

    fun objectPath(userId: String, mimeType: String): String =
        "$userId/profile.${extensionFor(canonicalMime(mimeType))}"

    fun publicUrl(environments: Environments, objectPath: String): String {
        val base = environments.supabaseUrl.trim().trimEnd('/')
        val bucket = environments.supabaseAvatarsBucket.trim().trim('/')
        return "$base/storage/v1/object/public/$bucket/$objectPath"
    }

    suspend fun upload(
        bytes: ByteArray,
        mimeType: String,
        objectPath: String,
        environments: Environments = AppConfig.environments,
    ) = withContext(Dispatchers.IO) {
        val base = environments.supabaseUrl.trim().trimEnd('/')
        val bucket = environments.supabaseAvatarsBucket.trim().trim('/')
        val anonKey = environments.supabaseAnonKey.trim()
        val mime = canonicalMime(mimeType)
        val (folder, fileName) = splitObjectPath(objectPath)
        val url = base.toHttpUrl().newBuilder()
            .addPathSegment("storage")
            .addPathSegment("v1")
            .addPathSegment("object")
            .addPathSegment(bucket)
            .apply {
                if (folder != null) addPathSegment(folder)
            }
            .addPathSegment(fileName)
            .build()
        val request = Request.Builder()
            .url(url)
            .header("apikey", anonKey)
            .header("Authorization", "Bearer $anonKey")
            .header("x-upsert", "true")
            .post(bytes.toRequestBody(mime.toMediaType()))
            .build()
        http.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                val body = response.body?.string().orEmpty()
                error(storageErrorMessage(response.code, body))
            }
        }
    }

    internal fun extensionFor(mimeType: String): String = when (canonicalMime(mimeType)) {
        "image/png" -> "png"
        "image/webp" -> "webp"
        else -> "jpg"
    }

    internal fun splitObjectPath(objectPath: String): Pair<String?, String> {
        val slash = objectPath.lastIndexOf('/')
        if (slash <= 0) return null to objectPath
        return objectPath.substring(0, slash) to objectPath.substring(slash + 1)
    }

    internal fun storageErrorMessage(code: Int, body: String): String {
        val detail = parseStorageMessage(body)
        return if (detail != null) {
            "Não foi possível enviar a foto ($code): $detail"
        } else {
            "Não foi possível enviar a foto ($code)."
        }
    }

    private fun parseStorageMessage(body: String): String? {
        if (body.isBlank()) return null
        return try {
            val obj = json.parseToJsonElement(body).jsonObject
            obj["message"]?.jsonPrimitive?.content
                ?: obj["error"]?.jsonPrimitive?.content
        } catch (_: Exception) {
            body.take(200).takeIf { it.isNotBlank() }
        }
    }
}
