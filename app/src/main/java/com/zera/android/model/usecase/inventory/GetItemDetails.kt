package com.zera.android.model.usecase.inventory

import com.zera.android.model.dto.inventory.ItemResponseDTO
import com.zera.android.model.remote.client.InventoryClient

class GetItemDetails {
    suspend fun execute(itemId: String): ItemResponseDTO {
        return InventoryClient.inventoryService.getItem(itemId)
    }
}
