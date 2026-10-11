package com.zera.android.model.usecase.inventory

import com.zera.android.model.dto.inventory.ItemResponseDTO
import com.zera.android.model.remote.client.InventoryClient
import retrofit2.HttpException

class GetItemByBarcode(
    private val fetch: suspend (String) -> ItemResponseDTO = {
        InventoryClient.inventoryService.getItemByBarcode(it)
    },
) {
    suspend fun execute(barcode: String): ItemResponseDTO? {
        return try {
            fetch(barcode)
        } catch (error: HttpException) {
            if (error.code() == 404) null else throw error
        }
    }
}
