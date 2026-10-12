package com.zera.android.model.usecase.inventory

import com.zera.android.model.dto.inventory.ItemResponseDTO
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response

class GetItemByBarcodeTest {
    @Test
    fun returnsItemWhenFound() = runBlocking {
        val item = ItemResponseDTO(id = "item-1", name = "Notebook", barcode = "789")
        val useCase = GetItemByBarcode { code ->
            assertEquals("789", code)
            item
        }

        assertEquals(item, useCase.execute("789"))
    }

    @Test
    fun returnsNullOn404() = runBlocking {
        val useCase = GetItemByBarcode { throw httpError(404) }

        assertNull(useCase.execute("missing"))
    }

    @Test
    fun relançaOutroHttpStatus() {
        val useCase = GetItemByBarcode { throw httpError(500) }

        assertThrows(HttpException::class.java) {
            runBlocking { useCase.execute("789") }
        }
    }

    private fun httpError(code: Int): HttpException {
        val body = "{}".toResponseBody("application/json".toMediaType())
        return HttpException(Response.error<ItemResponseDTO>(code, body))
    }
}
