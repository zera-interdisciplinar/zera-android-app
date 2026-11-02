package com.zera.android.model.usecase.inventory

import okio.Buffer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class UploadItemPhotoTest {
    @Test
    fun photoPartUsesTheFieldNameFromTheContract() {
        val part = UploadItemPhoto.photoPart(byteArrayOf(1, 2, 3), "image/jpeg")

        val disposition = part.headers?.get("Content-Disposition").orEmpty()
        assertTrue(disposition.contains("name=\"photo\""))
        assertTrue(disposition.contains("filename=\"photo.jpeg\""))
        assertEquals("image/jpeg", part.body.contentType().toString())
    }

    @Test
    fun photoPartCarriesTheImageBytes() {
        val part = UploadItemPhoto.photoPart(byteArrayOf(1, 2, 3), "image/jpeg")

        val buffer = Buffer()
        part.body.writeTo(buffer)
        assertEquals(listOf<Byte>(1, 2, 3), buffer.readByteArray().toList())
    }
}
