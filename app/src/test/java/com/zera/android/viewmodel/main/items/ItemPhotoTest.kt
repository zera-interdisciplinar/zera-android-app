package com.zera.android.viewmodel.main.items

import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response

class ItemPhotoTest {
    @Test
    fun explainsPhotoTooLargeAndStorageUnavailable() {
        assertEquals("A foto deve ter no máximo 5 MB.", ItemDetailsViewModel.itemPhotoWriteError(http(413)))
        assertEquals(
            "O envio de fotos está indisponível no momento.",
            ItemDetailsViewModel.itemPhotoWriteError(http(503)),
        )
    }

    @Test
    fun otherFailuresNeverShowTechnicalDetails() {
        val fallback = "Não foi possível enviar a foto. Tente novamente."

        assertEquals(fallback, ItemDetailsViewModel.itemPhotoWriteError(http(500)))
        assertEquals(fallback, ItemDetailsViewModel.itemPhotoWriteError(Exception("stack interna")))
    }

    @Test
    fun signedUrlKeepsItsQueryIntact() {
        val signed = "https://storage.example.com/photo.jpg?X-Amz-Signature=abc"

        assertEquals(signed, ItemDetailsViewModel.uploadedPhotoUrl(signed))
    }

    @Test
    fun urlWithoutQueryGetsCacheBustToShowTheNewPhoto() {
        val url = ItemDetailsViewModel.uploadedPhotoUrl("https://storage.example.com/photo.jpg")

        assertTrue(url.startsWith("https://storage.example.com/photo.jpg?t="))
    }

    private fun http(code: Int) = HttpException(Response.error<Unit>(code, "".toResponseBody()))
}
