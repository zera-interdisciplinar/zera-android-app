package com.zera.android.model.usecase.inventory

import com.zera.android.model.dto.inventory.CategoryResponseDTO
import com.zera.android.model.remote.client.InventoryClient

class GetCategories {
    suspend fun execute(): List<CategoryResponseDTO> {
        return InventoryClient.inventoryService.getCategories()
    }
}
