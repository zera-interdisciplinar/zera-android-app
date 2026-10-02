package com.zera.android.model.usecase.user

import com.zera.android.model.config.AppConfig
import com.zera.android.model.local.SharedPreferencesManager
import com.zera.android.model.remote.client.SupabaseStorage

class UploadAvatar {
    suspend fun execute(bytes: ByteArray, mimeType: String): String {
        val environments = AppConfig.environments
        if (!SupabaseStorage.isConfigured(environments)) {
            error("Upload de foto não configurado")
        }
        val userId = SharedPreferencesManager.getUserId()
            ?: error("Não há usuário logado")
        val mime = SupabaseStorage.canonicalMime(mimeType)
        val objectPath = SupabaseStorage.objectPath(userId, mime)
        SupabaseStorage.upload(bytes, mime, objectPath, environments)
        return SupabaseStorage.publicUrl(environments, objectPath)
    }
}
