package com.zera.android.model.usecase.inventory

import com.zera.android.model.dto.inventory.ItemResponseDTO
import com.zera.android.model.remote.client.InventoryClient

class SubmitItem(
    private val fetch: suspend (String) -> ItemResponseDTO = {
        InventoryClient.inventoryService.submitItem(it)
    },
) {
    suspend fun execute(itemId: String): ItemResponseDTO {
        return fetch(itemId)
    }
}
