package com.zera.android.model.usecase.inventory

import com.zera.android.model.dto.inventory.CategoryResponseDTO
import com.zera.android.model.remote.client.InventoryClient

class GetCategory {
    suspend fun execute(categoryId: String): CategoryResponseDTO =
        InventoryClient.inventoryService.getCategory(categoryId)
}
