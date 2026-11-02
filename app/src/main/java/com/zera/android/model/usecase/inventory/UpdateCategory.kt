package com.zera.android.model.usecase.inventory

import com.zera.android.model.dto.inventory.CategoryResponseDTO
import com.zera.android.model.dto.inventory.UpdateCategoryDescriptionRequestDTO
import com.zera.android.model.dto.inventory.UpdateCategoryNameRequestDTO
import com.zera.android.model.remote.client.InventoryClient

class UpdateCategory {
    suspend fun rename(categoryId: String, name: String): CategoryResponseDTO =
        InventoryClient.inventoryService.updateCategoryName(
            categoryId,
            UpdateCategoryNameRequestDTO(name = name),
        )

    suspend fun description(categoryId: String, description: String): CategoryResponseDTO =
        InventoryClient.inventoryService.updateCategoryDescription(
            categoryId,
            UpdateCategoryDescriptionRequestDTO(description = description),
        )
}
