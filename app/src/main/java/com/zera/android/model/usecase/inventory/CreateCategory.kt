package com.zera.android.model.usecase.inventory

import com.zera.android.model.dto.inventory.CategoryResponseDTO
import com.zera.android.model.dto.inventory.CreateCategoryRequestDTO
import com.zera.android.model.remote.client.InventoryClient

class CreateCategory {
    suspend fun execute(
        name: String,
        description: String?,
    ): CategoryResponseDTO {
        return InventoryClient.inventoryService.createCategory(
            CreateCategoryRequestDTO(
                name = name,
                description = description,
            ),
        )
    }
}
