package com.zera.android.model.usecase.inventory

import com.zera.android.model.dto.inventory.ItemResponseDTO
import com.zera.android.model.remote.client.InventoryClient
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class UploadItemPhoto {
    suspend fun execute(itemId: String, bytes: ByteArray, mimeType: String): ItemResponseDTO =
        InventoryClient.inventoryService.uploadItemPhoto(itemId, photoPart(bytes, mimeType))

    companion object {
        internal fun photoPart(bytes: ByteArray, mimeType: String): MultipartBody.Part =
            MultipartBody.Part.createFormData(
                name = "photo",
                filename = "photo.${mimeType.substringAfter('/')}",
                body = bytes.toRequestBody(mimeType.toMediaType()),
            )
    }
}
