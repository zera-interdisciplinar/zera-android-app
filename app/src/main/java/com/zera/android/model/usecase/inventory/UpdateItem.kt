package com.zera.android.model.usecase.inventory

import com.zera.android.model.dto.inventory.ItemResponseDTO
import com.zera.android.model.dto.inventory.UpdateItemRequestDTO
import com.zera.android.model.remote.client.InventoryClient

class UpdateItem {
    suspend fun execute(itemId: String, request: UpdateItemRequestDTO): ItemResponseDTO {
        return InventoryClient.inventoryService.updateItem(itemId, request)
    }
}
