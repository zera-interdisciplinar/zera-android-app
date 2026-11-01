package com.zera.android.model.usecase.inventory

import com.zera.android.model.config.AppConfig
import com.zera.android.model.remote.client.SupabaseStorage

class UploadItemPhoto {
    suspend fun execute(itemId: String, bytes: ByteArray, mimeType: String): String {
        val environments = AppConfig.environments
        val bucket = environments.supabaseItemsBucket
        if (!SupabaseStorage.isConfigured(environments, bucket)) {
            error("Upload de foto não configurado")
        }
        val mime = SupabaseStorage.canonicalMime(mimeType)
        val objectPath = SupabaseStorage.itemObjectPath(itemId, mime)
        SupabaseStorage.upload(bytes, mime, objectPath, environments, bucket)
        return SupabaseStorage.publicUrl(environments, objectPath, bucket)
    }
}
