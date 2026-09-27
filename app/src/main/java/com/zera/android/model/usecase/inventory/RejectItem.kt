package com.zera.android.model.usecase.inventory

import com.zera.android.model.dto.inventory.ItemResponseDTO
import com.zera.android.model.dto.inventory.RejectItemRequestDTO
import com.zera.android.model.remote.client.InventoryClient

class RejectItem {
    suspend fun execute(itemId: String, reason: String): ItemResponseDTO {
        return InventoryClient.inventoryService.rejectItem(
            itemId,
            RejectItemRequestDTO(reason = reason),
        )
    }
}
