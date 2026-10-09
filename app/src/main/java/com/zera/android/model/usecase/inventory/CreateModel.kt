package com.zera.android.model.usecase.inventory

import com.zera.android.model.dto.inventory.CreateModelRequestDTO
import com.zera.android.model.dto.inventory.ModelResponseDTO
import com.zera.android.model.remote.client.InventoryClient

class CreateModel {
    suspend fun execute(
        name: String,
        manufacturer: String,
        categoryId: String,
        materialCodes: List<String>,
        notes: String?,
    ): ModelResponseDTO {
        return InventoryClient.inventoryService.createModel(
            CreateModelRequestDTO(
                name = name,
                manufacturer = manufacturer,
                categoryId = categoryId,
                materials = materialCodes,
                notes = notes,
            ),
        )
    }
}
